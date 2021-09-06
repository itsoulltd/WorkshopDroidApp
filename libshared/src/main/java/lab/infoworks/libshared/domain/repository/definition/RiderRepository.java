package lab.infoworks.libshared.domain.repository.definition;

import android.app.Application;
import android.content.Context;

import java.util.List;
import java.util.function.Consumer;

import lab.infoworks.libshared.domain.model.Rider;
import lab.infoworks.libshared.domain.repository.impl.RiderRepositoryImpl;

public interface RiderRepository {

    static RiderRepository create(Application application, String localFirst, String baseUrl) {
        return new RiderRepositoryImpl(application, localFirst, baseUrl);
    }

    void findRiders(Consumer<List<Rider>> consumer);
    void addSampleData(Context context);
    boolean isEmpty();
}
