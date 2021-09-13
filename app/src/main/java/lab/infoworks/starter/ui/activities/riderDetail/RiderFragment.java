package lab.infoworks.starter.ui.activities.riderDetail;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import lab.infoworks.libshared.domain.model.Rider;
import lab.infoworks.libshared.notifications.NotificationCenter;
import lab.infoworks.starter.R;

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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
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