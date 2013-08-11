package like.app;

import static like.app.CommonUtilities.SENDER_ID;
import static like.app.CommonUtilities.SERVER_URL;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import Util.GPSTracker;
import Util.Utility;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;

public class RegisterActivity extends Activity implements OnClickListener {
	private static final int PICK_FROM_CAMERA = 1;
	private static final int CROP_FROM_CAMERA = 2;
	private static final int PICK_FROM_FILE = 3;

	// alert dialog manager
	private MyApp myApp;
	AlertDialogManager alert = new AlertDialogManager();
	private GPSTracker gps;

	// Internet detector
	ConnectionDetector cd;

	// UI elements
	private EditText txtName;
	private EditText txtEmail;
	private ImageView iv_avatar;
	private File avatarFile;
	private Uri mImageCaptureUri;
	private boolean isDefaultAvatar = true;
	// Register button
	Button btnRegister;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		myApp = (MyApp) getApplicationContext();
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
			alert.showAlertDialog(RegisterActivity.this,
					"Internet Connection Error",
					"Please connect to working Internet connection", false);
			// stop executing code by return
			return;
		}

		// Check if GCM configuration is set
		if (SERVER_URL == null || SENDER_ID == null || SERVER_URL.length() == 0
				|| SENDER_ID.length() == 0) {
			// GCM sernder id / server url is missing
			alert.showAlertDialog(RegisterActivity.this,
					"Configuration Error!",
					"Please set your Server URL and GCM Sender ID", false);
			// stop executing code by return
			return;
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

		txtName = (EditText) findViewById(R.id.txtName);
		txtEmail = (EditText) findViewById(R.id.txtEmail);
		iv_avatar = (ImageView) findViewById(R.id.avatar);
		btnRegister = (Button) findViewById(R.id.btnRegister);

		/*
		 * Click event on Register button
		 */
		btnRegister.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// Read EditText dat
				String name = txtName.getText().toString();
				String email = txtEmail.getText().toString();

				// Check if user filled the form
				if (name.trim().length() > 0 && email.trim().length() > 0) {
					// Launch Main Activity
					Intent i = new Intent(getApplicationContext(),
							MainActivityNew.class);

					// Registering user on our server
					// Sending registraiton details to MainActivity
					i.putExtra("name", name);
					i.putExtra("email", email);
					i.putExtra("lat", gps.getLatitude());
					i.putExtra("lon", gps.getLongitude());
					startActivity(i);
					finish();
				} else {
					// user doen't filled that data
					// ask him to fill the form
					alert.showAlertDialog(RegisterActivity.this,
							"Registration Error!", "Please enter your details",
							false);
				}
			}
		});
	}

	@Override
	protected void onDestroy() {
		// Stop using gps and exit
		gps.stopUsingGPS();
		super.onDestroy();
	}

	private void showAvatarOption(Boolean isDefault) {
		final String[] items;
		if (isDefault) {
			items = new String[] { "Select from Camera", "Select from gallery" };
		} else {
			items = new String[] { "Select from Camera", "Select from gallery",
					"Delete avatar" };
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.select_dialog_item, items);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Select!");
		builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) { // pick from
																	// camera
				switch (item) {
				case 0:
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					mImageCaptureUri = Uri.fromFile(new File(Environment
							.getExternalStorageDirectory(), "tmp_avatar_"
							+ String.valueOf(System.currentTimeMillis())
							+ ".jpg"));
					intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,
							mImageCaptureUri);

					try {
						intent.putExtra("return-data", true);
						startActivityForResult(intent, PICK_FROM_CAMERA);
					} catch (ActivityNotFoundException e) {
						e.printStackTrace();
					}
					break;
				case 1:
					Intent intent2 = new Intent();
					intent2.setType("image/*");
					intent2.setAction(Intent.ACTION_GET_CONTENT);
					startActivityForResult(Intent.createChooser(intent2,
							"Complete action using"), PICK_FROM_FILE);
					break;
				case 2:
					isDefaultAvatar = true;
					avatarFile = null;
					iv_avatar.setImageResource(R.drawable.no_avatar);

					break;
				default:
					break;
				}
			}
		});
		final AlertDialog dialog = builder.create();
		dialog.show();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.avatar:
			// TODO add image from camera/galery
			showAvatarOption(isDefaultAvatar);
			Toast.makeText(getApplicationContext(), "avatar",
					Toast.LENGTH_SHORT).show();
			break;

		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK)
			return;
		switch (requestCode) {
		case PICK_FROM_CAMERA:
			doCrop();
			break;
		case PICK_FROM_FILE:
			mImageCaptureUri = data.getData();
			doCrop();
			break;
		case CROP_FROM_CAMERA:
			Bundle extras = data.getExtras();
			if (extras != null) {
				Bitmap photo = extras.getParcelable("data");
				iv_avatar.setImageBitmap(photo);
			}
			Bitmap croped = (Bitmap) data.getExtras().get("data");
			Toast.makeText(RegisterActivity.this, "storing file ",
					Toast.LENGTH_SHORT).show();
			try {
				avatarFile = Utility.storeFile("" + new Date().getTime(),
						croped, getApplicationContext());
				myApp.setFile(avatarFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
			Log.d("store cropped file ok", "ok");
			isDefaultAvatar = false;
			break;
		}
	}

	private void doCrop() {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setType("image/*");
		List<ResolveInfo> list = getPackageManager().queryIntentActivities(
				intent, 0);
		int size = list.size();
		if (size == 0) {
			Toast.makeText(this, "Can not find image crop app",
					Toast.LENGTH_SHORT).show();
			return;
		} else {
			intent.setData(mImageCaptureUri);

			intent.putExtra("outputX", 150);
			intent.putExtra("outputY", 150);
			intent.putExtra("aspectX", 1);
			intent.putExtra("aspectY", 1);
			intent.putExtra("scale", true);
			intent.putExtra("return-data", true);
			// start crop
			Intent i = new Intent(intent);
			ResolveInfo res = list.get(0);
			i.setComponent(new ComponentName(res.activityInfo.packageName,
					res.activityInfo.name));
			startActivityForResult(i, CROP_FROM_CAMERA);
		}
	}

	public boolean isNetworkAvailable() {
		Context context = getApplicationContext();
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
		} else {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						Log.d("network", "ok");
						return true;
					}
				}
			}
		}
		Log.d("network", "no network");
		return false;
	}

}
