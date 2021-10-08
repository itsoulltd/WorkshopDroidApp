package lab.infoworks.libshared.domain.repository.definition;

import android.content.Context;

import com.infoworks.lab.rest.models.Message;
import com.infoworks.lab.rest.models.Response;

import lab.infoworks.libshared.domain.repository.impl.SecretServiceRepositoryImpl;

public interface SecretServiceRepository {

    static SecretServiceRepository create(Context context, String baseUrl){
        return new SecretServiceRepositoryImpl(context, baseUrl);
    }

    Response save(String alias, String secret);
    Response encryptedText(String alias);
    Response sendMessage(String alias, Message msg);
}
