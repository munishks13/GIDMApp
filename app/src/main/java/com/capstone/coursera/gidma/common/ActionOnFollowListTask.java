package com.capstone.coursera.gidma.common;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.capstone.coursera.gidma.R;
import com.capstone.coursera.gidma.activity.FollowListActivity;
import com.capstone.coursera.gidma.activity.UserHomeActivity;
import com.capstone.coursera.gidma.client.SecuredRestException;
import com.capstone.coursera.gidma.model.mediator.UserDataMediator;
import com.capstone.coursera.gidma.model.mediator.webdata.Follower;

import retrofit.RetrofitError;


public class ActionOnFollowListTask extends AsyncTask<Void, Void, Boolean> {

    // A tag used for debugging with Logcat
    static final String LOG_TAG = ActionOnFollowListTask.class.getCanonicalName();

    Context context;
    private String followerUserId;
    private String patientUserId;
    private long followerUniqueid;
    private long selectedItemId;
    private int selectedYesNoItemId;

    public ActionOnFollowListTask(Context context, long followerUniqueid, long selectedItemId, int selectedYesNoItemId, String followerUserId, String patientUserId) {
        this.context = context;
        this.followerUniqueid = followerUniqueid;
        this.selectedItemId = selectedItemId;
        this.selectedYesNoItemId = selectedYesNoItemId;
        this.followerUserId = followerUserId;
        this.patientUserId = patientUserId;
    }

    @Override
    protected Boolean doInBackground(Void... params) {

        try {
            Log.i(LOG_TAG, "Placed request for ....."
                            + " followerUniqueid : " + followerUniqueid
                            + " : selectedItemId : " + selectedItemId
                            + " : selectedYesNoItemId : " + selectedYesNoItemId
                            + " : followerUserId : " + followerUserId
                            + " : patientUserId : " + patientUserId
            );

            Log.i(LOG_TAG, "doInBackground: selectedItemId: " + context.getResources().getResourceName((int) selectedItemId)
                    + " : selectedYesNoItemId : " + context.getResources().getResourceName(selectedYesNoItemId));


            UserDataMediator userDataMediator = new UserDataMediator(context);

            if (selectedYesNoItemId == R.drawable.request_yes) {

                if (selectedItemId == R.drawable.request_to_follow) {
                    userDataMediator.placeFollowRequest(followerUserId, patientUserId);
                    Log.i(LOG_TAG, "YES is clicked placed request for .....followerUserId : " + followerUserId + " : patientUserId : " + patientUserId);
                } else if (selectedItemId == R.drawable.followers_request) {

                    Follower follower = new Follower(patientUserId, followerUserId);
                    follower.setId(followerUniqueid);
                    follower.setConfirmed(true);

                    follower.setBloodSugarLvlFasting(true);
                    follower.setBloodSugarLvlMT(true);
                    follower.setBloodSugarLvlBT(true);
                    follower.setBloodSugarLvlTime(true);
                    follower.setEatMT(true);
                    follower.setInsulin(true);
                    follower.setWhoWithYou(true);
                    follower.setWhereWereYou(true);
                    follower.setMood(true);
                    follower.setStress(true);
                    follower.setEnergyLvl(true);
                    follower.setBloodSugarLvlTimeEvent(true);

                    userDataMediator.approveFollowRequest(follower);
                }

            } else if (selectedYesNoItemId == R.drawable.request_no) {
                userDataMediator.deneyFollowRequest(String.valueOf(followerUniqueid));
                Log.i(LOG_TAG, "NO is clicked denied request for .....followerUniqueid : " + followerUniqueid);
            }

        } catch (RetrofitError eX1) {
            if (eX1.getCause() instanceof SecuredRestException) {
                Log.e(LOG_TAG, "Problem in ActionOnFollowListTask SecuredRestException", eX1);
            } else {
                Log.e(LOG_TAG, "Problem in ActionOnFollowListTask", eX1);
            }
            return false;
        } catch (Exception eX2) {
            Log.e(LOG_TAG, "ActionOnFollowListTask Unknown Exception occurred..", eX2);
            return false;
        }


        return true;
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        if (success) {
            Log.i(LOG_TAG, "onPostExecute : SUCCESS.....");
            Toast.makeText(context, "Operation Successful..", Toast.LENGTH_SHORT).show();
            context.startActivity(new Intent(context, FollowListActivity.class));
        } else {
            Log.i(LOG_TAG, "onPostExecute : FAILED....");
            Toast.makeText(context, "Operation Failed..", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }
}
