package lab.infoworks.libshared.domain.model;

import android.graphics.Bitmap;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "rider_photo", ignoredColumns = {"status", "error", "message", "payload", "event", "classType", "_isAutoIncremented"})
public class RiderPhoto extends ResponseExt{
    /**
     * Each entity must either have a no-arg constructor or a constructor whose parameters match fields (based on type and name).
     * Constructor does not have to receive all fields as parameters but if a field is not passed into the constructor,
     * it should either be public or have a public setter.
     * If a matching constructor is available, Room will always use it.
     */

    @PrimaryKey(autoGenerate = true)
    private Integer id;

    private int userid;
    @ColumnInfo(name = "album_name")
    private String albumName;
    @ColumnInfo(name = "image_name")
    private String imageName;

    @Ignore
    private Bitmap photo;

    public RiderPhoto() {}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public Bitmap getPhoto() {
        return photo;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }
}
