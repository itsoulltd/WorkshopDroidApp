package lab.infoworks.starter.ui.activities.app;

import android.app.DownloadManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.infoworks.lab.rest.models.Message;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import lab.infoworks.libshared.domain.remote.DownloadTracker;
import lab.infoworks.libshared.domain.shared.AssetManager;
import lab.infoworks.libshared.notifications.NotificationCenter;
import lab.infoworks.starter.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AppFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AppFragment extends Fragment {

    public static final String TAG = AppFragment.class.getSimpleName();
    public static final String MOVE_TO_RIDERS_FRAGMENT = "MOVE_TO_RIDERS_FRAGMENT";

    private Unbinder unbinder;
    private long dRef = 0l;

    @BindView(R.id.statusTextView)
    TextView statusTextView;

    private static final String ARG_TITLE = "title";
    private String fragTitle = "Hello Fragments";

    public AppFragment() {
        // Required empty public constructor
    }

    public static AppFragment newInstance(String title) {
        AppFragment fragment = new AppFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            fragTitle = getArguments().getString(ARG_TITLE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_app, container, false);
        unbinder = ButterKnife.bind(this, view);

        statusTextView.setText(fragTitle);
        //
        ViewModelProviders
                .of(this)
                .get(AppViewModel.class)
                .getUserStatusObservable()
                .observe(getViewLifecycleOwner(), verificationResult -> {
            //
            Log.d(TAG, "===> result: " + verificationResult.isVerified());
            statusTextView.setText("Rider is verified.... :) ");
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.statusButton)
    public void verifyRider() {
        //TODO:
        Log.d(TAG, "verifyRider clicked");
        ViewModelProviders
                .of(this)
                .get(AppViewModel.class)
                .verifyUser();
    }

    @OnClick(R.id.moveToRidersButton)
    public void findRiders(View view) {
        //TODO:
        Log.d(TAG, "findRiders clicked");
        Message msg = new Message().setPayload("This is message from AppFragment.");
        NotificationCenter.postNotification(getContext(), MOVE_TO_RIDERS_FRAGMENT, msg.marshallingToMap(true));
    }

    @OnClick(R.id.startDownloadButton)
    public void startDownload(){
        //DownloadTracker test
        String link = "https://upload.wikimedia.org/wikipedia/commons/c/c6/A_modern_Cricket_bat_%28back_view%29.jpg";
        dRef = new DownloadTracker.Builder(getActivity().getApplicationContext(), link)
                .setDestinationInExternalFilesDir("myImg.jpg", Environment.DIRECTORY_DOWNLOADS)
                .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI)
                .setTitle("myImg download")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .enqueue((ios) -> {
                    //Now do whatever you want:
                    try {
                        Bitmap img = AssetManager.readAsImage(ios, 0);
                        System.out.println("");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.out.println("");
                });
        //
    }

    @OnClick(R.id.stopDownloadButton)
    public void stopDownload(){
        String status = new DownloadTracker.Builder(getActivity()).cancel(dRef);
        statusTextView.setText(status);
    }

    @OnClick(R.id.statusDownloadButton)
    public void updateStatusDownload(){
        new DownloadTracker.Builder(getActivity())
                .checkStatus(dRef, (status) -> {
                    statusTextView.setText(status.getStatus());
                });
    }

    @OnClick(R.id.showDownloadsButton)
    public void showDownloads(){
        DownloadTracker.viewOnGoingDownloads(getActivity());
    }
}