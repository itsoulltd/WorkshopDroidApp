package lab.infoworks.libshared.domain.remote;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class BearerTokenInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        //TODO: Get The Saved jwt-token
        String jwt = "jwt-token";
        if (jwt == null || jwt.isEmpty()){
            return chain.proceed(chain.request());
        }
        //rewrite the request to add bearer token
        Request newRequest=chain.request().newBuilder()
                .header("Authorization","Bearer "+ jwt)
                .build();
        return chain.proceed(newRequest);
    }
}
