package lab.infoworks.libshared.domain.shared;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileManager {

    public enum StorageMode{
        INTERNAL, EXTERNAL;
    }

    private Context appContext;
    private StorageMode mode = StorageMode.INTERNAL;

    public Context getAppContext() {
        return appContext;
    }

    public FileManager(Context application) {
        this.appContext = (appContext instanceof Application)
                ? application.getApplicationContext()
                : appContext;
    }

    public FileManager(Context application, StorageMode mode) {
        this(application);
        this.mode = mode;
    }

    public File createFolder(String folderName) {
        if (mode == StorageMode.INTERNAL){
            File root = getAppContext().getFilesDir();
            final File folder = new File(root, folderName);
            boolean isCreated = !folder.exists() ? folder.mkdir() : false;
            return folder;
        } else {
            //TODO: Not implemented:
            return null;
        }
    }

    public File getFile(File folder, String fileName, boolean removeIfExist){
        File file = new File(folder, fileName);
        if (removeIfExist && file.exists()) file.delete();
        return file;
    }

    public void saveBitmap(Bitmap bitmap, File folder, String fileName, int quality) throws IOException {
        File imgFile = getFile(folder, fileName, true);
        try (FileOutputStream fos = new FileOutputStream(imgFile)){
            Bitmap.CompressFormat format = (fileName.toLowerCase().contains("png"))
                    ? Bitmap.CompressFormat.PNG
                    : Bitmap.CompressFormat.JPEG;
            bitmap.compress(format, quality, fos);
            fos.flush();
        }
    }

    public Bitmap readBitmap(File folder, String fileName) throws IOException {
        File imgFile = getFile(folder, fileName, false);
        if (imgFile.exists()){
            try(FileInputStream fos = new FileInputStream(imgFile)) {
                return AssetManager.readAsImage(fos, 0);
            }
        }
        return null;
    }

}
