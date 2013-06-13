package com.blackoutbuddy.android.data;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LocationData {
	
	private long id;
	private double latitude;
	private double longitude;
	private float accuracy;
	private long time;
	private long group;
	private String provider;

	@Override
	public String toString() {
		return provider + " " + formatDate(time) + "\n"
				+ id + " " + group + " " + accuracy;
	}

	public String formatDate(long date) {
		SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy hh:mm:ss a");
		Date resultdate = new Date(date);
		String result = sdf.format(resultdate);
		return result;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public float getAccuracy() {
		return accuracy;
	}

	public void setAccuracy(float accuracy) {
		this.accuracy = accuracy;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public long getGroup() {
		return group;
	}

	public void setGroup(long group) {
		this.group = group;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}
}
