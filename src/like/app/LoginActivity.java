package like.app;

import static like.app.CommonUtilities.SENDER_ID;

import org.w3c.dom.Text;

import Util.GPSTracker;
import Util.UserSAO;
import Util.Utility;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;

public class LoginActivity extends Activity implements OnClickListener {
	private MyApp myApp;
	private GPSTracker gps;

	// Internet detector
	ConnectionDetector cd;

	// UI elements
	private EditText txtName;
	private EditText txtPass;
	private String gcm_key;
	// Register button
	Button btnRegister;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		txtName = (EditText) findViewById(R.id.txtName);
		txtPass = (EditText) findViewById(R.id.txtPass);
		myApp = (MyApp) getApplicationContext();
		gcm_key = Utility.getGCM(getApplicationContext());
		// Get gps
		gps = new GPSTracker(this);
		// check if GPS enabled
		if (gps.canGetLocation()) {
			// double latitude = gps.getLatitude();
			// double longitude = gps.getLongitude();
			// Toast.makeText(
			// getApplicationContext(),
			// "Your Location is - \nLat: " + latitude + "\nLong: "
			// + longitude + "att = " + gps.getAttitude(),
			// Toast.LENGTH_LONG).show();
		} else {
			// gps.showGpsDialog();
		}

		cd = new ConnectionDetector(getApplicationContext());

		// Check if Internet present
		if (!cd.isConnectingToInternet()) {
			// Internet Connection is not present
		}

		// CHECK REGISTERED OR NOT
		final String regId = GCMRegistrar.getRegistrationId(this);
		// Check if regid already presents
		if (!regId.equals("") && GCMRegistrar.isRegisteredOnServer(this)) {
			// Launch Main Activity
			Log.e("gcm key = ", regId);
			// Intent i = new Intent(getApplicationContext(),
			// MainActivity.class);
			// i.putExtra("name", "");
			// i.putExtra("email", "");
			// startActivity(i);
			// finish();
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btnRegister:
			Intent intent = new Intent(this, RegisterActivity.class);
			startActivity(intent);
			finish();
			break;
		case R.id.btnLogin:
			// Connect to SAO to login with name and pass and gcm_key
			if (TextUtils.isEmpty(gcm_key)) {
				// get gcm_key, then connect to SAO
				// truong hop nay lam sau
			} else {
				// Connect to SAO
				new LoginAsyncTask().execute("");

			}
			break;

		default:
			break;
		}
	}

	private class LoginAsyncTask extends AsyncTask<String, Void, Boolean> {
		private ProgressDialog dialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = new ProgressDialog(LoginActivity.this);
			dialog.setCancelable(true);
			dialog.setMessage("logging in...");
			dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			dialog.show();
		}

		@Override
		protected Boolean doInBackground(String... params) {
			// Get GCM registration id
			return UserSAO
					.loginPhp(txtName.getText().toString(), txtPass.getText()
							.toString(), "21.0686319", "105.7899216", gcm_key);
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			dialog.dismiss();
			if (result) {
				Intent intent = new Intent(getApplicationContext(),
						ActivityMap.class);
				startActivity(intent);
			}
		}

	}

}