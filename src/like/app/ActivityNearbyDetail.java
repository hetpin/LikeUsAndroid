package like.app;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import lib.touchgallery.GalleryWidget.BasePagerAdapter.OnItemChangeListener;
import lib.touchgallery.GalleryWidget.GalleryViewPager;
import lib.touchgallery.GalleryWidget.UrlPagerAdapter;
import model.User;
import Util.LConfig;
import Util.UserSAO;
import Util.Utility;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityNearbyDetail extends Activity implements OnClickListener,
		AnimationListener {
	private MyApp myApp;
	private ArrayList<User> list_user;
	private static final int PICK_FROM_CAMERA = 1;
	private static final int CROP_FROM_CAMERA = 2;
	private static final int PICK_FROM_FILE = 3;
	private File avatarFile;
	private Uri mImageCaptureUri;

	private LinearLayout toolbar;
	private ImageView heart;
	private ImageView my_avatar;
	private TextView my_name;
	private ImageButton btn_like;
	private ImageButton btn_phone;
	private GalleryViewPager mViewPager;
	private Animation animationFadeIn;
	private Animation animationFadeOut;
	private Animation animationZoomIn;
	private Animation animationZoomOut;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nearby);
		myApp = (MyApp) getApplicationContext();
		list_user = myApp.getListUser();
		toolbar = (LinearLayout) findViewById(R.id.toolbar);
		my_avatar = (ImageView) findViewById(R.id.avatar);
		my_name = (TextView) findViewById(R.id.name);
		heart = (ImageView) findViewById(R.id.heart);
		btn_like = (ImageButton) findViewById(R.id.btn_like);
		btn_phone = (ImageButton) findViewById(R.id.btn_phone);
		animationFadeOut = AnimationUtils.loadAnimation(this, R.anim.fadeout);
		animationFadeIn = AnimationUtils.loadAnimation(this, R.anim.fadein);
		animationZoomIn = AnimationUtils.loadAnimation(this, R.anim.zoom_in);
		animationZoomOut = AnimationUtils.loadAnimation(this, R.anim.zoom_out);
		animationZoomIn.setAnimationListener(this);
		animationZoomOut.setAnimationListener(this);
		int size = list_user.size();
		String[] urls = new String[size];
		for (int i = 0; i < size; i++) {
			urls[i] = list_user.get(i).url_normal();
		}
		// String[] urls = {
		// "http://www.fimo.vn/firms_vf/php/temp/images/normal/1375293234.jpg",
		// "http://www.fimo.vn/firms_vf/php/temp/images/normal/1375291693.jpg",
		// "http://www.fimo.vn/firms_vf/php/temp/images/normal/1375293674.jpg",
		// "http://www.fimo.vn/firms_vf/php/temp/images/normal/1375293324.jpg",
		// "http://www.fimo.vn/firms_vf/php/temp/images/normal/1375293234.jpg",
		// "http://www.fimo.vn/firms_vf/php/temp/images/normal/1375199721.jpg",
		// "http://www.fimo.vn/firms_vf/php/temp/images/normal/1375293234.jpg",
		// "http://www.fimo.vn/firms_vf/php/temp/images/normal/1375199721.jpg",
		// "http://www.fimo.vn/firms_vf/php/temp/images/normal/1375293234.jpg",
		// "http://www.fimo.vn/firms_vf/php/temp/images/normal/1375199721.jpg",
		// "http://www.fimo.vn/firms_vf/php/temp/images/normal/1375293234.jpg",
		// "http://cs407831.userapi.com/v407831207/191e/QEQE83Ok0lQ.jpg" };
		List<String> items = new ArrayList<String>();
		Collections.addAll(items, urls);

		UrlPagerAdapter pagerAdapter = new UrlPagerAdapter(this, items);
		pagerAdapter.setOnItemChangeListener(new OnItemChangeListener() {
			@Override
			public void onItemChange(int currentPosition) {
				btn_like.setImageResource(R.drawable.icon_like);
				btn_phone.setImageResource(R.drawable.icon_phone);
				btn_phone.setClickable(false);
				// Toast.makeText(ActivityNearbyDetail.this,
				// "Current item is " + currentPosition,
				// Toast.LENGTH_SHORT).show();
			}
		});

		mViewPager = (GalleryViewPager) findViewById(R.id.viewer);
		mViewPager.setOffscreenPageLimit(15);
		mViewPager.setAdapter(pagerAdapter);
		mViewPager.setCurrentItem(myApp.getCurrentUserIndex(), false);// TODO
																		// goto
																		// current
																		// user
		// Bind my avatar from local
		String my_user_name = Utility.getKey(getApplicationContext(),
				LConfig.KEY_MY_NAME);
		if (!TextUtils.isEmpty(my_user_name)) {
			my_name.setText(my_user_name);
		}
		String path = Utility.getKey(getApplicationContext(),
				LConfig.KEY_PROFILE_PATH);
		if (!TextUtils.isEmpty(path)) {
			File imgFile = new File(path);
			if (imgFile.exists()) {
				Bitmap myBitmap = BitmapFactory.decodeFile(imgFile
						.getAbsolutePath());
				my_avatar.setImageBitmap(myBitmap);
			}

		}

	}

	private void showSetting() {
		final Dialog dialog = new Dialog(this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog_setting);
		Button btn_change_avatar = (Button) dialog
				.findViewById(R.id.btn_change_avatar);
		Button btn_logout = (Button) dialog.findViewById(R.id.btn_logout);
		btn_change_avatar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				showAvatarOption();
			}
		});
		btn_logout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog.show();

	}

	private void showAvatarOption() {
		final String[] items = new String[] { "Select from Camera",
				"Select from gallery" };
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
				default:
					break;
				}
			}
		});
		final AlertDialog dialog = builder.create();
		dialog.show();
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
				my_avatar.setImageBitmap(photo);
			}
			Bitmap croped = (Bitmap) data.getExtras().get("data");
			try {
				avatarFile = Utility.storeFile("" + new Date().getTime(),
						croped, getApplicationContext());
				// TODO connect to SAO
				new UpdateAvatarAsyncTask().execute("");
				Utility.saveKey(getApplicationContext(),
						LConfig.KEY_PROFILE_PATH, avatarFile.getAbsolutePath());
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		}
	}

	private class UpdateAvatarAsyncTask extends
			AsyncTask<String, Void, Boolean> {
		private ProgressDialog dialog;

		@Override
		protected void onPreExecute() {
			Log.e("UpdateAvatarAsyncTask", "start");
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
			// Connect to api
			// TODO test
			UserSAO.getNearUser(Utility.getKey(getApplicationContext(),
					LConfig.KEY_MY_USER_ID), "21.0686319", "105.7899216");
			int result = UserSAO.updateAvatar(Utility.getKey(
					getApplicationContext(), LConfig.KEY_MY_USER_ID),
					"21.0686319", "105.7899216", avatarFile);

			return (result == 1) ? true : false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			Log.e("LoadNearAsyncTask", "finish");
			super.onPostExecute(result);
			dialog.dismiss();
			if (result) {
				Toast.makeText(getApplicationContext(), "changed avatar",
						Toast.LENGTH_SHORT).show();
			} else {
			}
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

			intent.putExtra("outputX", 4 * 150);
			intent.putExtra("outputY", 4 * 150);
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_like:
			new SendGCMAsyncTask().execute("like");
			btn_phone.setClickable(true);
			btn_like.setImageResource(R.drawable.icon_liked);
			heart.setVisibility(View.VISIBLE);
			heart.bringToFront();
			heart.startAnimation(animationZoomIn);
			break;
		case R.id.btn_phone:
			new SendGCMAsyncTask().execute("phone");
			btn_phone.setImageResource(R.drawable.icon_phone_sent);
			btn_phone.setClickable(false);
			// heart.bringToFront();
			// heart.startAnimation(animationZoomOut);
			break;
		case R.id.btn_setting:
			showSetting();
			break;
		case R.id.btn_location:
			finish();
			break;
		case R.id.btn_liked_you:

			break;
		case R.id.btn_mutual_like:

			break;
		case R.id.btn_camera:
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			mImageCaptureUri = Uri.fromFile(new File(Environment
					.getExternalStorageDirectory(), "tmp_avatar_"
					+ String.valueOf(System.currentTimeMillis()) + ".jpg"));
			intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,
					mImageCaptureUri);
			try {
				intent.putExtra("return-data", true);
				startActivityForResult(intent, PICK_FROM_CAMERA);
			} catch (ActivityNotFoundException e) {
				e.printStackTrace();
			}
			break;

		default:
			break;
		}
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		if (animation == animationFadeIn) {
			heart.setVisibility(View.INVISIBLE);
		}
		if (animation == animationFadeOut) {
			heart.setVisibility(View.INVISIBLE);
		}
		if (animation == animationZoomIn) {
			Toast.makeText(getApplicationContext(), "zoom in stop",
					Toast.LENGTH_SHORT).show();
			heart.clearAnimation();
			heart.setVisibility(View.GONE);
			// heart.startAnimation(animationZoomOut);
		}
		if (animation == animationZoomOut) {
			Toast.makeText(getApplicationContext(), "zoom out stop",
					Toast.LENGTH_SHORT).show();
			heart.clearAnimation();
			heart.setVisibility(View.GONE);
		}

	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAnimationStart(Animation animation) {
		// TODO Auto-generated method stub

	}

	private class SendGCMAsyncTask extends AsyncTask<String, Void, Boolean> {
		private ProgressDialog dialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = new ProgressDialog(ActivityNearbyDetail.this);
			dialog.setCancelable(true);
			dialog.setMessage("sending...");
			dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			// dialog.show();
		}

		@Override
		protected Boolean doInBackground(String... params) {
			Boolean result = false;
			String lat = "21.0686319";// "21.0686319", "105.7899216"
			String lon = "105.7899216";// "21.0686319", "105.7899216"
			if (params[0].equals("like")) {
				// TODO test send to myself. Send to other by using: user.id
				result = UserSAO.sendGCMLike(Utility.getKey(
						getApplicationContext(), LConfig.KEY_MY_USER_ID),
						Utility.getKey(getApplicationContext(),
								LConfig.KEY_MY_USER_ID),
						"From Android with love", lat, lon);
			} else {
				// TODO test send to myself. Send to other by using: user.id
				result = UserSAO.sendGCMMessage(Utility.getKey(
						getApplicationContext(), LConfig.KEY_MY_USER_ID),
						Utility.getKey(getApplicationContext(),
								LConfig.KEY_MY_USER_ID), "phone number", lat,
						lon);
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