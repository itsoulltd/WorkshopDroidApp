package lab.infoworks.starter.ui.app;

import android.app.Application;
import android.util.Log;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lab.infoworks.libshared.domain.remote.DownloadTracker;
import lab.infoworks.libshared.util.crypto.SecretKeyStore;
import lab.infoworks.starter.BuildConfig;
import lab.infoworks.starter.util.DeviceUuid;

public class StarterApp extends Application {

    public static final String SECRET_ALIAS = "towhid@gmail.com";
    public static ExecutorService executor = Executors.newFixedThreadPool((Runtime.getRuntime().availableProcessors() / 2) + 1);

    @Override
    public void onCreate() {
        super.onCreate();
        //Initializing GeoTracker
        Log.i("StarterApp", "API Gateway: " + BuildConfig.api_gateway);

        //Generate Device UUID:
        UUID uuid = new DeviceUuid(getApplicationContext()).getUuid();

        //Save the device uuid into KeyStore:
        SecretKeyStore.init(this).storeSecret(SECRET_ALIAS, uuid.toString(), false);

        //Register for Download Complete:
        DownloadTracker.registerReceiverForCompletion(this);
    }

    @Override
    public void onTerminate() {
        if (executor != null && !executor.isShutdown()){
            executor.shutdown();
            executor = null;
        }
        super.onTerminate();
    }
}
