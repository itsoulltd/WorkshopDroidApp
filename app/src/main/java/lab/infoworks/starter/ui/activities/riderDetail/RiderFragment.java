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
import lab.infoworks.libshared.domain.model.Rider;
import lab.infoworks.libshared.notifications.NotificationCenter;
import lab.infoworks.starter.R;
import lab.infoworks.starter.ui.activities.riderList.RidersFragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RiderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RiderFragment extends Fragment {

    private static final String TAG = RiderFragment.class.getName();
    private static final String ARG_PARAM1 = "param1";
    private Rider rider;

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

    public static RiderFragment newInstance(Rider rider) {
        RiderFragment fragment = new RiderFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, rider.toString());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String json = getArguments().getString(ARG_PARAM1);
            rider = new Rider(json);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_rider, container, false);
        ButterKnife.bind(this, view);

        if (rider != null){
            riderName.setText("Name:" + rider.getName());
            riderAge.setText("Age: " + rider.getAge());
            riderGender.setText("Gender: " + rider.getGender());
            riderEmail.setText(rider.getEmail());
        }

        return view;
    }

    @OnClick(R.id.saveRider)
    public void save(View view){
        if (rider != null){
            rider.setEmail(riderEmail.getText().toString());

            //Prepare for back the result:
            Map<String, Object> data  = new HashMap<>();
            data.put(RidersFragment.RIDER_UPDATED_KEY, rider.toString());
            NotificationCenter.postNotification(getContext(), RidersFragment.RIDER_UPDATED_NOTIFICATION, data);
        }
    }
}