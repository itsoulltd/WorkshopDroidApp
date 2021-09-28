package lab.infoworks.libshared.domain.shared;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import androidx.test.InstrumentationRegistry;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import com.it.soul.lab.sql.query.models.Predicate;
import com.it.soul.lab.sql.query.models.Where;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4ClassRunner.class)
public class MediaStoreBrowserTest {

    private Context appContext;

    @Before
    public void before(){
        appContext = InstrumentationRegistry.getTargetContext();
    }

    @Test
    public void stockFlowTest(){

        // Need the READ_EXTERNAL_STORAGE permission if accessing video files that your
        // app didn't create.

        List<Video> videoList = new ArrayList<>();

        Uri collection;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            collection = MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
        } else {
            collection = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        }

        String[] projection = new String[] {
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.SIZE
        };
        String selection = MediaStore.Video.Media.DURATION + " >= ?";
        String[] selectionArgs = new String[] {
                String.valueOf(TimeUnit.MILLISECONDS.convert(5, TimeUnit.MINUTES))
        };
        String sortOrder = MediaStore.Video.Media.DISPLAY_NAME + " ASC";
        //
        try (Cursor cursor = appContext.getContentResolver().query(
                collection,
                projection,
                selection,
                selectionArgs,
                sortOrder )) {
            // Cache column indices.
            int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID);
            int nameColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME);
            int durationColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION);
            int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE);

            while (cursor.moveToNext()) {
                // Get values of columns for a given video.
                long id = cursor.getLong(idColumn);
                String name = cursor.getString(nameColumn);
                int duration = cursor.getInt(durationColumn);
                int size = cursor.getInt(sizeColumn);

                Uri contentUri = ContentUris.withAppendedId(
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id);

                // Stores column values and the contentUri in a local object
                // that represents the media file.
                videoList.add(new Video(contentUri, name, duration, size));
            }
        }
        //
    }

    @Test
    public void createFlowTest() {
        //Write the search clause:
        String durationIs = String.valueOf(TimeUnit.MILLISECONDS.convert(5, TimeUnit.MINUTES));
        Predicate predicate = new Where(MediaStore.Video.Media.DURATION)
                .isGreaterThenOrEqual(durationIs);
        //Fetch the query:
        List<MediaStoreBrowser.MediaStoreItem> items = new MediaStoreBrowser.Builder(appContext)
                .from(MediaStoreBrowser.Type.Video)
                .select(MediaStore.Video.Media._ID,
                        MediaStore.Video.Media.DISPLAY_NAME,
                        MediaStore.Video.Media.DURATION,
                        MediaStore.Video.Media.SIZE)
                .where(predicate)
                .orderBy(MediaStore.Video.Media.DISPLAY_NAME)
                .fetch((cursor, index) -> {
                    //Cache column indices.
                    int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID);
                    int nameColumn =
                            cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME);
                    int durationColumn =
                            cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION);
                    int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE);

                    //Get values of columns for a given video.
                    long id = cursor.getLong(idColumn);
                    String name = cursor.getString(nameColumn);
                    int duration = cursor.getInt(durationColumn);
                    int size = cursor.getInt(sizeColumn);

                    Uri contentUri = ContentUris.withAppendedId(
                            MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id);

                    return new Video(contentUri, name, duration, size);
                });
        //Do what you want to do with items
        //...
    }

    private class Video extends MediaStoreBrowser.MediaStoreItem {

        private final Uri uri;
        private final String name;
        private final int duration;
        private final int size;

        public Video(Uri uri, String name, int duration, int size) {
            super(MediaStoreBrowser.Type.Video, uri, name, duration, size);
            this.uri = uri;
            this.name = name;
            this.duration = duration;
            this.size = size;
        }
    }
}