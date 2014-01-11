package com.srcreigh.hub.auth;

import com.srcreigh.hub.R;
import com.srcreigh.hub.root.MainActivity;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

public class TwitterWrapper {
	
	// Singleton patterning.
	private static TwitterWrapper instance;
	protected TwitterWrapper() {

		mTwitter = new TwitterFactory().getInstance(); // new?
		Log.i("sourceray", "Got Twitter4j");
		
		mTwitter.setOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
		Log.i("sourceray", "Authenticated app with t4j");
		
	}
	
	public static TwitterWrapper getInstance() {
		if (instance == null) instance = new TwitterWrapper();
		return instance;
	}
	 
	// Consumer Key generated when you registered your app at https://dev.twitter.com/apps/
	public static final String CONSUMER_KEY = "hhmIfpgBwQBpvEGbb7tXg";
	// Consumer Secret generated when you registered your app at https://dev.twitter.com/apps/ 
	public static final String CONSUMER_SECRET = "WG0dm53aOOq3uz5wx4PQQniCeEU4kO76k7o3VaOtZ40"; // XXX Encode in your app
	public static final String CALLBACK_URL = "srcreighhub:///";
	private Twitter mTwitter;
	private RequestToken mReqToken;
	private AccessToken at;
	
	private static class TwitterActivity extends Activity {
		
		private String oauthVerifier;
		private String authUrl;
		
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			
			authUrl = getIntent().getStringExtra("AUTHURL");
			// THIS STUFF NEEDS TO HAPPEN IN THE AUTHENTICATION MENU SCREEN
			if (android.os.Build.VERSION.SDK_INT > 9) {
			      StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			      StrictMode.setThreadPolicy(policy);
		    }
			webviewLogin();	
		}
		
		public void webviewLogin() {
			try {
				Log.i("sourceray", "Starting Webview to login to twitter");
				WebView twitterSite = new WebView(this);
				twitterSite.loadUrl(authUrl);
				setContentView(twitterSite);
			} catch (TwitterException e) {
				Toast.makeText(this, "Twitter Login error, try again later", Toast.LENGTH_SHORT).show();
				Log.e("sourceray", "exception", e);
			}
		}
		
		/**
		 * Catch when Twitter redirects back to our {@link CALLBACK_URL}</br> 
		 * We use onNewIntent as in our manifest we have singleInstance="true" if we did not the
		 * getOAuthAccessToken() call would fail
		 */
		@Override
		protected void onNewIntent(Intent intent) {
			super.onNewIntent(intent);
			Log.i("sourceray", "Intent received from Twitter login");
			Uri uri = intent.getData();
			if (uri != null && uri.toString().startsWith(CALLBACK_URL)) { // If the user has just logged in
				oauthVerifier = uri.getQueryParameter("oauth_verifier");
				Intent data = new Intent();
				data.putExtra("OAUTH", oauthVerifier);
				setResult(RESULT_OK, data);
			} else {
				setResult(RESULT_CANCELED);
			}
			finish();
		}
		
	}
	
	public void login(Context context) {
		Log.i("sourceray", "Requesting App Authentication");
		mReqToken = mTwitter.getOAuthRequestToken(CALLBACK_URL);
		
		Intent intent = new Intent(context, TwitterActivity.class);
		intent.putExtra("AUTHURL", mReqToken.getAuthenticationURL());
		context.startActivityForResult(intent);
	}
	
	public void receiveOAuth(String oauth) {
		try {
			at = mTwitter.getOAuthAccessToken(mReqToken, oauth);
			mTwitter.setOAuthAccessToken(at);
			/* for debugging/yoloing purposes
			try {
				mTwitter.updateStatus("Good morning 'murica! #yhacks #twitter4j #yolo #swag");
				Log.i("sourceray", "Tweet sent :')");
			} catch (TwitterException e) {
				Log.i("sourceray", "tweet error :(");
				Log.e("sourceray", "exception", e);
			}
			*/
			
			Log.i("sourceray", "access token linked; passing tokens to prefs");
			onSuccessfulAuth(at);
			
		} catch (TwitterException e) {
			Toast.makeText(this, "Twitter auth error x01, try again later", Toast.LENGTH_SHORT).show();
		}
	}
	
	public void onSuccessfulAuth(AccessToken at) {
		// Save the twitter token in the prefs and finish the activity with RESULT_OK
		SharedPreferences settings = getSharedPreferences(MainActivity.PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();

		String token = at.getToken();
		String secret = at.getTokenSecret();
		String username = at.getScreenName();
		
		editor.putString(MainActivity.TWITTER_AUTH_TOKEN, token);
		editor.putString(MainActivity.TWITTER_AUTH_TOKEN_SECRET, secret);
		editor.putString(MainActivity.USER_ID, username);
	    editor.commit();
	    
	    Log.i("sourceray", "prefs saved to user" + username);
	}	
}
