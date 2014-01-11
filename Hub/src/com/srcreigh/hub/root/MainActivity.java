package com.srcreigh.hub.root;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends Activity {
	
	public static final String PREFS_NAME = "hubPrefs";
	public static final String USER_ID = "userId";

	public static final String baseUrl = "https://srcreigh-hub.firebaseIO.com/";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Decide here which activity to show next
		boolean hasShownSplash = getSharedPreferences(PREFS_NAME, 0).getBoolean("hasShownSplash", false);
		if (hasShownSplash) {
			// Show the home activity
			Intent intent = new Intent(this, HomeActivity.class);
			startActivity(intent);

		} else {
			// Launch the splash activity. Will launch home activity after 5 seconds.
			Intent intent = new Intent(this, SplashActivity.class);
			startActivity(intent);
		}
	}
}
