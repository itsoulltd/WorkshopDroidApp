package lab.infoworks.starter.ui.activities.riderList;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import lab.infoworks.libshared.domain.model.Rider;
import lab.infoworks.libshared.domain.repository.definition.RiderRepository;
import lab.infoworks.starter.BuildConfig;

public class RiderListViewModel extends AndroidViewModel {

    public RiderListViewModel(@NonNull Application application) {
        super(application);
    }

    private MutableLiveData<List<Rider>> riderLiveData = new MutableLiveData<>();
    private RiderRepository riderRepository = RiderRepository.create(getApplication(), BuildConfig.offline_mode, BuildConfig.api_gateway);
    public LiveData<List<Rider>> getRiderObservable() {
        return riderLiveData;
    }

    public void findRiders() {
        riderRepository.findRiders((riders) -> riderLiveData.postValue(riders));
    }

    public void update(Rider updated) {
        riderRepository.update(updated);
    }
}
