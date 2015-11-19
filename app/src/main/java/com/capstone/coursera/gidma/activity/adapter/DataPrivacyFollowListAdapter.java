package com.capstone.coursera.gidma.activity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.capstone.coursera.gidma.R;
import com.capstone.coursera.gidma.model.mediator.webdata.UserBasicInfo;

import java.util.List;

public class DataPrivacyFollowListAdapter extends BaseAdapter {

    // A tag used for debugging with Logcat
    static final String LOG_TAG = DataPrivacyFollowListAdapter.class.getCanonicalName();

    private Context context;
    private List<UserBasicInfo> followUsersList;
    private String currentUserId;

    public DataPrivacyFollowListAdapter(Context context, String userId) {
        this.context = context;
        this.currentUserId = userId;
    }

    public DataPrivacyFollowListAdapter(Context context, List<UserBasicInfo> followUsersList, String userId) {
        this.context = context;
        this.followUsersList = followUsersList;
        this.currentUserId = userId;
    }

    public void setFollowUsersList(List<UserBasicInfo> followUsersList) {
        this.followUsersList = followUsersList;
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

            followList_yesIv.setVisibility(View.INVISIBLE);
            followList_noIv.setVisibility(View.INVISIBLE);

        } else {
            followUsersListView = (View) convertView;
        }


        return followUsersListView;
    }


}
