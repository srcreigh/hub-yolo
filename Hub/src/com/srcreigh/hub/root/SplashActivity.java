package com.srcreigh.hub.root;

import com.srcreigh.hub.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends Activity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_activity);
		
		// Show this splash screen for only 5 seconds.
		final Intent intent = new Intent(this, HomeActivity.class);
		new Handler().postDelayed(new Runnable() {
			public void run() {
				// Store that this has happened in SharedPreferences
                SharedPreferences.Editor editor = getSharedPreferences(MainActivity.PREFS_NAME, 0).edit();
                editor.putBoolean("hasShownSplash", true);
                editor.commit();
                
                startActivity(intent);
			}
		}, 5000);
	}
}