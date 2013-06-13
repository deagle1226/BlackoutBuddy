package com.blackoutbuddy.android.app;

import com.blackoutbuddy.android.data.DataSource;
import com.blackoutbuddy.android.data.LocationData;
import com.blackoutbuddy.android.locpoll.LocationPollerResult;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

public class LocationReceiver extends BroadcastReceiver {
	
	public static final String TAG = LocationReceiver.class.getSimpleName();

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle b=intent.getExtras();

		  LocationPollerResult locationResult = new LocationPollerResult(b);

		  Location loc=locationResult.getLocation();
		  String msg;

		  if (loc==null) {
		    loc=locationResult.getLastKnownLocation();

		    if (loc==null) {
		      msg=locationResult.getError();
		    }
		    else {
		      msg="TIMEOUT, lastKnown="+loc.toString();
		    }
		  }
		  else {
		    msg=loc.toString();
		  }

		  if (msg==null) {
		    msg="Invalid broadcast received!";
		  }
		  Log.d(TAG, msg);
		  SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		  long group = prefs.getLong("group", 1);
		  DataSource datasource = new DataSource(context);
		  datasource.open();
		  LocationData location = datasource.insertLocation(loc.getLatitude(), loc.getLongitude(), loc.getAccuracy(), loc.getTime(), group, loc.getProvider());
		  datasource.close();
		  Log.d(TAG, "location added: " + location.toString());
	}

}
