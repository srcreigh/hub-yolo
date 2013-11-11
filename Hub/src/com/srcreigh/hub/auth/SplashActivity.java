package com.srcreigh.hub.auth;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

import com.srcreigh.hub.R;
import com.srcreigh.hub.root.MainActivity;

import android.app.Activity;
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

public class SplashActivity extends Activity {
	
	private Button twitterButton;
	public static AccessToken at;
	
	/** Consumer Key generated when you registered your app at https://dev.twitter.com/apps/ */
	public static final String CONSUMER_KEY = "hhmIfpgBwQBpvEGbb7tXg";
	/** Consumer Secret generated when you registered your app at https://dev.twitter.com/apps/  */
	public static final String CONSUMER_SECRET = "WG0dm53aOOq3uz5wx4PQQniCeEU4kO76k7o3VaOtZ40"; // XXX Encode in your app
	private static final String CALLBACK_URL = "srcreighhub:///";
	private Twitter mTwitter;
	private RequestToken mReqToken;
	
	
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_activity);
		
		if (android.os.Build.VERSION.SDK_INT > 9) {
		      StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		      StrictMode.setThreadPolicy(policy);
	    }
		
		mTwitter = new TwitterFactory().getInstance();
		Log.i("sourceray", "Got Twitter4j");
		
		mTwitter.setOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
		Log.i("sourceray", "Authenticated app with t4j");
		
		twitterButton = (Button) findViewById(R.id.connectToTwitterButton);
		twitterButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				// DO AUTH STUFF HERE CHARLES

				// okay, shane!!
				
				Log.i("sourceray", "Login Pressed");
				loginNewUser();
			}
		});
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
	    
	    Intent intent = new Intent(this, MainActivity.class);
	    startActivity(intent);
	}
	
	private void loginNewUser() {
		try {
			Log.i("sourceray", "Requesting App Authentication");
			mReqToken = mTwitter.getOAuthRequestToken(CALLBACK_URL);

			Log.i("sourceray", "Starting Webview to login to twitter");
			WebView twitterSite = new WebView(this);
			twitterSite.loadUrl(mReqToken.getAuthenticationURL());
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
		dealWithTwitterResponse(intent);
	}

	/**
	 * Twitter has sent us back into our app</br> 
	 * Within the intent it set back we have a 'key' we can use to authenticate the user
	 * 
	 * @param intent
	 */
	private void dealWithTwitterResponse(Intent intent) {
		Uri uri = intent.getData();
		if (uri != null && uri.toString().startsWith(CALLBACK_URL)) { // If the user has just logged in
			String oauthVerifier = uri.getQueryParameter("oauth_verifier");
			authoriseNewUser(oauthVerifier);
		}
	}
	
	/**
	 * Create an access token for this new user</br> 
	 * Fill out the Twitter4j helper</br> 
	 * And save these credentials so we can log the user straight in next time
	 * 
	 * @param oauthVerifier
	 */
	private void authoriseNewUser(String oauthVerifier) {
		try {
			at = mTwitter.getOAuthAccessToken(mReqToken, oauthVerifier);
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

			// Set the content view back after we changed to a webview
			setContentView(R.layout.splash_activity);
			
			Log.i("sourceray", "access token linked; passing tokens to prefs");
			onSuccessfulAuth(at);
			
		} catch (TwitterException e) {
			Toast.makeText(this, "Twitter auth error x01, try again later", Toast.LENGTH_SHORT).show();
		}
	}
	
}
