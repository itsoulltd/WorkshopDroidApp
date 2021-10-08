package lab.infoworks.libshared.domain.remote.interceptors;

import com.infoworks.lab.rest.models.Message;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import lab.infoworks.libshared.domain.remote.interceptors.definition.DecryptInterceptor;
import lab.infoworks.libshared.util.crypto.SecretKeyStore;
import okhttp3.MediaType;
import okhttp3.ResponseBody;

public class DecryptResponseInterceptor implements DecryptInterceptor {

    public ResponseBody decrypt(ResponseBody originalBody) {

        MediaType contentType = originalBody.contentType();
        ResponseBody body = originalBody;

        String subtype = originalBody.contentType().subtype();
        if (subtype.contains("text")){
            String strRes = bodyToString(originalBody);
            String decrypted = SecretKeyStore.getInstance()
                    .decrypt("towhid@gmail.com", strRes);
            //
            body = ResponseBody.create(decrypted.getBytes(StandardCharsets.UTF_8), contentType);
        } else if(subtype.contains("json")) {
            String json = bodyToString(originalBody);
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
}
