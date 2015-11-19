package com.capstone.coursera.gidma.model.provider.handler;

import com.capstone.coursera.gidma.model.mediator.webdata.Video;
import com.capstone.coursera.gidma.model.provider.VideoContract;
import com.capstone.coursera.gidma.utils.VideoStorageUtils;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

public class VideoDataProviderHandler {

    /**
     * LogCat tag.
     */
    private final static String TAG = VideoDataProviderHandler.class.getSimpleName();
	
    /**
     * Context used to access the contentResolver.
     */
    private Context mContext;
    
    public VideoDataProviderHandler(Context context) {
		this.mContext = context;
	}
	
    
    public boolean addVideoDatainDB(Video video){
    	mContext.getContentResolver().insert(VideoContract.VideoEntry.CONTENT_URI, makeVideDataContentValues(video));
    	Log.v(TAG, "Data added in Content Provider...video : " + video);
    	return true;
    }
    
    
    public Video getVideo(long videoId){
    	Video video = null;
    	
    	 Log.v (TAG, "getVideo entered, videoId: " + videoId);
    	
        // Selection clause to find rows with given video Id.
        final String SELETION_VIDEO_ID = 
        		VideoContract.VideoEntry.COLUMN_VIDEO_ID + " = ? ";
    	
        // Initializes an array to contain selection arguments.
        String [] selectionArgs = { Long.toString(videoId) };
        
   	 Log.v (TAG, "getVideo SELETION_VIDEO_ID : " + SELETION_VIDEO_ID);
   	 Log.v (TAG, "getVideo selectionArgs : " + selectionArgs);
        
        // Cursor that is returned as a result of database query which
        // points to one or more rows.
        try (Cursor cursor =
             mContext.getContentResolver().query
             (VideoContract.VideoEntry.CONTENT_URI,
              null,
              SELETION_VIDEO_ID,
              selectionArgs,
              null)) {
            // If there are not matches in the database return null 
            if (!cursor.moveToFirst()){
            	Log.i (TAG, "No Data Found for " + videoId);
            	
                return null;
            }
            else {
            	 Log.i (TAG, "Found Data for ID : videoId : " + videoId);
            	
            	 
            	 String videoLocalPath = cursor.getString(cursor.getColumnIndex(VideoContract.VideoEntry.COLUMN_LOCATION));
            	
            	 if(videoLocalPath != null && videoLocalPath.length() > 0 && VideoStorageUtils.doesFileExists(videoLocalPath)){
            		 return getVideoDataFromCursor(cursor);
            	 }
            }
        } catch (Exception eX){
        	eX.printStackTrace();
        }
    
    	
    	return video;
    }
    
    
    
    /**
     * Helper method that creates a content values object .
     */
    
    private ContentValues makeVideDataContentValues(Video video){
    	ContentValues cvs = new ContentValues();
    	
    	cvs.put(VideoContract.VideoEntry.COLUMN_VIDEO_ID, video.getId());
    	cvs.put(VideoContract.VideoEntry.COLUMN_TITLE, video.getTitle());
    	cvs.put(VideoContract.VideoEntry.COLUMN_DURATION, video.getDuration());
    	cvs.put(VideoContract.VideoEntry.COLUMN_CONTENTTYPE, video.getContentType());
    	cvs.put(VideoContract.VideoEntry.COLUMN_DATAURL, video.getUrl());
    	cvs.put(VideoContract.VideoEntry.COLUMN_RATING, 0);
    	cvs.put(VideoContract.VideoEntry.COLUMN_LOCATION, video.getLocation());
    	
    	return cvs;
    }
    
    
    
    private Video getVideoDataFromCursor(Cursor cursor){
   			 
    	 long id = cursor.getLong(cursor.getColumnIndex(VideoContract.VideoEntry.COLUMN_VIDEO_ID));
    	 String title = cursor.getString(cursor.getColumnIndex(VideoContract.VideoEntry.COLUMN_TITLE));
    	 long duration = cursor.getLong(cursor.getColumnIndex(VideoContract.VideoEntry.COLUMN_DURATION));
    	 String location = cursor.getString(cursor.getColumnIndex(VideoContract.VideoEntry.COLUMN_LOCATION));
    	 String contentType = cursor.getString(cursor.getColumnIndex(VideoContract.VideoEntry.COLUMN_CONTENTTYPE));
    	 String dataUrl = cursor.getString(cursor.getColumnIndex(VideoContract.VideoEntry.COLUMN_DATAURL));
    	 double rating = cursor.getDouble(cursor.getColumnIndex(VideoContract.VideoEntry.COLUMN_RATING));

     	Video video = new Video(id, title, duration, contentType, dataUrl);
     	
    	// Video video =  new Video(null, title, dataUrl, duration, 0, null);
    	 
     	video.setLocation(location);
     	//video.setCurrentUserRating(rating);
     	
     	Log.i (TAG, "getVideoDataFromCursor, video: " + video);
    	
    	return video;
    	
    }
    
    
}
