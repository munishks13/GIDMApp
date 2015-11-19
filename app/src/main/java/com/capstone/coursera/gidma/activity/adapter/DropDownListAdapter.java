package com.capstone.coursera.gidma.activity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.capstone.coursera.gidma.R;
import com.capstone.coursera.gidma.activity.DataPrivacyActivity;

import java.util.ArrayList;

public class DropDownListAdapter extends BaseAdapter {

    private static int selectedCount = 0;
    private static String firstSelected = "";
    private static String selected = "";    //shortened selected values representation
    private ArrayList<String> mListItems;
    private LayoutInflater mInflater;
    private TextView mSelectedItems;
    private ViewHolder holder;

    public DropDownListAdapter(Context context, ArrayList<String> items,
                               TextView tv) {
        mListItems = new ArrayList<String>();
        mListItems.addAll(items);
        mInflater = LayoutInflater.from(context);
        mSelectedItems = tv;
    }

    public static String getSelected() {
        return selected;
    }

    public void setSelected(String selected) {
        DropDownListAdapter.selected = selected;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mListItems.size();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.drop_down_list_row, null);
            holder = new ViewHolder();
            holder.tv = (TextView) convertView.findViewById(R.id.privacyFollowerSelectOption);
            holder.chkbox = (CheckBox) convertView.findViewById(R.id.privacyFollowercheckbox);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tv.setText(mListItems.get(position));

        final int position1 = position;

        //whenever the checkbox is clicked the selected values textview is updated with new selected values
        holder.chkbox.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                setText(position1);
            }
        });

        if (DataPrivacyActivity.checkSelected[position])
            holder.chkbox.setChecked(true);
        else
            holder.chkbox.setChecked(false);
        return convertView;
    }


    /*
     * Function which updates the selected values display and information(checkSelected[])
     * */
    private void setText(int position1) {
        if (!DataPrivacyActivity.checkSelected[position1]) {
            DataPrivacyActivity.checkSelected[position1] = true;
            selectedCount++;
        } else {
            DataPrivacyActivity.checkSelected[position1] = false;
            selectedCount--;
        }

        if (selectedCount == 0) {
            mSelectedItems.setText(R.string.followerList);
        } else if (selectedCount == 1) {
            for (int i = 0; i < DataPrivacyActivity.checkSelected.length; i++) {
                if (DataPrivacyActivity.checkSelected[i] == true) {
                    firstSelected = mListItems.get(i);
                    break;
                }
            }
            mSelectedItems.setText(firstSelected);
            setSelected(firstSelected);
        } else if (selectedCount > 1) {
            for (int i = 0; i < DataPrivacyActivity.checkSelected.length; i++) {
                if (DataPrivacyActivity.checkSelected[i] == true) {
                    firstSelected = mListItems.get(i);
                    break;
                }
            }
            mSelectedItems.setText(firstSelected + " & " + (selectedCount - 1) + " more");
            setSelected(firstSelected + " & " + (selectedCount - 1) + " more");
        }
    }

    private class ViewHolder {
        TextView tv;
        CheckBox chkbox;
    }
}
