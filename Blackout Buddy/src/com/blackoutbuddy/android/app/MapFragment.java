package com.blackoutbuddy.android.app;

import java.util.List;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;
import com.blackoutbuddy.android.data.DataSource;
import com.blackoutbuddy.android.data.LocationData;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class MapFragment extends SherlockFragment {
	
	private MapView mapView;
	
	public MapFragment(MapView mp){
		mapView = mp;
	}

	@Override
	public void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setRetainInstance(true);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup vg, Bundle data) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getSherlockActivity());
		DataSource datasource = new DataSource(getSherlockActivity());
		datasource.open();
		List<LocationData> locations = datasource.getLocations(prefs.getLong("selectedBlackout", 1));
		datasource.close();
		Log.d("BLACKOUS", "showing locs for: " + prefs.getLong("selectedBlackout", 1));
		List<Overlay> overlays = mapView.getOverlays();
		Drawable drawable = this.getResources().getDrawable(R.drawable.ic_launcher);
		MapOverlay mapOverlay = new MapOverlay(drawable);
		for (LocationData loc : locations){
			GeoPoint point = new GeoPoint((int)(loc.getLatitude() * 1E6), (int)(loc.getLongitude() * 1E6));
			OverlayItem overlayItem = new OverlayItem(point, "1", "text");
			mapOverlay.addOverlay(overlayItem);
		}
		overlays.add(mapOverlay);
		MapController controller = mapView.getController();
		controller.zoomToSpan(mapOverlay.getLatSpanE6(), mapOverlay.getLonSpanE6());
		
		// The Activity created the MapView for us, so we can do some init stuff.
		mapView.setClickable(true);
		mapView.setBuiltInZoomControls(true); // If you want.

		/*
		 * If you're getting Exceptions saying that the MapView already has
		 * a parent, uncomment the next lines of code, but I think that it
		 * won't be necessary. In other cases it was, but in this case I
		 * don't this should happen.
		 */
		
		 final ViewGroup parent = (ViewGroup) mapView.getParent();
		 if (parent != null) parent.removeView(mapView);
		 
		
		return mapView;
	}
}
