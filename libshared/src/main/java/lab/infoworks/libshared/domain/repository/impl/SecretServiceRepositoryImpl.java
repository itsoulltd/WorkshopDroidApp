package lab.infoworks.libshared.domain.repository.impl;

import android.content.Context;

import com.infoworks.lab.rest.models.Message;
import com.infoworks.lab.rest.models.Response;

import java.io.IOException;

import lab.infoworks.libshared.domain.remote.RemoteConfig;
import lab.infoworks.libshared.domain.remote.api.SecretServiceApi;
import lab.infoworks.libshared.domain.remote.interceptors.DecryptResponseInterceptor;
import lab.infoworks.libshared.domain.remote.interceptors.EncryptRequestInterceptor;
import lab.infoworks.libshared.domain.repository.definition.SecretServiceRepository;

public class SecretServiceRepositoryImpl implements SecretServiceRepository {

    private SecretServiceApi apiService;

    public SecretServiceRepositoryImpl(Context context, String baseURL) {
        //
        this.apiService = RemoteConfig.getInstance(baseURL
                , SecretServiceApi.class
                , new EncryptRequestInterceptor()
                /*, new DecryptResponseInterceptor()*/);
    }

    @Override
    public Response save(String alias, String secret) {
        try {
            Response response = apiService.saveSecret(alias, secret).execute().body();
            return response;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Response().setStatus(500);
    }

    @Override
    public Response encryptedText(String alias) {
        try {
            Response response = apiService.encrypt(alias).execute().body();
            return response;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Response().setStatus(500);
    }

    @Override
    public Response sendMessage(String alias, Message msg) {
        try {
            Response response = apiService.sendMessage(alias, msg).execute().body();
            return response;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Response().setStatus(500);
    }
}
