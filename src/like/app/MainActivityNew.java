package like.app;

import static like.app.CommonUtilities.DISPLAY_MESSAGE_ACTION;
import static like.app.CommonUtilities.EXTRA_MESSAGE;
import static like.app.CommonUtilities.SENDER_ID;

import java.io.File;

import Util.Utility;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;

public class MainActivityNew extends Activity {
	private MyApp myApp;
	// label to display gcm messages
	TextView lblMessage;

	// Asyntask
	AsyncTask<Void, Void, Void> mRegisterTask;

	// Alert dialog manager
	AlertDialogManager alert = new AlertDialogManager();

	// Connection detector
	ConnectionDetector cd;
	public static File file;
	public static String name;
	public static String pass;
	public static String phone;
	public static String age;
	public static String sex;
	public static String lat;
	public static String lon;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		myApp = (MyApp) getApplicationContext();
		file = myApp.getFile();
		cd = new ConnectionDetector(getApplicationContext());

		// Check if Internet present
		if (!cd.isConnectingToInternet()) {
			// Internet Connection is not present
			alert.showAlertDialog(MainActivityNew.this,
					"Internet Connection Error",
					"Please connect to working Internet connection", false);
			// stop executing code by return
			return;
		}

		// Getting name, email from intent
		Intent i = getIntent();

		name = i.getStringExtra("name");
		pass = i.getStringExtra("email");
		phone = "123213";// i.getStringExtra("phone");
		age = "20";// i.getStringExtra("age");
		sex = "0";// i.getStringExtra("sex");
		lat = "21.0686325";// i.getStringExtra("lat");
		lon = "105.7899225";// i.getStringExtra("lon");

		Log.e("test_1", name + pass + phone + age + sex + lat + lon);

		// Make sure the device has the proper dependencies.
		GCMRegistrar.checkDevice(this);

		// Make sure the manifest was properly set - comment out this line
		// while developing the app, then uncomment it when it's ready.
		GCMRegistrar.checkManifest(this);

		lblMessage = (TextView) findViewById(R.id.lblMessage);

		registerReceiver(mHandleMessageReceiver, new IntentFilter(
				DISPLAY_MESSAGE_ACTION));
		new RegisterAsyncTask().execute("");
	}

	private class RegisterAsyncTask extends AsyncTask<String, Void, Boolean> {
		private ProgressDialog dialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = new ProgressDialog(MainActivityNew.this);
			dialog.setCancelable(true);
			dialog.setMessage("connecting...");
			dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			dialog.show();
		}

		@Override
		protected Boolean doInBackground(String... params) {
			// Get GCM registration id
			final String regId = GCMRegistrar
					.getRegistrationId(getApplicationContext());
			// Check if regid already presents
			if (regId.equals("")) {
				// Registration is not present, register now with GCM
				GCMRegistrar.register(getApplicationContext(), SENDER_ID);
			} else {
				// Device is already registered on GCM
				if (GCMRegistrar.isRegisteredOnServer(getApplicationContext())) {
					// Skips registration.
					Toast.makeText(
							getApplicationContext(),
							"Already registered with GCM = "
									+ Utility.getGCM(getApplicationContext()),
							Toast.LENGTH_LONG).show();
					return true;
				} else {
					// Try to register again, but not in the UI thread.
					// It's also necessary to cancel the thread onDestroy(),
					// hence the use of AsyncTask instead of a raw thread.
					final Context context = getApplicationContext();
					boolean result = ServerUtilities.register(context, name,
							pass, file, phone, age, sex, lat, lon, regId);
					return result;
				}
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			dialog.dismiss();
			if (result) {
				Intent intent = new Intent(MainActivityNew.this,
						ActivityMap.class);
				startActivity(intent);
			}
		}

	}

	/**
	 * Receiving push messages
	 * */
	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);
			// Waking up mobile if it is sleeping
			WakeLocker.acquire(getApplicationContext());

			/**
			 * Take appropriate action on this message depending upon your app
			 * requirement For now i am just displaying it on the screen
			 * */

			// Showing received message
			lblMessage.append(newMessage + "\n");
			Toast.makeText(getApplicationContext(),
					"New Message: " + newMessage, Toast.LENGTH_LONG).show();

			// Releasing wake lock
			WakeLocker.release();
		}
	};

	@Override
	protected void onDestroy() {
		if (mRegisterTask != null) {
			mRegisterTask.cancel(true);
		}
		try {
			unregisterReceiver(mHandleMessageReceiver);
			GCMRegistrar.onDestroy(this);
		} catch (Exception e) {
			Log.e("UnRegister Receiver Error", "> " + e.getMessage());
		}
		super.onDestroy();
	}

}
