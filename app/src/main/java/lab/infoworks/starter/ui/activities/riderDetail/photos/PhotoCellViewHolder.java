package lab.infoworks.starter.ui.activities.riderDetail.photos;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PhotoCellViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    private Context context;
    private ImageView imgView;

    public PhotoCellViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public void onBind(Context context){
        //TODO:
    }

    @Override
    public void onClick(View view) {
        int position = getAdapterPosition(); // gets item position
        if (position != RecyclerView.NO_POSITION) { // Check if an item was deleted, but the user clicked it before the UI removed it
            //TODO:
        }
    }
}
