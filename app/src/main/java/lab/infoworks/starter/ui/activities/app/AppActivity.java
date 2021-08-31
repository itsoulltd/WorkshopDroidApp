package lab.infoworks.starter.ui.activities.app;

import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lab.infoworks.libshared.notifications.SystemNotificationTray;
import lab.infoworks.libui.BaseActivity.BaseActivity;
import lab.infoworks.libui.BaseActivity.BaseNetworkActivity;
import lab.infoworks.starter.R;


public class AppActivity extends AppCompatActivity {

    private static final String TAG = AppActivity.class.getName();
    @BindView(R.id.verificationStatusTextView)
    TextView verificationStatusTextView;

    @BindView(R.id.verifyButton)
    TextView verifyButton;

    private AppViewModel appViewModel;
    private SystemNotificationTray notificationTray;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_rider);
        ButterKnife.bind(this);

        notificationTray = new SystemNotificationTray(this);

        appViewModel = new AppViewModel(getApplication());
        appViewModel.getUserStatusObservable().observe(this, verificationResult -> {
            Log.d(TAG, "===> result: " + verificationResult.isVerified());
            verificationStatusTextView.setText("Rider is verified.... :) ");
            verifyButton.setEnabled(false);
        });

        appViewModel.getRiderObservable().observe(this, riders -> {
            Log.d(TAG, "===> number of riders found: " + riders.size());
            verificationStatusTextView.setText("number of riders found: " + riders.size());
            verifyButton.setEnabled(true);
            notifyTray();
        });

        Log.d(TAG, "onCreate");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    private void notifyTray(){
        //Notification:
        String title = getString(R.string.hello_title);
        String message = getString(R.string.hello_message);
        String ticker = getString(R.string.app_name);
        int icon = R.mipmap.ic_launcher_round;
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        //display msg on notificationTry
        notificationTray.notify(1, title, message, ticker, icon, sound);
    }

    @OnClick(R.id.verifyButton)
    public void verifyRider() {
        appViewModel.verifyUser();
    }

    @OnClick(R.id.findRidersButton)
    public void findRiders() {
        appViewModel.findRiders();
    }
}
