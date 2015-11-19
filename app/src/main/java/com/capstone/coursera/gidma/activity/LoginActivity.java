package com.capstone.coursera.gidma.activity;

import static com.capstone.coursera.gidma.utils.Constants.KEY_LOGGED_IN_USER_OBJ;
import static com.capstone.coursera.gidma.utils.Constants.SHARED_PREF_PRIVATE_FILE;


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
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.capstone.coursera.gidma.R;
import com.capstone.coursera.gidma.client.SecuredRestException;
import com.capstone.coursera.gidma.model.mediator.UserDataMediator;
import com.capstone.coursera.gidma.model.mediator.webdata.BloodSugarLevelTimeEvents;
import com.capstone.coursera.gidma.model.mediator.webdata.User;
import com.capstone.coursera.gidma.utils.Constants;
import com.capstone.coursera.gidma.view.ui.LoginActivityOld;
import com.google.gson.Gson;

import java.util.List;

import retrofit.RetrofitError;

/**
 * A login screen that offers login via user Id /password.
 */
public class LoginActivity extends AppCompatActivity {

    // A tag used for debugging with Logcat
    static final String LOG_TAG = LoginActivity.class.getCanonicalName();

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private EditText mUserIdView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mUserIdView = (EditText) findViewById(R.id.userId);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mNewUserSignUpButton = (Button) findViewById(R.id.newUserSignUpButton);
        mNewUserSignUpButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), SignUpActivity.class));
            }
        });

        Button mUserSignInButton = (Button) findViewById(R.id.sign_in_button);
        mUserSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid userIds, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mUserIdView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String userId = mUserIdView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid userId and password.
        if (TextUtils.isEmpty(userId)) {
            mUserIdView.setError(getString(R.string.error_field_required));
            focusView = mUserIdView;
            cancel = true;
        }
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));

            if (!TextUtils.isEmpty(userId)) focusView = mPasswordView;

            cancel = true;
        }


        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(userId, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void setPreferenceSummary(SharedPreferences prefs, String key, String value) {

        SharedPreferences.Editor editor = prefs.edit();

        if (TextUtils.equals(key, Constants.KEY_PREFERENCE_USER_NAME)) {
            editor.putString(Constants.KEY_PREFERENCE_USER_NAME, value);
        } else if (TextUtils.equals(key, Constants.KEY_PREFERENCE_PASSWORD)) {
            editor.putString(Constants.KEY_PREFERENCE_PASSWORD, value);
        }

        editor.commit();

    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String userId;
        private final String mPassword;

        UserLoginTask(String userId, String password) {
            this.userId = userId;
            this.mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            SharedPreferences prefs =
                    PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

            setPreferenceSummary(prefs, Constants.KEY_PREFERENCE_USER_NAME, userId);
            setPreferenceSummary(prefs, Constants.KEY_PREFERENCE_PASSWORD, mPassword);

            try {
                UserDataMediator userDataMediator = new UserDataMediator(getApplicationContext());

                User user = userDataMediator.getUser(userId);

                Log.d(LOG_TAG, "UserLoginTask.. : user : " + user);

                SharedPreferences  mPrefs = getSharedPreferences(SHARED_PREF_PRIVATE_FILE, MODE_PRIVATE);

                SharedPreferences.Editor prefsEditor = mPrefs.edit();
                Gson gson = new Gson();
                String userJsonObj = gson.toJson(user);
                prefsEditor.putString(KEY_LOGGED_IN_USER_OBJ, userJsonObj);

                prefsEditor.commit();

            } catch (RetrofitError eX1) {
                if (eX1.getCause() instanceof SecuredRestException) {
                    Log.e(LOG_TAG, "Problem in loginClicked SecuredRestException", eX1);
                } else {
                    Log.e(LOG_TAG, "Problem in loginClicked", eX1);
                }
                return false;
            } catch (Exception eX2) {
                Log.e(LOG_TAG, "Unknown Exception occurred..", eX2);
                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                finish();
                //startActivity(new Intent(getApplicationContext(), LoginActivityOld.class));
                startActivity(new Intent(getApplicationContext(), UserHomeActivity.class));
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

}

