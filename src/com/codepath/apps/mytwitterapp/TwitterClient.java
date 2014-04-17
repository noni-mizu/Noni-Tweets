package com.codepath.apps.mytwitterapp;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

import android.content.Context;
import android.content.SharedPreferences;

import com.codepath.apps.mytwitterapp.models.Tweet;
import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/fernandezpablo85/scribe-java/tree/master/src/main/java/org/scribe/builder/api
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * To modify the class: 
 * Specify the constants below to change the API being communicated with.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
    public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class; // Change this
    public static final String REST_URL = "https://api.twitter.com/1.1"; // Change this, base API URL
    public static final String REST_CONSUMER_KEY = "AmkT1B2IpmG1ZVMMMcTYp8mhD";  // Change this
    public static final String REST_CONSUMER_SECRET = "9EnOp840zWdBXjYo9ze44eB7BsO9gho3DSwjxaqrkrohVe19MZ"; // Change this
    public static final String REST_CALLBACK_URL = "oauth://mytwitterapp"; // Change this (here and in manifest), callback is arbitrary name
    private static boolean isInitialRequest = true; 
    
    public TwitterClient(Context context) {
        super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
    }
    
    /* Method makes GET request for home timeline resource from Twitter REST API v1.1. Twitter returns  
     * a rate-limited json-formatted response consisting of a collection of the most recent tweets and 
     * re-tweets posted by the authenticated user and the users he/she follows. 
     * 
     * General approach to defining methods for API endpoints: 
     * 1. Define the endpoint URL with getApiUrl and pass a relative path to the endpoint
     * 	  i.e getApiUrl("statuses/home_timeline.json");
     * 2. Define the parameters to pass to the request (query or body)
     *    i.e RequestParams params = new RequestParams("foo", "bar");
     * 3. Define the request method and make a call to the client
     *    i.e client.get(apiUrl, params, handler);
     *    i.e client.post(apiUrl, params, handler);
     */
    
    public void getHomeTimeline(AsyncHttpResponseHandler handler) {
    	String url = getApiUrl("statuses/home_timeline.json"); // Got this from my own Twitter API home_timeline
    	RequestParams params = new RequestParams();
    	params.put("count", "20");
    	
    	/*
    	 * To implement endless scrolling, update maxID (ie, the ID of the first tweet to be loaded) 
    	 * when BOTH of the following conditions hold: (i) you're making a follow-on request (ie, 
    	 * sIsInitialRequest == false) and (ii) the system isn't calling getHomeTimeline(...) 
    	 * after a change in runtime configuration, such as re-orienting the screen (ie, onPostDestroy 
    	 * == true)
    	 */
    	SharedPreferences prefs = context.getSharedPreferences("prefs", 0); 
    	boolean postOnDestroy = prefs.getBoolean("postOnDestroy", false); 
    	if (!isInitialRequest && !postOnDestroy) {
    		Tweet.decrementMaxId();								// Avoids duplication of max-ID tweet
    		params.put("max_id", Tweet.getMaxIdAsString());
    	}
    	
    	client.get(url, params, handler);
    	isInitialRequest = false;
    }
    
    /*
     * Method makes GET request to retrieve account settings for current user from Twitter REST API v1.1, 
     * e.g., to recover a screen_name. Twitter returns a rate-limited json-formatted response.
     */

    public void getAccountSettings(AsyncHttpResponseHandler handler) {
        String url = getApiUrl("account/settings.json");
        // Can specify query string params directly or through RequestParams.
        client.get(url, handler);
    }
    
    /*
     * Method makes GET request to retrieve information about specified user from Twitter REST API v1.1, 
     * e.g., to recover a profile_image_url. Twitter returns a rate-limited json-formatted response.
     */
    public void getUserInformation(String screenName, AsyncHttpResponseHandler handler) {
    	String url = getApiUrl("users/show.json");
    	RequestParams params = new RequestParams();
    		// Next line added because asycn request in getAccountSettings() does not return quickly enough 
    		// to supply a value for the screenName parameter. Documentation for AsyncHttpResponseHandler
			// indicates a setUseSynchronousMode(boolean), but the method appears to be broken.
    	screenName = "twitter_handle"; 				
    	params.put("screen_name", screenName);
    	client.get(url, params, handler); 
    }
    
    /*
     * Method issues POST command for submitting a tweet via Twitter REST API v1.1. Tweet text typically 
     * limited to 140 characters.  
     */
    public void postTweet(String tweetBody, AsyncHttpResponseHandler handler) { 
    	String url = getApiUrl("statuses/update.json");
    	RequestParams params = new RequestParams();
    	params.put("status", tweetBody);
    	client.post(url, params, handler); 
    }
    
    /*
     * --- NOTE: USE OF METHOD DISCOURAGED ---
     * Method makes GET request to verify account credentials with Twitter REST API v1.1, e.g., to recover
     * a profile_image_url. Twitter returns a rate-limited json-formatted response consisting of
     * key-value pairs representing the requesting user's current settings. 
     */
    public void getAccountCredentials(AsyncHttpResponseHandler handler) {
    	String url = getApiUrl("account/verify_credentials.json");
    	client.get(url, handler);
    }
    
    /* 1. Define the endpoint URL with getApiUrl and pass a relative path to the endpoint
     * 	  i.e getApiUrl("statuses/home_timeline.json");
     * 2. Define the parameters to pass to the request (query or body)
     *    i.e RequestParams params = new RequestParams("foo", "bar");
     * 3. Define the request method and make a call to the client
     *    i.e client.get(apiUrl, params, handler);
     *    i.e client.post(apiUrl, params, handler);
     */
}