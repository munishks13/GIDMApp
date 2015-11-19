package com.capstone.coursera.gidma.view;

import java.text.DecimalFormat;
import java.util.Locale;

import com.capstone.coursera.gidma.R;
import com.capstone.coursera.gidma.common.GenericActivity;
import com.capstone.coursera.gidma.common.Utils;
import com.capstone.coursera.gidma.model.mediator.webdata.Video;
import com.capstone.coursera.gidma.model.services.DownloadVideoService;
import com.capstone.coursera.gidma.model.services.VideoAvgRatingService;
import com.capstone.coursera.gidma.model.services.VideoRatingService;
import com.capstone.coursera.gidma.presenter.VideoViewOps;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

public class VideoViewActivity extends GenericActivity<VideoViewOps.View, VideoViewOps>  implements VideoViewOps.View{
	
	private DownloadResultReceiver mDownloadResultReceiver;
	private AvgRatingResultReceiver mAvgRatingResultReceiver;
	private VideoRatingServiceReceiver mVideoRatingServiceReceiver;
	
	private VideoView myVideoView;
	private int position = 0;
	private ProgressDialog progressDialog;
	private MediaController mediaControls;
	
	private RatingBar ratingBar;
	private TextView videoViewTitle;
	private TextView videoRatingView;
	
	private Button btnDownload;
	private Button btnViewRating;
	
	DecimalFormat decimalF = new DecimalFormat("##.00");
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		Log.i(TAG, "Entered VideoViewActivity onCreate method. ");
		
		mDownloadResultReceiver = new DownloadResultReceiver();
		mAvgRatingResultReceiver = new AvgRatingResultReceiver();
//		mVideoRatingServiceReceiver = new VideoRatingServiceReceiver();
		
		// Initialize the default layout.
        setContentView(R.layout.video_view_activity);
        
        // Collect received Data
		Intent intent = getIntent();
		
		Bundle bundle = intent.getExtras();

		final long videoId = bundle.getLong(VideoListActivity.EXTRA_RES_ID_VIDEO_ID);
		final String videoUrl = bundle.getString(VideoListActivity.EXTRA_RES_ID_VIDEO_URL);
		final String videoTitle = bundle.getString(VideoListActivity.EXTRA_RES_ID_VIDEO_TITLE); 
		
		Log.v(TAG, " : videoId : " + videoId);
		Log.v(TAG, " : videoTitle : " + videoTitle);
		Log.v(TAG, " : videoUrl : " + videoUrl);

		ratingBar = (RatingBar) findViewById(R.id.ratingBar);
		
		videoViewTitle = (TextView) findViewById(R.id.videoViewTitle);
		videoRatingView = (TextView) findViewById(R.id.videoRatingView);
		
		btnDownload = (Button) findViewById(R.id.btnDownload);
		btnViewRating = (Button) findViewById(R.id.btnViewRating);
		
		videoViewTitle.setText(videoTitle);

		ratingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
			public void onRatingChanged(RatingBar ratingBar, float rating,
					boolean fromUser) {

				getOps().rateVideo(videoId, rating);
				
				videoViewTitle.setText(videoTitle);
			}
		});

		btnDownload.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getOps().downloadVideo(videoId, videoTitle);
			}
		});
		
		
		btnViewRating.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getOps().getVideoRating(videoId);
				
//				videoViewTitle.setText(videoTitle + " : " + String.valueOf(rating));
			}
		});
		
		
		
		
        // Invoke the special onCreate() method in GenericActivity,
        // passing in the VideoViewOps class to instantiate/manage and
        // "this" to provide VideoViewOps with the VideoViewOps.View instance.
        super.onCreate(savedInstanceState,
        				VideoViewOps.class,
                       this);

      /// Video controls settings...
        
//		Video video =  new Video();
//		video.setTitle(videoTitle);
//		video.setDataUrl(videoUrl);
//		displayVideo(video);
		
		getVideoFromProvider(videoId);
		
		getOps().getVideoRating(videoId);
	}


	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putInt("Position", myVideoView.getCurrentPosition());
		myVideoView.pause();
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		position = savedInstanceState.getInt("Position");
		myVideoView.seekTo(position);
	}
	
	
	
	
    /**
     * first check the video in provide... if available.
     */
    public void getVideoFromProvider(long videoId) {
    	 getOps().getVideoFromProvider(videoId);
    }
	

    /**
     *  Hook method that is called when user resumes activity
     *  from paused state, onPause(). 
     */
    @Override
    protected void onResume() {
        // Call up to the superclass.
        super.onResume();

        // Register BroadcastReceiver that receives result from
        // defined Service.
        registerReceiver();
    }
    
    /**
     * Register a BroadcastReceiver that receives a result from the
     * DownloadVideoService when a video download completes.
     */
    private void registerReceiver() {
        
        // Create an Intent filter that handles Intents from the
        // DownloadVideoService.
        IntentFilter intentFilter =
            new IntentFilter(DownloadVideoService.ACTION_DOWNLOAD_SERVICE_RESPONSE);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);

        // Register the BroadcastReceiver.
        LocalBroadcastManager.getInstance(this)
               .registerReceiver(mDownloadResultReceiver,
                                 intentFilter);
        
        
        // Create an Intent filter that handles Intents from the
        // VideoAvgRatingService.
        IntentFilter intentFilterRating =
            new IntentFilter(VideoAvgRatingService.ACTION_VIDEO_AVG_RATING_SERVICE_RESPONSE);
        intentFilterRating.addCategory(Intent.CATEGORY_DEFAULT);

        // Register the BroadcastReceiver.
        LocalBroadcastManager.getInstance(this)
               .registerReceiver(mAvgRatingResultReceiver,
            		   intentFilterRating);
        

