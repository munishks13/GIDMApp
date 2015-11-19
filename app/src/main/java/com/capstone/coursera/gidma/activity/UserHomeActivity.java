package com.capstone.coursera.gidma.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.capstone.coursera.gidma.R;
import com.capstone.coursera.gidma.activity.adapter.UserHomeImageAdapter;
import com.capstone.coursera.gidma.broadcast.CheckInAlarmNotificationReceiver;
import com.capstone.coursera.gidma.utils.Constants;

import java.util.ArrayList;
import java.util.Arrays;

public class UserHomeActivity extends AppCompatActivity {

    // A tag used for debugging with Logcat
    static final String LOG_TAG = UserHomeActivity.class.getCanonicalName();
    GridView mGridView;
    private ArrayList<Integer> HOME_SCREEN_ALL = new ArrayList<Integer>(
            Arrays.asList(
                    R.drawable.checkin,
                    R.drawable.data_privacy,
                    R.drawable.feedback,
                    R.drawable.followers,
                    R.drawable.followers_request,
                    R.drawable.following_list,
                    R.drawable.waiting_following_request,
                    R.drawable.request_to_follow));


    private String mUserId;

    private AlarmManager mAlarmManager;
    private Intent mCheckInAlarmNotificationReceiverIntent;
    private PendingIntent mCheckInAlarmNotificationReceiverPendingIntent;

    private static final long REPEAT_ALARM_DELAY = 2 * 60 * 1000L; // 2 minutes...
    private static final long INITIAL_ALARM_DELAY = REPEAT_ALARM_DELAY;


    private boolean isAlarmSet = false;

    protected static final long JITTER = 5000L;

    private Menu userHomeMenu;

    private static final String PERF_NAME = "com.capstone.coursera.gidma_perf";
    private static final String PERF_ALARM_SET_KEY = "alarmSet";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);


        SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        mUserId = prefs.getString(Constants.KEY_PREFERENCE_USER_NAME, "");


        // Get the AlarmManager Service
        mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        // Create an Intent to broadcast to the CheckInAlarmNotificationReceiver
        mCheckInAlarmNotificationReceiverIntent = new Intent(UserHomeActivity.this, CheckInAlarmNotificationReceiver.class);

        // Create an PendingIntent that holds the NotificationReceiverIntent
        mCheckInAlarmNotificationReceiverPendingIntent = PendingIntent.getBroadcast(UserHomeActivity.this, 0, mCheckInAlarmNotificationReceiverIntent, 0);



        mGridView = (GridView) findViewById(R.id.userHomegGridView);

        mGridView.setAdapter(new UserHomeImageAdapter(this, HOME_SCREEN_ALL));

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                String selectedItemLabel = String.valueOf(((TextView) v.findViewById(R.id.grid_item_label)).getText());

                Toast.makeText(getApplicationContext(), selectedItemLabel, Toast.LENGTH_SHORT).show();

                Log.i(LOG_TAG, "setOnItemClickListener --id..." + id);

                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = prefs.edit();
                editor.putLong(Constants.KEY_PREFERENCE_USER_SELECTED_ID, id);
                editor.putString(Constants.KEY_PREFERENCE_USER_SELECTED_ID_LABEL, selectedItemLabel);
                editor.putString(Constants.KEY_PREFERENCE_FEEDBACK_GRAPHUSER_NAME, "");// initialize
                editor.commit();

                Intent userHomeIntent = null;

                if (id == R.drawable.checkin) {
                    userHomeIntent = new Intent(getApplicationContext(), CheckInActivity.class);
                } else if (id == R.drawable.feedback) {
                    userHomeIntent = new Intent(getApplicationContext(), FeedbackActivity.class);
                } else if (id == R.drawable.data_privacy) {
                    userHomeIntent = new Intent(getApplicationContext(), DataPrivacyActivity.class);
                } else if (id == R.drawable.followers
                        || id == R.drawable.followers_request
                        || id == R.drawable.following_list
                        || id == R.drawable.waiting_following_request
                        || id == R.drawable.request_to_follow) {
                    userHomeIntent = new Intent(getApplicationContext(), FollowListActivity.class);
                }

                if (userHomeIntent != null) {
                    startActivity(userHomeIntent);
                } else {
                    Log.i(LOG_TAG, "setOnItemClickListener -No Activity Found...");
                }
            }
        });


        reStoreAlarmState();
    }

    @Override
    protected void onResume() {
        super.onResume();

//        String title = String.valueOf(this.getTitle());
//        setTitle(title + " - ( " + mUserId + " ) ");

        setTitle(mUserId + "'s Home");

        reStoreAlarmState();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean isMenuAvailable = super.onCreateOptionsMenu(menu);

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_check_in, menu);
        this.userHomeMenu = menu;

        return isMenuAvailable;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean isMenuAvailable = super.onPrepareOptionsMenu(menu);
        this.userHomeMenu = menu;

        //Log.i(TAG, " onPrepareOptionsMenu : isMenuAvailable " + isMenuAvailable + " : menu : " + menu);
        reStoreAlarmState();
        return isMenuAvailable;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(LOG_TAG, "Entered onOptionsItemSelected : item : " + item);

        int id = item.getItemId();


        //Log.i(TAG, "onOptionsItemSelected : id :" + id);

        switch (id) {

            case R.id.action_set_Alarm:
                setUnsetRepeatingAlarm();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setUnsetRepeatingAlarm() {
        Log.i(LOG_TAG, " Entered setUnsetRepeatingAlarm...");

        if(isAlarmSet){
            // Cancel all alarms using mCheckInAlarmNotificationReceiverPendingIntent
            mAlarmManager.cancel(mCheckInAlarmNotificationReceiverPendingIntent);

            // Show Toast message
            Toast.makeText(getApplicationContext(), "Repeating Alarm Canceled", Toast.LENGTH_LONG).show();
            isAlarmSet = false;
        } else {
            // Set repeating alarm
            mAlarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME,
                    SystemClock.elapsedRealtime() + INITIAL_ALARM_DELAY,
                    REPEAT_ALARM_DELAY,
                    mCheckInAlarmNotificationReceiverPendingIntent);

            // Show Toast message
            Toast.makeText(getApplicationContext(), "Repeating Alarm Set", Toast.LENGTH_LONG).show();
            isAlarmSet = true;
        }

        storeAlarmState(isAlarmSet);
    }

    private void setAlarmStateImage() {
        Log.i(LOG_TAG, "setAlarmStateImage: setAlarmState : " + isAlarmSet );
        if(userHomeMenu != null){
            if(isAlarmSet){
                userHomeMenu.getItem(1).setIcon(getResources().getDrawable(R.drawable.alarm_set));
            } else {
                userHomeMenu.getItem(1).setIcon(getResources().getDrawable(R.drawable.alarm_not_set));
            }
        }
    }


    private void storeAlarmState(boolean isAlarmSet) {
        SharedPreferences prefs = getSharedPreferences(PERF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(PERF_ALARM_SET_KEY, isAlarmSet);
        editor.commit();

        setAlarmStateImage();
        Log.i(LOG_TAG, "storeAlarmState: isAlarmSet : " + isAlarmSet);
    }

    private void reStoreAlarmState() {
        SharedPreferences prefs = getSharedPreferences(PERF_NAME, MODE_PRIVATE);
        isAlarmSet = prefs.getBoolean(PERF_ALARM_SET_KEY, false);

        setAlarmStateImage();
        Log.i(LOG_TAG, "reStoreAlarmState: isAlarmSet : " + isAlarmSet);
    }

}
