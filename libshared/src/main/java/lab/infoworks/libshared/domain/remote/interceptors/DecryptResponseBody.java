package lab.infoworks.libshared.domain.remote.interceptors;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.BufferedSource;

public class DecryptResponseBody implements Interceptor {
    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Response original = chain.proceed(chain.request());
        if (original.body() == null) {
            return original;
        }
        //
        Response decryptedResponse = original.newBuilder()
                .body(decrypt(original.body()))
                .build();
        return decryptedResponse;
    }

    private ResponseBody decrypt(ResponseBody body) {
        return new ResponseBody() {
            @Nullable
            @Override
            public MediaType contentType() {
                return body.contentType();
            }

            @Override
            public long contentLength() {
                return body.contentLength();
            }

            @NonNull
            @Override
            public BufferedSource source() {
                //TODO:
                return null;
            }
        };
    }
}
