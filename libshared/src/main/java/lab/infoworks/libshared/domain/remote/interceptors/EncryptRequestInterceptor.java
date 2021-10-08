package lab.infoworks.libshared.domain.remote.interceptors;

import com.infoworks.lab.rest.models.Message;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import lab.infoworks.libshared.domain.remote.interceptors.definition.EncryptInterceptor;
import lab.infoworks.libshared.util.crypto.SecretKeyStore;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;

public class EncryptRequestInterceptor implements EncryptInterceptor {

    public RequestBody encrypt(RequestBody originalBody) {

        return new RequestBody() {

            @Override public MediaType contentType() {
                return originalBody.contentType();
            }

            @Override public long contentLength() throws IOException {
                return -1l;
            }

            @Override public void writeTo(BufferedSink sink) throws IOException {
                String subtype = originalBody.contentType().subtype();
                if(subtype.contains("json")
                    || subtype.contains("form")) {
                    //...
                    String bodyStr = bodyToString(originalBody);
                    String encoded = SecretKeyStore.getInstance()
                            .encrypt("towhid@gmail.com", bodyStr);
                    Message msg = new Message().setPayload(encoded);
                    byte[] bytes = msg.toString().getBytes(StandardCharsets.UTF_8);
                    //Writing new body-data into sink:
                    sink.write(bytes);
                    //
                    /*Buffer buffer = new Buffer();
                    buffer.write(bytes);
                    buffer.writeTo(sink.outputStream());*/
                        //
                    /*RequestBody nBody = RequestBody.create(bytes, originalBody.contentType());
                    nBody.writeTo(sink);*/
                }
                originalBody.writeTo(sink);
            }
        };
    }
}
