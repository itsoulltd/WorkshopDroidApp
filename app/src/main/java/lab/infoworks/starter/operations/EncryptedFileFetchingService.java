package lab.infoworks.starter.operations;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class EncryptedFileFetchingService extends Service {

    public static final String TAG = EncryptedFileFetchingService.class.getSimpleName();

    public EncryptedFileFetchingService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: ");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        super.onDestroy();
    }
}