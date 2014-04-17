package com.codepath.apps.mytwitterapp;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.mytwitterapp.models.Tweet;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ComposeTweetActivity extends ActionBarActivity {

	private Button 	  btnCancel,
				   	  btnTweet;
	private ImageView ivUserImage;
	private TextView  tvUserName;
	private EditText  etNewTweet;
	
	private boolean   alreadyToasted = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_compose_tweet);
		
		setupButtons();
		setupImageView();
		setupTextView();
		setupEditText();
	}

	private void setupButtons() {
		btnCancel = (Button) findViewById(R.id.btnCancel); 
		btnCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent();
				setResult(RESULT_CANCELED, i);
				finish(); 
			}
		});
		
		btnTweet = (Button) findViewById(R.id.btnTweet); 
		btnTweet.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String tweetBody = etNewTweet.getText().toString();
				tweet(tweetBody);
			}
		});	
	}
	
	private void setupImageView() {
		ivUserImage = (ImageView) findViewById(R.id.ivUserImage);
		Log.d("DEBUG", "TimelineActivity.sScreenName is: " + TimelineActivity.getScreenName());
		Log.d("DEBUG", "TimelineActivity.sImageUrl just before execution of "
				     + "ImageLoader.getInstance().displayImage(mImageUrl, mIvUserImage) is: " 
				     + TimelineActivity.getUserImageUrl());
		ImageLoader.getInstance().displayImage(TimelineActivity.getUserImageUrl(), ivUserImage);
	}
	
	private void setupTextView() {
		tvUserName = (TextView) findViewById(R.id.tvUserName); 
		tvUserName.setText("@" + TimelineActivity.getScreenName()); 
	}
	
	private void setupEditText() {
		etNewTweet = (EditText) findViewById(R.id.etNewTweet);  
		
		// Show soft keyboard when EditText field requests focus
		if (etNewTweet.requestFocus()) {
			InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			mgr.showSoftInput(etNewTweet, InputMethodManager.SHOW_IMPLICIT); 
		}
		
		etNewTweet.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (!alreadyToasted && s.length() == 140) {
					Toast.makeText(ComposeTweetActivity.this, "You've reached the 140 character"
							+ " limit", Toast.LENGTH_LONG).show(); 
					alreadyToasted = true; 
				}
				else if (s.length() > 140) {
					etNewTweet.setTextColor(Color.RED); 
				} else {
					etNewTweet.setTextColor(Color.BLACK); 
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
			
			@Override
			public void afterTextChanged(Editable s) { }
		});
		
	}
	
	private void tweet(String tweetBody) {
		MyTwitterApp.getRestClient().postTweet(tweetBody, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, JSONObject jsonTweetResponse) {
				Log.d("DEBUG", "Called onSuccess() in tweet()."); 
				Tweet newTweet = Tweet.fromJson(jsonTweetResponse); 
				new AsyncTweetSave().execute(newTweet); 
				Intent i = new Intent();
				i.putExtra("new_tweet", newTweet);
				setResult(RESULT_OK, i);
				finish(); 
			}	
	
			@Override
			public void onFailure(Throwable e, JSONObject error) {
				Log.d("DEBUG", "Called onFailure() in getUserScreenName(). Failure message: " 
								+ AsyncHttpResponseHandler.FAILURE_MESSAGE);
				Log.e("ERROR", e.getMessage());
			}
		});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.compose_tweet, menu);
		return true;
	}

}
