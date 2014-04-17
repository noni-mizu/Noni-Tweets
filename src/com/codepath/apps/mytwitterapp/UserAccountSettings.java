package com.codepath.apps.mytwitterapp;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.codepath.apps.mytwitterapp.models.User;

/**
 * Class acts as a Java-representation of user account settings retrieved as a JSONObject 
 * from the Twitter REST API v1.1. Fields are as specified in the API GET account/settings 
 * documentation. 
 */
@Table(name = "User_Account_Settings")
public class UserAccountSettings extends Model implements Serializable {

	@Column(name = "screen_name")
	private String screenName;
	
	@Column(name = "User")						// Created for ActiveAndroid to establish a direct  
	private User  user; 						// relationship with the User table
	
	public UserAccountSettings() {				// Empty-argument constructor required by ActiveAndroid
		super(); 
	}
	
	public static UserAccountSettings fromJson(JSONObject jsonObject) {
		UserAccountSettings settings = new UserAccountSettings();
		try {
			settings.screenName = jsonObject.getString("screen_name"); 
			Log.d("DEBUG", "Called UserAccountSettings.fromJson(...). mScreenName is: " + settings.screenName);
		} catch (JSONException e) {
			Log.d("DEBUG", "Thew exception in UserAccountSettings.fromJson(...)"); 
			e.printStackTrace();
		}
		return settings; 
	}

	public String getScreenName() {
		return screenName;
	}

	public void setScreenName(String screenName) {
		
	}
	
}
