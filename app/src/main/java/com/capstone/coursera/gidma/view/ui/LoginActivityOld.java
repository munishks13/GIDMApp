package com.capstone.coursera.gidma.view.ui;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;

import retrofit.RetrofitError;
import com.capstone.coursera.gidma.R;
import com.capstone.coursera.gidma.common.Utils;
import com.capstone.coursera.gidma.client.SecuredRestException;
import com.capstone.coursera.gidma.model.mediator.VideoDataMediator;
import com.capstone.coursera.gidma.utils.Constants;
import com.capstone.coursera.gidma.utils.StorageUtilities;
import com.capstone.coursera.gidma.view.VideoListActivity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

/**
 * The activity that allows the user to provide login information.
 */
public class LoginActivityOld extends Activity{

	// A tag used for debugging with Logcat
	static final String LOG_TAG = LoginActivityOld.class.getCanonicalName();
	
	// The edit texts used
	EditText mLoginId;
	EditText mPassword;
	
	// Make sure we use maximum security to store login credentials
	static final int MAX_SECURITY = Integer.MAX_VALUE;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Setup the UI
		setContentView(R.layout.login_activity);	

		//Find the edit texts
		mLoginId = (EditText) findViewById(R.id.username);
		mPassword = (EditText) findViewById(R.id.password);
		
	}

	/**
	 * Get the file used for storing login credentials
	 */
	public static File getLoginFile (Context context) {
		return StorageUtilities.getOutputMediaFile(context, 
				StorageUtilities.MEDIA_TYPE_TEXT, 
				StorageUtilities.SECURITY_PRIVATE, 
				"login.txt");
	}
	
	/**
	 * Returns the last LoginId input into this activity, or 0 if none is set.
	 */
	public static long getLoginId(Context context) {
		// Get the output file for the login information
		File loginFile = getLoginFile(context);		// Line 59
		
		String out = null;
		
		// If it already exists, read the login ID and return it
		if (loginFile != null && loginFile.exists()) {
			try {
				Scanner sc = new Scanner(loginFile);
				out = sc.nextLine();
				sc.close();
				return Long.parseLong(out);
			} catch (Exception e) {
				// This should never really happen
				Log.e(LOG_TAG, "Unable to get LoginID from file");
			}
		}

		return 0;
	}

	/**
	 * Returns the last password input into this activity, or null if one has not been set
	 */
	public static String getPassword(Context context) {
		// Get the output file for the login information
		File loginFile = getLoginFile(context);
		
		String out = null;
		
		// If it already exists, read the login information from the file and display it
		if (loginFile != null && loginFile.exists()) {
			try {
				Scanner sc = new Scanner(loginFile);
				sc.nextLine();
				out = sc.nextLine();
				sc.close();
				return out;
			} catch (Exception e) {
				// This should never really happen
				Log.e(LOG_TAG, "Unable to get password from file.");
			}
		}

		return out;
	}

	
	/**
	 * The login button was clicked.
	 */
	public void loginClicked(View v){
		// Save the input login information in a file so that the rest of the app can access it.
		File loginFile = getLoginFile(this);
		try {
			String loginId = mLoginId.getText().toString();
			String password = mPassword.getText().toString();

	        SharedPreferences prefs = 
	        		PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			
	        setPreferenceSummary(prefs, Constants.KEY_PREFERENCE_USER_NAME, loginId);
	        setPreferenceSummary(prefs, Constants.KEY_PREFERENCE_PASSWORD, password);
		    
	        VideoDataMediator mVideoMediator = new VideoDataMediator(getApplicationContext());
	        
	        //mVideoMediator.checkValidLogin(); // check if the user can access else secured rest exception thrown.

			mVideoMediator.getVideoList();
	        
				BufferedWriter writer = new BufferedWriter(new FileWriter(loginFile));
				writer.write(loginId);
				writer.newLine();
				writer.write(password);
				writer.newLine();
				writer.close();

				Intent intent = new Intent();
				intent.setClass(this, VideoListActivity.class); 
				startActivity(intent);

		} catch (RetrofitError eX1) {
			if(eX1.getCause() instanceof SecuredRestException){
				 Utils.showToast(this,
		                    "Login failed, Please try again.");
			} else {
				Log.e(LOG_TAG, "Problem in loginClicked", eX1);
			}
		}	
		catch (Exception eX2) {
			Log.e(LOG_TAG, "Unknown Exception occured..", eX2);
		}
	}
	
	private void setPreferenceSummary(SharedPreferences prefs, String key, String value){
		
		SharedPreferences.Editor editor = prefs.edit();
		
	    if(TextUtils.equals(key, Constants.KEY_PREFERENCE_USER_NAME)){
	    	editor.putString(Constants.KEY_PREFERENCE_USER_NAME, value);
	    }else if(TextUtils.equals(key, Constants.KEY_PREFERENCE_PASSWORD)){
	    	editor.putString(Constants.KEY_PREFERENCE_PASSWORD, value);
	    }

	    editor.commit();
	    
	}
	
	
    /**
     * Hook method called to initialize the contents of the Activity's
     * standard options menu.
     * 
     * @param menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it
        // is present.
        getMenuInflater().inflate(R.menu.video_list,
                                  menu);
        return true;
    }

    /**
     * Hook method called whenever an item in your options menu is
     * selected
     * 
     * @param item
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
	
}
