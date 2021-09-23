package lab.infoworks.starter.ui.activities.riderDetail.photos;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import lab.infoworks.libshared.domain.model.Rider;
import lab.infoworks.libshared.domain.model.RiderPhoto;
import lab.infoworks.starter.R;

public class PhotoCellViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    private Context context;
    private ImageView imgView;

    private RiderPhoto photo;

    public PhotoCellViewHolder(@NonNull View itemView) {
        super(itemView);
        imgView = itemView.findViewById(R.id.riderCellPhoto);
        //adding touch:
        itemView.setOnClickListener(this);
    }

    public void onBind(Context context, RiderPhoto photo){
        this.context = context;
        this.photo = photo;
        // Set item views based on your views and data model
        if (photo.getPhoto() != null)
            imgView.setImageBitmap(photo.getPhoto());
    }

    @Override
    public void onClick(View view) {
        int position = getAdapterPosition(); // gets item position
        if (position != RecyclerView.NO_POSITION) { // Check if an item was deleted, but the user clicked it before the UI removed it
            //TODO:
            RiderPhoto photo = this.photo;
            Toast.makeText(context,String.format("Index: %s, Image: %s", position, photo.getImageName()), Toast.LENGTH_SHORT).show();
        }
    }
}
