package lab.infoworks.starter.ui.activities.riderDetail.photos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import lab.infoworks.libshared.domain.model.Rider;
import lab.infoworks.libshared.domain.model.RiderPhoto;
import lab.infoworks.starter.R;

public class PhotosAdapter extends RecyclerView.Adapter<PhotoCellViewHolder> {

    private List<RiderPhoto> photos;
    private Context context;

    public PhotosAdapter(List<RiderPhoto> photos) {
        this.photos = photos;
    }

    @NonNull
    @Override
    public PhotoCellViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // Inflate the custom layout
        View photoView = inflater.inflate(R.layout.item_photo, parent, false);
        return new PhotoCellViewHolder(photoView);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoCellViewHolder holder, int position) {
        RiderPhoto photo = photos.get(position);
        holder.onBind(context, photo);
    }

    @Override
    public int getItemCount() {
        return (photos != null) ? photos.size() : 0;
    }
}
