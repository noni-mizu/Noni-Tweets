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
 * Class acts as a Java-representation of user information retrieved as a JSONObject 
 * from the Twitter REST API v1.1. Fields are as specified in the API GET users/show 
 * documentation. 
 */
@Table(name = "User_Info")
public class UserInfo extends Model implements Serializable {

	@Column(name = "profile_image_url")
	private String profileImageUrl;
	
	@Column(name = "User")						// Created for ActiveAndroid to establish a direct  
	private User user; 						// relationship with the User table
	
	public UserInfo() {							// Empty-argument constructor required by ActiveAndroid
		super();
	}
	
	public static UserInfo fromJson(JSONObject jsonObject) {
		UserInfo info = new UserInfo();
		try {
			info.profileImageUrl = jsonObject.getString("profile_image_url");
			Log.d("DEBUG", "Called UserInfo.fromJson(...). profileImageUrl is: " + info.profileImageUrl);
		} catch (JSONException e) {
			Log.d("DEBUG", "Thew exception in UserInfo.fromJson(...)"); 
			e.printStackTrace();
		}
		return info;
	}

	public String getProfileImageUrl() {
		return profileImageUrl;
	}

	public void setProfileImageUrl(String profileImageUrl) {
		
	}	
}
