package lab.infoworks.libshared.domain.shared;

import android.content.Context;
import android.provider.MediaStore;

import androidx.test.InstrumentationRegistry;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import com.it.soul.lab.sql.query.models.Predicate;
import com.it.soul.lab.sql.query.models.Where;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

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
    public void createFlowTest() {
        //Write the search clause:
        String durationIs = String.valueOf(TimeUnit.MILLISECONDS.convert(5, TimeUnit.MINUTES));
        Predicate predicate = new Where(MediaStore.Video.Media.DURATION)
                                    .isGreaterThenOrEqual(durationIs);
        //Fetch the query:
        List<MediaStoreBrowser.MediaStoreItem> items = new MediaStoreBrowser.Builder(appContext)
                .from(MediaStoreBrowser.Type.Video)
                .select(MediaStore.Video.Media.ALBUM
                        , MediaStore.Video.Media.ALBUM_ARTIST)
                .where(predicate)
                .orderBy(MediaStore.Video.Media.DISPLAY_NAME)
                .search((cursor, index) -> {
                    //TODO:
                    return null;
                });
    }
}