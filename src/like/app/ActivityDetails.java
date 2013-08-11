package like.app;

import model.User;
import Util.UserSAO;
import Util.Utility;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.Marker;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

public class ActivityDetails extends BaseActivity implements
		android.view.View.OnClickListener {
	private MyApp myApp;
	private User user;
	private Marker marker;
	private TextView tv_info;
	private ImageView image;
	private ProgressBar loading;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_details);
		myApp = (MyApp) getApplicationContext();
		marker = myApp.getCurrentMarker();
		if (marker == null) {
			return;
		}
		user = myApp.getCurrentUser();
		if (user == null) {
			return;
		}
		loading = (ProgressBar) findViewById(R.id.loading);
		tv_info = (TextView) findViewById(R.id.info);
		image = (ImageView) findViewById(R.id.image);
		tv_info.setText(user.toString());
		//
		imageLoader.loadImage(user.url_normal(),
				new SimpleImageLoadingListener() {
					@Override
					public void onLoadingStarted(String imageUri, View view) {
						loading.setVisibility(View.VISIBLE);
					}

					@Override
					public void onLoadingFailed(String imageUri, View view,
							FailReason failReason) {
						loading.setVisibility(View.GONE);
					}

					@Override
					public void onLoadingComplete(String imageUri, View view,
							Bitmap loadedImage) {
						loading.setVisibility(View.GONE);
						image.setImageBitmap(loadedImage);
					}

					@Override
					public void onLoadingCancelled(String imageUri, View view) {
						loading.setVisibility(View.GONE);
					}
				});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_like:
			new SendGCMAsyncTask().execute("like");
			Toast.makeText(getApplicationContext(), "Clicked like",
					Toast.LENGTH_SHORT).show();
			break;
		case R.id.btn_send_phone:
			new SendGCMAsyncTask().execute("phone");
			Toast.makeText(getApplicationContext(), "Clicked like",
					Toast.LENGTH_SHORT).show();
			break;

		default:
			break;
		}
	}

	private class SendGCMAsyncTask extends AsyncTask<String, Void, Boolean> {
		private ProgressDialog dialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = new ProgressDialog(ActivityDetails.this);
			dialog.setCancelable(true);
			dialog.setMessage("sending...");
			dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			dialog.show();
		}

		@Override
		protected Boolean doInBackground(String... params) {
			Boolean result = false;
			if (params[0].equals("like")) {
				Log.e("sending like",
						"gmc =" + Utility.getGCM(getApplicationContext())
								+ " user = " + user.id);
				// TODO test send to myself. Send to other by using: user.id
				result = UserSAO.sendGCMLike(
						Utility.getGCM(getApplicationContext()), "24",
						"From Android with love","21.0686319", "105.7899216");
			} else {
				// TODO test send to myself. Send to other by using: user.id
				Log.e("sending phone",
						"gmc =" + Utility.getGCM(getApplicationContext())
								+ " user = " + user.id);
				result = UserSAO.sendGCMMessage(
						Utility.getGCM(getApplicationContext()), "24",
						user.phone,"21.0686319", "105.7899216");
			}
			// TODO test
			return result;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			dialog.dismiss();
			if (result) {
				Toast.makeText(getApplicationContext(), "success",
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(getApplicationContext(), "failed",
						Toast.LENGTH_SHORT).show();
			}
		}
	}
}
