package lab.infoworks.starter.ui.activities.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lab.infoworks.starter.R;
import lab.infoworks.starter.ui.activities.riderList.RiderList;


public class AppActivity extends AppCompatActivity {

    private static final String TAG = AppActivity.class.getName();

    @BindView(R.id.verificationStatusTextView)
    TextView verificationStatusTextView;

    @BindView(R.id.verifyButton)
    Button verifyButton;

    private AppViewModel appViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_app);
        ButterKnife.bind(this);
        //We can initialize viewModel here:
        /*appViewModel = new AppViewModel(getApplication());*/
        //Following is also acceptable way of getting viewModel:
        appViewModel = new ViewModelProvider(this).get(AppViewModel.class);
        appViewModel.getUserStatusObservable().observe(this, verificationResult -> {
            //
            Log.d(TAG, "===> result: " + verificationResult.isVerified());
            verificationStatusTextView.setText("Rider is verified.... :) ");
        });

        //Setting Up ActionBar Title
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setTitle(R.string.app_name);
        }

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
    }

    @OnClick(R.id.verifyButton)
    public void verifyRider() {
        appViewModel.verifyUser();
    }

    @OnClick(R.id.riderListButton)
    public void findRiders(View view) {
        startActivity(new Intent(this, RiderList.class));
    }

}
