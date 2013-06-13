package com.blackoutbuddy.android.app;

import java.util.Date;
import java.util.List;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.Window;
import com.blackoutbuddy.android.data.BlackoutData;
import com.blackoutbuddy.android.data.DataSource;
import com.blackoutbuddy.android.locpoll.LocationPoller;
import com.blackoutbuddy.android.locpoll.LocationPollerParameter;

public class BlackoutList extends SherlockListActivity {
	
	public static final String TAG = BlackoutList.class.getSimpleName();
	
	private int PERIOD;
	private int TIMEOUT = 30 * 1000;
	private AlarmManager mgr = null;
	private PendingIntent pi = null;
	
	private DataSource datasource;
	private BlackoutData selected;
	private boolean isPolling;
	private SharedPreferences prefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		datasource = new DataSource(this);
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		PERIOD = Integer.parseInt(prefs.getString("interval", "5")) * 60 * 1000;
		isPolling = prefs.getBoolean("isPolling", false);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		datasource.open();
		List<BlackoutData> blackouts = datasource.getAllBlackouts();
		ArrayAdapter<BlackoutData> adapter = new ArrayAdapter<BlackoutData>(this, android.R.layout.simple_list_item_1, blackouts);
		setListAdapter(adapter);
		registerForContextMenu(getListView());
		
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		PERIOD = Integer.parseInt(prefs.getString("interval", "5")) * 60 * 1000;
		isPolling = prefs.getBoolean("isPolling", false);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		datasource.close();
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		BlackoutData blackout = (BlackoutData) l.getItemAtPosition(position);
		long group = blackout.getId();
		Intent intent = new Intent(this, BlackoutMap.class);
		intent.putExtra("group", group);
		startActivity(intent);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		AdapterView.AdapterContextMenuInfo info;
		try {
			info = (AdapterView.AdapterContextMenuInfo) menuInfo;
		} catch (ClassCastException e){
			Log.e(TAG, "bad menu info", e);
			return;
		}
		selected = (BlackoutData) getListAdapter().getItem(info.position);
		menu.setHeaderTitle(selected.toString());
		menu.add("Delete");
		menu.add("List");
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (item.getTitle().toString().equalsIgnoreCase("Delete")){
			datasource.deleteBlackout(selected);
			List<BlackoutData> blackouts = datasource.getAllBlackouts();
			ArrayAdapter<BlackoutData> adapter = new ArrayAdapter<BlackoutData>(this, android.R.layout.simple_list_item_1, blackouts);
			setListAdapter(adapter);
		} else if (item.getTitle().toString().equalsIgnoreCase("List")){
			Intent intent = new Intent(this, LocationsList.class);
			intent.putExtra("group", selected.getId());
			startActivity(intent);
		}
		return super.onContextItemSelected(item);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(R.string.menu_settings)
			.setIntent(new Intent(this, BBPreferences.class))
	        .setIcon(R.drawable.ic_settings)
	        .setShowAsAction(MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		if (isPolling) {
			menu.add("Stop")
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_WITH_TEXT | MenuItem.SHOW_AS_ACTION_ALWAYS);
			menu.add("Insert")
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_WITH_TEXT | MenuItem.SHOW_AS_ACTION_ALWAYS);
		} else {
			menu.add("Start")
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_WITH_TEXT | MenuItem.SHOW_AS_ACTION_ALWAYS);
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(
			com.actionbarsherlock.view.MenuItem item) {
		if (item.getTitle().toString().equalsIgnoreCase("Start")){
			startCollection();
			Intent intent  = new Intent(this, BlackoutList.class);
			startActivity(intent);
			finish();
		} else if (item.getTitle().toString().equalsIgnoreCase("Stop")){
			stopCollection();
			Intent intent  = new Intent(this, BlackoutList.class);
			startActivity(intent);
			finish();
		}
		
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
	}
	
	public void startCollection(){
		BlackoutData blackout = datasource.insertBlackout(new Date().getTime());
		
		SharedPreferences.Editor prefsEditor = prefs.edit();
		prefsEditor.putBoolean("isPolling", true);
		prefsEditor.putLong("group", blackout.getId());
		prefsEditor.commit();
		
		mgr = (AlarmManager) getSystemService(ALARM_SERVICE);
		
		Intent i = new Intent(this, LocationPoller.class);
		
		Bundle bundle = new Bundle();
		LocationPollerParameter parameter = new LocationPollerParameter(bundle);
	    parameter.setIntentToBroadcastOnCompletion(new Intent(this, LocationReceiver.class));
	    // try network and fallback to gps?
	    // parameter.setProviders(new String[] {LocationManager.NETWORK_PROVIDER, LocationManager.GPS_PROVIDER});
	    // only gps
	    parameter.setProviders(new String[] {LocationManager.GPS_PROVIDER});
	    parameter.setTimeout(TIMEOUT);
	    i.putExtras(bundle);


	    pi=PendingIntent.getBroadcast(this, 0, i, 0);
	    mgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
	                                        SystemClock.elapsedRealtime(),
	                                        PERIOD,
	                                        pi);
	}
	
	public void stopCollection(){
		mgr = (AlarmManager) getSystemService(ALARM_SERVICE);
		Intent i = new Intent(this, LocationPoller.class);
		pi=PendingIntent.getBroadcast(this, 0, i, 0);
		mgr.cancel(pi);
		
		SharedPreferences.Editor prefsEditor = prefs.edit();
		prefsEditor.putBoolean("isPolling", false);
		prefsEditor.commit();
	}

}
