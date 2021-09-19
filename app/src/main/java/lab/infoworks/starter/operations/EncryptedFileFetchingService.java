package lab.infoworks.starter.operations;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.util.List;

import lab.infoworks.libshared.domain.repository.definition.RiderPhotoRepository;
import lab.infoworks.libshared.notifications.NotificationCenter;

public class EncryptedFileFetchingService extends Service {

    public static final String ENCRYPTED_SERVICE_COMPLETE = "encrypted-complete";
    public static final String TAG = EncryptedFileFetchingService.class.getSimpleName();
    private String baseUrl;
    private String jwtToken;
    private Integer userid;

    private RiderPhotoRepository photoRepository;

    public RiderPhotoRepository getPhotoRepository() {
        if (photoRepository == null){
            photoRepository = RiderPhotoRepository.create(getApplication(), "true", baseUrl);
        }
        return photoRepository;
    }

    public EncryptedFileFetchingService() {}

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
        //
        getPhotoRepository().fetchPhotos(userid, (imgPaths) -> {
            int length = 0;
            for (String imgPath : imgPaths) {
                getPhotoRepository().fetchPhoto(userid, imgPath, (decryptedBase64) -> {
                    //TODO: Create Image From String
                    // then save into internal storage:
                    if (decryptedBase64 != null && !decryptedBase64.isEmpty()){
                        //
                    }
                });
                if (++length == imgPaths.size()){
                    NotificationCenter.postNotification(getApplication().getApplicationContext(), ENCRYPTED_SERVICE_COMPLETE, null);
                }
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