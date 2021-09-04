package lab.infoworks.starter.ui.activities.riderDetail;

import android.app.ActionBar;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import lab.infoworks.starter.R;

public class RiderDetail extends AppCompatActivity {

    private static final String TAG = RiderDetail.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(lab.infoworks.starter.R.layout.activity_rider_detail);

        //Setting Up ActionBar Title
        ActionBar actionBar = getActionBar();
        if (actionBar != null){
            actionBar.setTitle(R.string.rider_detail_title);
        }

        Log.d(TAG + "-lifecycle", "onCreate");
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
    }
}