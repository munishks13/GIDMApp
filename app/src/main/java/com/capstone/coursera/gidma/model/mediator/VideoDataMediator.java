package com.capstone.coursera.gidma.model.mediator;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import com.capstone.coursera.gidma.client.SecuredRestBuilder;
import com.capstone.coursera.gidma.client.UnsafeHttpsClient;
import com.capstone.coursera.gidma.model.mediator.webdata.AverageVideoRating;
import com.capstone.coursera.gidma.model.mediator.webdata.Video;
import com.capstone.coursera.gidma.model.mediator.webdata.VideoServiceProxy;
import com.capstone.coursera.gidma.model.mediator.webdata.VideoStatus;
import com.capstone.coursera.gidma.model.mediator.webdata.VideoStatus.VideoState;
import com.capstone.coursera.gidma.model.provider.handler.VideoDataProviderHandler;
import com.capstone.coursera.gidma.utils.Constants;
import com.capstone.coursera.gidma.utils.VideoMediaStoreUtils;
import com.capstone.coursera.gidma.utils.VideoStorageUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

/**
 * Mediates communication between the Video Service and the local
 * storage on the Android device.  The methods in this class block, so
 * they should be called from a background thread (e.g., via an
 * AsyncTask).
 */
public class VideoDataMediator {

    /**
     * LogCat tag.
     */
    private final static String TAG = VideoDataMediator.class.getSimpleName();

    /**
     * Status code to indicate that file is successfully
     * uploaded.
     */
    public static final String STATUS_UPLOAD_SUCCESSFUL =
            "Upload succeeded";

    public static final String STATUS_DOWNLOAD_SUCCESSFUL =
            "Download succeeded";

    public static final String STATUS_RATING_SUCCESSFUL =
            "Rating Updated";

    public static final String STATUS_AVG_RATING_SUCCESSFUL =
            "Successfully get Video Rating";


    /**
     * Status code to indicate that file upload failed
     * due to large video size.
     */
    public static final String STATUS_UPLOAD_ERROR_FILE_TOO_LARGE =
            "Upload failed: File too big";

    /**
     * Status code to indicate that file upload failed.
     */
    public static final String STATUS_UPLOAD_ERROR =
            "Upload failed";

    public static final String STATUS_DOWNLOAD_ERROR =
            "Download failed";

    public static final String STATUS_RATING_ERROR =
            "Rating update failed";

    public static final String STATUS_AVG_RATING_ERROR =
            "getting Video Rating failed";

    /**
     * Defines methods that communicate with the Video Service.
     */
    private VideoServiceProxy mVideoServiceProxy;

    private AverageVideoRating averageVideoRating;

    public AverageVideoRating getAverageVideoRating() {
        return averageVideoRating;
    }

    /**
     * Constructor that initializes the VideoDataMediator.
     *
     * @param context
     */
    public VideoDataMediator(Context context) {
        // Initialize the VideoServiceProxy.

        SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(context);

        String userName = prefs.getString(Constants.KEY_PREFERENCE_USER_NAME, "");
        String password = prefs.getString(Constants.KEY_PREFERENCE_PASSWORD, "");

        Log.v(TAG, "VideoDataMediator: userName : " + userName);

        mVideoServiceProxy = new SecuredRestBuilder()
                .setLoginEndpoint(
                        Constants.SERCURE_SERVER_URL
                                + VideoServiceProxy.TOKEN_PATH)
                .setUsername(userName)
                .setPassword(password)
                .setClientId("mobile")
                .setClient(new OkClient(UnsafeHttpsClient.getUnsafeOkHttpClient()))
                .setEndpoint(Constants.SERCURE_SERVER_URL)
                .setLogLevel(RestAdapter.LogLevel.FULL).build()
                .create(VideoServiceProxy.class);
    }

