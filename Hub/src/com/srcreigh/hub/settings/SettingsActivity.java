package com.srcreigh.hub.settings;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.ValueEventListener;
import com.firebase.client.core.view.View;
import com.shaded.fasterxml.jackson.databind.node.NodeCursor.Object;
import com.srcreigh.hub.R;
import com.srcreigh.hub.root.MainActivity;

public class SettingsActivity extends Activity {
	
	// UI components
	EditText nameText;
	EditText messageText;
	Button submitButton;
	
	Firebase userRef;
	Firebase locationsRef;
	
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
		final String userId = settings.getString(MainActivity.USER_ID, null);

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
		
		locationsRef = new Firebase(MainActivity.baseUrl + "location/" + userId);

		
		
		
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
					
					// Add a location thing
					Firebase newPush = locationsRef.push();
					Map<String, Object> location = new HashMap<String, Object>();
					location.put("name", name);
					location.put("message", message);
					location.put("time", "");
					location.put("lat", "");
					location.put("lon", "");
					location.put("twitter", userId);
					
					newPush.setValue(location);
					
					// close the activity
					finish();
				}
			}
		});
	}
	
	// Function for dealing with authentication activities that return data
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			String string = data.getStringExtra("OAUTH");
			TwitterWrapper.getInstance().receiveOAuth(string);
		}
	}
	
}