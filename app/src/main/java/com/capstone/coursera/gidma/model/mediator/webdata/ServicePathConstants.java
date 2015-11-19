package com.capstone.coursera.gidma.model.mediator.webdata;


public class ServicePathConstants {

    public static final String TOKEN_PATH = "/oauth/token";

    // The path where we expect the Services to live
    public static final String USER_SVC_PATH = "/user";

    public static final String USER_SVC_SIGN_UP_PATH = USER_SVC_PATH + "/signUpUser";

    public static final String USER_SVC_ALL_USER_LIST_PATH = USER_SVC_PATH + "/allUserList";

    public static final String USER_SVC_PATIENT_USER_LIST_PATH = USER_SVC_PATH + "/patientUsersList";

    public static final String USER_SVC_FOLLOWERS_LIST_PATH = USER_SVC_PATH + "/followersList";

    public static final String USER_SVC_FOLLOWING_LIST_PATH = USER_SVC_PATH + "/followingList";

    public static final String USER_SVC_PATIENT_USER_FOR_FOLLOW_LIST = USER_SVC_PATH + "/patientUserForFollowList";

    public static final String USER_SVC_INFO_PATH = USER_SVC_PATH + "/{userId}";

    public static final String USER_SVC_FOLLOW_REQ = USER_SVC_PATH + "/{followerUserId}/{patientUserId}/followRequest";

    public static final String USER_SVC_APPROVE_FOLLOW_REQ = USER_SVC_PATH + "/appproveRequest";

    public static final String USER_SVC_DENY_FOLLOW_REQ = USER_SVC_PATH + "/denyRequest/{deniedFollowerReqId}";

    public static final String USER_SVC_APPROVE_MULTI_FOLLOW_REQ = USER_SVC_INFO_PATH + "/appproveOrUpdateMultipleRequest";

    public static final String CHECKIN_SVC_PATH = USER_SVC_PATH + "/checkIn";

    public static final String CHECKIN_SVC_GET_INFO = USER_SVC_PATH + "/getCheckInInfo";

    public static final String CHECKIN_SVC_QSTNS = USER_SVC_PATH + "/checkInQuestions";

    public static final String CHECKIN_SVC_BSLTE = USER_SVC_PATH + "/bloodSugarLevelTimeEvents";

    public static final String LOGIN_CHECK = "/login";
}