//        // Create an Intent filter that handles Intents from the
//        // VideoAvgRatingService.
//        IntentFilter intentFilterRatingUpdate =
//            new IntentFilter(VideoRatingService.ACTION_VIDEO_RATING_SERVICE_RESPONSE);
//        intentFilterRatingUpdate.addCategory(Intent.CATEGORY_DEFAULT);
//
//        // Register the BroadcastReceiver.
//        LocalBroadcastManager.getInstance(this)
//               .registerReceiver(mVideoRatingServiceReceiver,
//            		   intentFilterRatingUpdate);
        
    }

    /**
     * Hook method that gives a final chance to release resources and
     * stop spawned threads.  onDestroy() may not always be
     * called-when system kills hosting process.
     */
    @Override
    protected void onPause() {
        // Call onPause() in superclass.
        super.onPause();
        
        // Unregister BroadcastReceiver.
        LocalBroadcastManager.getInstance(this)
          .unregisterReceiver(mDownloadResultReceiver);
    }

    /**
     * The Broadcast Receiver that registers itself to receive result
     * from DownloadVideoService.
     */
    private class DownloadResultReceiver 
            extends BroadcastReceiver {
        /**
         * Hook method that's dispatched when the DownloadService has
         * downloaded the Video.
         */
        @Override
        public void onReceive(Context context,
                              Intent intent) {
        	
        	Toast.makeText(VideoViewActivity.this,
					"Downloaded Video.",
					Toast.LENGTH_SHORT).show();        	
        	Log.v(TAG, " : DownloadResultReceiver onReceive : ");
        }
    }

   
    /**
     * The Broadcast Receiver that registers itself to receive result
     * from VideoAvgRatingService.
     */
    private class AvgRatingResultReceiver 
            extends BroadcastReceiver {
        /**
         * Hook method that's dispatched when the VideoAvgRatingService has
         * got Video rating.
         */
        @Override
        public void onReceive(Context context,
                              Intent intent) {

        	updateUiRatingData(intent);
        	
        	Log.v(TAG, " : AvgRatingResultReceiver onReceive : ");
        }
    }
    
    /**
     * The Broadcast Receiver that registers itself to receive result
     * from VideoRatingService.
     */
    private class VideoRatingServiceReceiver 
            extends BroadcastReceiver {
        /**
         * Hook method that's dispatched when the VideoRatingService has
         * got Video rating.
         */
        @Override
        public void onReceive(Context context,
                              Intent intent) {

        	updateUiRatingData(intent);
        	
        	Log.v(TAG, " : VideoRatingServiceReceiver onReceive : ");
        }
    }
    
    private void updateUiRatingData(Intent intent){
    	int totalRatings = intent.getIntExtra("VIDEO_TOTAL_RATING", 0);
    	double currentUserRating = intent.getDoubleExtra("VIDEO_USER_RATING", 0);
    	double rating = intent.getDoubleExtra("VIDEO_AVG_RATING", 0);
    	
    	videoRatingView.setText("Your Rating : " + String.valueOf(decimalF.format(currentUserRating)) + " : Avg. :" + String.valueOf(decimalF.format(rating)) + " : Total :" + String.valueOf(totalRatings));
    	
    	ratingBar.setRating(Double.valueOf(currentUserRating).floatValue());
    	
    	
    	Toast.makeText(VideoViewActivity.this,
				"Check Ratings.......",
				Toast.LENGTH_SHORT).show();
    }
    
    
    /**
     * Finishes this Activity.
     */
    @Override
    public void finish() {
        super.finish();
    }

	@Override
	public void setVideoView() {
		// TODO Auto-generated method stub
		
	}
    
    /**
     * Display the Video.
     * 
     * @param videos
     */
    public void displayVideo(Video video) {
        if (video != null) {
            
            Log.v(TAG, "Displaying Videos FROM LOCAITON ." + video.getLocation());
            
          /// Video controls settings...
    		
    		
    		if (mediaControls == null) {
    			mediaControls = new MediaController(VideoViewActivity.this);
    		}

    		// Find your VideoView in your video_main.xml layout
    		myVideoView = (VideoView) findViewById(R.id.video_view);

    		// Create a progressbar
    		progressDialog = new ProgressDialog(VideoViewActivity.this);
    		// Set progressbar title
    		progressDialog.setTitle(video.getTitle());
    		// Set progressbar message
    		progressDialog.setMessage("Loading...");

    		progressDialog.setCancelable(false);
    		// Show progressbar
    		progressDialog.show();

    		try {
    			myVideoView.setMediaController(mediaControls);
    			myVideoView.setVideoURI(Uri.parse(video.getLocation()));

    		} catch (Exception e) {
    			Log.e("Error", e.getMessage());
    			e.printStackTrace();
    		}

    		myVideoView.requestFocus();
    		myVideoView.setOnPreparedListener(new OnPreparedListener() {
    			// Close the progress bar and play the video
    			public void onPrepared(MediaPlayer mp) {
    				progressDialog.dismiss();
    				myVideoView.seekTo(position);
    				if (position == 0) {
    					myVideoView.start();
    				} else {
    					myVideoView.pause();
    				}
    			}
    		});
            
            
        } else {
            Utils.showToast(VideoViewActivity.this,
                           "Please connect to the Video Service");

            // Close down the Activity.
            //finish();
        }
    }
	
}
