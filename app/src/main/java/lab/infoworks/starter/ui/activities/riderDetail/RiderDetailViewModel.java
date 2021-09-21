package lab.infoworks.starter.ui.activities.riderDetail;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import lab.infoworks.libshared.domain.model.RiderPhoto;
import lab.infoworks.libshared.domain.repository.definition.RiderPhotoRepository;
import lab.infoworks.starter.BuildConfig;

public class RiderDetailViewModel extends AndroidViewModel {

    public RiderDetailViewModel(@NonNull Application application) {
        super(application);
    }

    private RiderPhotoRepository photoRepository = RiderPhotoRepository.create(getApplication(), "true", BuildConfig.api_gateway);

    private MutableLiveData<List<RiderPhoto>> photos = new MutableLiveData<>();

    public LiveData<List<RiderPhoto>> getPhotos() {
        return photos;
    }

    public void findPhotosBy(int userid){
        photoRepository.findBy(userid, (riderPhotos) -> {
            photos.postValue(riderPhotos);
        });
    }
}
