package lab.infoworks.starter.ui.activities.app;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.infoworks.lab.rest.models.Message;
import com.infoworks.lab.rest.models.Response;

import lab.infoworks.libshared.domain.model.VerificationResult;
import lab.infoworks.libshared.domain.repository.definition.RiderRepository;
import lab.infoworks.libshared.domain.repository.definition.SecretServiceRepository;
import lab.infoworks.starter.BuildConfig;
import lab.infoworks.starter.ui.app.StarterApp;

public class AppViewModel extends AndroidViewModel {

    public AppViewModel(@NonNull Application application) {
        super(application);
    }

    private MutableLiveData<VerificationResult> userStatusLiveData = new MutableLiveData<>();
    public LiveData<VerificationResult> getUserStatusObservable() {
        return userStatusLiveData;
    }

    public void verifyUser() {
        if(riderRepository.isEmpty()) riderRepository.addSampleData(getApplication());
        userStatusLiveData.postValue(new VerificationResult(true));
    }

    private RiderRepository riderRepository = RiderRepository.create(getApplication(), BuildConfig.offline_mode, BuildConfig.api_gateway);

    private SecretServiceRepository secretRepository = SecretServiceRepository.create(getApplication(), BuildConfig.api_gateway);

    private MutableLiveData<Response> secretObserver = new MutableLiveData<>();

    public LiveData<Response> getSecretObserver() {
        return secretObserver;
    }

    public void saveSecret(String alias, String secret){
        StarterApp.executor.submit(() -> {
            Response response = secretRepository.save(alias, secret);
            secretObserver.postValue(response);
        });
    }

    public void getEncryptedText(String alias){
        StarterApp.executor.submit(() -> {
            Response response = secretRepository.encryptedText(alias);
            secretObserver.postValue(response);
        });
    }

    public void sendMsg(String alias, Message msg){
        StarterApp.executor.submit(() -> {
            Response response = secretRepository.sendMessage(alias, msg);
            secretObserver.postValue(response);
        });
    }

}
