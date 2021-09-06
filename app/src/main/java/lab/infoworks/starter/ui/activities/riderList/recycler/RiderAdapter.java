package lab.infoworks.starter.ui.activities.riderList.recycler;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Comparator;
import java.util.List;

import lab.infoworks.libshared.domain.model.Rider;
import lab.infoworks.starter.R;

public class RiderAdapter extends RecyclerView.Adapter<RiderCellViewHolder>{

    private List<Rider> riders;
    private Context context;

    public RiderAdapter(List<Rider> riders) {
        this.riders = riders;
    }

    @NonNull
    @Override
    public RiderCellViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // Inflate the custom layout
        View riderView = inflater.inflate(R.layout.item_rider, parent, false);
        return new RiderCellViewHolder(riderView);
    }

    @Override
    public void onBindViewHolder(@NonNull RiderCellViewHolder holder, int position) {
        Rider rider = riders.get(position);
        holder.onBind(context, rider);
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

}
