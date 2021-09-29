package lab.infoworks.libshared.domain.repository.definition;

import android.content.Context;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

import lab.infoworks.libshared.domain.model.RiderPhoto;
import lab.infoworks.libshared.domain.repository.impl.RiderRepositoryImpl;

public interface RiderPhotoRepository {

    static RiderPhotoRepository create(Context application, String localFirst, String baseUrl) {
        return new RiderRepositoryImpl(application, localFirst, baseUrl);
    }

    List<String> fetchPhotos(Integer userId) throws IOException;
    void fetchPhotos(Integer userId, Consumer<List<String>> consumer);
    String fetchPhoto(Integer userId, String imgPath) throws IOException;
    void fetchPhoto(Integer userId, String imgPath, Consumer<String> consumer);

    void addPhotoToAlbum(int userid, String albumName, String imgName);
    void findBy(int userid, Consumer<List<RiderPhoto>> consumer);

}
