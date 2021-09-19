package lab.infoworks.libshared.domain.repository.definition;

import android.app.Application;

import java.util.List;
import java.util.function.Consumer;

import lab.infoworks.libshared.domain.repository.impl.RiderRepositoryImpl;

public interface RiderPhotoRepository {

    static RiderPhotoRepository create(Application application, String localFirst, String baseUrl) {
        return new RiderRepositoryImpl(application, localFirst, baseUrl);
    }

    void fetchPhotos(Integer userId, Consumer<List<String>> consumer);
    void fetchPhoto(Integer userId, String imgPath, Consumer<String> consumer);

}
