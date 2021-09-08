package lab.infoworks.starter.ui.activities.riderList;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import lab.infoworks.libshared.domain.model.Rider;
import lab.infoworks.libshared.notifications.NotificationCenter;
import lab.infoworks.starter.R;
import lab.infoworks.starter.ui.activities.riderList.recycler.RiderAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RidersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RidersFragment extends Fragment {

    public static final String TAG = RidersFragment.class.getSimpleName();

    public static final String RIDER_SELECTED_INDEX_KEY = "rider_selected_index";
    public static final String RIDER_SELECTED_KEY = "rider_selected";
    public static final String RIDER_SELECTED_NOTIFICATION = "rider_selected_notification";
    public static final String RIDER_UPDATED_KEY = "rider_updated";

    private Unbinder unbinder;
    @BindView(R.id.rvFrgRiders)
    RecyclerView rvRiders;
    RiderAdapter rvAdapter;

    public RidersFragment() {
        // Required empty public constructor
    }

    public static RidersFragment newInstance() {
        RidersFragment fragment = new RidersFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //TODO:
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_riders, container, false);
        unbinder = ButterKnife.bind(this, view);

        //Another way of getting viewModel:
        ViewModelProviders.of(this)
                .get(RiderListViewModel.class)
                .getRiderObservable()
                .observe(getViewLifecycleOwner(), (riders) -> {
            //TODO:
            Log.d(TAG, "===> number of riders found: " + riders.size());
            RiderAdapter adapter = new RiderAdapter(riders);
            rvRiders.setAdapter(adapter);
            rvAdapter = adapter;
            rvRiders.setLayoutManager(new LinearLayoutManager(getActivity()));
            //
        });

        //Handling Notifications
        NotificationCenter.addObserver(getContext(), RIDER_SELECTED_NOTIFICATION, (context, data) -> {
            //TODO:
            String json = data.getStringExtra(RIDER_SELECTED_KEY);
            Integer index = Integer.valueOf(data.getStringExtra(RIDER_SELECTED_INDEX_KEY));
            Rider selected = new Rider(json);
            Toast.makeText(context,String.format("Index: %s, Name: %s", index, selected.getName()), Toast.LENGTH_SHORT).show();

            //TODO: PlayWith
            //moveToDetail(_selected);
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.loadRiders)
    public void onFetch(View view){
        ViewModelProviders.of(this)
                .get(RiderListViewModel.class).findRiders();
    }

}