package lab.infoworks.starter.operations;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.infoworks.lab.cryptor.impl.AESCryptor;

import java.io.IOException;
import java.util.List;

import lab.infoworks.libshared.domain.remote.RemoteConfig;
import lab.infoworks.libshared.domain.remote.api.RiderApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EncryptedFileFetchingService extends Service {

    public static final String SECRET = "my-country-man";
    public static final String ENCRYPTED_SERVICE_COMPLETE = "encrypted-complete";
    public static final String TAG = EncryptedFileFetchingService.class.getSimpleName();
    private AESCryptor cryptor;
    private String baseUrl;
    private String jwtToken;
    private Integer userid;

    private RiderApiService apiService;
    public RiderApiService getApiService() {
        if (baseUrl == null || baseUrl.isEmpty()) return null;
        if (apiService == null){
            //Interceptor jwtToken = new BearerTokenInterceptor(jwtToken);
            apiService = RemoteConfig.getInstance(baseUrl, RiderApiService.class);
        }
        return apiService;
    }

    public EncryptedFileFetchingService() {
        cryptor = new AESCryptor();
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: ");
        baseUrl = intent.getStringExtra("baseUrl");
        jwtToken = intent.getStringExtra("jwt-token");
        userid = intent.getIntExtra("userid", 0000);

        getApiService().fetchAlbums(userid)
                .enqueue(new Callback<List<String>>() {
                    @Override
                    public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                        //TODO:
                        List<String> imgPaths = response.body();
                        for (String imgPath : imgPaths) {
                            fetchImage(userid, imgPath);
                        }
                    }

                    private boolean fetchImage(Integer userid, String imgPath) {
                        try {
                            Response<String> response = getApiService().fetchAlbum(userid, imgPath).execute();
                            String encrypted = response.body();
                            String decryptedBase64 = cryptor.decrypt(SECRET, encrypted);
                            //TODO: Create Image From String
                            // then save into internal storage:
                            return true;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return false;
                    }

                    @Override
                    public void onFailure(Call<List<String>> call, Throwable t) {
                        //TODO:
                    }
                });

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        super.onDestroy();
    }
}