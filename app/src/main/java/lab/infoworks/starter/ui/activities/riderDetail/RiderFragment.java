package lab.infoworks.starter.ui.activities.riderDetail;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import lab.infoworks.libshared.domain.model.Rider;
import lab.infoworks.libshared.notifications.NotificationCenter;
import lab.infoworks.starter.BuildConfig;
import lab.infoworks.starter.R;
import lab.infoworks.starter.operations.EncryptedFileFetchingService;
import lab.infoworks.starter.ui.activities.riderDetail.photos.PhotosFragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RiderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RiderFragment extends Fragment {

    private static final String TAG = RiderFragment.class.getName();
    public static final String RIDER_UPDATED_KEY = "rider_updated";
    public static final String RIDER_UPDATED_INDEX_KEY = "rider_updated_index";
    public static final String RIDER_UPDATED_NOTIFICATION = "rider_update_notification";
    private Rider rider;
    private Integer index;

    private Unbinder unbinder;
    @BindView(R.id.frgRiderName)
    TextView riderName;

    @BindView(R.id.frgRiderAge)
    TextView riderAge;

    @BindView(R.id.frgRiderGender)
    TextView riderGender;

    @BindView(R.id.frgRiderEmail)
    EditText riderEmail;

    public RiderFragment() {
        // Required empty public constructor
    }

    public static RiderFragment newInstance(Rider rider, Integer index) {
        RiderFragment fragment = new RiderFragment();
        Bundle args = new Bundle();
        args.putString(RIDER_UPDATED_KEY, rider.toString());
        args.putString(RIDER_UPDATED_INDEX_KEY, index.toString());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            rider = new Rider(getArguments().getString(RIDER_UPDATED_KEY));
            index = Integer.valueOf(getArguments().getString(RIDER_UPDATED_INDEX_KEY));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_rider, container, false);
        unbinder = ButterKnife.bind(this, view);

        if (rider != null){
            riderName.setText("Name:" + rider.getName());
            riderAge.setText("Age: " + rider.getAge());
            riderGender.setText("Gender: " + rider.getGender());
            riderEmail.setText(rider.getEmail());
        }

        return view;
    }

    // This event is triggered soon after onCreateView().
    // onViewCreated() is only called if the view returned from onCreateView() is non-null.
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        addNestedFragment();
        //Start EncryptedFileFetchingService
        Intent intent = new Intent(getActivity(), EncryptedFileFetchingService.class);
        intent.putExtra("baseUrl", BuildConfig.api_gateway);
        intent.putExtra("jwt-token", "---");
        intent.putExtra("userid", 1011);
        getActivity().startService(intent);
    }

    private void addNestedFragment() {
        Fragment photosFragment = new PhotosFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.riderPhotosFragmentContainer, photosFragment).commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        //Stop EncryptedFileFetchingService
        getActivity().stopService(new Intent(getActivity(), EncryptedFileFetchingService.class));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @OnClick(R.id.saveRider)
    public void save(View view){
        if (rider != null){
            rider.setEmail(riderEmail.getText().toString());
            //Prepare for back the result:
            Map<String, Object> data  = new HashMap<>();
            data.put(RiderFragment.RIDER_UPDATED_KEY, rider.toString());
            data.put(RiderFragment.RIDER_UPDATED_INDEX_KEY, index);
            NotificationCenter.postNotification(getContext(), RiderFragment.RIDER_UPDATED_NOTIFICATION, data);
        }
    }
}