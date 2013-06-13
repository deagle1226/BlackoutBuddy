package com.blackoutbuddy.android.app;

import com.google.android.maps.MapView;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class FragmentAdapter extends FragmentPagerAdapter {
	
	public static final int PAGE_COUNT = 2;
	private MapView mapView;

	public FragmentAdapter(FragmentManager fm, MapView mp) {
        super(fm);
        mapView = mp;
    }

    @Override
    public Fragment getItem(int position) {
    	BlackoutFragment bf = new BlackoutFragment();
    	MainFragment mf = new MainFragment();
    	MapFragment mp = new MapFragment(mapView);
    	if (position == 0) return mp;
    	else return bf;
    }

    @Override
    public int getCount() {
    	return PAGE_COUNT;
    }
    
}