    /**
     * Uploads the Video having the given Id.  This Id is the Id of
     * Video in Android Video Content Provider.
     *
     * @param videoId Id of the Video to be uploaded.
     * @return A String indicating the status of the video upload operation.
     */
    public String uploadVideo(Context context,
                              Uri videoUri) {
        Log.v(TAG, "uploadVideo: Going to upload...video....");

        // Get the path of video file from videoUri.
        String filePath =
                VideoMediaStoreUtils.getPath(context,
                        videoUri);

        // Get the Video from Android Video Content Provider having
        // the given filePath.
        Video androidVideo =
                VideoMediaStoreUtils.getVideo(context,
                        filePath);

        Log.v(TAG, "uploadVideo: androidVideo : " + androidVideo);

        // Check if any such Video exists in Android Video Content
        // Provider.
        if (androidVideo != null) {
            // Prepare to Upload the Video data.

            // Create an instance of the file to upload.
            File videoFile = new File(filePath);

            Log.v(TAG, "uploadVideo: videoFile filePath : " + filePath);
            Log.v(TAG, "uploadVideo: videoFile length : " + videoFile.length());

            // Check if the file size is less than the size of the
            // video that can be uploaded to the server.
            if (videoFile.length() < Constants.MAX_SIZE_MEGA_BYTE) {

                try {
                    // Add the metadata of the Video to the Video Service
                    // and get the resulting Video that contains
                    // additional meta-data (e.g., Id and ContentType)
                    // generated by the Video Service.
                    Video receivedVideo =
                            mVideoServiceProxy.addVideo(androidVideo);

                    Log.v(TAG, "uploadVideo: receivedVideo : " + receivedVideo);

                    // Check if the Server returns any Video metadata.
                    if (receivedVideo != null) {

                        Log.v(TAG, "uploadVideo: Finally, upload the Video data to the server : " + receivedVideo);

                        // Finally, upload the Video data to the server
                        // and get the status of the uploaded video data.
                        VideoStatus status =
                                mVideoServiceProxy.setVideoData
                                        (receivedVideo.getId(),
                                                new TypedFile(receivedVideo.getContentType(),
                                                        videoFile));

                        Log.v(TAG, "video uploaded...status.getState : " + status.getState());

                        // Check if the Status of the Video or not.
                        if (status.getState() == VideoState.READY) {
                            // Video successfully uploaded.

                            VideoDataProviderHandler providerHandler = new VideoDataProviderHandler(context);
                            providerHandler.addVideoDatainDB(receivedVideo);

                            Log.v(TAG, "video Added in Video Content Provider: receivedVideo : " + receivedVideo);

                            return STATUS_UPLOAD_SUCCESSFUL;
                        }
                    }
                } catch (Exception e) {
                    // Error occured while uploading the video.
                    return STATUS_UPLOAD_ERROR;
                }
            } else
                // Video can't be uploaded due to large video size.
                return STATUS_UPLOAD_ERROR_FILE_TOO_LARGE;
        }

        // Error occured while uploading the video.
        return STATUS_UPLOAD_ERROR;
    }

    /**
     * Get the List of Videos from Video Service.
     *
     * @return the List of Videos from Server or null if there is
     * failure in getting the Videos.
     */
    public List<Video> getVideoList() {
        try {
            return (ArrayList<Video>)
                    mVideoServiceProxy.getVideoList();
        } catch (Exception e) {
            return null;
        }
    }

    public void checkValidLogin() {
        mVideoServiceProxy.checkValidLogin();
    }


    /**
     * downloads the Video having the given Id.  This Id is the Id of
     * Video in Video Content Provider.
     *
     * @param videoId Id of the Video to download.
     * @return A String indicating the status of the video download operation.
     */
    public String downloadVideo(Context context, long videoId, String videoTitle) {
        Log.v(TAG, "Going to download...video from Server.... for videoId : " + videoId + " : For videoTitle : " + videoTitle);

        // Create an instance of the file aafter download.
        try {
            Response response = mVideoServiceProxy.getVideoData(videoId);

            File videoFile = VideoStorageUtils.storeVideoInExternalDirectory(context, response, videoTitle);

            // Check if the Server returns any Video metadata.
            if (videoFile != null) {

                Log.v(TAG, "SuccessFul DOwnload ..video from Server....videoFile.getPath : " + videoFile.getPath());
                return STATUS_DOWNLOAD_SUCCESSFUL;
            }


        } catch (Exception e) {
            // Error occured while downloading the video.
            return STATUS_DOWNLOAD_ERROR;
        }

        // Error occurred while downloading the video.
        return STATUS_DOWNLOAD_ERROR;
    }


    public String rateVideo(Context context, long videoId, double rating) {
        Log.v(TAG, "Going to rate...video from Server.... for videoId : " + videoId + " : with rating : " + rating);

        try {
            averageVideoRating = mVideoServiceProxy.rateVideo(videoId, rating);
            // Check if the Server returns any averageVideoRating.
            if (averageVideoRating != null) {

                Log.v(TAG, "Succesful Rated ..video on Server....averageVideoRating : " + averageVideoRating);
                return STATUS_RATING_SUCCESSFUL;
            }


        } catch (Exception e) {
            // Error occured while rating the video.
            return STATUS_RATING_ERROR;
        }

        // Error occured while rating the video.
        return STATUS_RATING_ERROR;
    }

    public String getVideoRating(Context context, long videoId) {
        Log.v(TAG, "Going getVideoRating from Server.... for videoId : " + videoId);

        try {
            averageVideoRating = mVideoServiceProxy.getVideoRating(videoId);


            // Check if the Server returns any averageVideoRating.
            if (averageVideoRating != null) {
                Log.v(TAG, "SuccessFul getVideoRating .from Server....averageVideoRating : " + averageVideoRating);
                return STATUS_AVG_RATING_SUCCESSFUL;
            }

        } catch (Exception e) {
            //Error occured while getting video rating .
            return STATUS_AVG_RATING_ERROR;
        }

        // Error occured while getting video rating .
        return STATUS_AVG_RATING_ERROR;
    }

}
