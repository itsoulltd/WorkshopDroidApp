package lab.infoworks.libshared.domain.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import lab.infoworks.libshared.domain.model.RiderPhoto;

@Dao
public interface RiderPhotoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(RiderPhoto photo);

    @Delete
    void delete(RiderPhoto photo);

    @Query("SELECT * FROM rider_photo WHERE userid = :userid")
    List<RiderPhoto> getAll(int userid);

}
