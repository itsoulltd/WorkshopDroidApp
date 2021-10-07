package lab.infoworks.starter.ui.app;

import android.app.Application;
import android.util.Log;

import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lab.infoworks.libshared.domain.remote.DownloadTracker;
import lab.infoworks.starter.BuildConfig;
import lab.infoworks.starter.util.DeviceUuid;

public class StarterApp extends Application {

    public static ExecutorService executor = Executors.newFixedThreadPool((Runtime.getRuntime().availableProcessors() / 2) + 1);

    @Override
    public void onCreate() {
        super.onCreate();
        //Initializing GeoTracker
        Log.i("StarterApp", "API Gateway: " + BuildConfig.api_gateway);
        //Generate Device UUID:
        UUID uuid = new DeviceUuid(getApplicationContext()).getUuid();
        Log.d("StarterApp", "Device UUID: " + uuid);
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
