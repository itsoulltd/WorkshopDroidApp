package lab.infoworks.libshared.domain.remote.interceptors;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;

public class EncryptRequestBody implements Interceptor {
    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request original = chain.request();
        if (original.body() == null) {
            return chain.proceed(original);
        }
        //
        Request encryptedRequest = original.newBuilder()
                .method(original.method(), encrypt(original.body()))
                .build();
        return chain.proceed(encryptedRequest);
    }

    private RequestBody encrypt(RequestBody body) {
        return new RequestBody() {
            @Override public MediaType contentType() {
                return body.contentType();
            }

            @Override public long contentLength() throws IOException {
                return body.contentLength();
            }

            @Override public void writeTo(BufferedSink sink) throws IOException {
                //TODO
            }
        };
    }
}
