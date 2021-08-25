package lab.infoworks.libshared.domain.remote;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import lab.infoworks.libshared.BuildConfig;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class RemoteConfig {

    public static <ApiService> ApiService getInstance(String BASE_URL, Class<ApiService> serviceClass) {
        //
        OkHttpClient okHttpClient;
        if (BuildConfig.DEBUG) {
            okHttpClient = new OkHttpClient().newBuilder()
                    .addInterceptor(new BearerTokenInterceptor())
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .sslSocketFactory(BypassHttpsForDebug.getSSLSocketFactory())
                    .build();
        } else {
            okHttpClient = new OkHttpClient().newBuilder()
                    .addInterceptor(new BearerTokenInterceptor())
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                //.addConverterFactory(GsonConverterFactory.create(getGson()))
                .addConverterFactory(JacksonConverterFactory.create(getMapper()))
                .build();

        return retrofit.create(serviceClass);
    }

    private static ObjectMapper getMapper(){
        return new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    private static Gson getGson(){
        return new GsonBuilder()
                .setLenient()
                .create();
    }

}
