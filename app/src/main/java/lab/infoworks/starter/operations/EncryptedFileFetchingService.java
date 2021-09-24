package lab.infoworks.starter.operations;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
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
        //
        getPhotoRepository().fetchPhotos(userid, (imgPaths) -> {
            int length = 0;
            for (String imgPath : imgPaths) {
                //Create User's internal Dir
                String albumName = userid.toString();
                FileManager fileManager = new FileManager(getApplication());
                /*File internalFilesDir = getApplicationContext().getFilesDir();
                final File userDir = new File(internalFilesDir, albumName);
                if (!userDir.exists()) {
                    Log.d(TAG, "onStartCommand: Dir created ? " + (userDir.mkdir() ? "true" : "false"));;
                }*/
                File albumDir = fileManager.createFolder(albumName);
                //fetch Images:
                getPhotoRepository().fetchPhoto(userid, imgPath, (decryptedBase64) -> {
                    //Create Image From String
                    //then save into internal storage:
                    if (decryptedBase64 != null && !decryptedBase64.isEmpty()){
                        try {
                            Bitmap bitmap = AssetManager.readImageFromBase64(decryptedBase64);
                            String fileName = imgPath.replace("sample/", "");
                            //Now save into internal disk:
                            /*File imgFile = new File(albumDir, fileName);
                            if (imgFile.exists()){
                                //IF-EXIST-DELETE
                                Log.d(TAG, "onStartCommand: File Deleted when Exist:"
                                        + (imgFile.delete() ? "true" : "false"));
                            }*/
                            /*File imgFile = fileManager.getFile(albumDir, fileName, true);
                            FileOutputStream fos = new FileOutputStream(imgFile);
                            Bitmap.CompressFormat format = (fileName.toLowerCase().contains("png"))
                                    ? Bitmap.CompressFormat.PNG
                                    : Bitmap.CompressFormat.JPEG;
                            bitmap.compress(format, 100, fos);
                            fos.flush();
                            fos.close();*/
                            fileManager.saveBitmap(bitmap, albumDir, fileName, 100);
                            //Now save meta-data into db:
                            getPhotoRepository().addPhotoToAlbum(userid, albumName, fileName);
                        } catch (IOException e) {
                            Log.e(TAG, e.getMessage());
                        }
                    }
                });
                if (++length == imgPaths.size()){
                    Map<String, Object> data = new HashMap<>();
                    data.put("albumName", albumName);
                    data.put("userid", userid);
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