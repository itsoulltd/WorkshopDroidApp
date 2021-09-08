package lab.infoworks.starter.ui.activities.app;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.infoworks.lab.rest.models.Message;

import lab.infoworks.libshared.notifications.NotificationCenter;
import lab.infoworks.starter.R;


public class AppActivity extends AppCompatActivity {

    private static final String TAG = AppActivity.class.getName();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app);
        /**/
        //Setting Up ActionBar Title
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setTitle(R.string.app_name);
        }

        NotificationCenter.addObserver(this, AppFragment.MOVE_TO_RIDERS_FRAGMENT, (context, data) -> {
            //TODO:
            String payload = data.getStringExtra("payload");
            Message msg = new Message().setPayload(payload);
            moveToRiders(msg);
        });

        Log.d(TAG + "-lifecycle", "onCreate");
    }

    /**
     * This version of OnSaveInstanceState will automatically called by the
     * framework when an activity closing down:
     * @param outState
     */
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        //Save your UI State programmatically:
        Log.d(TAG + "-lifecycle", "onSaveInstanceState");
        //
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG + "-lifecycle", "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG + "-lifecycle", "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG + "-lifecycle", "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG + "-lifecycle", "onStop");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG + "-lifecycle", "onRestart");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG + "-lifecycle", "onDestroy");
        NotificationCenter.removeObserver(this, AppFragment.MOVE_TO_RIDERS_FRAGMENT);
    }

    public void moveToRiders(Message msg) {
        //startActivity(new Intent(this, RiderList.class));
        Log.d(TAG, "Click moveToRiders: " + msg.getPayload());
    }

}
