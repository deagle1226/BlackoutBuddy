package com.blackoutbuddy.android.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLiteHelper extends SQLiteOpenHelper {
	
	private static final String TAG = SQLiteHelper.class.getSimpleName();
	
	public static final String TABLE_LOCATIONS = "locations";
	public static final String LOCATION_ID = "_id";
	public static final String LOCATION_LATITUDE = "latitude";
	public static final String LOCATION_LONGITUDE = "longitude";
	public static final String LOCATION_ACCURACY = "accuracy";
	public static final String LOCATION_TIME = "time";
	public static final String LOCATION_PROVIDER = "provider";
	public static final String LOCATION_GROUP = "blackout";
	
	public static final String TABLE_BLACKOUTS = "blackouts";
	public static final String BLACKOUT_ID = "_id";
	public static final String BLACKOUT_TIME = "time";
	
	private static final String DATABASE_NAME = "blackouts.db";
	private static final int DATABASE_VERSION = 1;
	
	// Database creation sql statement
	private static final String LOCATIONS_CREATE = "create table "
			+ TABLE_LOCATIONS + "("
			+ LOCATION_ID + " integer primary key autoincrement, "
			+ LOCATION_LATITUDE + " real not null,"
			+ LOCATION_LONGITUDE + " real not null,"
			+ LOCATION_ACCURACY + " real not null,"
			+ LOCATION_TIME + " integer not null,"
			+ LOCATION_GROUP + " integer not null,"
			+ LOCATION_PROVIDER + " text not null"+ ");";
	private static final String BLACKOUTS_CREATE = "create table "
			+ TABLE_BLACKOUTS + "("
			+ BLACKOUT_ID + " integer primary key autoincrement, "
			+ BLACKOUT_TIME + " integer not null"+ ");";
	

	public SQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db){
		db.execSQL(LOCATIONS_CREATE);
		db.execSQL(BLACKOUTS_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.d(TAG,
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATIONS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_BLACKOUTS);
		onCreate(db);
	}
}
