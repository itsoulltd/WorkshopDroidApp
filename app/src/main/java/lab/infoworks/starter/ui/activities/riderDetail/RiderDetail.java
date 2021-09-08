package lab.infoworks.starter.ui.activities.riderDetail;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import lab.infoworks.libshared.domain.model.Rider;
import lab.infoworks.starter.R;
import lab.infoworks.starter.ui.activities.riderList.RiderList;

public class RiderDetail extends AppCompatActivity {

    private static final String TAG = RiderDetail.class.getName();

    @BindView(R.id.riderName)
    TextView riderName;

    @BindView(R.id.riderAge)
    TextView riderAge;

    @BindView(R.id.riderGender)
    TextView riderGender;

    @BindView(R.id.riderEmail)
    EditText riderEmail;

    private Rider rider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(lab.infoworks.starter.R.layout.activity_rider_detail);
        ButterKnife.bind(this);

        //Setting Up ActionBar Title
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setTitle(R.string.rider_detail_title);
            actionBar.setHomeAsUpIndicator(R.drawable.def_checker);
        }

        //Read the passed rider:
        Intent intent = getIntent();
        String json = intent.getStringExtra("rider_selected");
        if (json != null && !json.isEmpty()){
            Rider rider = new Rider(json);
            this.rider = rider;
            riderName.setText("Name:" + rider.getName());
            riderAge.setText("Age: " + rider.getAge());
            riderGender.setText("Gender: " + rider.getGender());
            riderEmail.setText(rider.getEmail());
        }

        Log.d(TAG + "-lifecycle", "onCreate");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG + "-lifecycle", "onDestroy");
    }

    /**
     * How to intercept device back-button pressed:
     */
    @Override
    public void onBackPressed() {
        Log.d(TAG + "-lifecycle", "onBackPressed");

        //TODO: Update all fields if you wants
        if (rider != null){
            rider.setEmail(riderEmail.getText().toString());

            //Prepare for back the result:
            Intent resultIntent = new Intent();
            resultIntent.putExtra(RiderList.RIDER_UPDATED_KEY, rider.toString());
            setResult(Activity.RESULT_OK, resultIntent);
        }
        //Simply call either one of these:
        super.onBackPressed();
        //OR
        //finish();
    }

    /**
     * How to intercept action bar back pressed:
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }else {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            return super.onOptionsItemSelected(item);
        }
    }
}