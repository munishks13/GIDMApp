package com.capstone.coursera.gidma.broadcast;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

import com.capstone.coursera.gidma.R;
import com.capstone.coursera.gidma.activity.CheckInActivity;

import java.text.DateFormat;
import java.util.Date;

public class CheckInAlarmNotificationReceiver extends BroadcastReceiver {
    // A tag used for debugging with Logcat
    static final String LOG_TAG = CheckInAlarmNotificationReceiver.class.getCanonicalName();
    // Notification ID to allow for future updates
    private static final int MY_NOTIFICATION_ID = 1;
    // Notification Text Elements
    private final CharSequence tickerText = "Time for another check-in...!!";
    private final CharSequence contentTitle = "Check In Time";
    private final CharSequence contentText = "Time for another check-in...!!";
    // Notification Sound and Vibration on Arrival
    private final Uri soundURI = Uri
            .parse("android.resource://com.capstone.coursera.gidma/" + R.raw.alarm_rooster);
    private final long[] mVibratePattern = {0, 200, 200, 300};
    RemoteViews mContentView = new RemoteViews("com.capstone.coursera.gidma", R.layout.custom_notification);
    // Notification Action Elements
    private Intent mNotificationIntent;
    private PendingIntent mContentIntent;

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i(LOG_TAG, "Entring onReceive:");

        // The Intent to be used when the user clicks on the Notification View
        mNotificationIntent = new Intent(context, CheckInActivity.class);

        // The PendingIntent that wraps the underlying Intent
        mContentIntent = PendingIntent.getActivity(context, 0,
                mNotificationIntent, Intent.FILL_IN_ACTION);


        Date notification_DateTime = new Date();
        //String timeStamp = new SimpleDateFormat("hh:mm").format(notification_DateTime);
        String timeStamp = DateFormat.getTimeInstance(DateFormat.SHORT).format(notification_DateTime);

        mContentView.setTextViewText(R.id.notification_title, contentTitle);
        mContentView.setTextViewText(R.id.notification_text, contentText);
        mContentView.setTextViewText(R.id.notification_time, timeStamp);

        // Build the Notification
        Notification.Builder notificationBuilder = new Notification.Builder(
                context).setTicker(tickerText)
                .setSmallIcon(android.R.drawable.stat_sys_warning)
                .setAutoCancel(true)
                .setContentIntent(mContentIntent)
                .setSound(soundURI)
                .setVibrate(mVibratePattern)
                .setContent(mContentView);

        //notificationBuilder.setLargeIcon(BitmapFactory.decodeFile("/res/drawable-hdpi/camera_icon.png"));

        // Get the NotificationManager
        NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        // Pass the Notification to the NotificationManager:
        mNotificationManager.notify(MY_NOTIFICATION_ID,
                notificationBuilder.build());

        // Log occurence of notify() call
        Log.i(LOG_TAG, "Sending notification at:"
                + DateFormat.getDateTimeInstance().format(new Date()));

    }
}
