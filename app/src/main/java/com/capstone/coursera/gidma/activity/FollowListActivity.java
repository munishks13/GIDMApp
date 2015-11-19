package com.capstone.coursera.gidma.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.capstone.coursera.gidma.R;
import com.capstone.coursera.gidma.activity.adapter.FollowListAdapter;
import com.capstone.coursera.gidma.client.SecuredRestException;
import com.capstone.coursera.gidma.model.mediator.UserDataMediator;
import com.capstone.coursera.gidma.model.mediator.webdata.User;
import com.capstone.coursera.gidma.model.mediator.webdata.UserBasicInfo;
import com.capstone.coursera.gidma.utils.Constants;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit.RetrofitError;

import static com.capstone.coursera.gidma.utils.Constants.KEY_LOGGED_IN_USER_OBJ;
import static com.capstone.coursera.gidma.utils.Constants.SHARED_PREF_PRIVATE_FILE;

public class FollowListActivity extends AppCompatActivity implements GestureOverlayView.OnGesturePerformedListener {

    // A tag used for debugging with Logcat
    static final String LOG_TAG = FollowListActivity.class.getCanonicalName();

    // UI references.
    private ListView mFollowersListView;
    private User mUserObj;

    private FollowListAdapter mFollowListAdapter;


    private View mFollow_req_progress;
    private View mFollowListForm;

    private FollowListTask mFollowListTask;

    private long mSelectedItemId = 0;
    private String mSelectedItemLabel = "Followers or Following List";


