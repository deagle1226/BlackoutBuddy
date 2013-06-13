package com.blackoutbuddy.android.app;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.blackoutbuddy.android.locpoll.LocationPoller;
import com.blackoutbuddy.android.locpoll.LocationPollerParameter;
import com.google.android.maps.MapView;
import com.viewpagerindicator.LinePageIndicator;
import com.viewpagerindicator.PageIndicator;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MotionEvent;

public class MainActivity extends SherlockFragmentActivity {
	public static final String TAG = MainActivity.class.getSimpleName();
	
	private FragmentAdapter adapter;
	private ViewPager pager;
	private PageIndicator indicator;
	private MapView mapView;
	

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mapView = new MapView(this, "0g7BoH8AlOA3w7Z92kQd7WMs1LtlJtWglm6ggGA");
    }

	@Override
	protected void onResume() {
		super.onResume();
		adapter = new FragmentAdapter(getSupportFragmentManager(), mapView);
		adapter.notifyDataSetChanged();
		
		pager = (ViewPager)findViewById(R.id.pager);
		pager.setAdapter(adapter);
		
		indicator = (LinePageIndicator)findViewById(R.id.indicator);
		indicator.setViewPager(pager);
		indicator.notifyDataSetChanged();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(R.string.menu_settings)
			.setIntent(new Intent(this, BBPreferences.class))
	        .setIcon(R.drawable.ic_settings)
	        .setShowAsAction(MenuItem.SHOW_AS_ACTION_WITH_TEXT);
	
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}

    
}
