package com.blackoutbuddy.android.app;

import java.util.Date;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.blackoutbuddy.android.data.BlackoutData;
import com.blackoutbuddy.android.data.DataSource;
import com.blackoutbuddy.android.locpoll.LocationPoller;
import com.blackoutbuddy.android.locpoll.LocationPollerParameter;


public class MainFragment extends SherlockFragment {
	public static final String TAG = MainFragment.class.getSimpleName();
	
	private int PERIOD;
	private int TIMEOUT = 30 * 1000;
	private AlarmManager mgr = null;
	private PendingIntent pi = null;
	
	private DataSource datasource;
	private boolean isPolling;
	private SharedPreferences  prefs;
	private Button button;
	
	public MainFragment(){
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		datasource = new DataSource(getSherlockActivity());
		
		button = new Button(getSherlockActivity());
		button.setGravity(Gravity.CENTER);
		button.setOnClickListener(buttonListener);
		
		LinearLayout layout = new LinearLayout(getSherlockActivity());
        layout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        layout.setGravity(Gravity.CENTER);
        layout.addView(button);

        return layout;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		PERIOD = Integer.parseInt(prefs.getString("interval", "5")) * 60 * 1000;
		isPolling = prefs.getBoolean("isPolling", false);
		if (isPolling) {
			button.setText("Stop");
		} else {
			button.setText("Start");
		}
	}

	private OnClickListener buttonListener = new OnClickListener() {

		public void onClick(View v) {
			SharedPreferences.Editor prefsEditor = prefs.edit();
			if (!isPolling){
				datasource.open();
				BlackoutData blackout = datasource.insertBlackout(new Date().getTime());
				datasource.close();
				prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
				prefsEditor.putLong("group", blackout.getId());
				prefsEditor.putBoolean("isPolling", true);
				isPolling = true;
				getLocation();
				Log.d(TAG, "Poller turned on");
				button.setText("Stop");
			} else if (isPolling){
				stopCollection();
				prefsEditor.putBoolean("isPolling", false);
				isPolling = false;
				Log.d(TAG, "Poller turned off");
				button.setText("Start");
			}
			prefsEditor.commit();
		}
		
	};
	

	public void getLocation(){
		mgr = (AlarmManager) getSherlockActivity().getSystemService(getActivity().ALARM_SERVICE);
		
		Intent i = new Intent(getSherlockActivity(), LocationPoller.class);
		
		Bundle bundle = new Bundle();
		LocationPollerParameter parameter = new LocationPollerParameter(bundle);
	    parameter.setIntentToBroadcastOnCompletion(new Intent(getSherlockActivity(), LocationReceiver.class));
	    // try network and fallback to gps?
	    // parameter.setProviders(new String[] {LocationManager.NETWORK_PROVIDER, LocationManager.GPS_PROVIDER});
	    // only gps
	    parameter.setProviders(new String[] {LocationManager.GPS_PROVIDER});
	    parameter.setTimeout(TIMEOUT);
	    i.putExtras(bundle);


	    pi=PendingIntent.getBroadcast(getSherlockActivity(), 0, i, 0);
	    mgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
	                                        SystemClock.elapsedRealtime(),
	                                        PERIOD,
	                                        pi);
	}
	
	public void stopCollection(){
		mgr = (AlarmManager) getSherlockActivity().getSystemService(getActivity().ALARM_SERVICE);
		Intent i = new Intent(getSherlockActivity(), LocationPoller.class);
		pi=PendingIntent.getBroadcast(getSherlockActivity(), 0, i, 0);
		mgr.cancel(pi);
	}

}
