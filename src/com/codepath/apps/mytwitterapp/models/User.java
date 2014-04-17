package com.codepath.apps.mytwitterapp.models;

import java.io.Serializable;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.codepath.apps.mytwitterapp.UserAccountSettings;
import com.codepath.apps.mytwitterapp.UserInfo;

/**
 * Class acts as a Java-representation of a single user retrieved as a JSONObject from the 
 * Twitter REST API v1.1. Fields are as specified in the API Users object documentation. 
 */
@Table(name = "Users")
public class User extends Model implements Serializable {
	
	@Column(name = "user_name")
	private String name;
	@Column(name = "user_id", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
	private long   userId;						// User ID
	@Column(name = "screen_name")
	private String screenName;
	@Column(name = "profile_image_url")
	private String profileImageUrl;
	@Column(name = "profile_background_image_url")
	private String profileBackgroundImageUrl;				
	@Column(name = "tweet_count")
	private int    tweetCount;					// Referred to as statuses_count in Twitter API
	@Column(name = "followers_count")
	private int    followersCount;
	@Column(name = "friends_count") 
	private int    friendsCount;
	
	@Column(name = "Tweet")						// Created for ActiveAndroid to establish a direct  
	private Tweet  tweet; 						// relationship with the Tweets table
	
	public User() {								// Empty-argument constructor required by ActiveAndroid
		super(); 
	}
	
    public String getName() {
        return name;
    }

    public long getUserId() {
        return userId;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getProfileImageUrl() {
    	return profileImageUrl;
    }
    
    public String getProfileBackgroundImageUrl() {
        return profileBackgroundImageUrl;
    }

    public int getNumTweets() {
        return tweetCount;
    }

    public int getFollowersCount() {
        return followersCount;
    }

    public int getFriendsCount() {
        return friendsCount;
    }

    // Optional helper method for ActiveAndroid to establish a direct relationship with the 
    // UserAccountSettings table
  	public List<UserAccountSettings> getUserAccountSettings() {
  		return getMany(UserAccountSettings.class, "User");
  	}
  	
  	// Optional helper method for ActiveAndroid to establish a direct relationship with the 
    // UserInfo table
  	public List<UserInfo> getUserInfo() {
  		return getMany(UserInfo.class, "User");
  	}
    
    public static User fromJson(JSONObject jsonObject) {
        User u = new User();
        try {
        	u.name 			 	 	 = jsonObject.getString("name");
        	u.userId 						 = jsonObject.getLong("id");
        	u.screenName 	    		 = jsonObject.getString("screen_name");
        	u.profileImageUrl   		 = jsonObject.getString("profile_image_url");
        	u.profileBackgroundImageUrl = jsonObject.getString("profile_background_image_url");
        	u.tweetCount 				 = jsonObject.getInt("statuses_count");
        	u.followersCount 			 = jsonObject.getInt("followers_count");
        	u.friendsCount 	 		 = jsonObject.getInt("friends_count");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return u;
    }

}

