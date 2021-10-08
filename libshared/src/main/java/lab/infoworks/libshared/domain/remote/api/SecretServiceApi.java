package lab.infoworks.libshared.domain.remote.api;

import com.infoworks.lab.rest.models.Message;
import com.infoworks.lab.rest.models.Response;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface SecretServiceApi {

    @POST("/encrypt/save/{alias}/{secret}")
    Call<Response> saveSecret(@Path("alias") String alias, @Path("secret") String secret);

    @GET("/encrypt/{alias}")
    Call<Response> encrypt(@Path("alias") String alias);

    @POST("/encrypt/message/{alias}")
    Call<Response> sendMessage(@Path("alias") String alias, @Body Message msg);

}
