package com.capstone.coursera.gidma.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import com.capstone.coursera.gidma.R;
import com.capstone.coursera.gidma.client.SecuredRestException;
import com.capstone.coursera.gidma.model.mediator.UserDataMediator;
import com.capstone.coursera.gidma.model.mediator.webdata.CustomQueryParams;
import com.capstone.coursera.gidma.model.mediator.webdata.UserCheckIn;
import com.capstone.coursera.gidma.utils.Constants;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TreeSet;

import retrofit.RetrofitError;

public class FeedbackActivity extends AppCompatActivity {

    // A tag used for debugging with Logcat
    static final String LOG_TAG = FeedbackActivity.class.getCanonicalName();

    public static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd");
    public static final SimpleDateFormat SDF_1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    private View mFeedback_data_progressView;
    private View mFeedbackFormView;

    private String mLoggedInUserId;
    private String mFeedbackForUserId;

    private FeedbackTask mFeedbackTask;

    private GraphView mGraphView;

    private DatePicker dpFrom;
    private DatePicker dpTo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        mLoggedInUserId = prefs.getString(Constants.KEY_PREFERENCE_USER_NAME, "");

        mFeedbackForUserId = prefs.getString(Constants.KEY_PREFERENCE_FEEDBACK_GRAPHUSER_NAME, "");

        if(StringUtils.isBlank(mFeedbackForUserId)){
            mFeedbackForUserId = mLoggedInUserId;
        }

        mFeedback_data_progressView = findViewById(R.id.feedback_data_progress);
        mFeedbackFormView = findViewById(R.id.feedbackForm);


        // we get graph view instance
        mGraphView = (GraphView) findViewById(R.id.feedback_graph);

