package com.srcreigh.hub.root;

import java.util.ArrayList;
import java.util.HashMap;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.srcreigh.hub.R;

import android.content.Context;
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

	public ConnectFragment() {
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// Reference the locations Firebase and set it up
		locationsRef = new Firebase(MainActivity.baseUrl + "locations");
		locationsRef.addChildEventListener(new ChildEventListener() {

			@Override
			public void onChildAdded(DataSnapshot snapshot, String previousChildName) {
				HashMap<String, Object> values = (HashMap<String, Object>)snapshot.getValue();

				// Remove old item if it's old
			}

			@Override public void onCancelled() { }
			@Override public void onChildChanged(DataSnapshot arg0, String arg1) { }
			@Override public void onChildMoved(DataSnapshot arg0, String arg1) { }
			@Override public void onChildRemoved(DataSnapshot arg0) { }
		});
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.connect_fragment, container, false);
		
		// Dummy array list
		ArrayList<Connection> connections = new ArrayList<Connection>();
		connections.add(new Connection("Shane", "Hi I'm shane", ""));
		connections.add(new Connection("Heming", "f u", ""));
		connections.add(new Connection("Jason", "I'm a designer derupaderup", ""));
		
		ListView listView = (ListView) rootView.findViewById(R.id.list);
		ConnectionsAdapter adapter = new ConnectionsAdapter(getActivity(), 
				R.layout.connect_cell, connections);
		listView.setAdapter(adapter);

		return rootView;
	}
	
	public class Connection {
		public String name;
		public String message;
		public String twitterInfo; // ????

		public Connection(String n, String m, String t) {
			name = n; message = m; twitterInfo = t;
		}
	}

	/*
	 * Custom ArrayAdapter to display our connections
	 */
	public class ConnectionsAdapter extends ArrayAdapter<Connection> {
		
		private int resId;
		private Context context;
		
		private ArrayList<Connection> connections;

		public ConnectionsAdapter(Context ctxt, int resource, ArrayList<Connection> data) {
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

		    nameView.setText(connections.get(position).name);
		    messageView.setText(connections.get(position).message);
		    followButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// Follow on twitter somehow
				}
			});

		    return rowView;
		}
	}
}
