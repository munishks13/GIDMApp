package com.capstone.coursera.gidma.presenter;

import java.lang.ref.WeakReference;

import com.capstone.coursera.gidma.common.ConfigurableOps;
import com.capstone.coursera.gidma.common.ContextView;
import com.capstone.coursera.gidma.common.GenericAsyncTask;
import com.capstone.coursera.gidma.common.GenericAsyncTaskOps;
import com.capstone.coursera.gidma.common.Utils;
import com.capstone.coursera.gidma.model.mediator.VideoDataMediator;
import com.capstone.coursera.gidma.model.mediator.webdata.Video;
import com.capstone.coursera.gidma.model.provider.handler.VideoDataProviderHandler;
import com.capstone.coursera.gidma.model.services.DownloadVideoService;
import com.capstone.coursera.gidma.model.services.VideoAvgRatingService;
import com.capstone.coursera.gidma.model.services.VideoRatingService;
import com.capstone.coursera.gidma.view.VideoViewActivity;
import android.util.Log;

/**
 * Provides all the Video-related operations.  It implements
 * ConfigurableOps so it can be created/managed by the GenericActivity
 * framework.  It extends GenericAsyncTaskOps so its doInBackground()
 * method runs in a background task.  It plays the role of the
 * "Abstraction" in Bridge pattern and the role of the "Presenter" in
 * the Model-View-Presenter pattern.
 */
public class VideoViewOps
       implements GenericAsyncTaskOps<Long, Void, Video>,
                  ConfigurableOps<VideoViewOps.View> {
    /**
     * Debugging tag used by the Android logger.
     */
    private static final String TAG =
        VideoViewOps.class.getSimpleName();
    
    /**
     * This interface defines the minimum interface needed by the
     * VideoOps class in the "Presenter" layer to interact with the
     * VideoListActivity in the "View" layer.
     */
    public interface View extends ContextView {
        /**
         * Finishes the Activity the VideoOps is
         * associated with.
         */
        void finish();
        
        void setVideoView();
    }
        
    /**
     * Used to enable garbage collection.
     */
    private WeakReference<VideoViewOps.View> mVideoView;
    
    /**
     * The GenericAsyncTask used to expand an Video in a background
     * thread via the Video web service.
     */
    private GenericAsyncTask<Long,
                             Void,
                             Video,
                             VideoViewOps> mAsyncTask;
    
    /**
     * VideoDataMediator mediates the communication between Video
     * Service and local storage on the Android device.
     */
    VideoDataMediator mVideoMediator;
    
    
    /**
     * Default constructor that's needed by the GenericActivity
     * framework.
     */
    public VideoViewOps() {
    }
    
    /**
     * Called after a runtime configuration change occurs to finish
     * the initialisation steps.
     */
    public void onConfiguration(VideoViewOps.View view,
                                boolean firstTimeIn) {
        final String time =
            firstTimeIn 
            ? "first time" 
            : "second+ time";
        
        Log.d(TAG,
              "onConfiguration() called the "
              + time
              + " with VideoViewOps view = "
              + view);

        // (Re)set the mVideoView WeakReference.
        mVideoView =
            new WeakReference<>(view);
        
        if (firstTimeIn) {
            // Create VideoDataMediator that will mediate the
            // communication between Server and Android Storage.
            mVideoMediator =
                new VideoDataMediator(mVideoView.get().getActivityContext());

        }

    }

    /**
     * Start a service that Downloads the Video having given Id.
     *   
     * @param videoId
     */
    public void downloadVideo(long videoId, String videoTitle){
        // Sends an Intent command to the DownloadVideoService.
        mVideoView.get().getApplicationContext().startService
            (DownloadVideoService.makeIntent 
                 (mVideoView.get().getApplicationContext(),
                		 videoId, videoTitle));
    }

    /**
     * Start a service that get Video Ratings.
     *   
     * @param videoId
     */
    public void getVideoRating(long videoId){
        // Sends an Intent command to the VideoAvgRatingService.
        mVideoView.get().getApplicationContext().startService
            (VideoAvgRatingService.makeIntent 
                 (mVideoView.get().getApplicationContext(),
                		 videoId));
    }
    
    /**
     * Start a service that Rate the Video having given Id.
     *   
     * @param videoId
     */
    public void rateVideo(long videoId, double rating){
        // Sends an Intent command to the VideoRatingService.
        mVideoView.get().getApplicationContext().startService
            (VideoRatingService.makeIntent 
                 (mVideoView.get().getApplicationContext(),
                		 videoId, rating));
    }
    
    /**
     * Gets the VideoList from Server by executing the AsyncTask to
     * expand the video without blocking the caller.
     */
    public void getVideoFromProvider(long videoId){
    	Log.d(TAG,"getVideoFromProvider : videoId : " + videoId);
    	
    	if(mAsyncTask != null)
    		mAsyncTask.cancel(true);

    	mAsyncTask = new GenericAsyncTask<>(this);
        mAsyncTask.execute(videoId);
    }
    
    /**
     * Retrieve the List of Videos by help of VideoDataMediator via a
     * synchronous two-way method call, which runs in a background
     * thread to avoid blocking the UI thread.
     */
	@Override
	public Video doInBackground(Long... params) {
		 Log.d(TAG,"Getting Video from Async Task for videoId : " + params[0]);
		return new VideoDataProviderHandler(mVideoView.get().getApplicationContext()).getVideo(params[0]);
	}

    /**
     * Display the results in the UI Thread.
     */
	@Override
	public void onPostExecute(Video result) {
		
		Log.d(TAG,"onPostExecute Video : " + result);
		if(result != null){
//			mVideoView.get().displayVideo(result);
			//mVideoView.get().
			
			displayVideo(result);
		}
	}
    
    /**
     * Display the Video.
     * 
     * @param videos
     */
    public void displayVideo(Video video) {
        if (video != null) {
        	
            Utils.showToast(mVideoView.get().getActivityContext(),
                            "Videos available from the Video Content Provider");
        	
        	((VideoViewActivity) mVideoView.get().getActivityContext()).displayVideo(video);

        } else {
            Utils.showToast(mVideoView.get().getActivityContext(),
                           "No Video Found Please DownLoad.");

            // Close down the Activity.
           // mVideoView.get().finish();
        }
    }

}
