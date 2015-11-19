package com.capstone.coursera.gidma.utils;

import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

/**
 * Class that contains all the Constants required in our client App.
 */
public class Constants {
    /**
     * Base URL of the WebService.  Please Read the Instructions in
     * README.md to set up the SERVER_URL.
     */
    public static final String SERCURE_SERVER_URL = "https://192.168.1.141:8443";


    public static final String UNSERCURE_SERVER_URL = "http://192.168.1.141:8080";

    public static final String DATE_FORMAT_1 = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    public static final String DATE_FORMAT_2 = "yyyy-MM-dd";

    public static final SimpleDateFormat SDF_1 = new SimpleDateFormat(DATE_FORMAT_1);
    public static final SimpleDateFormat SDF_2 = new SimpleDateFormat(DATE_FORMAT_2);


    /**
     * Define a constant for 1 MB.
     */
    public static final long MEGA_BYTE = 1024 * 1024;


    /**
     * Maximum size of Video to be uploaded in MB.
     */
    public static final long MAX_SIZE_MEGA_BYTE = 50 * MEGA_BYTE;


    public static final String KEY_PREFERENCE_USER_NAME =
            "pref_key_username";

    public static final String KEY_PREFERENCE_PASSWORD =
            "pref_key_password";

    public static final String KEY_PREFERENCE_FEEDBACK_GRAPHUSER_NAME =
            "pref_key_feedback_graph_username";

    public static final String KEY_PREFERENCE_USER_SELECTED_ID =
            "pref_key_user_selected_id";
    public static final String KEY_PREFERENCE_USER_SELECTED_ID_LABEL =
            "pref_key_user_selected_id_label";

    public static final String SHARED_PREF_PRIVATE_FILE = "PrivatePerfFile";

    public static final String KEY_LOGGED_IN_USER_OBJ = "KEY_LOGGED_IN_USER_OBJ";


    public static final String DATE_FORMAT_MMDDYYY_REGEX = "^(1[0-2]|0[1-9])/(3[01]|[12][0-9]|0[1-9])/[0-9]{4}$";

    public static final Pattern pattern_MMDDYYYY = Pattern.compile(DATE_FORMAT_MMDDYYY_REGEX);


}
