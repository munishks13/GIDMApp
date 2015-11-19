package com.capstone.coursera.gidma.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.capstone.coursera.gidma.R;
import com.capstone.coursera.gidma.client.SecuredRestException;
import com.capstone.coursera.gidma.common.Utils;
import com.capstone.coursera.gidma.model.mediator.UserDataMediator;
import com.capstone.coursera.gidma.model.mediator.webdata.User;
import com.capstone.coursera.gidma.model.mediator.webdata.UserMedicalRecord;
import com.capstone.coursera.gidma.utils.Constants;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit.RetrofitError;

public class SignUpActivity extends AppCompatActivity {

    // A tag used for debugging with Logcat
    static final String LOG_TAG = SignUpActivity.class.getCanonicalName();

    // UI references.
    private EditText mUserIdEditTextView;
    private EditText mPasswordEditTextView;
    private EditText mRepetePassEditTextView;
    private EditText mFirstNameEditTextView;
    private EditText mLastNameEditTextView;

    private EditText mDateOfBirthEditText;

    private CheckBox mIsPatientView;
    private EditText mMedRecNumEditTextView;
    private CheckBox mWantToBeFollowedView;

    private View mSignUpProgressView;
    private View mSignUpFormView;


    private User mUserForSignUp = null;

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserSignUpTask mSignUpTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Button mSignUpBtn = (Button) findViewById(R.id.signUpBtn);

        mUserIdEditTextView = (EditText) findViewById(R.id.userIdEditText);
        mPasswordEditTextView = (EditText) findViewById(R.id.passwordEditText);
        mRepetePassEditTextView = (EditText) findViewById(R.id.repetePassEditText);
        mFirstNameEditTextView = (EditText) findViewById(R.id.firstNameEditText);
        mLastNameEditTextView = (EditText) findViewById(R.id.lastNameEditText);

        mDateOfBirthEditText = (EditText) findViewById(R.id.dateOfBirthEditText);

        mIsPatientView = (CheckBox) findViewById(R.id.isPatentCheckBox);
        mMedRecNumEditTextView = (EditText) findViewById(R.id.medRecNumEditText);
        mWantToBeFollowedView = (CheckBox) findViewById(R.id.wantToBeFollowedCheckBox);


        mSignUpFormView = findViewById(R.id.signUp_form);
        mSignUpProgressView = findViewById(R.id.signUp_progress);


        mIsPatientView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mWantToBeFollowedView.setChecked(true);
                } else {
                    mWantToBeFollowedView.setChecked(false);
                }
            }
        });


    }

    /*
        newUserSignUpButton on activity_sign_up.xml is clicked.
     */
    public void signUpNewUser(View v) {

        if (mSignUpTask != null) {
            return;
        }

        try {

            if (!(checkRequiredAvailable(mUserIdEditTextView)
                    && checkRequiredAvailable(mPasswordEditTextView)
                    && checkRequiredAvailable(mRepetePassEditTextView)
            )) {
                return;
            }

            // user data validation
            if (!mPasswordEditTextView.getText().toString().equals(mRepetePassEditTextView.getText().toString())) {
                Utils.showToast(this, getString(R.string.validationFailed_pswd_repetePswd_notMatch));
                mPasswordEditTextView.requestFocus();
                return;
            }

            if (StringUtils.isNotBlank(mDateOfBirthEditText.getText().toString()) && !Constants.pattern_MMDDYYYY.matcher(mDateOfBirthEditText.getText().toString().trim()).matches()) {
                Utils.showToast(this, getString(R.string.dob_pattern_MMDDYYY_Error));
                mDateOfBirthEditText.requestFocus();
                return;
            }


            // Show a progress spinner, and kick off a background task to
            // perform the user signup operation.
            showProgress(true);

            mUserForSignUp = new User();

            mUserForSignUp.setUserId(mUserIdEditTextView.getText().toString());
            mUserForSignUp.setPassword(mPasswordEditTextView.getText().toString());

            mUserForSignUp.setFirstName(mFirstNameEditTextView.getText().toString());
            mUserForSignUp.setLastName(mLastNameEditTextView.getText().toString());

            //mUserForSignUp.setFollowers("");


            //user.setUserAlarmTimes(List < UserAlarmTimes > userAlarmTimes);

            // if user is Patient then only add for Medical record number
            if (mIsPatientView.isChecked()) {

                if (!(checkRequiredAvailable(mDateOfBirthEditText)
                        && checkRequiredAvailable(mMedRecNumEditTextView)
                )) {
                    return;
                }

                mUserForSignUp.setDateOfBirth(mDateOfBirthEditText.getText().toString());

                mUserForSignUp.setMedicalRecordNumber(mMedRecNumEditTextView.getText().toString());
                mUserForSignUp.setFollowed(mWantToBeFollowedView.isChecked());

            }

            mSignUpTask = new UserSignUpTask(mUserForSignUp);
            mSignUpTask.execute((Void) null);


        } catch (RetrofitError eX1) {
            if (eX1.getCause() instanceof SecuredRestException) {
                Utils.showToast(this,
                        "Sign Up failed, Please try again.");
            } else {
                Log.e(LOG_TAG, "Problem in signUpNewUser", eX1);
            }
        } catch (Exception eX2) {
            Log.e(LOG_TAG, "signUpNewUser : Unknown Exception occured..", eX2);
        }
    }


    private boolean checkRequiredAvailable(EditText editText) {
        if (StringUtils.isNotBlank(String.valueOf(editText.getText()))) {
            return true;
        }

        editText.setError(getString(R.string.error_field_required));
        editText.requestFocus();

        return false;
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

            mSignUpFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mSignUpFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mSignUpFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mSignUpProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mSignUpProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mSignUpProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mSignUpProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mSignUpFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserSignUpTask extends AsyncTask<Void, Void, Boolean> {

        private final User userForSignUp;

        UserSignUpTask(User userForSignUp) {
            this.userForSignUp = userForSignUp;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            UserDataMediator userDataMediator = new UserDataMediator(getApplicationContext());

            if (userDataMediator.signUpUser(userForSignUp) == null) {
                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mSignUpTask = null;
            showProgress(false);

            if (success) {
                finish();

                // if sign up is successful than send user to Login page after register the new account here.
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            } else {

            }
        }

        @Override
        protected void onCancelled() {
            mSignUpTask = null;
            showProgress(false);
        }
    }

}
