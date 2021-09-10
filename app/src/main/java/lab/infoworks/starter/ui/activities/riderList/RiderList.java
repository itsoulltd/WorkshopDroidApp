package lab.infoworks.starter.ui.activities.riderList;

import android.app.Activity;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Comparator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lab.infoworks.libshared.domain.model.Rider;
import lab.infoworks.libshared.notifications.NotificationCenter;
import lab.infoworks.libshared.notifications.SystemNotificationTray;
import lab.infoworks.starter.R;
import lab.infoworks.starter.ui.activities.riderDetail.RiderDetail;
import lab.infoworks.starter.ui.activities.riderList.recycler.RiderAdapter;

public class RiderList extends AppCompatActivity {

    public static final String RIDER_SELECTED_INDEX_KEY = "rider_selected_index";
    public static final String RIDER_SELECTED_KEY = "rider_selected";
    public static final String RIDER_SELECTED_NOTIFICATION = "rider_selected_notification";
    public static final String RIDER_UPDATED_KEY = "rider_updated";
    public static final int RIDER_DETAIL_UPDATE_REQUEST = 10001;

    private static final String TAG = RiderList.class.getName();
    private SystemNotificationTray notificationTray;

    @BindView(R.id.rvRiders)
    RecyclerView rvRiders;
    RiderAdapter rvAdapter;

    private Rider _selected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(lab.infoworks.starter.R.layout.activity_rider_list);
        ButterKnife.bind(this);

        notificationTray = new SystemNotificationTray(this);

        //Another way of getting viewModel:
        ViewModelProviders.of(this)
                .get(RiderListViewModel.class)
                .getRiderObservable().observe(this, (riders) -> {

            //TODO:
            Log.d(TAG, "===> number of riders found: " + riders.size());
            notifyTray();

            RiderAdapter adapter = new RiderAdapter(riders);
            rvRiders.setAdapter(adapter);
            rvAdapter = adapter;
            rvRiders.setLayoutManager(new LinearLayoutManager(this));
            //
        });

        //Setting Up ActionBar Title
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setTitle(R.string.rider_list_title);
        }

        //Handling Notifications
        NotificationCenter.addObserver(this, RIDER_SELECTED_NOTIFICATION, (context, data) -> {
            //TODO:
            String json = data.getStringExtra(RIDER_SELECTED_KEY);
            Integer index = Integer.valueOf(data.getStringExtra(RIDER_SELECTED_INDEX_KEY));
            _selected = new Rider(json);
            Toast.makeText(context,String.format("Index: %s, Name: %s", index, _selected.getName()), Toast.LENGTH_SHORT).show();

            //TODO: PlayWith
            //moveToDetail(_selected);
        });

        Log.d(TAG+ "-lifecycle", "onCreate");
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
        NotificationCenter.removeObserver(this, RIDER_SELECTED_NOTIFICATION);
        Log.d(TAG + "-lifecycle", "onDestroy");
    }

    /**
     * How to intercept device back-button pressed:
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d(TAG + "-lifecycle", "onBackPressed");
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

    @OnClick(R.id.fetchRiderButton)
    public void onFetch(View view){
        ViewModelProviders.of(this)
                .get(RiderListViewModel.class).findRiders();
    }

    @OnClick(R.id.riderButton)
    public void onClick(View view){
        moveToDetail(_selected);
    }

    private void moveToDetail(Rider selected) {
        //
        Intent intent = new Intent(this, RiderDetail.class);

        //Pass the selected Rider:
        if(selected != null)
            intent.putExtra(RIDER_SELECTED_KEY, selected.toString());

        //This is how we normally start an activity:
        //startActivity(intent);

        //To start an activity for getting result back from calling activity
        //need slight change on the calling method: (Since Deprecated we will check whats in New)
        startActivityForResult(intent, RIDER_DETAIL_UPDATE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK
                && requestCode == RIDER_DETAIL_UPDATE_REQUEST){
            //Unpack data from intent
            String json = data.getStringExtra(RIDER_UPDATED_KEY);
            Rider updated = new Rider(json);

            //Update On RecyclerView:
            if (rvAdapter != null){
                rvAdapter.notifyItemChanged(updated, (oldOne, newOne) -> {
                    int res = oldOne.getId().compareTo(newOne.getId());
                    return res;
                });
            }

            //we update rider into persistence using
            ViewModelProviders.of(this)
                    .get(RiderListViewModel.class)
                    .update(updated);
            //
        }
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