package com.codepath.apps.mytwitterapp;

import java.util.ArrayList;  

import org.json.JSONArray;  
import org.json.JSONObject;

import android.content.Intent;  
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ListView;

import com.activeandroid.util.Log;
import com.codepath.apps.mytwitterapp.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;
   
   
public class TimelineActivity extends ActionBarActivity {
	
//	private PullDownListView listView; //(for pull to refresh) 
	private static final int COMPOSE_TWEET_REQUEST = 1;
	
	private TweetsAdapter adapter;
	private ArrayList<Tweet> tweetsArray; 
	private ListView      	 lvTweets;  
	 
	private static String    screenName;
	private static String    userImageUrl; 
	private static boolean   firstTimeCalled = true;
 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_timeline);
		
		// For the pull to refresh feature
//		ListView lvTweets = (ListView) findViewById(R.id.lvTweets);
//		listView = new PullDownListView(this);
//        setContentView(listView);
//        listView.setListViewTouchListener(this);
		
		tweetsArray = new ArrayList<Tweet>(); 
		adapter = new TweetsAdapter(TimelineActivity.this, tweetsArray);
		adapter.clear(); 
		
		lvTweets = (ListView) findViewById(R.id.lvTweets); 
		lvTweets.setAdapter(adapter);
		adapter.clear(); 
		
		createHomeTimeline();
		
		if (firstTimeCalled) {
			getUserScreenName();			// Methods' results needed for ComposeTweetActivity. (Initiated here 
			getUserImageUrl(screenName); 	// because of async delay.) 
			firstTimeCalled = false;		// Assumes no intervening update of screen name or profile image.
		}
	}	
		
		private void createHomeTimeline() {
			MyTwitterApp.getRestClient().getHomeTimeline(new TwitterListViewHandler());
			showProgressBar(); 
		}
		
		private class TwitterListViewHandler extends JsonHttpResponseHandler {
		//JSON data interpreted
			@Override
			public void onSuccess (JSONArray jsonTweets) {
				ArrayList<Tweet> tweets = Tweet.fromJson(jsonTweets);
				tweetsArray.addAll(tweets); 
				adapter.notifyDataSetChanged();
				
				lvTweets.setOnScrollListener(new EndlessScrollListener() {
					@Override
					public void onLoadMore(int page, int totalItemsCount) {
						createHomeTimeline();
						adapter.notifyDataSetChanged(); 	// Unlike setAdapter(...), notifyDataSetChanged() will
															// not reset the user's position to the top of the ListView
					}
				}); //End setOnScrollListener
				
				hideProgressBar();
			} // End onSuccess 
			@Override
			public void onFailure(Throwable e, JSONArray error) {
				Log.e("ERROR", e.getMessage()); 
			}		
		} // End the handlers
		
		private void getUserScreenName() {
			MyTwitterApp.getRestClient().getAccountSettings(new JsonHttpResponseHandler() {
				@Override
			 	public void onSuccess(int statusCode, JSONObject jsonSettings) {
					screenName = UserAccountSettings.fromJson(jsonSettings).getScreenName(); 
				}
				
				@Override
				public void onFailure(Throwable e, JSONObject error) {
					Log.e("ERROR", e.getMessage()); 
				}
			});
		}
		
		private void getUserImageUrl(String screenName) {
			MyTwitterApp.getRestClient().getUserInformation(screenName, new JsonHttpResponseHandler() {
				@Override
				public void onSuccess(int statusCode, JSONObject jsonUserInfo) {
					userImageUrl = UserInfo.fromJson(jsonUserInfo).getProfileImageUrl(); 
				}
		
				@Override
				public void onFailure(Throwable e, JSONObject error) {
					Log.e("ERROR", e.getMessage()); 
				}
			});
		}
		
		@Override
		protected void onStop() {
			super.onStop(); 
			if (tweetsArray != null) {
				new AsyncTweetsSave().execute(tweetsArray); 
			}
		}
		
		@Override
		protected void onDestroy() {
			super.onDestroy();
			SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
			SharedPreferences.Editor editor = prefs.edit();
			editor.putBoolean("postOnDestroy", true); 
			editor.commit(); 
		}
		
		// To be called manually when an async task has started
		public void showProgressBar() {
			setProgressBarIndeterminateVisibility(true);
		}
		
		// To be called manually when an async task has finished
		public void hideProgressBar() {
			setProgressBarIndeterminateVisibility(false); 
		}
		
		public static String getScreenName() {  
			return screenName;
		}
		
		public static String getUserImageUrl() {
			return userImageUrl;
		}
	
//	 public void onListViewPulledDown(){
//         Log.d("PullDownListViewActivity", "ListView pulled down");
//     }
  
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.timeline, menu);
	    return super.onCreateOptionsMenu(menu);
	}
      
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {
		case R.id.menu_compose_tweet:
			Intent i = new Intent(TimelineActivity.this, ComposeTweetActivity.class);
			startActivityForResult(i, COMPOSE_TWEET_REQUEST); 
			break;
		}
		return true; 
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK && requestCode == COMPOSE_TWEET_REQUEST) {
			Tweet tweet = (Tweet) data.getSerializableExtra("new_tweet"); 
			tweetsArray.add(0, tweet);
			adapter.notifyDataSetChanged(); 
		} 
	}

}
