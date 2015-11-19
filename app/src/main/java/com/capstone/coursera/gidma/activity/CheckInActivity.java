package com.capstone.coursera.gidma.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;

import com.capstone.coursera.gidma.R;
import com.capstone.coursera.gidma.client.SecuredRestException;
import com.capstone.coursera.gidma.model.mediator.UserDataMediator;
import com.capstone.coursera.gidma.model.mediator.webdata.UserCheckIn;
import com.capstone.coursera.gidma.utils.Constants;

import java.text.ParseException;
import java.util.Date;

import retrofit.RetrofitError;

import static com.capstone.coursera.gidma.utils.Constants.SDF_1;

public class CheckInActivity extends AppCompatActivity {

    // A tag used for debugging with Logcat
    static final String LOG_TAG = CheckInActivity.class.getCanonicalName();


    // UI references.
    private EditText mCheckin_bloodSugarLvlFastingEditText;
    private EditText mCheckin_bloodSugarLvlMTEditText;
    private EditText mCheckin_bloodSugarLvlBTEditText;
    private EditText mCheckin_bloodSugarLvlTimeEditText;
    private EditText mCheckin_eatMTEditText;
    private EditText mCheckin_whoWithYouEditText;
    private EditText mCheckin_whereWereYouEditText;

    private RatingBar mCheckin_moodRatingBar;
    private RatingBar mCheckin_stressRatingBar;
    private RatingBar mCheckin_energyLvlRatingBar;

    private Spinner mCheckin_bloodSugarLvlTimeEventData;

    private CheckBox mIsPatentCheckBox;

    private View mCheckin_progressView;
    private View mCheckInFormView;

    private String mUserId;


    private UserCheckInTask mUserCheckInTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });


        SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        mUserId = prefs.getString(Constants.KEY_PREFERENCE_USER_NAME, "");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Button mCheckInBtn = (Button) findViewById(R.id.checkInBtn);


        mCheckin_bloodSugarLvlFastingEditText = (EditText) findViewById(R.id.checkin_bloodSugarLvlFastingEditText);
        mCheckin_bloodSugarLvlMTEditText = (EditText) findViewById(R.id.checkin_bloodSugarLvlMTEditText);
        mCheckin_bloodSugarLvlBTEditText = (EditText) findViewById(R.id.checkin_bloodSugarLvlBTEditText);
        mCheckin_bloodSugarLvlTimeEditText = (EditText) findViewById(R.id.checkin_bloodSugarLvlTimeEditText);
        mCheckin_eatMTEditText = (EditText) findViewById(R.id.checkin_eatMTEditText);
        mCheckin_whoWithYouEditText = (EditText) findViewById(R.id.checkin_whoWithYouEditText);
        mCheckin_whereWereYouEditText = (EditText) findViewById(R.id.checkin_whereWereYouEditText);

        mCheckin_moodRatingBar = (RatingBar) findViewById(R.id.checkin_moodRatingBar);
        mCheckin_stressRatingBar = (RatingBar) findViewById(R.id.checkin_stressRatingBar);
        mCheckin_energyLvlRatingBar = (RatingBar) findViewById(R.id.checkin_energyLvlRatingBar);

        mCheckin_bloodSugarLvlTimeEventData = (Spinner) findViewById(R.id.checkin_bloodSugarLvlTimeEventData);

        mIsPatentCheckBox = (CheckBox) findViewById(R.id.isPatentCheckBox);

        mCheckin_progressView = findViewById(R.id.checkin_progress);
        mCheckInFormView = findViewById(R.id.checkInForm);

    }


    public void checkInData(View view) {
        Log.i(LOG_TAG, "checkInData --Started...");

        if (mUserCheckInTask != null) {
            return;
        }

        UserCheckIn userCheckIn = new UserCheckIn();
        userCheckIn.setUserId(mUserId);

        Date currentDate = new Date();

        userCheckIn.setBloodSugarLvlFasting(String.valueOf(mCheckin_bloodSugarLvlFastingEditText.getText()));
        userCheckIn.setBloodSugarLvlMT(String.valueOf(mCheckin_bloodSugarLvlMTEditText.getText()));
        userCheckIn.setBloodSugarLvlBT(String.valueOf(mCheckin_bloodSugarLvlBTEditText.getText()));

        try {
            currentDate = SDF_1.parse(SDF_1.format(currentDate));
        } catch (ParseException eX) {
            eX.printStackTrace();
        }

        userCheckIn.setCheckInDateTime(currentDate);

        userCheckIn.setBloodSugarLvlTime(String.valueOf(mCheckin_bloodSugarLvlTimeEditText.getText()));

        userCheckIn.setEatMT(String.valueOf(mCheckin_eatMTEditText.getText()));
        userCheckIn.setWhoWithYou(String.valueOf(mCheckin_whoWithYouEditText.getText()));
        userCheckIn.setWhereWereYou(String.valueOf(mCheckin_whereWereYouEditText.getText()));

        userCheckIn.setInsulin(String.valueOf(mIsPatentCheckBox.isChecked()));

        userCheckIn.setMood(String.valueOf(mCheckin_moodRatingBar.getRating()));
        userCheckIn.setStress(String.valueOf(mCheckin_stressRatingBar.getRating()));
        userCheckIn.setEnergyLvl(String.valueOf(mCheckin_energyLvlRatingBar.getRating()));

        userCheckIn.setBloodSugarLvlTimeEvent(String.valueOf(mCheckin_bloodSugarLvlTimeEventData.getSelectedItem()));

        Log.i(LOG_TAG, "checkInData --userCheckIn Data..." + userCheckIn);

        showProgress(true);
        mUserCheckInTask = new UserCheckInTask(userCheckIn);
        mUserCheckInTask.execute((Void) null);


        Log.i(LOG_TAG, "checkInData --Finished...");
    }


    @Override
    protected void onResume() {
        super.onResume();

//        String title = String.valueOf(this.getTitle());
//        setTitle(title + " - ( " + mUserId +" ) ");

        setTitle(mUserId + "'s Check in ");
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

            mCheckInFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mCheckInFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mCheckInFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mCheckin_progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mCheckin_progressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mCheckin_progressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mCheckin_progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mCheckInFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Represents an asynchronous data check in task.
     */
    public class UserCheckInTask extends AsyncTask<Void, Void, Boolean> {

        private final UserCheckIn userCheckIn;

        UserCheckInTask(UserCheckIn userCheckIn) {
            this.userCheckIn = userCheckIn;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                UserDataMediator userDataMediator = new UserDataMediator(getApplicationContext());

                if (userDataMediator.userCheckIn(userCheckIn) == null) {
                    return false;
                }
            } catch (RetrofitError eX1) {
                if (eX1.getCause() instanceof SecuredRestException) {
//                Utils.showToast(this,
//                        "Check in failed, Please try again.");
                    Log.e(LOG_TAG, "SecuredRestException in checkInData", eX1);
                } else {
                    Log.e(LOG_TAG, "Problem in checkInData", eX1);
                }
            } catch (Exception eX2) {
                Log.e(LOG_TAG, "checkInData : Unknown Exception occured..", eX2);
            }


            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mUserCheckInTask = null;
            showProgress(false);

            if (success) {
                finish();

                // if sign up is successful than send user to Login page after register the new account here.
                startActivity(new Intent(getApplicationContext(), UserHomeActivity.class));
            } else {

            }
        }

        @Override
        protected void onCancelled() {
            mUserCheckInTask = null;
            showProgress(false);
        }
    }


}
