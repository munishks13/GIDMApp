package com.capstone.coursera.gidma.model.provider;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Defines table and column names for the Video database.
 */
public class VideoContract {
    /**
     * The "Content authority" is a name for the entire content
     * provider, similar to the relationship between a domain name and
     * its web site.  A convenient string to use for the content
     * authority is the package name for the app, which must be unique
     * on the device.
     */
    public static final String CONTENT_AUTHORITY =
        "com.test.assignment3.video";

    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's that apps
     * will use to contact the content provider.
     */
    public static final Uri BASE_CONTENT_URI =
        Uri.parse("content://"
                  + CONTENT_AUTHORITY);

    /**
     * Possible paths (appended to base content URI for possible
     * URI's), e.g., content://com.capstone.coursera.gidma/video/ is a valid path for
     * Video data. However, content://com.capstone.coursera.gidma/givemeroot/ will
     * fail since the ContentProvider hasn't been given any
     * information on what to do with "givemeroot".
     */
    public static final String PATH_VIDEO =
        VideoEntry.TABLE_NAME;

    /**
     * Inner class that defines the contents of the Video table.
     */
    public static final class VideoEntry implements BaseColumns {
        /**
         * Use BASE_CONTENT_URI to create the unique URI for Video
         * Table that apps will use to contact the content provider.
         */
        public static final Uri CONTENT_URI = 
            BASE_CONTENT_URI.buildUpon()
            .appendPath(PATH_VIDEO).build();

        /**
         * When the Cursor returned for a given URI by the
         * ContentProvider contains 0..x items.
         */
        public static final String CONTENT_ITEMS_TYPE =
            "vnd.android.cursor.dir/"
            + CONTENT_AUTHORITY 
            + "/" 
            + PATH_VIDEO;

        /**
         * When the Cursor returned for a given URI by the
         * ContentProvider contains 1 item.
         */
        public static final String CONTENT_ITEM_TYPE =
            "vnd.android.cursor.item/"
            + CONTENT_AUTHORITY 
            + "/" 
            + PATH_VIDEO;

        /**
         * Name of the database table.
         */
        public static final String TABLE_NAME =
            "video_table";

        /**
         * Columns to store Data of each Video Data.
         */
        public static final String COLUMN_VIDEO_ID = "id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_DURATION = "duration";
        public static final String COLUMN_CONTENTTYPE = "contentType";
        public static final String COLUMN_DATAURL = "dataUrl";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_LOCATION = "location";

        /**
         * Return a Uri that points to the row containing a given id.
         * 
         * @param id
         * @return Uri
         */
        public static Uri buildVideoUri(Long id) {
            return ContentUris.withAppendedId(CONTENT_URI,
                                              id);
        }
    }
}