    private static final String NO = "No";
    private static final String YES = "Yes";
    private static final String PREV = "Prev";
    private static final String NEXT = "Next";
    private GestureLibrary mLibrary;
    private int mBgColor = 0;
    private int mFirstColor, mStartBgColor = Color.GRAY;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow_list);

        Log.i(LOG_TAG, "FollowListActivity: onCreate : ");

        setSelectedItemIdAndLabel();

        SharedPreferences mPrefs = this.getSharedPreferences(SHARED_PREF_PRIVATE_FILE, MODE_PRIVATE);
        Gson gson = new Gson();
        String userJsonObj = mPrefs.getString(KEY_LOGGED_IN_USER_OBJ, "");
        mUserObj = gson.fromJson(userJsonObj, User.class);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mFollow_req_progress = findViewById(R.id.follow_req_progress);
        mFollowListForm = findViewById(R.id.followListForm);

        mFollowersListView = (ListView) findViewById(R.id.followersList);

        mFollowListAdapter = new FollowListAdapter(this, mUserObj.getUserId());
        mFollowersListView.setAdapter(mFollowListAdapter);

        mLibrary = GestureLibraries.fromRawResource(this, R.raw.gestures);
        if (!mLibrary.load()) {
            finish();
        }

        // Make this the target of gesture detection callbacks
        GestureOverlayView gestureView = (GestureOverlayView) findViewById(R.id.gestures_overlay);
        gestureView.addOnGesturePerformedListener(this);


        getFollowList();
    }


    @Override
    protected void onResume() {
        super.onResume();
        setTitle(mUserObj.getUserId() + "'s " + mSelectedItemLabel);
    }

    private void setSelectedItemIdAndLabel() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        mSelectedItemId = prefs.getLong(Constants.KEY_PREFERENCE_USER_SELECTED_ID, 0);
        mSelectedItemLabel = prefs.getString(Constants.KEY_PREFERENCE_USER_SELECTED_ID_LABEL, "");
        Log.i(LOG_TAG, "setSelectedItemIdAndLabel: mSelectedItemId : " + mSelectedItemId + " : mSelectedItemLabel : " + mSelectedItemLabel);
    }


    public void getFollowList() {
        Log.i(LOG_TAG, "FollowListActivity: getFollowList : ");

        if (mFollowListTask != null) {
            return;
        }


        showProgress(true);
        mFollowListTask = new FollowListTask(mUserObj.getUserId());
        mFollowListTask.execute((Void) null);
    }


    /**
     * Shows the progress UI and hides the Signup form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mFollowListForm.setVisibility(show ? View.GONE : View.VISIBLE);
            mFollowListForm.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mFollowListForm.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mFollow_req_progress.setVisibility(show ? View.VISIBLE : View.GONE);
            mFollow_req_progress.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mFollow_req_progress.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mFollow_req_progress.setVisibility(show ? View.VISIBLE : View.GONE);
            mFollowListForm.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class FollowListTask extends AsyncTask<Void, Void, Boolean> {

        private final String userId;
        private List<UserBasicInfo> followUsersList;

        FollowListTask(String userId) {
            this.userId = userId;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                Log.i(LOG_TAG, "doInBackground: mSelectedItemId : " + mSelectedItemId);

                UserDataMediator userDataMediator = new UserDataMediator(getApplicationContext());

                if (mSelectedItemId == R.drawable.followers
                        ||  mSelectedItemId == R.drawable.followers_request
                        ||  mSelectedItemId == R.drawable.data_privacy )
                {
                    followUsersList = userDataMediator.getFollowersList();
                } else if (mSelectedItemId == R.drawable.following_list
                        ||  mSelectedItemId == R.drawable.waiting_following_request)
                {
                    followUsersList = userDataMediator.getFollowingList();
                } else if (mSelectedItemId == R.drawable.request_to_follow)
                {
                    followUsersList = userDataMediator.getPatientUserForFollowList();
                }


            } catch (RetrofitError eX1) {
                if (eX1.getCause() instanceof SecuredRestException) {
                    Log.e(LOG_TAG, "Problem in FollowListTask SecuredRestException", eX1);
                } else {
                    Log.e(LOG_TAG, "Problem in FollowListTask", eX1);
                }
                return false;
            } catch (Exception eX2) {
                Log.e(LOG_TAG, "FollowListTask Unknown Exception occurred..", eX2);
                return false;
            }


            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mFollowListTask = null;
            showProgress(false);

            if (success) {
                if(followUsersList != null && !followUsersList.isEmpty()) {
                    mFollowListAdapter.setFollowUsersList(getListBasedOnSelectedItem());
                    mFollowListAdapter.setSelectedItemId(mSelectedItemId);
                    mFollowListAdapter.notifyDataSetChanged();
                } else {
                    Log.i(LOG_TAG, "onPostExecute : NO DATA FOUND.....");
                    Toast.makeText(getApplicationContext(), "No Data Found", Toast.LENGTH_SHORT).show();
                }

                //finish();

                //startActivity(new Intent(getApplicationContext(), UserHomeActivity.class));
            } else {
                Toast.makeText(getApplicationContext(), "Request Failed....", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            mFollowListTask = null;
            showProgress(false);
        }

        private List<UserBasicInfo> getListBasedOnSelectedItem() {
            Log.i(LOG_TAG, "FollowListActivity: getListBasedOnSelectedItem : mSelectedItemId : " + mSelectedItemId);

            List<UserBasicInfo> listBasedOnSelectedItem = new ArrayList<>();
            if (followUsersList != null && !followUsersList.isEmpty()) {
                for (UserBasicInfo userBasicInfo : followUsersList) {

                    if (mSelectedItemId == R.drawable.followers || mSelectedItemId == R.drawable.data_privacy || mSelectedItemId == R.drawable.following_list) {
                        if(userBasicInfo.isConfirmed()){
                            listBasedOnSelectedItem.add(userBasicInfo);
                        }
                    } else if (mSelectedItemId == R.drawable.followers_request || mSelectedItemId == R.drawable.waiting_following_request) {
                        if(! userBasicInfo.isConfirmed()){
                            listBasedOnSelectedItem.add(userBasicInfo);
                        }
                    } else if (mSelectedItemId == R.drawable.request_to_follow) {
                        listBasedOnSelectedItem.add(userBasicInfo);
                    }
                }
            }
            return listBasedOnSelectedItem;
        }

    }


    public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {

        // Get gesture predictions
        ArrayList<Prediction> predictions = mLibrary.recognize(gesture);

        // Get highest-ranked prediction
        if (predictions.size() > 0) {
            Prediction prediction = predictions.get(0);

            // Ignore weak predictions

            if (prediction.score > 2.0) {

                if (prediction.name.equals(PREV)) {
                    // If User Clicks Previous then go to next activity
                    Toast.makeText(this, "Go To Home Screen.", Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(this, UserHomeActivity.class));
                    finish();

                } else if (prediction.name.equals(NEXT)) {


                } else if (prediction.name.equals(YES)) {

                    Toast.makeText(this, prediction.name, Toast.LENGTH_SHORT).show();
                } else if (prediction.name.equals(NO)) {

                    Toast.makeText(this, prediction.name, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "No prediction", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

}
