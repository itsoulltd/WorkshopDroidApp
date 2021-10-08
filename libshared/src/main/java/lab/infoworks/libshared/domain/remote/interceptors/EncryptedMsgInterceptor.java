package lab.infoworks.libshared.domain.remote.interceptors;

import com.infoworks.lab.rest.models.Message;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import lab.infoworks.libshared.domain.remote.interceptors.definition.CryptoInterceptor;
import lab.infoworks.libshared.util.crypto.SecretKeyStore;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.BufferedSink;

public class EncryptedMsgInterceptor implements CryptoInterceptor {

    @Override
    public ResponseBody decrypt(Response original) {

        MediaType contentType = original.body().contentType();
        ResponseBody body = original.body();

        String subtype = original.body().contentType().subtype();
        if (subtype.contains("text")){
            String strRes = readResponseBody(original.body());
            String decrypted = SecretKeyStore.getInstance()
                    .decrypt("towhid@gmail.com", strRes);
            //
            body = ResponseBody.create(decrypted.getBytes(StandardCharsets.UTF_8), contentType);
        } else if(subtype.contains("json")) {
            String json = readResponseBody(original.body());
            if (Message.isValidJson(json)){
                try {
                    Message msg = Message.unmarshal(com.infoworks.lab.rest.models.Response.class, json);
                    String decrypted = SecretKeyStore.getInstance()
                            .decrypt("towhid@gmail.com", msg.getPayload());
                    msg.setPayload(decrypted);
                    //
                    body = ResponseBody.create(msg.toString().getBytes(StandardCharsets.UTF_8), contentType);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        //
        return body;
    }

    @Override
    public RequestBody encrypt(Request original) {

        return new RequestBody() {

            @Override public MediaType contentType() {
                return original.body().contentType();
            }

            @Override public long contentLength() throws IOException {
                return -1l;
            }

            @Override public void writeTo(BufferedSink sink) throws IOException {
                String subtype = original.body().contentType().subtype();
                if(subtype.contains("json")
                        || subtype.contains("form")) {
                    //...
                    String bodyStr = readRequestBody(original.body());
                    String encoded = SecretKeyStore.getInstance()
                            .encrypt("towhid@gmail.com", bodyStr);
                    encoded = encoded.replace(System.getProperty("line.separator"), "");
                    Message msg = new Message().setPayload(encoded);
                    byte[] bytes = msg.toString().getBytes(StandardCharsets.UTF_8);
                    //writing new body-data into sink:
                    sink.write(bytes);
                } else {
                    original.body().writeTo(sink);
                }
            }
        };
    }
}
