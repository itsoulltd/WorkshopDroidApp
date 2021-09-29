package lab.infoworks.starter.operations;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lab.infoworks.libshared.domain.repository.definition.RiderPhotoRepository;
import lab.infoworks.libshared.domain.shared.AssetManager;
import lab.infoworks.libshared.domain.shared.FileManager;
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
        //Example
        try{
            //Create User's internal Dir
            String albumName = userid.toString();
            FileManager fileManager = new FileManager(getApplication());
            File albumDir = fileManager.createFolder(albumName);
            //
            List<String> imgPaths = getPhotoRepository().fetchPhotos(userid);
            for (String imgPath : imgPaths) {
                //fetch Images:
                String decryptedBase64 = getPhotoRepository().fetchPhoto(userid, imgPath);
                //Create Image From String
                //then save into internal storage:
                if (decryptedBase64 != null && !decryptedBase64.isEmpty()){
                    try {
                        Bitmap bitmap = AssetManager.readImageFromBase64(decryptedBase64);
                        String fileName = imgPath.replace("sample/", "");
                        fileManager.saveBitmap(bitmap, albumDir, fileName, 100);
                        //Now save meta-data into db:
                        getPhotoRepository().addPhotoToAlbum(userid, albumName, fileName);
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }
            //
            Map<String, Object> data = new HashMap<>();
            data.put("albumName", albumName);
            data.put("userid", userid);
            NotificationCenter.postNotification(getApplication().getApplicationContext(), ENCRYPTED_SERVICE_COMPLETE, data);
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        super.onDestroy();
    }
}