package lab.infoworks.libshared.domain.remote.interceptors;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.infoworks.lab.rest.models.Message;
import com.infoworks.lab.rest.models.Response;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import lab.infoworks.libshared.domain.remote.interceptors.definition.DecryptInterceptor;
import lab.infoworks.libshared.util.crypto.SecretKeyStore;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

public class DecryptResponseInterceptor implements DecryptInterceptor {

    public ResponseBody decrypt(ResponseBody originalBody) {

        return new ResponseBody() {

            @Nullable @Override
            public MediaType contentType() {
                return originalBody.contentType();
            }

            @Override
            public long contentLength() {
                return -1l;
            }

            @NonNull @Override
            public BufferedSource source() {
                String subtype = originalBody.contentType().subtype();
                if (subtype.contains("text")){
                    String strRes = bodyToString(originalBody);
                    String decrypted = SecretKeyStore.getInstance()
                            .decrypt("towhid@gmail.com", strRes);
                    //
                    Buffer buffer = new Buffer();
                    buffer.write(decrypted.getBytes(StandardCharsets.UTF_8));
                    return buffer;
                } else if(subtype.contains("json")) {
                    String json = bodyToString(originalBody);
                    if (Message.isValidJson(json)){
                        try {
                            Message msg = Message.unmarshal(Response.class, json);
                            String decrypted = SecretKeyStore.getInstance()
                                    .decrypt("towhid@gmail.com", msg.getPayload());
                            msg.setPayload(decrypted);
                            //
                            Buffer buffer = new Buffer();
                            buffer.write(msg.toString().getBytes(StandardCharsets.UTF_8));
                            return buffer;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                //if failed any:
                return originalBody.source();
            }
        };
    }
}
