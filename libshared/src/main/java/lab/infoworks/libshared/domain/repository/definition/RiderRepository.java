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
    void findRiders(int page, int limit, Consumer<List<Rider>> consumer);
    void update(Rider updated);
    boolean isEmpty();

    void addSampleData(Context context);
}
