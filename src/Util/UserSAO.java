package Util;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import like.app.CommonUtilities;
import model.User;

import org.apache.http.NameValuePair;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;
import android.util.Log;

public class UserSAO {
	private static boolean is150 = false;

	private static String filter150(String string) {
		if (is150) {
			string = string.substring(0, string.length() - 150);
		}
		return string;
	}

	public static ArrayList<User> getNearUser(String user_id, String lat,
			String lon) {
		ArrayList<User> list = new ArrayList<User>();
		String jsonString = null;
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(LConfig.KEY_USER_ID, user_id));
		params.add(new BasicNameValuePair(LConfig.KEY_LAT, lat));
		params.add(new BasicNameValuePair(LConfig.KEY_LON, lon));
		params.add(new BasicNameValuePair(LConfig.KEY_RADIUS,
				LConfig.KEY_RADIUS_DEFAULT));
		try {
			jsonString = GetData.postInfo(CommonUtilities.URL_GET_NEAR, params,
					null);
			Log.e("getNearUser before filter", "json = " + jsonString);
			jsonString = filter150(jsonString);
			Log.e("getNearUser", CommonUtilities.URL_GET_NEAR);
			Log.e("getNearUser", "json = " + jsonString);
		} catch (IOException e) {
			Log.e("getNearUser-IOException", "" + e.getMessage());
			return null;
		}
		JSONObject jsonObj = null;
		JSONArray jsonArr = null;
		try {
			jsonObj = new JSONObject(jsonString);
			jsonArr = jsonObj.getJSONArray("members");
		} catch (JSONException e) {
			Log.e("getNearUser-JSONException", "" + e.getMessage());
			return null;
		}
		if (jsonArr != null) {
			try {
				int size = jsonArr.length();
				for (int i = 0; i < size; i++) {
					JSONObject json_user = jsonArr.getJSONObject(i);
					User user = new User(json_user.getJSONObject("member"));
					list.add(user);
				}
			} catch (JSONException e) {
				Log.e("getNearUser-JSONException", "" + e.getMessage());
				return null;
			}
		}
		return list;
	}

	public static boolean sendGCMLike(String source_id, String des_id,
			String message, String lat, String lon) {
		message = "";// In this version, we dont want to send any message
						// content.
		String jsonString = null;
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(LConfig.KEY_USER_ID, source_id));
		params.add(new BasicNameValuePair(LConfig.KEY_LAT, lat));
		params.add(new BasicNameValuePair(LConfig.KEY_LON, lon));
		params.add(new BasicNameValuePair(LConfig.KEY_MESSAGE, message));
		params.add(new BasicNameValuePair(LConfig.KEY_DESTINATION, des_id));

		try {
			jsonString = GetData.postInfo(CommonUtilities.URL_POST_LIKE,
					params, null);
			Log.e("pre-filter-sendGCMLike", jsonString);
			jsonString = filter150(jsonString);
			Log.e("filted-sendGCMLike", jsonString);
		} catch (IOException e) {
			Log.e("sendGCMLike-IOException", "" + e.getMessage());
			return false;
		}
		JSONObject jsonObj = null;
		int result;
		try {
			jsonObj = new JSONObject(jsonString);
			result = jsonObj.getInt(LConfig.KEY_SUCCESS);
		} catch (JSONException e) {
			Log.e("sendGCMLike-JSONException", "" + e.getMessage());
			return false;
		}
		if (result == LConfig.SUCCESS) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean sendGCMMessage(String source_id, String des_id,
			String message, String lat, String lon) {
		String jsonString = null;
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(LConfig.KEY_USER_ID, source_id));
		params.add(new BasicNameValuePair(LConfig.KEY_LAT, lat));
		params.add(new BasicNameValuePair(LConfig.KEY_LON, lon));
		params.add(new BasicNameValuePair(LConfig.KEY_MESSAGE, message));
		params.add(new BasicNameValuePair(LConfig.KEY_DESTINATION, des_id));

		try {
			jsonString = GetData.postInfo(
					CommonUtilities.URL_POST_SEND_MESSAGE, params, null);
			jsonString = filter150(jsonString);
			Log.e("sendGCMMessage-sendGCM", jsonString);
		} catch (IOException e) {
			Log.e("sendGCMMessage-IOException", "" + e.getMessage());
			return false;
		}
		JSONObject jsonObj = null;
		int result;
		try {
			jsonObj = new JSONObject(jsonString);
			result = jsonObj.getInt(LConfig.KEY_SUCCESS);
		} catch (JSONException e) {
			Log.e("sendGCMMessage-JSONException", "" + e.getMessage());
			return false;
		}
		if (result == LConfig.SUCCESS) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean removeAccount(String user_id) {
		String jsonString = null;
		if (TextUtils.isEmpty(user_id)) {
			return false;
		}
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(LConfig.KEY_USER_ID, user_id));

		try {
			jsonString = GetData.postInfo(
					CommonUtilities.URL_POST_REMOVE_ACCOUNT, params, null);
			jsonString = filter150(jsonString);
			Log.v("removeAccount-sendGCM", jsonString);
		} catch (IOException e) {
			Log.e("removeAccount-IOException", "" + e.getMessage());
			return false;
		}
		JSONObject jsonObj = null;
		int result;
		try {
			jsonObj = new JSONObject(jsonString);
			result = jsonObj.getInt(LConfig.KEY_SUCCESS);
		} catch (JSONException e) {
			Log.e("removeAccount-JSONException", "" + e.getMessage());
			return false;
		}
		if (result == LConfig.SUCCESS) {
			return true;
		} else {
			return false;
		}

	}

	public static boolean loginPhp(String username, String pass, String lat,
			String lon, String gcm_key) {
		show(username + pass + lat + lon + gcm_key);
		ArrayList<User> list = new ArrayList<User>();
		String jsonString = null;
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("name", username));
		params.add(new BasicNameValuePair("password", pass));
		params.add(new BasicNameValuePair(LConfig.KEY_LAT, lat));
		params.add(new BasicNameValuePair(LConfig.KEY_LON, lon));
		params.add(new BasicNameValuePair(LConfig.KEY_GCM_ID, gcm_key));

		params.add(new BasicNameValuePair(LConfig.KEY_RADIUS,
				LConfig.KEY_RADIUS_DEFAULT));
		try {
			jsonString = GetData.postInfo(CommonUtilities.URL_GET_NEAR, params,
					null);
			Log.e("loginPhp before filter", "json = " + jsonString);
			jsonString = filter150(jsonString);
			Log.e("loginPhp", CommonUtilities.URL_GET_NEAR);
			Log.e("loginPhp", "json = " + jsonString);
		} catch (IOException e) {
			Log.e("loginPhp-IOException", "" + e.getMessage());
			return false;
		}
		JSONObject jsonObj = null;
		JSONArray jsonArr = null;
		try {
			jsonObj = new JSONObject(jsonString);
			String success = jsonObj.getString("success");
		} catch (JSONException e) {
			Log.e("loginPhp-JSONException", "" + e.getMessage());
			return false;
		}
		return true;
	}

	public static int registerPhp(String username, String pass, File file,
			String phone, String age, String male, String lat, String lon,
			String gcm_key) {
		show(username + pass + phone + age + male + lat + lon);
		show(gcm_key);
		MultipartEntity entity = new MultipartEntity();
		if (file != null) {
			show(file.getAbsolutePath());
			entity.addPart(LConfig.KEY_FILE, new FileBody(file));
		} else {
		}
		Charset chars = Charset.forName("UTF-8"); // Setting up the
		// encoding
		try {
			entity.addPart(LConfig.KEY_USER_ID, new StringBody("", chars));
			entity.addPart(LConfig.KEY_GCM_ID, new StringBody(gcm_key, chars));
			entity.addPart(LConfig.KEY_NAME, new StringBody(username, chars));
			entity.addPart(LConfig.KEY_EMAIL, new StringBody(username
					+ "ongaivotu@gmail.com", chars));
			entity.addPart(LConfig.KEY_PASSWORD, new StringBody(pass));
			entity.addPart(LConfig.KEY_PHONE, new StringBody(phone, chars));
			entity.addPart(LConfig.KEY_SEX, new StringBody(male, chars));// o-female
			entity.addPart(LConfig.KEY_AGE, new StringBody(age + "", chars));
			entity.addPart(LConfig.KEY_LAT, new StringBody(lat + "", chars));
			entity.addPart(LConfig.KEY_LON, new StringBody(lon + "", chars));
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		String jsonString = null;
		try {
			jsonString = GetData.postInfo(CommonUtilities.URL_REGISTER_PHP,
					null, entity, LConfig.TIME_OUT_FOR_FILE_TRANSFER);
			jsonString = filter150(jsonString);
			show(jsonString);
		} catch (IOException e) {
			Log.e("registerPhp-IOException", e.getMessage());
			return 0;
		}
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(jsonString);
		} catch (JSONException e) {
			Log.e("registerPhp-JSONException", e.getMessage());
			return 0;
		}
		show("3");
		if (jsonObject != null) {
			try {
				String result = jsonObject.getString(LConfig.KEY_SUCCESS);
				String message = jsonObject.getString(LConfig.KEY_MESSAGE);
				return Integer.parseInt(message);
			} catch (JSONException e) {
				Log.e("registerphp-JSONException", e.getMessage());
				return 0;
			}
		}
		return 1;
	}

	public static int updateAvatar(String user_id, String lat, String lon,
			File file) {
		MultipartEntity entity = new MultipartEntity();
		if (file != null) {
			show(file.getAbsolutePath());
			entity.addPart(LConfig.KEY_FILE, new FileBody(file));
		} else {
			return 0;
		}
		Charset chars = Charset.forName("UTF-8"); // Setting up the
		// encoding
		try {
			entity.addPart(LConfig.KEY_USER_ID, new StringBody(user_id, chars));
			entity.addPart(LConfig.KEY_LAT, new StringBody(lat + "", chars));
			entity.addPart(LConfig.KEY_LON, new StringBody(lon + "", chars));
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		String jsonString = null;
		try {
			jsonString = GetData.postInfo(CommonUtilities.URL_UPDATE_AVATAR,
					null, entity, LConfig.TIME_OUT_FOR_FILE_TRANSFER);
			jsonString = filter150(jsonString);
			show(jsonString);
		} catch (IOException e) {
			Log.e("updateAvatar-IOException", e.getMessage());
			return 0;
		}
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(jsonString);
		} catch (JSONException e) {
			Log.e("updateAvatar-JSONException", e.getMessage());
			return 0;
		}
		show("3");
		if (jsonObject != null) {
			try {
				String result = jsonObject.getString(LConfig.KEY_SUCCESS);
				String message = jsonObject.getString(LConfig.KEY_MESSAGE);
				return 1;
			} catch (JSONException e) {
				Log.e("updateAvatar-JSONException", e.getMessage());
				return 0;
			}
		}
		return 1;
	}

	public static void show(String string) {
		Log.e("USER_SAO", string);
	}
}
