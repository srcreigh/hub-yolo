package com.srcreigh.hub.root;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.firebase.client.Firebase;
import com.srcreigh.hub.R;
import com.srcreigh.hub.settings.SettingsActivity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity extends FragmentActivity implements
		ActionBar.TabListener {
	
	// Constants
	public static final int CONNECT_POSITION = 0;
	public static final int DISCOVER_POSITION = 1;

	SectionsPagerAdapter sectionsPagerAdapter;

	ViewPager viewPager;
	
	LocationListener locationListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		viewPager = (ViewPager) findViewById(R.id.pager);
		viewPager.setAdapter(sectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				actionBar.setSelectedNavigationItem(position);
			}
		});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < sectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(sectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
		
		// Set up the location listener with callbacks
		// Define a listener that responds to location updates
		final Activity self = this;
		locationListener = new LocationListener() {
			@Override
		    public void onStatusChanged(String provider, int status, Bundle extras) {}

			@Override
		    public void onProviderEnabled(String provider) {
				Toast.makeText(self, "Provider enabled!", Toast.LENGTH_SHORT).show();
			}

			@Override
		    public void onProviderDisabled(String provider) {
				Toast.makeText(self, "Provider disabled... :(", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onLocationChanged(Location location) {
				Toast.makeText(self, "lat: " + location.getLatitude() + " lon: " + location.getLongitude(), Toast.LENGTH_SHORT).show();
			
				// Add location to Firebase
				Firebase locationRef = new Firebase(MainActivity.baseUrl + "location/");
				Map<String, Object> data = new HashMap<String, Object>();
				data.put("time", System.currentTimeMillis() / 1000);
				data.put("lat", String.valueOf(location.getLatitude()));
				data.put("lon", String.valueOf(location.getLongitude()));
				locationRef.setValue(data);
			}
		};
		
		// Set location updates on
		// TODO: make this based on broadcast?
		setLocationUpdatesEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.main_activity, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.action_settings:
			// Launch settings activity
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
			break;
		}
		
		return true;
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		viewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			if (position == CONNECT_POSITION) {
				Fragment fragment = new ConnectFragment();
				return fragment;
				
			} else {
				Fragment fragment = new DummySectionFragment();
				Bundle args = new Bundle();
				args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position + 1);
				fragment.setArguments(args);
				return fragment;
			}
		}

		@Override
		public int getCount() {
			// Show 2 total pages.
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_connect).toUpperCase(l);
			case 1:
				return getString(R.string.title_discover).toUpperCase(l);
			}
			return null;
		}
	}

	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public static class DummySectionFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";

		public DummySectionFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(
					R.layout.fragment_main_activity_dummy, container, false);
			TextView dummyTextView = (TextView) rootView
					.findViewById(R.id.section_label);
			dummyTextView.setText(Integer.toString(getArguments().getInt(
					ARG_SECTION_NUMBER)));
			return rootView;
		}
	}

	public void setLocationUpdatesEnabled(boolean enabled) {
		// Get the location manager
		LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

		if (enabled) {
			// Register the listener with the Location Manager to receive location updates
			// 5 minutes between updates, and 100 meters minimum
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5 * 60 * 1000, 100, locationListener);
			
		} else {
			// Remove the updates
			locationManager.removeUpdates(locationListener);
		}
	}
}
