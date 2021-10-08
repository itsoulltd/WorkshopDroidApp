package lab.infoworks.libshared.domain.remote.interceptors.definition;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;

public interface CryptoInterceptor extends Interceptor {

    ResponseBody decrypt(Response original);
    RequestBody encrypt(Request original);

    @NonNull @Override
    default Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();
        if (request.body() != null && request.body().contentLength() > 0) {
            request = request.newBuilder()
                    .method(request.method(), encrypt(request))
                    .build();
        }
        //
        Response response = chain.proceed(request);
        if (response.body() == null) {
            return response;
        } else {
            return response.newBuilder()
                    .body(decrypt(response))
                    .build();
        }
    }

    default String readRequestBody(final RequestBody request){
        try {
            final RequestBody copy = request;
            final Buffer buffer = new Buffer();
            if(copy != null)
                copy.writeTo(buffer);
            else
                return "";
            return buffer.readUtf8();
        }
        catch (final IOException e) {
            return "did not work";
        }
    }

    default String readResponseBody(final ResponseBody response){
        try {
            final ResponseBody copy = response;
            final Buffer buffer = new Buffer();
            if(copy != null)
                buffer.write(copy.bytes());
            else
                return "";
            return buffer.readUtf8();
        }
        catch (final IOException e) {
            return "did not work";
        }
    }
}
