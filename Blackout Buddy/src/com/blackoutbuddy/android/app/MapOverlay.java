package com.blackoutbuddy.android.app;

import java.util.ArrayList;

import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

@SuppressWarnings("rawtypes")
public class MapOverlay extends ItemizedOverlay {
	
	private ArrayList<OverlayItem> overlays = new ArrayList<OverlayItem>();

	public MapOverlay(Drawable drawable) {
		super(boundCenterBottom(drawable));
		
	}

	@Override
	protected OverlayItem createItem(int i) {
		return overlays.get(i);
	}

	@Override
	public int size() {
		return overlays.size();
	}
	
	public void addOverlay(OverlayItem overlay) {
	    overlays.add(overlay);
	    populate();
	}

}
