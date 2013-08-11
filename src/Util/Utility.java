package Util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class Utility {
	private static final String DIRECTORY_PICTURES = "Pictures";

	public static void saveGCM(Context context, String value) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		Editor edit = preferences.edit();
		edit.putString(LConfig.KEY_GCM_KEY, value);
		edit.commit();
		Log.e("saveGCM", "key = " + value);
	}

	public static String getGCM(Context context) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		// Log.e("getGCM",
		// "key = " + preferences.getString(LConfig.KEY_GCM_KEY, ""));
		return preferences.getString(LConfig.KEY_GCM_KEY, "");
	}

	public static void saveKey(Context context, String key, String value) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		Editor edit = preferences.edit();
		edit.putString(key, value);
		edit.commit();
		Log.e("save" + key, "value = " + value);
	}

	public static String getKey(Context context, String key) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		Log.e(key , "value = " + preferences.getString(key, ""));
		return preferences.getString(key, "");
	}

	/**
	 * @param fileName
	 *            is the name of the file.
	 * @param bitmap
	 *            is the bitmap to be updated.
	 * @param context
	 *            is the context of the app.
	 * @return the file object.
	 * @throws IOException
	 *             is the exception input-output.
	 */
	public static File storeFile(String fileName, Bitmap bitmap, Context context)
			throws IOException {
		File file = null;
		String filePath = getOutputMediaFilePath("image", context, fileName
				+ ".jpg");
		if (filePath == null) {
			// toast error message not enough memory.
			Toast.makeText(context, "Not enough memory", Toast.LENGTH_SHORT)
					.show();
			return null;
		} else {
			file = new File(filePath);
		}
		FileOutputStream out = new FileOutputStream(file);
		long fileSize = bitmap.getHeight() * bitmap.getRowBytes();
		int quality = 100;
		if (fileSize > 50000) {
			quality = (int) (100 * 50000 / fileSize);
		}
		bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out);
		out.flush();
		out.close();
		bitmap.recycle();
		if (file == null)
			Log.d("thanhnx", "file = null");
		return file;
	}

	/** Create a file Uri for saving an image or video */
	/**
	 * @param fileType
	 *            type of media.
	 * @return the uri for the image.
	 */
	public static String getOutputMediaFilePath(String fileType,
			Context context, String fileName) {
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.
		File mediaStorageDir = getMediaStorageDirectory(fileType, context);
		File mediaFile = new File(mediaStorageDir.getPath() + File.separator
				+ fileName);
		return mediaFile.getAbsolutePath();
	}

	public static File getMediaStorageDirectory(String fileType, Context context) {
		File mediaStorageDir;
		boolean isExternalStorageAvailable = Environment.MEDIA_MOUNTED
				.equals(Environment.getExternalStorageState());

		if (hasFroyo() && isExternalStorageAvailable) {
			String mediaFolder;
			mediaFolder = Environment.DIRECTORY_PICTURES;
			if (fileType.equals("image")) {
				mediaFolder = Environment.DIRECTORY_PICTURES;
			}
			mediaStorageDir = new File(
					Environment.getExternalStoragePublicDirectory(mediaFolder),
					"likeus");

		} else {
			String mediaFolder = DIRECTORY_PICTURES;
			if (fileType.equals("image")) {
				mediaFolder = DIRECTORY_PICTURES;

			}
			if (isExternalStorageAvailable) {
				mediaStorageDir = new File(
						Environment.getExternalStorageDirectory(), mediaFolder);
			} else {
				mediaStorageDir = new File(context.getFilesDir(), mediaFolder);
			}
		}
		// This location works best if you want the created images to be
		// shared
		// between applications and persist after your app has been
		// uninstalled.

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.e("MyCameraApp", "failed to create directory:"
						+ mediaStorageDir.getAbsolutePath());
				return null;
			}
		}
		return mediaStorageDir;
	}

	public static boolean hasFroyo() {
		// Can use static final constants like FROYO, declared in later versions
		// of the OS since they are inlined at compile time. This is guaranteed
		// behavior.
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
	}

}
