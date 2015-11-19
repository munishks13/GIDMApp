package com.capstone.coursera.gidma.activity.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.capstone.coursera.gidma.R;
import com.capstone.coursera.gidma.activity.FeedbackActivity;
import com.capstone.coursera.gidma.common.ActionOnFollowListTask;
import com.capstone.coursera.gidma.model.mediator.webdata.UserBasicInfo;
import com.capstone.coursera.gidma.utils.Constants;

import java.util.List;

public class FollowListAdapter extends BaseAdapter {

    // A tag used for debugging with Logcat
    static final String LOG_TAG = FollowListAdapter.class.getCanonicalName();

    private Context context;
    private List<UserBasicInfo> followUsersList;
    private String currentUserId;
    private long selectedItemId;


    public FollowListAdapter(Context context, String userId) {
        this.context = context;
        this.currentUserId = userId;
    }

    public FollowListAdapter(Context context, List<UserBasicInfo> followUsersList, String userId) {
        this.context = context;
        this.followUsersList = followUsersList;
        this.currentUserId = userId;
    }

    public void setFollowUsersList(List<UserBasicInfo> followUsersList) {
        this.followUsersList = followUsersList;
    }

    public void setSelectedItemId(long selectedItemId) {
        this.selectedItemId = selectedItemId;
    }

    @Override
    public int getCount() {
        if (followUsersList != null && !followUsersList.isEmpty()) {
            return followUsersList.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (followUsersList != null && !followUsersList.isEmpty()) {
            return followUsersList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
//        if (followUsersList != null && !followUsersList.isEmpty()) {
//            return followUsersList.get(position).getFollowerUniqueid();
//        }
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View followUsersListView;

        if (convertView == null) {

            followUsersListView = new View(context);

            // get layout
            followUsersListView = inflater.inflate(R.layout.follow_list_custom, null);

            UserBasicInfo userBasicInfo = followUsersList.get(position);

            // set value into textview
            TextView followListUserIdTv = (TextView) followUsersListView.findViewById(R.id.followListUserId);
            TextView followListUserNameTv = (TextView) followUsersListView.findViewById(R.id.followListUserName);

            followListUserIdTv.setText(userBasicInfo.getUserId());
            followListUserNameTv.setText(userBasicInfo.getLastName() + ", " + userBasicInfo.getFirstName());

            ImageView followList_yesIv = (ImageView) followUsersListView.findViewById(R.id.followList_yes);
            ImageView followList_noIv = (ImageView) followUsersListView.findViewById(R.id.followList_no);


            if (selectedItemId == R.drawable.followers || selectedItemId == R.drawable.following_list || selectedItemId == R.drawable.waiting_following_request) {
                followList_yesIv.setVisibility(View.INVISIBLE);
                followList_noIv.setImageResource(R.drawable.request_no);
            } else if (selectedItemId == R.drawable.followers_request) {
                followList_yesIv.setImageResource(R.drawable.request_yes);
                followList_noIv.setImageResource(R.drawable.request_no);
            } else if (selectedItemId == R.drawable.request_to_follow) {
                followList_yesIv.setImageResource(R.drawable.follow_this);
                followList_noIv.setVisibility(View.INVISIBLE);
            }


            if (selectedItemId == R.drawable.following_list) { // special case to see the feed back of the following users
                followList_yesIv.setVisibility(View.VISIBLE);
                followList_yesIv.setImageResource(R.drawable.feedback);
            }


            followList_yesIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Log.i(LOG_TAG, "YES is clicked....." + v.getId());
//                    Log.i(LOG_TAG, "YES is clicked.....position : " + position);
//                    Log.i(LOG_TAG, "YES is clicked.....getItem(position) : " + getItem(position));

                    UserBasicInfo info = (UserBasicInfo) getItem(position);

                    Log.i(LOG_TAG, "YES is clicked  for .....currentUserId : " + currentUserId + " : UserBasicInfo : " + info);

                    if (selectedItemId == R.drawable.following_list) {
                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString(Constants.KEY_PREFERENCE_FEEDBACK_GRAPHUSER_NAME, info.getUserId());
                        editor.commit();

                        context.startActivity(new Intent(context, FeedbackActivity.class));
                    } else {
                        ActionOnFollowListTask asyncTask = new ActionOnFollowListTask(context, info.getFollowerUniqueid(),
                                selectedItemId, R.drawable.request_yes, currentUserId, info.getUserId());
                        asyncTask.execute((Void) null);
                    }
                }
            });


            followList_noIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Log.i(LOG_TAG, "YES is clicked....." + v.getId());
//                    Log.i(LOG_TAG, "YES is clicked.....position : " + position);
//                    Log.i(LOG_TAG, "YES is clicked.....getItem(position) : " + getItem(position));

                    UserBasicInfo info = (UserBasicInfo) getItem(position);

                    ActionOnFollowListTask asyncTask = new ActionOnFollowListTask(context, info.getFollowerUniqueid(),
                            selectedItemId, R.drawable.request_no, currentUserId, info.getUserId());
                    asyncTask.execute((Void) null);

                    Log.i(LOG_TAG, "NO is clicked denied request for .....followerUniqueid : " + info.getFollowerUniqueid());

                }
            });


        } else {
            followUsersListView = (View) convertView;
        }


        return followUsersListView;
    }


}
