package com.capstone.coursera.gidma.model.provider;

import java.io.File;

import com.capstone.coursera.gidma.model.provider.VideoContract.VideoEntry;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Manages a local database for Video data.
 */
public class VideoDatabaseHelper extends SQLiteOpenHelper {
    /**
     * If the database schema is changed, the database version must be
     * incremented.
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * Database name.
     */
    public static final String DATABASE_NAME =
        "video.db";

    /**
     * Constructor for VideoDatabaseHelper.  Store the database in
     * the cache directory so Android can remove it if memory is low.
     * 
     * @param context
     */
    public VideoDatabaseHelper(Context context) {
    	super(context, 
              context.getCacheDir()
              + File.separator 
              + DATABASE_NAME,
              null, 
              DATABASE_VERSION);
    }

    /**
     * Hook method called when Database is created.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Define an SQL string that creates a table to hold Videos.
        // Each Video has a list of LongForms in Json.
        final String SQL_CREATE_VIDEO_TABLE =
            "CREATE TABLE "
            + VideoEntry.TABLE_NAME + " (" 
            + VideoEntry._ID + " INTEGER PRIMARY KEY, " 
            + VideoEntry.COLUMN_VIDEO_ID + " INTEGER NOT NULL, " 
            + VideoEntry.COLUMN_TITLE + " TEXT NOT NULL, " 
            + VideoEntry.COLUMN_DURATION + " INTEGER NOT NULL, " 
            + VideoEntry.COLUMN_CONTENTTYPE + " TEXT NOT NULL, " 
            + VideoEntry.COLUMN_DATAURL + " TEXT NOT NULL, " 
            + VideoEntry.COLUMN_LOCATION + " TEXT , "
            + VideoEntry.COLUMN_RATING + " REAL NOT NULL " 
            + " );";
        
        // Create the table.
        db.execSQL(SQL_CREATE_VIDEO_TABLE);
    }

    /**
     * Hook method called when Database is upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db,
                          int oldVersion,
                          int newVersion) {
        // This database is only a cache for online data, so its
        // upgrade policy is to simply to discard the data and start
        // over.  This method only fires if you change the version
        // number for your database.  It does NOT depend on the
        // version number for your application.  If the schema is
        // updated without wiping data, commenting out the next 2
        // lines should be the top priority before modifying this
        // method.
        db.execSQL("DROP TABLE IF EXISTS " 
                   + VideoEntry.TABLE_NAME);
        onCreate(db);
    }
}
