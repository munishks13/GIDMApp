package com.capstone.coursera.gidma.model.mediator.webdata;

import static com.capstone.coursera.gidma.model.mediator.webdata.ServicePathConstants.*;


import java.util.List;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

/**
 * This interface defines an API for a User Service. The
 * interface is used to provide a contract for client/server
 * interactions. The interface is annotated with Retrofit
 * annotations so that clients can automatically convert the
 */
public interface UserServiceProxy {

    @POST(USER_SVC_SIGN_UP_PATH)
    public User signUpUser(@Body User userForSignUp);

    @GET(USER_SVC_INFO_PATH)
    public User getUser(@Path("userId") String userId);

    @GET(USER_SVC_ALL_USER_LIST_PATH)
    public List<User> allUserList();

    @POST(CHECKIN_SVC_PATH)
    public UserCheckIn userCheckIn(@Body UserCheckIn userCheckIn);

    @POST(CHECKIN_SVC_GET_INFO)
    public List<UserCheckIn> getUserCheckInInfo(@Body CustomQueryParams queryParams) throws IllegalAccessException;

    @POST(USER_SVC_FOLLOW_REQ)
    public Follower placeFollowRequest(@Path("followerUserId") String followerUserId, @Path("patientUserId") String patientUserId);

    @POST(USER_SVC_APPROVE_FOLLOW_REQ)
    public Follower approveFollowRequest(@Body Follower follower);

    @POST(USER_SVC_DENY_FOLLOW_REQ)
    public String denyFollowerReqId(@Path("deniedFollowerReqId") String deniedFollowerReqId);

    @POST(USER_SVC_APPROVE_MULTI_FOLLOW_REQ)
    public List<Follower> approveOrUpdateMultipleFollowRequest(@Body List<Follower> followersList, @Path("userId") String userId);

    @GET(USER_SVC_PATIENT_USER_LIST_PATH)
    public List<User> getPatientUsersList();

    @GET(USER_SVC_FOLLOWERS_LIST_PATH)
    public List<UserBasicInfo> getFollowersList();

    @GET(USER_SVC_FOLLOWING_LIST_PATH)
    public List<UserBasicInfo> getFollowingList();

    @GET(USER_SVC_PATIENT_USER_FOR_FOLLOW_LIST)
    public List<UserBasicInfo> getPatientUserForFollowList();

    @GET(CHECKIN_SVC_QSTNS)
    public List<CheckInQuestion> getCheckInQuestions();

    @GET(CHECKIN_SVC_BSLTE)
    public List<BloodSugarLevelTimeEvents> getBloodSugarLevelTimeEvents();


}
