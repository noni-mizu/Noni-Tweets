package com.codepath.apps.mytwitterapp.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Class acts as a Java-representation of a single tweet retrieved as a JSONObject from the 
 * Twitter REST API v1.1. Fields are as specified in the API Tweets object documentation. 
 */
@Table(name = "Tweets")
public class Tweet extends Model implements Serializable {
	
	@Column(name = "max_id")
	private static long maxId; 	// ID of the last (ie, earliest-timestamped) tweet to be processed in the current JSONArray
	
	@Column(name = "tweet_id", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
	private long    	tweetId;	// Tweet ID
	@Column(name = "created_at")
	private String  	createdAt; 
	@Column(name = "tweet_body")
	private String  	body;
	@Column(name = "favorited")
	private boolean 	favorited;
	@Column(name = "retweeted")
	private boolean 	retweeted;
	@Column(name = "user")
    private User    	user;

	public Tweet() {				// Empty-argument constructor required by ActiveAndroid
		super(); 
	}	
	
    public static long getMaxId() {
    	return maxId;
    }
    
    public static String getMaxIdAsString() {
    	return String.valueOf(maxId);
    }
    
    public static void decrementMaxId() {
    	maxId -= 1; 
    }
    
    public long getTweetId() {
        return tweetId;
    }

    public String getCreatedAt() {
    	return createdAt;
    }
    
    public String getBody() {
        return body;
    }

    public boolean isFavorited() {
        return favorited;
    }

    public boolean isRetweeted() {
        return retweeted;
    }

    public User getUser() {
        return user;
    }
    
    // Optional helper method for ActiveAndroid to establish a direct relationship with the Users table
 	public List<User> getUsers() {
 		return getMany(User.class, "Tweet");
 	}
    
    public static Tweet fromJson(JSONObject jsonObject) {
        Tweet tweet = new Tweet();
        try {
        	tweet.tweetId 		 = jsonObject.getLong("id");
        	tweet.createdAt = jsonObject.getString("created_at");
        	tweet.body 	 = jsonObject.getString("text");
        	tweet.favorited = jsonObject.getBoolean("favorited");
        	tweet.retweeted = jsonObject.getBoolean("retweeted");
            tweet.user 	 = User.fromJson(jsonObject.getJSONObject("user"));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return tweet;
    }

    public static ArrayList<Tweet> fromJson(JSONArray jsonArray) {
        ArrayList<Tweet> tweets = new ArrayList<Tweet>(jsonArray.length());

        for (int i=0; i < jsonArray.length(); i++) {
            JSONObject tweetJson = null;
            try {
                tweetJson = jsonArray.getJSONObject(i);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }

            Tweet tweet = Tweet.fromJson(tweetJson);
            if (tweet != null) {
            	maxId = tweet.getTweetId();
                tweets.add(tweet);
            }
            
        }
        return tweets;
    }
    
    public static void saveTweet(Tweet tweet) {
    	tweet.user.save();
    	tweet.save();
    }
    
    public static void saveTweets(ArrayList<Tweet> tweets) {
    	ActiveAndroid.beginTransaction();
    	try {
	    	for (int i = 0; i < tweets.size(); i++) {
	    		Tweet t = tweets.get(i);
	    		Log.d("DEBUG", "Inside saveTweets(ArrayList<Tweet>), current tweet is: "  + t.toString());
	    		if (t != null) {
	    			if (t.user != null) {
	    				t.user.save();
	    			} 
	    			t.save(); 
	    		}
	    	}
	    	ActiveAndroid.setTransactionSuccessful();
    	} finally {
    		ActiveAndroid.endTransaction(); 
    	}
    }
    
}