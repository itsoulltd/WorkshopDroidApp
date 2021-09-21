package lab.infoworks.libshared.domain.datasource;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.it.soul.lab.data.base.DataStorage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import lab.infoworks.libshared.domain.db.AppDB;
import lab.infoworks.libshared.domain.db.dao.RiderDAO;
import lab.infoworks.libshared.domain.model.Rider;
import lab.infoworks.libshared.domain.remote.BearerTokenInterceptor;
import lab.infoworks.libshared.domain.remote.RemoteConfig;
import lab.infoworks.libshared.domain.remote.api.RiderApiService;
import okhttp3.Interceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RiderDataSource extends CMDataSource<Integer, Rider> implements DataStorage, AutoCloseable {

    public static final String TAG = RiderDataSource.class.getSimpleName();
    private AppDB db;
    private ExecutorService executor;
    private String baseUrl;
    private RiderApiService apiService;

    /**
     * if (local_first == true)
     * -> fetch: first get what was locally saved -> notify UI -> fire async-remote call -> update local store -> notify UI.
     * -> insert/update/delete: first happen locally -> notify UI -> fire async-remote call.
     * else
     * -> fetch: first fetch from remote -> update local -> notify UI.
     * -> insert/update/delete: first happen remote -> update in local -> notify UI.
     */
    private final boolean sync_state_local_first;

    private ExecutorService getExecutor() {
        if (executor == null){
            executor = Executors.newSingleThreadExecutor();
        }
        return executor;
    }

    private RiderApiService getApiService() {
        if (baseUrl == null || baseUrl.isEmpty()) return null;
        if (apiService == null){
            //Interceptor jwtToken = new BearerTokenInterceptor("jwt-token"); //Get From Some-where
            apiService = RemoteConfig.getInstance(baseUrl, RiderApiService.class);
        }
        return apiService;
    }

    public RiderDataSource(Context context){
        this(context, "true", null);
    }

    public RiderDataSource(Context context, String localFirst, String baseUrl) {
        this.db = AppDB.getInstance(context);
        this.sync_state_local_first = Boolean.parseBoolean(localFirst);
        this.baseUrl = baseUrl;
        if (sync_state_local_first){
            retrieve();
        }
    }

    @Override @RequiresApi(Build.VERSION_CODES.N)
    public void readAsync(int offset, int pageSize, Consumer<Rider[]> consumer) {
        if (consumer != null) {
            //
            if (sync_state_local_first){
                //Fetch Locally:
                List<Rider> items = readSyncAsList(offset, pageSize);
                //Notify UI:
                consumer.accept(items.toArray(new Rider[0]));
                //Fetch async from remote:
                if (getApiService() != null){
                    getApiService()
                            .fetch(pageSize, offset)
                            .enqueue(new Callback<List<Rider>>() {
                                @Override
                                public void onResponse(Call<List<Rider>> call, Response<List<Rider>> response) {
                                    List<Rider> riders = response.body();
                                    if (riders != null){
                                        Log.d("RIDER-API", "Size: " + response.body().size());
                                        for (Rider rdr : riders) {
                                            put(rdr.getId(), rdr);
                                        }
                                        //Update Local-Store:
                                        save(true);
                                        //Notify UI Again
                                        items.addAll(riders);
                                        consumer.accept(items.toArray(new Rider[0]));
                                    }
                                }

                                @Override
                                public void onFailure(Call<List<Rider>> call, Throwable t) {
                                    Log.d("RIDER-API-FAILED", t.getLocalizedMessage());
                                }
                            });
                    //
                }
            }else {
                //...
                if (getApiService() != null){}
            }
        }
    }

    @Override
    public void save(boolean async) {
        //TODO: Save Data using Preferred Persistence Technology:
        if (async){
            getExecutor().submit(() -> {
                RiderDAO dao = db.riderDao();
                dao.insert(new ArrayList<>(getInMemoryStorage().values()));
            });
        }else {
            //...
        }
    }

    @Override
    public boolean retrieve() {
        //TODO: Retrieve Data using Preferred Persistence Technology:
        getExecutor().submit(() -> {
            int size = db.riderDao().rowCount();
            List<Rider> results = db.riderDao().read(size, 0);
            for (Rider rider: results) {
                put(rider.getId(), rider);
            }
        });
        return true;
    }

    @Override
    public boolean delete() {
        //TODO: Delete Data using Preferred Persistence Technology:
        if (sync_state_local_first){
            getExecutor().submit(() -> {
                db.riderDao().deleteAll();
            });
            return true;
        }else {
            //...
            if (getApiService() != null){}
            return false;
        }
    }

    @Override
    public void close() throws Exception {
        if (executor != null && !executor.isShutdown()){
            executor.shutdown();
            executor = null;
        }
    }

    @Override
    public Rider replace(Integer integer, Rider update) {
        if (sync_state_local_first){
            //Update in memory:
            Rider oldVal = super.replace(integer, update);
            //Update roomBD & remoteDb
            getExecutor().submit(() -> {
                //roomDB
                RiderDAO dao = db.riderDao();
                dao.insert(update);
                //remoteDB
                if (getApiService() != null){
                    Call<Rider> updateCall = getApiService().update(update);
                    try {
                        Response<Rider> response = updateCall.execute();
                        //Example of retry: 1 time:
                        if (!response.isSuccessful()){
                            response = updateCall.clone().execute();
                        }
                        Log.d(TAG, "replace: " + response.isSuccessful());
                    } catch (IOException e) {}
                }
                //
            });
            return oldVal;
        }else {
            //...
            final Rider oldVal = super.replace(integer, update);
            return oldVal;
        }
    }
}
