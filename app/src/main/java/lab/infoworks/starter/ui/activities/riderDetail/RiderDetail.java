package lab.infoworks.starter.ui.activities.riderDetail;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import lab.infoworks.libshared.domain.model.Rider;
import lab.infoworks.starter.R;

public class RiderDetail extends AppCompatActivity {

    private static final String TAG = RiderDetail.class.getName();

    @BindView(R.id.riderName)
    TextView riderName;

    @BindView(R.id.riderAge)
    TextView riderAge;

    @BindView(R.id.riderGender)
    TextView riderGender;

    @BindView(R.id.riderEmail)
    TextView riderEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(lab.infoworks.starter.R.layout.activity_rider_detail);
        ButterKnife.bind(this);

        //Read the passed rider:
        Intent intent = getIntent();
        String json = intent.getStringExtra("rider_selected");
        if (json != null && !json.isEmpty()){
            Rider rider = new Rider(json);
            riderName.setText("Name:" + rider.getName());
            riderAge.setText("Age: " + rider.getAge());
            riderGender.setText("Gender: " + rider.getGender());
            riderEmail.setText("Email: " + rider.getEmail());
        }

        //Setting Up ActionBar Title
        ActionBar actionBar = getSupportActionBar();
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