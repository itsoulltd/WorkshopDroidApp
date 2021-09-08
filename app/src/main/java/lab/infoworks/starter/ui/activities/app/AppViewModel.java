package lab.infoworks.starter.ui.activities.app;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import lab.infoworks.libshared.domain.model.VerificationResult;
import lab.infoworks.libshared.domain.repository.definition.RiderRepository;

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

    private RiderRepository riderRepository = RiderRepository.create(getApplication());
}
