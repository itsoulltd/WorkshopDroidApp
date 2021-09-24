package lab.infoworks.libshared.domain.remote.api;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RiderPhotoApiService {

    @GET("/rider/photos/{userid}")
    Call<List<String>> fetchPhotos(@Path("userid") Integer userid);

    @GET("/rider/photo/{userid}")
    Call<Map<String,String>> fetchPhoto(@Path("userid") Integer userid, @Query("imgPath") String imagePath);

}
