package com.codepath.apps.mytwitterapp;

import java.util.List;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.mytwitterapp.models.Tweet;
import com.nostra13.universalimageloader.core.ImageLoader;

// This class creates a custom adapter
public class TweetsAdapter extends ArrayAdapter<Tweet>{
	
	// The tweets are channeled through the super, so that ArrayAdapter can use
	// all of its helper methods
	public TweetsAdapter(Context context, List<Tweet> tweets){
		super(context, 0, tweets);
	}
	
	/*
	 * Class lets us retain the handles to View objects we retrieve from the rather
	 * expensive method findViewById(int). Reusing these handles, rather than calling 
	 * findViewById(int) unnecessarily, provides a performance boost for the ListView 
	 * associated with the TweetsAdapter. 
	 */
	private static class ViewHolder {
		ImageView imageView;
		TextView  nameView;
		TextView  createdAtView;
		TextView  bodyView;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// getItem() ask our adapter for the tweet at our current position 
		// that we're trying to populate
		Tweet tweet = getItem(position);
		ViewHolder viewHolder;
		// convertView lets you reuse views, when scrolling, views come and go
		// so you can reuse them, instead of re-inflating them
		View view = convertView;
		// If statement: if our view is null, then get from our layout file: tweet_item
		if (convertView == null) {
			viewHolder = new ViewHolder();
			LayoutInflater inflator = (LayoutInflater) getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflator.inflate(R.layout.tweet_item, null);
			viewHolder.imageView     = (ImageView) convertView.findViewById(R.id.ivProfile); 
			viewHolder.nameView      = (TextView)  convertView.findViewById(R.id.tvName);
			viewHolder.createdAtView = (TextView)  convertView.findViewById(R.id.tvCreatedAt); 
			viewHolder.bodyView      = (TextView)  convertView.findViewById(R.id.tvBody);
			
			// Avoid superfluous calls to (expensive) findViewById(int) by tagging the now 
			// assembled convertView
			convertView.setTag(viewHolder); 
		} else {
			// Leverage the assembled View handles in convertView by retrieving its tag
			viewHolder = (ViewHolder) convertView.getTag();
		}
	
		// Here we load our image, and the ImageLoader asynchronously gets
		// our image from the network and sets our imageView. ImageLoader is 
		// from a 3rd party library
		ImageLoader.getInstance().displayImage(tweet.getUser()
				.getProfileImageUrl(), viewHolder.imageView);
	              
		
		
		// Name of the user
		String formattedName = "<b>" + tweet.getUser().getName() + "</b>" 
				+ " <small><font color='#777777'>@"
				+ tweet.getUser().getScreenName() + "</font></small>";
		viewHolder.nameView.setText(Html.fromHtml(formattedName));
		
		
		// Timestamp
		//createdAtView.setText(Html.fromHtml(tweet.getCreatedAt()));--MAYBE TAKE OUT
		// Relative timestamp part
		RelativeTimestamp rt = new RelativeTimestamp(getContext(), tweet.getCreatedAt());
		String timestamp = rt.getRelativeTimestamp();
		viewHolder.createdAtView.setText(Html.fromHtml(timestamp));
		
		
		// Body of the tweet message 
		viewHolder.bodyView.setText(Html.fromHtml(tweet.getBody()));
		
		return convertView;
	}

}
