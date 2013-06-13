package com.blackoutbuddy.android.data;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

public class DataSource {
	
	private static final String TAG = DataSource.class.getSimpleName();
	
	private SQLiteDatabase database;
	private SQLiteHelper dbHelper;
	private String[] locationColumns = {SQLiteHelper.LOCATION_ID,
			SQLiteHelper.LOCATION_LATITUDE, SQLiteHelper.LOCATION_LONGITUDE,
			SQLiteHelper.LOCATION_ACCURACY, SQLiteHelper.LOCATION_TIME,
			SQLiteHelper.LOCATION_GROUP, SQLiteHelper.LOCATION_PROVIDER};
	private String[] blackoutColumns = {SQLiteHelper.BLACKOUT_ID,
			SQLiteHelper.BLACKOUT_TIME};
	
	public DataSource(Context c){
		dbHelper = new SQLiteHelper(c);
	}
	
	public void open() throws SQLiteException {
		database = dbHelper.getWritableDatabase();
	}
	
	public void close() {
		dbHelper.close();
	}
	
	public LocationData insertLocation(double latitude, double longitude, float accuracy, long time, long group, String provider){
		ContentValues values = new ContentValues();
		values.put(SQLiteHelper.LOCATION_LATITUDE, latitude);
		values.put(SQLiteHelper.LOCATION_LONGITUDE, longitude);
		values.put(SQLiteHelper.LOCATION_ACCURACY, accuracy);
		values.put(SQLiteHelper.LOCATION_TIME, time);
		values.put(SQLiteHelper.LOCATION_GROUP, group);
		values.put(SQLiteHelper.LOCATION_PROVIDER, provider);
		
		long insertId = database.insert(SQLiteHelper.TABLE_LOCATIONS, null, values);
		
		Cursor cursor = database.query(SQLiteHelper.TABLE_LOCATIONS, locationColumns,
				SQLiteHelper.LOCATION_ID + " = " + insertId, null, null, null, null);
		cursor.moveToFirst();
		LocationData location = cursorToLocation(cursor);
		cursor.close();
		return location;
	}
	
	public void deleteLocation(LocationData location){
		long id = location.getId();
		database.delete(SQLiteHelper.TABLE_LOCATIONS, SQLiteHelper.LOCATION_ID
				+ " = " + id, null);
		Log.d(TAG, "Location deleted with id: " + id);
	}
	
	public List<LocationData> getAllLocations(){
		List<LocationData> list = new ArrayList<LocationData>();
		
		Cursor cursor = database.query(SQLiteHelper.TABLE_LOCATIONS, locationColumns, null, null, null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()){
			LocationData location = cursorToLocation(cursor);
			list.add(location);
			cursor.moveToNext();
		}
		cursor.close();
		return list;
	}
	
	public List<LocationData> getLocations(long group){
		List<LocationData> list = new ArrayList<LocationData>();
		
		String selection = SQLiteHelper.LOCATION_GROUP + " = " + group;
		
		Cursor cursor = database.query(SQLiteHelper.TABLE_LOCATIONS, locationColumns, selection, null, null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()){
			LocationData location = cursorToLocation(cursor);
			list.add(location);
			cursor.moveToNext();
		}
		cursor.close();
		return list;
	}
	
	private LocationData cursorToLocation(Cursor cursor) {
		LocationData location = new LocationData();
		location.setId(cursor.getLong(0));
		location.setLatitude(cursor.getDouble(1));
		location.setLongitude(cursor.getDouble(2));
		location.setAccuracy(cursor.getFloat(3));
		location.setTime(cursor.getLong(4));
		location.setGroup(cursor.getLong(5));
		location.setProvider(cursor.getString(6));
		return location;
	}
	
	public BlackoutData insertBlackout(long time){
		ContentValues values = new ContentValues();
		values.put(SQLiteHelper.BLACKOUT_TIME, time);
		
		long insertId = database.insert(SQLiteHelper.TABLE_BLACKOUTS, null, values);
		
		Cursor cursor = database.query(SQLiteHelper.TABLE_BLACKOUTS, blackoutColumns, SQLiteHelper.BLACKOUT_ID + " = " + insertId, null, null, null, null);
		cursor.moveToFirst();
		BlackoutData blackout = cursorToBlackout(cursor);
		cursor.close();
		return blackout;
	}
	
	public void deleteBlackout(BlackoutData blackout){
		long id = blackout.getId();
		database.delete(SQLiteHelper.TABLE_BLACKOUTS, SQLiteHelper.BLACKOUT_ID + " = " + id, null);
		Log.d(TAG, "Blackout deleted with id: " + id);
		database.delete(SQLiteHelper.TABLE_LOCATIONS, SQLiteHelper.LOCATION_GROUP + " = " + id, null);
		Log.d(TAG, "Locations deleted with group id: " + id);
	}
	
	public List<BlackoutData> getAllBlackouts(){
		List<BlackoutData> list = new ArrayList<BlackoutData>();
		
		Cursor cursor = database.query(SQLiteHelper.TABLE_BLACKOUTS, blackoutColumns, null, null, null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()){
			BlackoutData blackout = cursorToBlackout(cursor);
			list.add(blackout);
			cursor.moveToNext();
		}
		return list;
	}

	private BlackoutData cursorToBlackout(Cursor cursor) {
		BlackoutData blackout = new BlackoutData();
		blackout.setId(cursor.getLong(0));
		blackout.setTime(cursor.getLong(1));
		return blackout;
	}

}