        dpFrom = (DatePicker) findViewById(R.id.fromDatePicker);
        dpTo = (DatePicker) findViewById(R.id.toDatePicker);

    }

    public void getFeedBackData(View view) {
        Log.i(LOG_TAG, "getFeedBackData --Started...");

        if (mFeedbackTask != null) {
            return;
        }

        CustomQueryParams customQueryParams = new CustomQueryParams();

        Calendar calFrom = new GregorianCalendar();
        calFrom.set(dpFrom.getYear(), dpFrom.getMonth(), dpFrom.getDayOfMonth());

        Calendar calTo = new GregorianCalendar();
        calTo.set(dpTo.getYear(), dpTo.getMonth(), dpTo.getDayOfMonth());

        customQueryParams.setUserId(mFeedbackForUserId);
        customQueryParams.setFromDate(SDF.format(calFrom.getTime()));
        customQueryParams.setToDate(SDF.format(calTo.getTime()));

        showProgress(true);
        mFeedbackTask = new FeedbackTask(customQueryParams);
        mFeedbackTask.execute((Void) null);
    }


    @Override
    protected void onResume() {
        super.onResume();

        setTitle(mFeedbackForUserId + "'s Feedback");
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

            mFeedbackFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mFeedbackFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mFeedbackFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mFeedback_data_progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mFeedback_data_progressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mFeedback_data_progressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mFeedback_data_progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mFeedbackFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Represents an asynchronous task to fetch check in data.
     */
    public class FeedbackTask extends AsyncTask<Void, Void, Boolean> {

        private final CustomQueryParams customQueryParams;

        List<UserCheckIn> userCheckInDataList = new ArrayList<UserCheckIn>();

        FeedbackTask(CustomQueryParams customQueryParams) {
            this.customQueryParams = customQueryParams;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                UserDataMediator userDataMediator = new UserDataMediator(getApplicationContext());

                userCheckInDataList = userDataMediator.getUserCheckInInfo(customQueryParams);

                if (userCheckInDataList != null) {
                    Log.i(LOG_TAG, "FeedbackTask in doInBackground : userCheckInDataList SIZE" + userCheckInDataList.size());
                } else {
                    Log.i(LOG_TAG, "FeedbackTask in doInBackground : userCheckInDataList is NULL");
                }


            } catch (RetrofitError eX1) {
                if (eX1.getCause() instanceof SecuredRestException) {
                    Log.e(LOG_TAG, "SecuredRestException in doInBackground", eX1);
                } else {
                    Log.e(LOG_TAG, "Problem in doInBackground", eX1);
                }
            } catch (Exception eX2) {
                Log.e(LOG_TAG, "checkInData : Unknown Exception occured..", eX2);
            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mFeedbackTask = null;
            showProgress(false);

            List<Date> dateList = new ArrayList<Date>();

            if (success) {

                LineGraphSeries<DataPoint> bloodSugarLvlFastingSeries = new LineGraphSeries<DataPoint>();
                LineGraphSeries<DataPoint> bloodSugarLvlMTSeries = new LineGraphSeries<DataPoint>();
                LineGraphSeries<DataPoint> bloodSugarLvlBTSeries = new LineGraphSeries<DataPoint>();

                if (userCheckInDataList != null && !userCheckInDataList.isEmpty()) {

                    List<DataPoint> bloodSugarLvlFastingDataPointsList = new ArrayList<>();
                    List<DataPoint> bloodSugarLvlMTDataPointsList = new ArrayList<>();
                    List<DataPoint> bloodSugarLvlBTDataPointsList = new ArrayList<>();

                    int countFasting = 0;
                    int countMT = 0;
                    int countBT = 0;

                    try {
                        for (UserCheckIn userCheckInInfo : userCheckInDataList) {
                            Date checkinDate = SDF_1.parse(SDF_1.format(userCheckInInfo.getCheckInDateTime()));

                            Log.i(LOG_TAG, "FeedbackTask in onPostExecute : checkinDate is ==>: " + checkinDate);
                            dateList.add(checkinDate);

                            if (StringUtils.isNoneBlank(userCheckInInfo.getBloodSugarLvlFasting())) {
                                bloodSugarLvlFastingDataPointsList.add(new DataPoint(checkinDate, Double.parseDouble(userCheckInInfo.getBloodSugarLvlFasting())));
                                countFasting++;
                            }
                            if (StringUtils.isNoneBlank(userCheckInInfo.getBloodSugarLvlMT())) {
                                bloodSugarLvlMTDataPointsList.add(new DataPoint(checkinDate, Double.parseDouble(userCheckInInfo.getBloodSugarLvlMT())));
                                countMT++;
                            }
                            if (StringUtils.isNoneBlank(userCheckInInfo.getBloodSugarLvlBT())) {
                                bloodSugarLvlBTDataPointsList.add(new DataPoint(checkinDate,  Double.parseDouble(userCheckInInfo.getBloodSugarLvlBT())));
                                countBT++;
                            }
                        }
                    } catch ( Exception eX) {
                        eX.printStackTrace();;
                    }

                    bloodSugarLvlFastingSeries = new LineGraphSeries<DataPoint>(bloodSugarLvlFastingDataPointsList.toArray(new DataPoint[bloodSugarLvlFastingDataPointsList.size()]));
                    bloodSugarLvlFastingSeries.setTitle("Fasting");
                    bloodSugarLvlFastingSeries.setColor(Color.YELLOW);
                    bloodSugarLvlFastingSeries.setThickness(5);

                    bloodSugarLvlMTSeries = new LineGraphSeries<DataPoint>(bloodSugarLvlMTDataPointsList.toArray(new DataPoint[bloodSugarLvlMTDataPointsList.size()]));
                    bloodSugarLvlMTSeries.setTitle("Meal Time");
                    bloodSugarLvlMTSeries.setColor(Color.BLUE);
                    bloodSugarLvlMTSeries.setThickness(5);

                    bloodSugarLvlBTSeries = new LineGraphSeries<DataPoint>(bloodSugarLvlBTDataPointsList.toArray(new DataPoint[bloodSugarLvlBTDataPointsList.size()]));
                    bloodSugarLvlBTSeries.setTitle("Bed Time");
                    bloodSugarLvlBTSeries.setColor(Color.MAGENTA);
                    bloodSugarLvlBTSeries.setThickness(5);

                } else {
                    bloodSugarLvlFastingSeries = new LineGraphSeries<DataPoint>(new DataPoint[] {new DataPoint(new Date(), 0)});
                    bloodSugarLvlMTSeries = new LineGraphSeries<DataPoint>(new DataPoint[] {new DataPoint(new Date(), 0)});
                    bloodSugarLvlBTSeries = new LineGraphSeries<DataPoint>(new DataPoint[] {new DataPoint(new Date(), 0)});


                    Toast.makeText(getApplicationContext(), "No Data Found...", Toast.LENGTH_SHORT).show();
                }


                mGraphView.addSeries(bloodSugarLvlFastingSeries);

                mGraphView.addSeries(bloodSugarLvlMTSeries);

                mGraphView.addSeries(bloodSugarLvlBTSeries);

                // set date label formatter
                mGraphView.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getApplicationContext()));
                mGraphView.getGridLabelRenderer().setNumHorizontalLabels(4); // only 4 because of the space


                if(dateList.size() > 0){
                    Collections.sort(dateList);

                    // set manual x bounds to have nice steps
                    mGraphView.getViewport().setMinX(dateList.get(0).getTime());
                    mGraphView.getViewport().setMaxX(dateList.get(dateList.size() -1).getTime());
                    mGraphView.getViewport().setXAxisBoundsManual(true);
                }

                // customize a little bit viewport
                Viewport viewport = mGraphView.getViewport();
                viewport.setYAxisBoundsManual(true);
                viewport.setMinY(50);
                viewport.setMaxY(200);
                viewport.setScrollable(true);


            } else {
                Toast.makeText(getApplicationContext(), "Request Failed....", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            mFeedbackTask = null;
            showProgress(false);
        }
    }

}
