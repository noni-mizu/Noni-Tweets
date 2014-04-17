package com.codepath.apps.mytwitterapp;

import java.util.ArrayList;

import android.os.AsyncTask;
import android.util.Log;

import com.codepath.apps.mytwitterapp.models.Tweet;

/**
 * Helper class uses Tweet.saveTweets(...) to save an array of tweets asynchronously.
 */
public class AsyncTweetsSave extends AsyncTask<ArrayList<Tweet>, Void, Void> {

	@Override
	protected Void doInBackground(ArrayList<Tweet>... tweets) {
		Tweet.saveTweets(tweets[0]); 
		return null;
	}
	
	@Override
	protected void onPostExecute(Void result) {
		Log.i("INFO", "Tweet.saveTweets(...) executed."); 
	}
	
}