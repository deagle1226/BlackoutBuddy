package com.blackoutbuddy.android.app;

import java.util.List;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockMapActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.blackoutbuddy.android.data.DataSource;
import com.blackoutbuddy.android.data.LocationData;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class BlackoutMap extends SherlockMapActivity {

	private MapView map;
	private MapController controller;
	private List<Overlay> overlays;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.map);
		map = (MapView) findViewById(R.id.mapview);
		controller = map.getController();
		overlays = map.getOverlays();
		Drawable drawable = this.getResources().getDrawable(
				R.drawable.map_overlay);
		MapOverlay mapOverlay = new MapOverlay(drawable);

		Intent intent = getIntent();
		long group = intent.getLongExtra("group", 0);
		DataSource datasource = new DataSource(this);
		datasource.open();
		List<LocationData> locations = datasource.getLocations(group);
		datasource.close();

		for (LocationData loc : locations) {
			GeoPoint point = new GeoPoint((int) (loc.getLatitude() * 1E6),
					(int) (loc.getLongitude() * 1E6));
			OverlayItem overlayItem = new OverlayItem(point, "1", "text");
			mapOverlay.addOverlay(overlayItem);
		}
		overlays.add(mapOverlay);
		map.invalidate();
		if (locations.size() > 0) {
			controller.animateTo(new GeoPoint((int) (locations.get(0)
					.getLatitude() * 1E6), (int) (locations.get(0)
					.getLongitude() * 1E6)));
		}

	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(R.string.menu_settings)
				.setIntent(new Intent(this, BBPreferences.class))
				.setIcon(R.drawable.ic_settings)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		menu.add("current")
				.setIcon(R.drawable.ic_loc_on)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		menu.add("location")
				.setIcon(R.drawable.ic_location)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

		return super.onCreateOptionsMenu(menu);
	}

}
