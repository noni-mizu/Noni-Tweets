package com.codepath.apps.mytwitterapp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.text.format.DateUtils;

import com.activeandroid.util.Log;


public class RelativeTimestamp {
	
	private Date mTimeTweetCreated;
	private String mRelativeTimestamp;
	
	public RelativeTimestamp(Context context, String createdAt) {
		mTimeTweetCreated = parseString(createdAt);
		
		mRelativeTimestamp = (String) DateUtils
				.getRelativeDateTimeString(context, mTimeTweetCreated.getTime(),
						DateUtils.MINUTE_IN_MILLIS, DateUtils.WEEK_IN_MILLIS, 0);
	}
	
	private Date parseString(String createdAt) {
		
		SimpleDateFormat formatter = new SimpleDateFormat
				("EEE MMM dd HH:mm:ss zzzz yyyy");
		Date createdAtDate = null;
		
		try{
			createdAtDate = formatter.parse(createdAt);
		} catch (ParseException e) {
			Log.d("DEBUG", "Threw ParseException");
			e.printStackTrace();
		}
	return createdAtDate;
		
	}
	
	public void setTimeTweetCreated(Date timeCreated) {
		mTimeTweetCreated = timeCreated;
	}
	
	public String getRelativeTimestamp() {
		return mRelativeTimestamp;
	}
	
	public void setRelativeTimestamp(String timestamp) {
		mRelativeTimestamp = timestamp;
	}
}
