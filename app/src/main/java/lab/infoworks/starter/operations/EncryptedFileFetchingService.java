package lab.infoworks.starter.operations;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import lab.infoworks.libshared.domain.repository.definition.RiderPhotoRepository;
import lab.infoworks.libshared.domain.shared.AssetManager;
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
                //Create User's internal Dir
                String dirName = userid.toString();
                try {
                    File userDir = new File(getApplicationContext().getFilesDir(), dirName);
                    Log.d(TAG, "onStartCommand: Created: " + (userDir.createNewFile() ? "true" : "false"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //fetch Images:
                getPhotoRepository().fetchPhoto(userid, imgPath, (decryptedBase64) -> {
                    //TODO: Create Image From String
                    // then save into internal storage:
                    if (decryptedBase64 != null && !decryptedBase64.isEmpty()){
                        try {
                            Bitmap bitmap = AssetManager.readImageFromBase64(decryptedBase64);
                            //Now save into internal disk:
                            String fileName = imgPath.replace("sample/", "");
                            FileOutputStream fileStream = getApplicationContext().openFileOutput(fileName, Context.MODE_PRIVATE);
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            fileStream.write(baos.toByteArray());
                            baos.close();
                            fileStream.close();
                            //
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                if (++length == imgPaths.size()){
                    Map<String, Object> data = new HashMap<>();
                    data.put("userDir", dirName);
                    NotificationCenter.postNotification(getApplication().getApplicationContext(), ENCRYPTED_SERVICE_COMPLETE, data);
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