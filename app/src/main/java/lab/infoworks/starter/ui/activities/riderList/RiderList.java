package lab.infoworks.starter.ui.activities.riderList;

import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lab.infoworks.libshared.notifications.SystemNotificationTray;
import lab.infoworks.starter.R;
import lab.infoworks.starter.ui.activities.riderDetail.RiderDetail;

public class RiderList extends AppCompatActivity {

    private static final String TAG = RiderList.class.getName();
    private SystemNotificationTray notificationTray;

    @BindView(R.id.riderTitle)
    TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(lab.infoworks.starter.R.layout.activity_rider_list);

        notificationTray = new SystemNotificationTray(this);
        ButterKnife.bind(this);

        //Another way of getting viewModel:
        ViewModelProviders.of(this)
                .get(RiderListViewModel.class)
                .getRiderObservable().observe(this, (riders) -> {
            //
            Log.d(TAG, "===> number of riders found: " + riders.size());
            title.setText("number of riders found: " + riders.size());
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

    @OnClick(R.id.riderButton)
    public void onClick(View view){
        startActivity(new Intent(this, RiderDetail.class));
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

}