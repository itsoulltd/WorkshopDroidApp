package lab.infoworks.starter.ui.activities.app;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.infoworks.lab.rest.models.Message;

import lab.infoworks.libshared.notifications.NotificationCenter;
import lab.infoworks.starter.R;
import lab.infoworks.starter.ui.activities.riderList.RidersFragment;


public class AppActivity extends AppCompatActivity {

    private static final String TAG = AppActivity.class.getName();

    private NavStack navStack;

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

        navStack = NavStack.create(getSupportFragmentManager());
        if (savedInstanceState == null){
            navStack.pushNavStack(getSupportFragmentManager().findFragmentByTag("AppFragment"), null);
        }

        NotificationCenter.addObserver(this, AppFragment.MOVE_TO_RIDERS_FRAGMENT, (context, data) -> {
            //TODO:
            String payload = data.getStringExtra("payload");
            Message msg = new Message().setPayload(payload);
            Log.d(TAG, "Click moveToRiders: " + msg.getPayload());
            //TODO:
            Fragment fragment = RidersFragment.newInstance();
            navStack.pushNavStack(fragment, "RidersFragment");
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
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG + "-lifecycle", "onDestroy");
        NotificationCenter.removeObserver(this, AppFragment.MOVE_TO_RIDERS_FRAGMENT);
        navStack.close();
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG + "-lifecycle", "onBackPressed");
        navStack.popNavStack();
        //super.onBackPressed();
    }
}
