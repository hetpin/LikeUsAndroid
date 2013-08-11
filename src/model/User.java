package model;

import like.app.CommonUtilities;

import org.json.JSONException;
import org.json.JSONObject;

import Util.LConfig;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

public class User {
	public String id;
	public String name;
	public String phone = "+84.1649.588.847";
	public String lat;// 0
	public String lon;// 1
	public String last_update_time;
	public String distance;
	private String url_image;
	public boolean is_friend = true;// Liked each other

	public User(JSONObject json) {
		try {
			id = json.getString(LConfig.KEY_ID);
			name = json.getString(LConfig.KEY_NAME);
			this.lat = json.getString(LConfig.KEY_LAT);
			this.lon = json.getString(LConfig.KEY_LON);
			this.distance = json.getString(LConfig.KEY_DISTANCE);
			this.last_update_time = json.getString(LConfig.KEY_LAST_UPDATE);
			url_image = json.getString(LConfig.KEY_IMAGE);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Log.e("user", this.toString());
	}

	private double getLatDouble() {
		return Double.parseDouble(this.lat);
	}

	private double getLonDouble() {
		return Double.parseDouble(this.lon);
	}

	public LatLng getLatLng() {
		if (lat != null && lon != null) {
			return new LatLng(this.getLatDouble(), this.getLonDouble());
		} else {
			return null;
		}
	}

	public String url_thumbnail() {
		return CommonUtilities.URL_THUMBNAIL + this.url_image;
	}

	public String url_small() {
		return CommonUtilities.URL_SMALL + this.url_image;
	}

	public String url_normal() {
		return CommonUtilities.URL_NORMAL + this.url_image;
	}

	public String toString() {
		String result = this.name + "\n";
		if (this.last_update_time != null) {
			result = result + this.last_update_time;
		}
		if (this.distance != null) {
			result = result + "\n" + this.distance;
		}
		return result;
	}
	/*
	 * "id":"19", "name":"Test1", "user_location":"37.885844,-122.506427",
	 * "email":"test1@teamios.info", "image":"1374852870.png"
	 */
}
