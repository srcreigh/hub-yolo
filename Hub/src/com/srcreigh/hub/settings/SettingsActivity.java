package com.srcreigh.hub.settings;

import java.util.Map;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.ValueEventListener;
import com.srcreigh.hub.R;
import com.srcreigh.hub.root.MainActivity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SettingsActivity extends Activity {
	
	// UI components
	EditText nameText;
	EditText messageText;
	Button submitButton;
	
	Firebase userRef;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_activity);
		
		nameText = (EditText)findViewById(R.id.nameText);
		messageText = (EditText)findViewById(R.id.messageText);
		submitButton = (Button)findViewById(R.id.saveButton);
		
		// Set up our user reference
		// Get the authenticated user id
		SharedPreferences settings = getSharedPreferences(MainActivity.PREFS_NAME, 0);
		String userId = settings.getString(MainActivity.USER_ID, null);

		userRef = new Firebase(MainActivity.baseUrl + "users/" + userId); 
		userRef.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot snapshot) {
				Object value = snapshot.getValue();
				
				if (value != null) {
					// Populate text fields with the user's name, etc
					nameText.setText((String)((Map)value).get("name"));
					messageText.setText((String)((Map)value).get("message"));
				}
			}

			@Override public void onCancelled() { }
		});

		final Activity self = this;
		submitButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				boolean nameTextEmpty = nameText.getText().toString() == "";
				boolean messageTextEmpty = messageText.getText().toString() == "";
				
				if (nameTextEmpty) {
					Toast.makeText(self, "Please enter a name.", Toast.LENGTH_SHORT).show();
				} else if (messageTextEmpty) {
					Toast.makeText(self, "Please enter a name.", Toast.LENGTH_SHORT).show();
				} else {
					// Successful name change; update the user's name in FireBase
					String name = nameText.getText().toString();
					String message = messageText.getText().toString();
					
					userRef.child("name").setValue(name);
					userRef.child("message").setValue(message);
					
					// close the activity
					finish();
				}
			}
		});
	}
}