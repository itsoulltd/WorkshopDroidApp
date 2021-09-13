package lab.infoworks.starter.ui.activities.app;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.infoworks.lab.rest.models.Message;
import com.it.soul.lab.sql.query.models.Property;

import lab.infoworks.libshared.domain.model.Rider;
import lab.infoworks.libshared.notifications.NotificationCenter;
import lab.infoworks.libui.BaseActivity.NavStack;
import lab.infoworks.starter.R;
import lab.infoworks.starter.ui.activities.riderDetail.RiderFragment;
import lab.infoworks.starter.ui.activities.riderList.RiderListViewModel;
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

        navStack = NavStack.create(this, R.id.fragmentContainer);
        navStack.pushNavStack(getSupportFragmentManager().findFragmentByTag("AppFragment"), "AppFragment");

        //Handling Notifications
        NotificationCenter.addObserverOnMain(this, AppFragment.MOVE_TO_RIDERS_FRAGMENT, (context, data) -> {
            //TODO:
            String payload = data.getStringExtra("payload");
            Message msg = new Message().setPayload(payload);
            Log.d(TAG, "Click moveToRiders: " + msg.getPayload());
            //TODO:
            Fragment fragment = RidersFragment.newInstance();
            navStack.pushNavStack(fragment, "RidersFragment");
        });

        //Handling Notifications
        NotificationCenter.addObserverOnMain(this, RidersFragment.RIDER_SELECTED_NOTIFICATION, (context, data) -> {
            //TODO:
            Integer index = Integer.valueOf(data.getStringExtra(RidersFragment.RIDER_SELECTED_INDEX_KEY));
            Rider selected = new Rider(data.getStringExtra(RidersFragment.RIDER_SELECTED_KEY));
            Toast.makeText(context,String.format("Index: %s, Name: %s", index, selected.getName()), Toast.LENGTH_SHORT).show();

            //TODO: PlayWith
            Fragment fragment = RiderFragment.newInstance(selected, index);
            navStack.pushNavStack(fragment, "RiderFragment");

            //If-want to change back-arrow icon:
            /*if (actionBar != null){
                actionBar.setHomeAsUpIndicator(R.drawable.def_checker);
            }*/
        });

        //Handling Notifications
        NotificationCenter.addObserverOnMain(this, RiderFragment.RIDER_UPDATED_NOTIFICATION, (context, data) -> {
            //Unpack data from intent
            Rider updated = new Rider(data.getStringExtra(RiderFragment.RIDER_UPDATED_KEY));
            //We update rider into persistence using
            ViewModelProviders
                    .of(this)
                    .get(RiderListViewModel.class)
                    .update(updated);
            //Set the updated item's index to scroll to that position:
            Integer index = Integer.valueOf(data.getStringExtra(RiderFragment.RIDER_UPDATED_INDEX_KEY));
            navStack.popNavStack(
                    new Property(RidersFragment.RIDER_SELECTED_INDEX_KEY, index)
            );
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
        NotificationCenter.removeObserver(this, RidersFragment.RIDER_SELECTED_NOTIFICATION);
        NotificationCenter.removeObserver(this, RiderFragment.RIDER_UPDATED_NOTIFICATION);
        navStack.close();
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG + "-lifecycle", "onBackPressed");
        if(navStack.isOnTop())
            super.onBackPressed();
        else
            navStack.popNavStack();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            onBackPressed();
            return true;
        }else {
            return super.onOptionsItemSelected(item);
        }
    }
}
