package com.capstone.coursera.gidma.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.capstone.coursera.gidma.R;
import com.capstone.coursera.gidma.activity.adapter.DataPrivacyFollowListAdapter;
import com.capstone.coursera.gidma.activity.adapter.DropDownListAdapter;
import com.capstone.coursera.gidma.client.SecuredRestException;
import com.capstone.coursera.gidma.common.ActionOnDataPrivacyListTask;
import com.capstone.coursera.gidma.model.mediator.UserDataMediator;
import com.capstone.coursera.gidma.model.mediator.webdata.User;
import com.capstone.coursera.gidma.model.mediator.webdata.UserBasicInfo;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit.RetrofitError;

import static com.capstone.coursera.gidma.utils.Constants.KEY_LOGGED_IN_USER_OBJ;
import static com.capstone.coursera.gidma.utils.Constants.SHARED_PREF_PRIVATE_FILE;

public class DataPrivacyActivity extends AppCompatActivity {

    // A tag used for debugging with Logcat
    static final String LOG_TAG = DataPrivacyActivity.class.getCanonicalName();
    public static boolean[] checkSelected;    // store select/unselect information about the values in the list
    DataPrivacyFollowListAdapter mFollowersListForPrivacyAdapter;
    ArrayAdapter<String> mPrivacyQuestionListAdapter;
    // UI references.
    private ListView mFollowersListForPrivacyListView;
    private ListView mPrivacyQuestionListListView;
    private View mPrivacy_req_progress;
    private View mDataPrivacyForm;
    private DataPrivacyTask mDataPrivacytTask;
    private List<UserBasicInfo> mFollowersUsersList;
    private User mUserObj;
    private PopupWindow pw;
    private boolean expanded;        //to  store information whether the selected values are displayed completely or in shortened representatn

    private ArrayList<String> items;


    private Spinner mDataPrivacyFollowerDataSpinner;
    private ArrayAdapter<String> spinnerAdapter;

    private ActionOnDataPrivacyListTask mActionOnDataPrivacyListTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(LOG_TAG, "onCreate..");

        setContentView(R.layout.activity_data_privacy);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences mPrefs = this.getSharedPreferences(SHARED_PREF_PRIVATE_FILE, MODE_PRIVATE);
        Gson gson = new Gson();
        String userJsonObj = mPrefs.getString(KEY_LOGGED_IN_USER_OBJ, "");
        mUserObj = gson.fromJson(userJsonObj, User.class);


        mPrivacy_req_progress = findViewById(R.id.privacy_req_progress);
        mDataPrivacyForm = findViewById(R.id.dataPrivacyForm);


        mDataPrivacyFollowerDataSpinner = (Spinner) findViewById(R.id.dataPrivacyFollowerDataSpinner);

        spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, android.R.id.text1);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mDataPrivacyFollowerDataSpinner.setAdapter(spinnerAdapter);


       // mFollowersListForPrivacyListView = (ListView) findViewById(R.id.followersListForPrivacy);
        mPrivacyQuestionListListView = (ListView) findViewById(R.id.privacyQuestionList);


        String[] privacyQuestionArray = getResources().getStringArray(R.array.checkin_qstn_privacy_arrays);
        mPrivacyQuestionListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, privacyQuestionArray);
        mPrivacyQuestionListListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        mPrivacyQuestionListListView.setAdapter(mPrivacyQuestionListAdapter);


//        mFollowersListForPrivacyAdapter = new DataPrivacyFollowListAdapter(this, mUserObj.getUserId());
//        mFollowersListForPrivacyListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
//        mFollowersListForPrivacyListView.setAdapter(mFollowersListForPrivacyAdapter);


        Button dataPrivacyBtn = (Button) findViewById(R.id.setDataPrivacyBtn);
        dataPrivacyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mActionOnDataPrivacyListTask != null){
                    Toast.makeText(getApplicationContext(), "Request in Progress..", Toast.LENGTH_SHORT).show();
                    return;
                }

                String selectedUserId = String.valueOf(mDataPrivacyFollowerDataSpinner.getSelectedItem());

