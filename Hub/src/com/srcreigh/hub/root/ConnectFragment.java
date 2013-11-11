package com.srcreigh.hub.root;

import java.util.ArrayList;
import java.util.HashMap;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.User;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.srcreigh.hub.R;
import com.srcreigh.hub.auth.SplashActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class ConnectFragment extends Fragment {
	
	Firebase locationsRef;
	ArrayList<DataSnapshot> connections;
	ConnectionsAdapter adapter;
	Twitter twitter;
	
	public ConnectFragment() {
	}
	
	public interface OnLocationInitializedListener {
		public void onLocationInitialized(Location location);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Reference the locations Firebase and set it up
		connections = new ArrayList<DataSnapshot>();
		
		// Get twitter factory
		twitter = new TwitterFactory().getInstance();
		twitter.setOAuthConsumer(SplashActivity.CONSUMER_KEY, SplashActivity.CONSUMER_SECRET);
		twitter.setOAuthAccessToken(SplashActivity.at);

		locationsRef = new Firebase(MainActivity.baseUrl + "locations");
		locationsRef.addChildEventListener(new ChildEventListener() {

			@Override
			public void onChildAdded(DataSnapshot snapshot, String previousChildName) {
				HashMap<String, Object> location = (HashMap<String, Object>)snapshot.getValue();

				int halfHourSeconds = 30 * 60 * 60;
				int currentTimeSeconds = (int) System.currentTimeMillis() / 1000;
				if (currentTimeSeconds - (Integer)location.get("time") < halfHourSeconds) {
					// Over a half hour before a location update; remove from the list
					locationsRef.child(snapshot.getName()).removeValue();
				}
				
				// If the guy is close enough and not us, put 'em in there
				SharedPreferences settings = getActivity().getSharedPreferences(MainActivity.PREFS_NAME, 0);
				String currentUserId = settings.getString(MainActivity.USER_ID, null);
				if (closeEnough() && !snapshot.getName().equals(currentUserId)) {
					connections.add(snapshot);
					adapter.notifyDataSetChanged();
				}
			}

			@Override
			public void onChildRemoved(DataSnapshot snapshot) {
				connections.remove(locationsRef.child(snapshot.getName()));
				adapter.notifyDataSetChanged();
			}

			@Override public void onCancelled() { }
			@Override public void onChildMoved(DataSnapshot arg0, String arg1) { }
			@Override public void onChildChanged(DataSnapshot arg0, String arg1) { }
		});
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.connect_fragment, container, false);
		
		ListView listView = (ListView) rootView.findViewById(R.id.list);
		ConnectionsAdapter adapter = new ConnectionsAdapter(getActivity(), R.layout.connect_cell, connections);
		listView.setAdapter(adapter);

		return rootView;
	}
	
	public class Connection {
		public String name;
		public String message;

		public Connection(String n, String m, String t) {
			name = n; message = m;
		}
	}

	/*
	 * Custom ArrayAdapter to display our connections
	 */
	public class ConnectionsAdapter extends ArrayAdapter<DataSnapshot> {
		
		private int resId;
		private Context context;
		
		private ArrayList<DataSnapshot> connections;

		public ConnectionsAdapter(Context ctxt, int resource, ArrayList<DataSnapshot> data) {
			super(ctxt, resource, data);
			context = ctxt;
			resId = resource;
			connections = data;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		    View rowView = inflater.inflate(resId, parent, false);

		    TextView nameView = (TextView) rowView.findViewById(R.id.nameText);
		    TextView messageView = (TextView) rowView.findViewById(R.id.messageText);
		    Button followButton = (Button) rowView.findViewById(R.id.followButton);

			final HashMap<String, Object> location = (HashMap<String, Object>)connections.get(position).getValue();

		    nameView.setText((String)location.get("name"));
		    messageView.setText((String)location.get("message"));
		    followButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					try {
						twitter.createFriendship((String)location.get("twitter"));
					} catch (Exception e) { }
				}
			});

		    return rowView;
		}
	}

	private boolean closeEnough() {
		// Connect with errbody
		
		// This closeEnough() function, you could say, is "close enough"
		return true;
	}
}
