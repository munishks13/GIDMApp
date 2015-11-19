package com.capstone.coursera.gidma.activity.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.capstone.coursera.gidma.R;
import com.capstone.coursera.gidma.model.mediator.webdata.User;
import com.google.gson.Gson;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.capstone.coursera.gidma.utils.Constants.KEY_LOGGED_IN_USER_OBJ;
import static com.capstone.coursera.gidma.utils.Constants.SHARED_PREF_PRIVATE_FILE;

public class UserHomeImageAdapter extends BaseAdapter {

    // A tag used for debugging with Logcat
    static final String LOG_TAG = UserHomeImageAdapter.class.getCanonicalName();

    //private final String[] userHomeSelectionValues;
    public final Map<Integer, String> iconDrawAndTextMap = new HashMap<Integer, String>();

    private Context context;

    private List<Integer> homeScreenIconList;
    private List<Integer> userApecificHomeScreenIconList;

    public UserHomeImageAdapter(Context context, List<Integer> homeScreenIconList) {
        this.context = context;
        this.homeScreenIconList = homeScreenIconList;
        this.userApecificHomeScreenIconList = new ArrayList<>();


        iconDrawAndTextMap.put(R.drawable.checkin, context.getString(R.string.checkIn));
        iconDrawAndTextMap.put(R.drawable.data_privacy, context.getString(R.string.dataPrivacy));
        iconDrawAndTextMap.put(R.drawable.feedback, context.getString(R.string.feedback));

        iconDrawAndTextMap.put(R.drawable.followers, context.getString(R.string.followerList));
        iconDrawAndTextMap.put(R.drawable.followers_request, context.getString(R.string.followerRequest));

        iconDrawAndTextMap.put(R.drawable.following_list, context.getString(R.string.following_list));
        iconDrawAndTextMap.put(R.drawable.waiting_following_request, context.getString(R.string.waiting_following_request));

        iconDrawAndTextMap.put(R.drawable.request_to_follow, context.getString(R.string.requestToFollow));
    }

    @Override
    public int getCount() {
        int count = homeScreenIconList.size();
        Log.i(LOG_TAG, "getCount -- count: " + count);
        return homeScreenIconList.size();
    }

    @Override
    public Object getItem(int position) {
        Object obj = homeScreenIconList.get(position);
        Log.i(LOG_TAG, "getItem -- obj: " + obj + " : position : " + position);
        return obj;
    }

    @Override
    public long getItemId(int position) {
        long iteamId = homeScreenIconList.get(position);
        Log.i(LOG_TAG, "getItem -- iteamId: " + iteamId + " : position : " + position);
        return iteamId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View gridView;

        SharedPreferences mPrefs = context.getSharedPreferences(SHARED_PREF_PRIVATE_FILE, context.MODE_PRIVATE);
        Gson gson = new Gson();
        String userJsonObj = mPrefs.getString(KEY_LOGGED_IN_USER_OBJ, "");
        User userObj = gson.fromJson(userJsonObj, User.class);

        if (convertView == null) {

            gridView = new View(context);

            // get layout
            gridView = inflater.inflate(R.layout.user_home_grid_custom, null);

            Integer icon = homeScreenIconList.get(position);

            boolean addIcon = true;

            if (userObj != null && StringUtils.isBlank(userObj.getMedicalRecordNumber())) {
                // so he is follower. so do not display some icons.
                if (icon == R.drawable.checkin
                        || icon == R.drawable.data_privacy
                        || icon == R.drawable.feedback
                        || icon == R.drawable.followers
                        || icon == R.drawable.followers_request
                        ) {
                    addIcon = false;
                }
            }

            Log.i(LOG_TAG, "getView --icon..." + icon + " : addIcon : " + addIcon);

            if (addIcon) {
                userApecificHomeScreenIconList.add(icon);

                // set value into textview
                TextView textView = (TextView) gridView.findViewById(R.id.grid_item_label);
                textView.setText(iconDrawAndTextMap.get(icon));

                // set image based on selected text
                ImageView imageView = (ImageView) gridView.findViewById(R.id.grid_item_image);
                imageView.setImageResource(icon);


                Log.i(LOG_TAG, "getView --icon..." + icon + " : Icon or Image File Name... : " + context.getResources().getResourceName(icon));
            } else {
                gridView.setVisibility(View.GONE);
            }

        } else {
            gridView = (View) convertView;
        }

        return gridView;


    }
}