//                mActionOnDataPrivacyListTask = new ActionOnDataPrivacyListTask(getApplicationContext());
//                mActionOnDataPrivacyListTask.execute((Void) null);

                startActivity(new Intent(getApplicationContext(), UserHomeActivity.class));
            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();
        getFollowersList();
    }

    public void getFollowersList() {
        Log.i(LOG_TAG, "DataPrivacyActivity: getFollowersList : ");

        if (mDataPrivacytTask != null) {
            return;
        }

        //mFollowersListForPrivacyAdapter =


        showProgress(true);
        mDataPrivacytTask = new DataPrivacyTask(mUserObj.getUserId());
        mDataPrivacytTask.execute((Void) null);
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

            mDataPrivacyForm.setVisibility(show ? View.GONE : View.VISIBLE);
            mDataPrivacyForm.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mDataPrivacyForm.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mPrivacy_req_progress.setVisibility(show ? View.VISIBLE : View.GONE);
            mPrivacy_req_progress.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mPrivacy_req_progress.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mPrivacy_req_progress.setVisibility(show ? View.VISIBLE : View.GONE);
            mDataPrivacyForm.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /*
     * Function to set up initial settings: Creating the data source for drop-down list, initialising the checkselected[], set the drop-down list
     * */
    private void initialize() {
//        //data source for drop-down list
//        final ArrayList<String> items = new ArrayList<String>();
//        items.add("Item 1");
//        items.add("Item 2");
//        items.add("Item 3");
//        items.add("Item 4");
//        items.add("Item 5");

        checkSelected = new boolean[items.size()];

        Log.i(LOG_TAG, "initialize : " + items);

        //initialize all values of list to 'unselected' initially
        for (int i = 0; i < checkSelected.length; i++) {
            checkSelected[i] = false;
        }

        Log.i(LOG_TAG, "checkSelected : " + checkSelected);

	/*SelectBox is the TextView where the selected values will be displayed in the form of "Item 1 & 'n' more".
         * When this selectBox is clicked it will display all the selected values
    	 * and when clicked again it will display in shortened representation as before.
    	 * */
        final TextView tv = (TextView) findViewById(R.id.privacySelectBox);
        tv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (!expanded) {
                    //display all selected values
                    String selected = "";
                    int flag = 0;
                    for (int i = 0; i < items.size(); i++) {
                        if (checkSelected[i] == true) {
                            selected += items.get(i);
                            selected += ", ";
                            flag = 1;
                        }
                    }
                    if (flag == 1)
                        tv.setText(selected);
                    expanded = true;
                } else {
                    //display shortened representation of selected values
                    tv.setText(DropDownListAdapter.getSelected());
                    expanded = false;
                }
            }
        });

        //onClickListener to initiate the dropDown list
        Button createButton = (Button) findViewById(R.id.create);
        createButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                initiatePopUp(items, tv);
            }
        });
    }

    /*
     * Function to set up the pop-up window which acts as drop-down list
     * */
    private void initiatePopUp(ArrayList<String> items, TextView tv) {
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //get the pop-up window i.e.  drop-down layout
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.pop_up_window, (ViewGroup) findViewById(R.id.PopUpView));

        //get the view to which drop-down layout is to be anchored
        RelativeLayout layout1 = (RelativeLayout) findViewById(R.id.dataPrivacyForm);
        pw = new PopupWindow(layout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

        //Pop-up window background cannot be null if we want the pop-up to listen touch events outside its window
        pw.setBackgroundDrawable(new BitmapDrawable());
        pw.setTouchable(true);

        //let pop-up be informed about touch events outside its window. This  should be done before setting the content of pop-up
        pw.setOutsideTouchable(true);
        pw.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

        //dismiss the pop-up i.e. drop-down when touched anywhere outside the pop-up
        pw.setTouchInterceptor(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    pw.dismiss();
                    return true;
                }
                return false;
            }
        });

        //provide the source layout for drop-down
        pw.setContentView(layout);

        //anchor the drop-down to bottom-left corner of 'layout1'
        pw.showAsDropDown(layout1);

        //populate the drop-down list
        final ListView list = (ListView) layout.findViewById(R.id.privacyFollowerDropDownList);
        DropDownListAdapter adapter = new DropDownListAdapter(this, items, tv);
        list.setAdapter(adapter);
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class DataPrivacyTask extends AsyncTask<Void, Void, Boolean> {

        private final String userId;
        private List<UserBasicInfo> followUsersList;

        DataPrivacyTask(String userId) {
            this.userId = userId;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                Log.i(LOG_TAG, "doInBackground:  :");

                UserDataMediator userDataMediator = new UserDataMediator(getApplicationContext());

                followUsersList = userDataMediator.getFollowersList();

            } catch (RetrofitError eX1) {
                if (eX1.getCause() instanceof SecuredRestException) {
                    Log.e(LOG_TAG, "Problem in DataPrivacyTask SecuredRestException", eX1);
                } else {
                    Log.e(LOG_TAG, "Problem in DataPrivacyTask", eX1);
                }
                return false;
            } catch (Exception eX2) {
                Log.e(LOG_TAG, "DataPrivacyTask Unknown Exception occurred..", eX2);
                return false;
            }


            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mDataPrivacytTask = null;
            showProgress(false);

            if (success) {
                if (followUsersList != null && !followUsersList.isEmpty()) {
//                    mFollowersListForPrivacyAdapter.setFollowUsersList(followUsersList);
//                    mFollowersListForPrivacyAdapter.notifyDataSetChanged();


                    items = new ArrayList<String>();

                    if (followUsersList != null && !followUsersList.isEmpty()) {
                        for (UserBasicInfo userBasicInfo : followUsersList){
//                            items.add(userBasicInfo.getUserId() + " - " +  userBasicInfo.getLastName() + ", " + userBasicInfo.getFirstName());
                            spinnerAdapter.add(userBasicInfo.getUserId() + " - " +  userBasicInfo.getLastName() + ", " + userBasicInfo.getFirstName());

                        }

                        spinnerAdapter.notifyDataSetChanged();

                    }

                   // initialize();
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
            mDataPrivacytTask = null;
            showProgress(false);
        }

    }

}
