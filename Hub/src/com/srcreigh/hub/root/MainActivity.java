package com.srcreigh.hub.root;

import com.srcreigh.hub.auth.SplashActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class MainActivity extends Activity {
	
	public static final String PREFS_NAME = "hubPrefs";
	public static final String USER_ID = "userId";
	public static final String TWITTER_AUTH_TOKEN = "twitterAuthToken";

	public static final String baseUrl = "https://srcreigh-hub.firebaseIO.com/";

	private static final int HOME_REQUEST_CODE = 0;
	private static final int SPLASH_REQUEST_CODE = 1;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Check if we have twitter auth token
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		String twitterAuthToken = settings.getString(TWITTER_AUTH_TOKEN, null);
		
		if (twitterAuthToken == null) {
			// Open splash activity. This guy needs to auth with twitter
			showSplash();

		} else {
			// We've authenticated previously, so just go straight into the app.
			showHome();
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == SPLASH_REQUEST_CODE) {
				// Successful authentication! Open home activity
				showHome();
			}
		} else {
			// Backed out of the app; close
			finish();
		}
	}
	
	public void showSplash() {
		Intent intent = new Intent(this, SplashActivity.class);
		startActivityForResult(intent, SPLASH_REQUEST_CODE);
	}

	public void showHome() {
		Intent intent = new Intent(this, HomeActivity.class);
		startActivityForResult(intent, HOME_REQUEST_CODE);
	}
}
