package com.blackoutbuddy.android.app;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.actionbarsherlock.app.SherlockListActivity;
import com.blackoutbuddy.android.data.DataSource;
import com.blackoutbuddy.android.data.LocationData;

public class LocationsList extends SherlockListActivity {
	
	public static final String TAG = LocationsList.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		long group = intent.getLongExtra("group", 0);
		DataSource datasource = new DataSource(this);
		datasource.open();
		List<LocationData> locations = datasource.getLocations(group);
		Log.d(TAG, "size: " + locations.size());
		datasource.close();
		ArrayAdapter<LocationData> adapter = new ArrayAdapter<LocationData>(this, android.R.layout.simple_list_item_1, locations);
        setListAdapter(adapter);
	}

}
