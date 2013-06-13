package com.blackoutbuddy.android.data;

import java.util.Date;
import java.text.SimpleDateFormat;

public class BlackoutData {
	
	private long id;
	private long time;
	
	@Override
	public String toString() {
		return formatDate(time);
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

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

}
