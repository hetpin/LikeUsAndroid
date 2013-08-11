package like.app;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import model.User;
import Util.LConfig;
import Util.UserSAO;
import Util.Utility;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

public class ActivityMap extends BaseActivity implements OnMarkerClickListener,
		OnInfoWindowClickListener, OnMarkerDragListener, ConnectionCallbacks,
		OnConnectionFailedListener, LocationListener {
	private MyApp myApp;
	private ArrayList<Marker> list_marker = new ArrayList<Marker>();
	private ArrayList<User> list_user = new ArrayList<User>();
	private boolean isFirstLoad = true;
	private static final LatLng BRISBANE = new LatLng(-27.47093, 153.0235);
	private static final LatLng MELBOURNE = new LatLng(-37.81319, 144.96298);
	private static final LatLng SYDNEY = new LatLng(-33.87365, 151.20689);
	private static final LatLng ADELAIDE = new LatLng(-34.92873, 138.59995);
	private static final LatLng PERTH = new LatLng(-31.952854, 115.857342);
	private static LatLng MY_LOCATION = new LatLng(0, 0);
	private Circle my_location_circle;
	private Marker marker_my_profile;
	// FOR MY LOCATION
	private LocationClient mLocationClient;
	// These settings are the same as the settings for the map. They will in
	// fact give you updates at
	// the maximal rates currently possible.
	private static final LocationRequest REQUEST = LocationRequest.create()
			.setInterval(5000) // 5 seconds
			.setFastestInterval(16) // 16ms = 60fps
			.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

	/** Demonstrates customizing the info window and/or its contents. */
	class CustomInfoWindowAdapter implements InfoWindowAdapter {
		private final RadioGroup mOptions;

		// These a both viewgroups containing an ImageView with id "badge" and
		// two TextViews with id
		// "title" and "snippet".
		private final View mWindow;
		private final View mContents;

		CustomInfoWindowAdapter() {
			mWindow = getLayoutInflater().inflate(R.layout.custom_info_window,
					null);
			mContents = getLayoutInflater().inflate(
					R.layout.custom_info_contents, null);
			mOptions = (RadioGroup) findViewById(R.id.custom_info_window_options);
		}

		@Override
		public View getInfoWindow(Marker marker) {
			if (mOptions.getCheckedRadioButtonId() != R.id.custom_info_window) {
				// This means that getInfoContents will be called.
				return null;
			}
			render(marker, mWindow);
			return mWindow;
		}

		@Override
		public View getInfoContents(Marker marker) {
			if (mOptions.getCheckedRadioButtonId() != R.id.custom_info_contents) {
				// This means that the default info contents will be used.
				return null;
			}
			render(marker, mContents);
			return mContents;
		}

		private void render(Marker marker, View view) {
			int badge;
			// Use the equals() method on a Marker to check for equals. Do not
			// use ==.
			if (marker.equals(mBrisbane)) {
				badge = R.drawable.badge_qld;
			} else if (marker.equals(mAdelaide)) {
				badge = R.drawable.badge_sa;
			} else if (marker.equals(mSydney)) {
				badge = R.drawable.badge_nsw;
			} else if (marker.equals(mMelbourne)) {
				badge = R.drawable.badge_victoria;
			} else if (marker.equals(mPerth)) {
				badge = R.drawable.badge_wa;
			} else {
				// Passing 0 to setImageResource will clear the image view.
				badge = 0;
			}
			((ImageView) view.findViewById(R.id.badge)).setImageResource(badge);

			String title = marker.getTitle();
			TextView titleUi = ((TextView) view.findViewById(R.id.title));
			if (title != null) {
				// Spannable string allows us to edit the formatting of the
				// text.
				SpannableString titleText = new SpannableString(title);
				titleText.setSpan(new ForegroundColorSpan(Color.RED), 0,
						titleText.length(), 0);
				titleUi.setText(titleText);
			} else {
				titleUi.setText("");
			}

			String snippet = marker.getSnippet();
			TextView snippetUi = ((TextView) view.findViewById(R.id.snippet));
			if (snippet != null && snippet.length() > 12) {
				SpannableString snippetText = new SpannableString(snippet);
				snippetText.setSpan(new ForegroundColorSpan(Color.MAGENTA), 0,
						10, 0);
				snippetText.setSpan(new ForegroundColorSpan(Color.BLUE), 12,
						snippet.length(), 0);
				snippetUi.setText(snippetText);
			} else {
				snippetUi.setText("");
			}
		}
	}

	private GoogleMap mMap;

	private Marker mPerth;
	private Marker mSydney;
	private Marker mBrisbane;
	private Marker mAdelaide;
	private Marker mMelbourne;
	private TextView mTopText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		myApp = (MyApp) getApplicationContext();
		mTopText = (TextView) findViewById(R.id.top_text);

		setUpMapIfNeeded();
		new LoadNearAsyncTask().execute("");
		gotoLastKnownLocation();
	}

	@Override
	protected void onDestroy() {
		myApp.clear();
		super.onDestroy();
	}

	private void gotoLastKnownLocation() {
		LatLng latLng = getLatKnownLocation();
		if (latLng != null) {
			CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
					latLng, 7);
			mMap.animateCamera(cameraUpdate);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		setUpMapIfNeeded();
		setUpLocationClientIfNeeded();
		mLocationClient.connect();
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (mLocationClient != null) {
			mLocationClient.disconnect();
		}
	}

	private void setUpLocationClientIfNeeded() {
		if (mLocationClient == null) {
			mLocationClient = new LocationClient(getApplicationContext(), this, // ConnectionCallbacks
					this); // OnConnectionFailedListener
		}
	}

	private void setUpMapIfNeeded() {
		// Do a null check to confirm that we have not already instantiated the
		// map.
		if (mMap == null) {
			// Try to obtain the map from the SupportMapFragment.
			mMap = ((SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.map)).getMap();
			// Check if we were successful in obtaining the map.
			if (mMap != null) {
				setUpMap();
				mMap.setMyLocationEnabled(true);
			}
		}
	}

	private void setUpMap() {
		// Hide the zoom controls as the button panel will cover it.
		mMap.getUiSettings().setZoomControlsEnabled(false);

		// Add lots of markers to the map.
		// addMarkersToMap();

		// Setting an info window adapter allows us to change the both the
		// contents and look of the
		// info window.
		mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());

		// Set listeners for marker events. See the bottom of this class for
		// their behavior.
		mMap.setOnMarkerClickListener(this);
		mMap.setOnInfoWindowClickListener(this);
		mMap.setOnMarkerDragListener(this);

		// Pan to see all markers in view.
		// Cannot zoom to bounds until the map has a size.
		final View mapView = getSupportFragmentManager().findFragmentById(
				R.id.map).getView();
	}

	private void addMarkersToMap() {
		// Uses a colored icon.
		mBrisbane = mMap.addMarker(new MarkerOptions()
				.position(BRISBANE)
				.title("Brisbane")
				.snippet("Population: 2,074,200")
				.icon(BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
		// list.add(mBrisbane);
		// Uses a custom icon.
		mSydney = mMap.addMarker(new MarkerOptions().position(SYDNEY)
				.title("Sydney").snippet("Population: 4,627,300")
				.icon(BitmapDescriptorFactory.fromResource(R.drawable.arrow)));

		// Creates a draggable marker. Long press to drag.
		mMelbourne = mMap.addMarker(new MarkerOptions().position(MELBOURNE)
				.title("Melbourne").snippet("Population: 4,137,400")
				.draggable(true));
		// list.add(mMelbourne);
		// A few more markers for good measure.
		mPerth = mMap.addMarker(new MarkerOptions().position(PERTH)
				.title("Perth").snippet("Population: 1,738,800"));
		mAdelaide = mMap.addMarker(new MarkerOptions().position(ADELAIDE)
				.title("Adelaide").snippet("Population: 1,213,000"));
		// list.add(mAdelaide);

	}

	private boolean checkReady() {
		if (mMap == null) {
			Toast.makeText(this, "Map not ready", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	/** Called when the Clear button is clicked. */
	public void onClearMap(View view) {
		if (!checkReady()) {
			return;
		}
		mMap.clear();
	}

	/** Called when the Reset button is clicked. */
	public void onResetMap(View view) {
		if (!checkReady()) {
			return;
		}
		// Clear the map because we don't want duplicates of the markers.
		mMap.clear();
		addMarkersToMap();
	}

	//
	// Marker related listeners.
	//

	@Override
	public boolean onMarkerClick(final Marker marker) {
		if (marker.equals(mPerth)) {
			// This causes the marker at Perth to bounce into position when it
			// is clicked.
			final Handler handler = new Handler();
			final long start = SystemClock.uptimeMillis();
			final long duration = 1500;

			final Interpolator interpolator = new BounceInterpolator();

			handler.post(new Runnable() {
				@Override
				public void run() {
					long elapsed = SystemClock.uptimeMillis() - start;
					float t = Math.max(
							1 - interpolator.getInterpolation((float) elapsed
									/ duration), 0);
					marker.setAnchor(0.5f, 1.0f + 2 * t);

					if (t > 0.0) {
						// Post again 16ms later.
						handler.postDelayed(this, 16);
					}
				}
			});
		} else if (marker.equals(mAdelaide)) {
			// This causes the marker at Adelaide to change color.
			marker.setIcon(BitmapDescriptorFactory.defaultMarker(new Random()
					.nextFloat() * 360));
		}
		// We return false to indicate that we have not consumed the event and
		// that we wish
		// for the default behavior to occur (which is for the camera to move
		// such that the
		// marker is centered and for the marker's info window to open, if it
		// has one).
		return false;
	}

	@Override
	public void onInfoWindowClick(Marker marker) {
		myApp.setCurrentMarker(marker);
		// find current user
		int size = list_user.size();
		String title = marker.getTitle();
		for (int i = 0; i < size; i++) {
			if (list_user.get(i).toString().equals(title)) {
				myApp.setCurrenUser(list_user.get(i));
				myApp.setCurrentUserIndex(i);
				break;
			}
		}
		Intent intent = new Intent(ActivityMap.this, ActivityNearbyDetail.class);
		startActivity(intent);
		// Intent intent = new Intent(this, ActivityDetails.class);
		// startActivity(intent);
	}

	@Override
	public void onMarkerDragStart(Marker marker) {
		mTopText.setText("onMarkerDragStart");
	}

	@Override
	public void onMarkerDragEnd(Marker marker) {
		mTopText.setText("onMarkerDragEnd");
	}

	@Override
	public void onMarkerDrag(Marker marker) {
		mTopText.setText("onMarkerDrag.  Current Position: "
				+ marker.getPosition());
	}

	@Override
	public void onLocationChanged(Location location) {
		LatLng latLng = new LatLng(location.getLatitude(),
				location.getLongitude());
		if (isFirstLoad) {
			CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
					latLng, 15);
			mMap.animateCamera(cameraUpdate);
			// add marker to map
			MY_LOCATION = new LatLng(location.getLatitude(),
					location.getLongitude());
			Log.e("lat = " + MY_LOCATION.latitude, "long = "
					+ MY_LOCATION.longitude);
			saveLocation(MY_LOCATION);
			addMarkersNearBy();
			isFirstLoad = false;
			mMap.animateCamera(cameraUpdate);
			View view_marker = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE))
					.inflate(R.layout.custom_marker_self, null);
			marker_my_profile = mMap.addMarker(new MarkerOptions().position(
					latLng).icon(
					BitmapDescriptorFactory.fromBitmap(createDrawableFromView(
							ActivityMap.this, view_marker))));
		}
		// Draw cycle
		drawCircle(latLng, LConfig.CIRCLE_RADIUS);
		// Add myself to map
		if (marker_my_profile != null) {
			// Log.e("marker_my_profile", "my_location chagne");
			Toast.makeText(ActivityMap.this,
					"marker_my_profile change location", Toast.LENGTH_SHORT)
					.show();
			String path = Utility.getKey(getApplicationContext(),
					LConfig.KEY_PROFILE_PATH);
			if (!TextUtils.isEmpty(path)) {
				File imgFile = new File(path);
				if (imgFile.exists()) {
					Bitmap myBitmap = BitmapFactory.decodeFile(imgFile
							.getAbsolutePath());
					View view_marker = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE))
							.inflate(R.layout.custom_marker_self, null);
					ImageView thumbnail = (ImageView) view_marker
							.findViewById(R.id.thumbnail);
					thumbnail.setImageBitmap(myBitmap);
					marker_my_profile.setIcon(BitmapDescriptorFactory
							.fromBitmap(createDrawableFromView(
									ActivityMap.this, view_marker)));
				}

			}
			marker_my_profile.setPosition(latLng);
		}
	}

	private void drawCircle(LatLng center, int radius) {
		if (my_location_circle != null) {
			my_location_circle.remove();
		}
		CircleOptions circleOptions = new CircleOptions().center(center) // set
																			// center
				.radius(radius) // set radius in meters
				.fillColor(0x10000000) // default
				.strokeColor(Color.GRAY).strokeWidth(2);

		my_location_circle = mMap.addCircle(circleOptions);
	}

	private void addMarkersNearBy() {
		Log.e("addMarkersNearBy", "start");
		// A few more markers for good measure.
		mPerth = mMap.addMarker(new MarkerOptions()
				.position(
						new LatLng(MY_LOCATION.latitude - 0.06,
								MY_LOCATION.longitude - 0.06))
				.title("First marker").snippet("Population: 1,738,800"));

		// Add user near by to map
		if (list_user == null) {
			return;
		}
		int size = list_user.size();
		list_marker.clear();
		Log.e("addMarkersNearBy", "doing size =" + size);
		for (int i = 0; i < size; i++) {
			View view_marker = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE))
					.inflate(R.layout.custom_marker, null);
			Log.e("addMarkersNearBy", "i =" + i);
			LatLng latlng = list_user.get(i).getLatLng();
			// LatLng latlng = new LatLng(MY_LOCATION.latitude + 0.02
			// * Math.sin(i * Math.PI / (size - 1)), MY_LOCATION.longitude
			// - 0.02 * Math.cos(i * Math.PI / (size - 1)));
			if (latlng != null) {
				Marker marker = mMap.addMarker(new MarkerOptions()
						.position(latlng)
						.title(list_user.get(i).toString())
						.icon(BitmapDescriptorFactory
								.fromBitmap(createDrawableFromView(
										ActivityMap.this, view_marker))));
				list_marker.add(marker);
			}
		}
		loadMarkerAvatar();
	}

	public void loadMarkerAvatar() {
		Toast.makeText(getApplicationContext(), "loadMarkerAvatar", Toast.LENGTH_SHORT).show();
		// Load image, decode it to Bitmap and return Bitmap to callback
		int size = list_user.size();
		for (int i = 0; i < size; i++) {
			Log.e("loadMarkerAvatar", "binding " + i);
			imageLoader.loadImage(list_user.get(i).url_thumbnail(),
					new MySimpleImageLoadingListener(i) {
						@Override
						public void onLoadingComplete(String imageUri,
								View view, Bitmap loadedImage) {
							Log.e("onLoadingComplete", "id = " + this.getId());
							View view_marker = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE))
									.inflate(R.layout.custom_marker, null);
							ImageView thumbnail = (ImageView) view_marker
									.findViewById(R.id.thumbnail);
							thumbnail.setImageBitmap(loadedImage);
							list_marker.get(this.getId()).setIcon(
									BitmapDescriptorFactory
											.fromBitmap(createDrawableFromView(
													ActivityMap.this,
													view_marker)));
						}
					});
		}
	}

	// Convert a view to bitmap
	public static Bitmap createDrawableFromView(Context context, View view) {
		DisplayMetrics displayMetrics = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay()
				.getMetrics(displayMetrics);
		view.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
		view.layout(0, 0, displayMetrics.widthPixels,
				displayMetrics.heightPixels);
		view.buildDrawingCache();
		Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(),
				view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

		Canvas canvas = new Canvas(bitmap);
		view.draw(canvas);
		return bitmap;
	}

	private class MySimpleImageLoadingListener extends
			SimpleImageLoadingListener {
		private int id;

		protected int getId() {
			return this.id;
		}

		public MySimpleImageLoadingListener(int id) {
			this.id = id;
		}

		@Override
		public void onLoadingComplete(String imageUri, View view,
				Bitmap loadedImage) {
			super.onLoadingComplete(imageUri, view, loadedImage);
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {

	}

	@Override
	public void onConnected(Bundle connectionHint) {
		mLocationClient.requestLocationUpdates(REQUEST, this); // LocationListener
	}

	@Override
	public void onDisconnected() {
	}

	private class LoadNearAsyncTask extends AsyncTask<String, Void, Boolean> {
		private ProgressDialog dialog;

		@Override
		protected void onPreExecute() {
			Log.e("LoadNearAsyncTask", "start");
			super.onPreExecute();
			dialog = new ProgressDialog(getApplicationContext());
			dialog.setCancelable(true);
			dialog.setMessage("Loading near...");
			dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			// dialog.show();
		}

		@Override
		protected Boolean doInBackground(String... params) {
			Log.e("LoadNearAsyncTask", "doing");
			Boolean result = false;
			// Connect to api
			// TODO test
			list_user = UserSAO.getNearUser(Utility.getKey(
					getApplicationContext(), LConfig.KEY_MY_USER_ID),
					"21.0686319", "105.7899216");
			// list_user = UserSAO.getNearUser("1001",
			// "21.0686319", "105.7899216");
			if (list_user != null && list_user.size() > 0) {
				result = true;
				Log.e("list_user result", "= " + list_user.size());
			}
			return result;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			Log.e("LoadNearAsyncTask", "finish");
			super.onPostExecute(result);
			dialog.dismiss();
			if (result && list_user != null) {
				myApp.setListUser(list_user);
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						// TODO add marker here.
						addMarkersNearBy();
					}
				});
			} else {
			}
		}
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.layout.menu, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.remove_account:
			Toast.makeText(ActivityMap.this, "Remove account",
					Toast.LENGTH_SHORT).show();
			return true;
		case R.id.menu_about:
			Toast.makeText(ActivityMap.this, "About this app",
					Toast.LENGTH_SHORT).show();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private LatLng getLatKnownLocation() {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		String lat = preferences.getString("lat", "");
		String lon = preferences.getString("lon", "");
		if (lat.equals("") || lon.equals("")) {
			return null;
		} else {
			return new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));
		}
	}

	private void saveLocation(LatLng latLng) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		Editor edit = preferences.edit();
		edit.putString("lat", latLng.latitude + "");
		edit.putString("lon", latLng.longitude + "");
		edit.commit();

	}

}
