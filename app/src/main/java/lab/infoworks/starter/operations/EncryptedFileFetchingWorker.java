package lab.infoworks.starter.operations;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lab.infoworks.libshared.domain.repository.definition.RiderPhotoRepository;
import lab.infoworks.libshared.domain.shared.AssetManager;
import lab.infoworks.libshared.domain.shared.FileManager;
import lab.infoworks.libshared.notifications.NotificationCenter;

public class EncryptedFileFetchingWorker extends Worker {

    public static final String ENCRYPTED_SERVICE_COMPLETE = "encrypted-complete";
    public static final String TAG = EncryptedFileFetchingWorker.class.getSimpleName();
    private String baseUrl;
    private String jwtToken;
    private Integer userid;

    private RiderPhotoRepository photoRepository;

    public RiderPhotoRepository getPhotoRepository() {
        if (photoRepository == null){
            photoRepository = RiderPhotoRepository.create(getApplicationContext(), "true", baseUrl);
        }
        return photoRepository;
    }

    public EncryptedFileFetchingWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.baseUrl = workerParams.getInputData().getString("baseUrl");
        this.jwtToken = workerParams.getInputData().getString("jwt-token");
        this.userid = workerParams.getInputData().getInt("userid", 0000);
    }

    @NonNull
    @Override
    public Result doWork() {
        //Example:
        try {
            List<String> imgPaths = getPhotoRepository().fetchPhotos(userid);
            String albumName = userid.toString();
            FileManager fileManager = new FileManager(getApplicationContext());
            File albumDir = fileManager.createFolder(albumName);
            //
            for (String imgPath : imgPaths) {
                //Create User's internal Dir
                //fetch Images:
                String decryptedBase64 = getPhotoRepository().fetchPhoto(userid, imgPath);
                //Create Image From String
                //then save into internal storage:
                if (decryptedBase64 != null && !decryptedBase64.isEmpty()){
                    try {
                        Bitmap bitmap = AssetManager.readImageFromBase64(decryptedBase64);
                        String fileName = imgPath.replace("sample/", "");
                        //Now save into internal disk:
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
            NotificationCenter.postNotification(getApplicationContext(), ENCRYPTED_SERVICE_COMPLETE, data);
            return Result.success();
        } catch (IOException e) {
            Log.d(TAG, "doWork: " + e.getMessage());
        }
        return Result.failure();
    }

}
