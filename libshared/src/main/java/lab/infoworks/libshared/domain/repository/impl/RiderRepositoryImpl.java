package lab.infoworks.libshared.domain.repository.impl;

import android.content.Context;
import android.os.Build;
import android.os.Handler;

import androidx.annotation.RequiresApi;

import com.infoworks.lab.cryptor.impl.AESCryptor;
import com.it.soul.lab.data.base.DataSource;
import com.it.soul.lab.data.base.DataStorage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import lab.infoworks.libshared.domain.datasource.RiderDataSource;
import lab.infoworks.libshared.domain.datasource.SampleData;
import lab.infoworks.libshared.domain.model.Rider;
import lab.infoworks.libshared.domain.remote.RemoteConfig;
import lab.infoworks.libshared.domain.remote.api.RiderApiService;
import lab.infoworks.libshared.domain.repository.definition.RiderPhotoRepository;
import lab.infoworks.libshared.domain.repository.definition.RiderRepository;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RiderRepositoryImpl implements RiderRepository, RiderPhotoRepository {

    private static final String SECRET = "my-country-man";
    private final DataSource<Integer, Rider> dataSource;
    private RiderApiService apiService;
    private AESCryptor cryptor;

    public RiderRepositoryImpl(Context context, String localFirst, String baseUrl) {
        this.dataSource = new RiderDataSource(context, localFirst, baseUrl);
        //Interceptor jwtToken = new BearerTokenInterceptor(jwtToken);
        this.apiService = RemoteConfig.getInstance(baseUrl, RiderApiService.class);
        this.cryptor = new AESCryptor();
    }

    public RiderApiService getApiService() {
        return apiService;
    }

    @Override @RequiresApi(Build.VERSION_CODES.N)
    public void findRiders(Consumer<List<Rider>> consumer) {
        if (consumer == null) return;
        final int maxItem = dataSource.size();
        //TODO: For Simulating Network: Delay-1 sec
        new Handler().postDelayed(() -> {
                    //Calling API:
                    dataSource.readAsync(0, maxItem, (riders) ->
                            consumer.accept(Arrays.asList(riders))
                    );
                }
                , 1000);
    }

    @Override
    public boolean isEmpty() {
        return dataSource.size() <= 0;
    }

    @Override
    public void update(Rider updated) {
        dataSource.replace(updated.getId(), updated);
        ((DataStorage)dataSource).save(true);
    }

    @Override
    public void addSampleData(Context context) {
        int idx = 0;
        for (Rider rider : SampleData.getRidersFrom(context)) {
            rider.setId(++idx);
            dataSource.put(idx, rider);
        }
        ((DataStorage)dataSource).save(true);
    }

    @Override @RequiresApi(Build.VERSION_CODES.N)
    public void fetchPhotos(Integer userId, Consumer<List<String>> consumer) {
        getApiService().fetchPhotos(userId)
                .enqueue(new Callback<List<String>>() {
                    @Override
                    public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                        List<String> imgPaths = response.body();
                        if(consumer != null) consumer.accept(imgPaths);
                    }

                    @Override
                    public void onFailure(Call<List<String>> call, Throwable t) {
                        if (consumer != null) consumer.accept(new ArrayList<>());
                    }
                });
    }

    @Override @RequiresApi(Build.VERSION_CODES.N)
    public void fetchPhoto(Integer userId, String imgPath, Consumer<String> consumer) {
        getApiService().fetchPhoto(userId, imgPath).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String encrypted = response.body();
                String decryptedBase64 = cryptor.decrypt(SECRET, encrypted);
                if(consumer != null) consumer.accept(decryptedBase64);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                if (consumer != null) consumer.accept(null);
            }
        });
    }
}
