package com.srcreigh.hub.auth;

import com.srcreigh.hub.R;
import com.srcreigh.hub.root.MainActivity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SplashActivity extends Activity {
	
	private Button twitterButton;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_activity);
		
		twitterButton = (Button) findViewById(R.id.connectToTwitterButton);
		twitterButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				// DO AUTH STUFF HERE CHARLES

				// On successful auth call onSuccessfulAuth(twitterAuthToken)
				onSuccessfulAuth("foo");
			}
		});
	}
	
	public void onSuccessfulAuth(String twitterAuthToken) {
		// Save the twitter token in the prefs and finish the activity with RESULT_OK
		SharedPreferences settings = getSharedPreferences(MainActivity.PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();

		editor.putString(MainActivity.TWITTER_AUTH_TOKEN, twitterAuthToken);
		editor.putString(MainActivity.USER_ID, "srcreigh");
	    editor.commit();
	    
	    setResult(Activity.RESULT_OK);
	    finish();
	}
}