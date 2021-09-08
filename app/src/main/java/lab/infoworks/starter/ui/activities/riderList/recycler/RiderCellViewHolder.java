package lab.infoworks.starter.ui.activities.riderList.recycler;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.Map;

import lab.infoworks.libshared.domain.model.Rider;
import lab.infoworks.libshared.notifications.NotificationCenter;
import lab.infoworks.starter.R;
import lab.infoworks.starter.ui.activities.riderList.RidersFragment;

public class RiderCellViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    private TextView nameTextView;
    private TextView emailTextView;

    private Rider rider;
    private Context context;

    public RiderCellViewHolder(@NonNull View itemView) {
        super(itemView);
        nameTextView = (TextView) itemView.findViewById(R.id.riderName);
        emailTextView = (TextView) itemView.findViewById(R.id.riderEmail);
        //adding touch:
        itemView.setOnClickListener(this);
    }

    public void onBind(Context context, Rider rider){
        this.context = context;
        this.rider = rider;
        // Set item views based on your views and data model
        nameTextView.setText(rider.getName());
        emailTextView.setText(rider.getEmail());
    }

    @Override
    public void onClick(View view) {
        int position = getAdapterPosition(); // gets item position
        if (position != RecyclerView.NO_POSITION) { // Check if an item was deleted, but the user clicked it before the UI removed it
            Rider rider = this.rider;
            Map riderData = new HashMap();
            riderData.put(RidersFragment.RIDER_SELECTED_KEY, rider.toString());
            riderData.put(RidersFragment.RIDER_SELECTED_INDEX_KEY, position);
            // We can access the data within the views
            NotificationCenter.postNotification(context, RidersFragment.RIDER_SELECTED_NOTIFICATION, riderData);
        }
    }
}
