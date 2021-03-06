package com.capstone.coursera.gidma.model.services;

import com.capstone.coursera.gidma.model.mediator.VideoDataMediator;
import com.capstone.coursera.gidma.model.mediator.webdata.AverageVideoRating;
import com.capstone.coursera.gidma.view.VideoListActivity;
import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.support.v4.content.LocalBroadcastManager;

/**
 * Intent Service that rate the videos.
 */
public class VideoRatingService 
       extends IntentService {
    /**
     * Custom Action that will be used to send Broadcast to the
     * VideoViewActivity.
     */
    public static final String ACTION_VIDEO_RATING_SERVICE_RESPONSE =
                "com.capstone.coursera.gidma.services.VideoRatingService.RESPONSE";
    
    /**
     * It is used by Notification Manager to send Notifications.
     */
    private static final int NOTIFICATION_ID = 1;
    
    /**
     * VideoDataMediator mediates the communication between Video
     * Service and local storage in the Android device.
     */
    private VideoDataMediator mVideoMediator;
    
    /**
     * Manages the Notification displayed in System UI.
     */
    private NotificationManager mNotifyManager;
    
    /**
     * Builder used to build the Notification.
     */
    private Builder mBuilder;
    
    /**
     * Constructor for VideoRatingService.
     * 
     * @param name
     */
    public VideoRatingService(String name) {
        super("VideoRatingService");     
    }
    
    /**
     * Constructor for VideoRatingService.
     * 
     * @param name
     */
    public VideoRatingService() {
        super("VideoRatingService");     
    }
    
    private AverageVideoRating averageVideoRating;
    
    /**
     * Factory method that makes the explicit intent another Activity
     * uses to call this Service.
     * 
     * @param context
     * @param videoId
     * @return
     */
    public static Intent makeIntent(Context context, long videoId, double rating) {
    	Intent intent  =  new Intent(context, VideoRatingService.class);
    	
    	intent.putExtra(VideoListActivity.EXTRA_RES_ID_VIDEO_ID, videoId);
    	intent.putExtra(VideoListActivity.EXTRA_RES_ID_VIDEO_RATING, rating);
    	
    	return intent;
    }
    
    /**
     * Hook method that is invoked on the worker thread with a request
     * to process. Only one Intent is processed at a time, but the
     * processing happens on a worker thread that runs independently
     * from other application logic.
     * 
     * @param intent
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        // Starts the Notification to show the progress of video
        // rate.
        startNotification();
        
        // Create VideoDataMediator that will mediate the communication
        // between Server and Android Storage.
        mVideoMediator =
            new VideoDataMediator(getApplicationContext()); 
        
		Bundle bundle = intent.getExtras();

		long videoId = bundle.getLong(VideoListActivity.EXTRA_RES_ID_VIDEO_ID);
		double videoRating = bundle.getDouble(VideoListActivity.EXTRA_RES_ID_VIDEO_RATING); 
        

		String status = mVideoMediator.rateVideo(getApplicationContext(),
        		videoId, videoRating);
		
		averageVideoRating = mVideoMediator.getAverageVideoRating();
		
        // Check if Video rate is successful.
        finishNotification(status);
             
        // Send the Broadcast to VideoViewActivity that the Video
        // rate is completed.
        //sendBroadcast();
    }
    
    /**
     * Send the Broadcast to Activity that the Video rate is
     * completed.
     */
    private void sendBroadcast(){
    	
    	Intent ratingUpdateIntent = new Intent(ACTION_VIDEO_RATING_SERVICE_RESPONSE)
        .addCategory(Intent.CATEGORY_DEFAULT);
    	
    	if(ratingUpdateIntent != null){
    		ratingUpdateIntent.putExtra("VIDEO_USER_RATING", averageVideoRating.getCurrentUserRating());
    		ratingUpdateIntent.putExtra("VIDEO_AVG_RATING", averageVideoRating.getRating());
    		ratingUpdateIntent.putExtra("VIDEO_TOTAL_RATING", averageVideoRating.getTotalRatings());
    	}

        // Use a LocalBroadcastManager to restrict the scope of this
        // Intent to the AVideoManager application.
        LocalBroadcastManager.getInstance(this)
             .sendBroadcast(ratingUpdateIntent);
    }
    
    /**
     * Finish the Notification after the Video is rateed.
     * 
     * @param status
     */
    private void finishNotification(String status) {
        // When the loop is finished, updates the notification.
        mBuilder.setContentTitle(status)
                // Removes the progress bar.
                .setProgress (0,
                              0,
                              false)
                .setSmallIcon(android.R.drawable.star_on)
                .setContentText("") 
                .setTicker(status);

        // Build the Notification with the given
        // Notification Id.
        mNotifyManager.notify(NOTIFICATION_ID,
                              mBuilder.build());
    }
    
    /**
     * Starts the Notification to show the progress of video rate.
     */
    private void startNotification() {
        // Gets access to the Android Notification Service.
        mNotifyManager = (NotificationManager)
            getSystemService(Context.NOTIFICATION_SERVICE); 

        // Create the Notification and set a progress indicator for an
        // operation of indeterminate length.
        mBuilder = new NotificationCompat
                       .Builder(this)
                       .setContentTitle("Video rating") 
                       .setContentText("Video rating in progress") 
                       .setSmallIcon(android.R.drawable.star_on)
                       .setTicker("Rating video")
                       .setProgress(0,
                                    0,
                                    true);
 
        // Build and issue the notification.
        mNotifyManager.notify(NOTIFICATION_ID,
                              mBuilder.build());
    }
}
