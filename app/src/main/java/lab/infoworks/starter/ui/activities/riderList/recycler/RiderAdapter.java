package lab.infoworks.starter.ui.activities.riderList.recycler;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

import lab.infoworks.libshared.domain.model.Rider;
import lab.infoworks.libshared.notifications.NotificationCenter;
import lab.infoworks.starter.R;
import lab.infoworks.starter.ui.activities.riderList.RiderList;

public class RiderAdapter extends RecyclerView.Adapter<RiderAdapter.ViewHolder>{

    private List<Rider> riders;
    private Context context;

    public RiderAdapter(List<Rider> riders) {
        this.riders = riders;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // Inflate the custom layout
        View riderView = inflater.inflate(R.layout.item_rider, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(riderView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Rider rider = riders.get(position);
        // Set item views based on your views and data model
        holder.nameTextView.setText(rider.getName());
        holder.emailTextView.setText(rider.getEmail());
    }

    @Override
    public int getItemCount() {
        return (riders != null) ? riders.size() : 0;
    }

    public void notifyItemChanged(Rider updated, Comparator<Rider> comparator) {
        //TODO:
        if (comparator == null || updated == null) return;
        int index = 0;
        for (Rider rider : riders) {
            if (comparator.compare(rider, updated) == 0)
                break;
            ++index;
        }
        Rider old = riders.get(index);
        old.unmarshallingFromMap(updated.marshallingToMap(true), true);
        notifyItemChanged(index);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView nameTextView;
        public TextView emailTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = (TextView) itemView.findViewById(R.id.riderName);
            emailTextView = (TextView) itemView.findViewById(R.id.riderEmail);

            //adding touch:
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition(); // gets item position
            if (position != RecyclerView.NO_POSITION) { // Check if an item was deleted, but the user clicked it before the UI removed it
                Rider rider = riders.get(position);
                Map riderData = new HashMap();
                riderData.put(RiderList.RIDER_SELECTED_KEY, rider.toString());
                riderData.put(RiderList.RIDER_SELECTED_INDEX_KEY, position);
                // We can access the data within the views
                NotificationCenter.postNotification(context, RiderList.RIDER_SELECTED_NOTIFICATION, riderData);
            }
        }
    }

}
