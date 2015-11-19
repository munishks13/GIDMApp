package com.capstone.coursera.gidma.model.mediator;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.capstone.coursera.gidma.client.SecuredRestBuilder;
import com.capstone.coursera.gidma.client.UnsafeHttpsClient;
import com.capstone.coursera.gidma.model.mediator.webdata.AverageVideoRating;
import com.capstone.coursera.gidma.model.mediator.webdata.CheckInQuestion;
import com.capstone.coursera.gidma.model.mediator.webdata.CustomQueryParams;
import com.capstone.coursera.gidma.model.mediator.webdata.Follower;
import com.capstone.coursera.gidma.model.mediator.webdata.ServicePathConstants;
import com.capstone.coursera.gidma.model.mediator.webdata.User;
import com.capstone.coursera.gidma.model.mediator.webdata.UserBasicInfo;
import com.capstone.coursera.gidma.model.mediator.webdata.UserCheckIn;
import com.capstone.coursera.gidma.model.mediator.webdata.UserServiceProxy;
import com.capstone.coursera.gidma.utils.Constants;
import com.google.gson.GsonBuilder;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

/**
 * Mediates communication between the User Service and the local
 * storage on the Android device.  The methods in this class block, so
 * they are called from a background thread (e.g., via an
 * AsyncTask).
 */
public class UserDataMediator {

    /**
     * Status code to indicate that file is successfully
     * uploaded.
     */
    public static final String STATUS_UPLOAD_SUCCESSFUL =
            "Upload succeeded";
    /**
     * Status code to indicate that file upload failed
     * due to large video size.
     */
    public static final String STATUS_UPLOAD_ERROR_FILE_TOO_LARGE =
            "Upload failed: File too big";
    /**
     * Status code to indicate that file upload failed.
     */
    public static final String STATUS_UPLOAD_ERROR =
            "Upload failed";

    // A tag used for debugging with Logcat
    static final String LOG_TAG = UserDataMediator.class.getCanonicalName();
    /**
     * Defines methods that communicate with the User Service.
     */
    private UserServiceProxy mUserServiceProxy;

    private AverageVideoRating averageVideoRating;

    /**
     * Constructor that initializes the UserDataMediator.
     *
     * @param context
     */
    public UserDataMediator(Context context) {
        // Initialize the UserServiceProxy.

        SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(context);

        String userName = prefs.getString(Constants.KEY_PREFERENCE_USER_NAME, "");
        String password = prefs.getString(Constants.KEY_PREFERENCE_PASSWORD, "");

        Log.v(LOG_TAG, "UserDataMediator: userName : " + userName);


        if(mUserServiceProxy == null) {

            mUserServiceProxy = new SecuredRestBuilder()
                    .setLoginEndpoint(
                            Constants.SERCURE_SERVER_URL
                                    + ServicePathConstants.TOKEN_PATH)
                    .setUsername(userName)
                    .setPassword(password)
                    .setClientId("mobile")
                    .setClient(new OkClient(UnsafeHttpsClient.getUnsafeOkHttpClient()))
                    .setEndpoint(Constants.SERCURE_SERVER_URL)
                    .setConverter(new GsonConverter(new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create()))
                    .setLogLevel(RestAdapter.LogLevel.FULL).build()
                    .create(UserServiceProxy.class);

            Log.v(LOG_TAG, "UserDataMediator: mUserServiceProxy Creation Done... mUserServiceProxy : " + mUserServiceProxy);
        } else {
            Log.v(LOG_TAG, "UserDataMediator: mUserServiceProxy Already got one... mUserServiceProxy : " + mUserServiceProxy);
        }
    }

    public User signUpUser(User userForSignUp) {
        Log.v(LOG_TAG, "signUpUser: Going to Sign up for User : userForSignUp : " + userForSignUp);
        User signedUpUser = null;
        try {

            UserServiceProxy signUpUserServiceProxy = new SecuredRestBuilder()
                    .setLoginEndpoint(
                            Constants.SERCURE_SERVER_URL
                                    + ServicePathConstants.TOKEN_PATH)
                    .setUsername("signUpUser")
                    .setPassword("signUpUserPass")
                    .setClientId("mobile")
                    .setClient(new OkClient(UnsafeHttpsClient.getUnsafeOkHttpClient()))
                    .setEndpoint(Constants.SERCURE_SERVER_URL)
                    .setLogLevel(RestAdapter.LogLevel.FULL).build()
                    .create(UserServiceProxy.class);

            signedUpUser = signUpUserServiceProxy.signUpUser(userForSignUp);

        } catch (Exception eX) {
            eX.printStackTrace();
        }

        Log.v(LOG_TAG, "signUpUser: Finished Sign up for User : signedUpUser : " + signedUpUser);
        return signedUpUser;
    }


    public List<CheckInQuestion> geCheckInQuestionList(){
        return mUserServiceProxy.getCheckInQuestions();
    }

    public List<User> getPatientUsersList(){
        return mUserServiceProxy.getPatientUsersList();
    }

    public UserCheckIn userCheckIn(UserCheckIn userCheckIn){
        return mUserServiceProxy.userCheckIn(userCheckIn);
    }

    public User getUser(String userId){
        return mUserServiceProxy.getUser(userId);
    }

    public List<UserBasicInfo> getFollowersList(){
        return mUserServiceProxy.getFollowersList();
    }

    public List<UserBasicInfo> getFollowingList(){
        return mUserServiceProxy.getFollowingList();
    }

    public List<UserBasicInfo> getPatientUserForFollowList(){
        return mUserServiceProxy.getPatientUserForFollowList();
    }


    public Follower placeFollowRequest(String followerUserId, String patientUserId){
        Log.i(LOG_TAG, "placeFollowRequest : followerUserId : " + followerUserId + " : patientUserId : " + patientUserId);
        return mUserServiceProxy.placeFollowRequest(followerUserId, patientUserId);
    }


    public String deneyFollowRequest(String denyFollowerReqId){
        return mUserServiceProxy.denyFollowerReqId(denyFollowerReqId);
    }


    public Follower approveFollowRequest(Follower follower){
        return mUserServiceProxy.approveFollowRequest(follower);
    }

    public List<Follower> approveOrUpdateMultipleFollowRequest(List<Follower> followersList, String userId){
        return  mUserServiceProxy.approveOrUpdateMultipleFollowRequest(followersList, userId);
    }

    public List<UserCheckIn> getUserCheckInInfo(CustomQueryParams queryParams) throws IllegalAccessException{
        return mUserServiceProxy.getUserCheckInInfo(queryParams);
    }

}
