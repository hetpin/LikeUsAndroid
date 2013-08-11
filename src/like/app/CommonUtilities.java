package like.app;

import org.apache.http.entity.ByteArrayEntity;

import android.content.Context;
import android.content.Intent;

public final class CommonUtilities {
	public static final String BASE_URL = "http://phongkhamhanoi.site40.net/LikeUs/";// "http://teamios.info/LikeUs/";
	public static final String URL_NORMAL = BASE_URL + "images/normal/";
	public static final String URL_SMALL = BASE_URL + "images/small/";
	public static final String URL_THUMBNAIL = BASE_URL + "images/thumbnail/";
	// Get all other user near me
	public static final String URL_GET_NEAR = BASE_URL + "apis.php?api=near_by";
	public static final String URL_POST_LIKE = BASE_URL + "apis.php?api=like";
	public static final String URL_POST_SEND_MESSAGE = BASE_URL
			+ "apis.php?api=send_message";
	public static final String URL_POST_REMOVE_ACCOUNT = BASE_URL
			+ "apis.php?api=remove_account";
	public static final String URL_REGISTER_PHP = BASE_URL
			+ "apis.php?api=register_android";
	public static final String URL_UPDATE_AVATAR = BASE_URL
			+ "apis.php?api=update_avatar";

	// give your server registration url here
	static final String SERVER_URL = BASE_URL + "register.php";// "http://10.0.2.2/gcm_server_php/register.php";

	// Google project id
	static final String SENDER_ID = "685179133086";

	/**
	 * Tag used on log messages.
	 */
	static final String TAG = "like.app GCM";

	static final String DISPLAY_MESSAGE_ACTION = "like.app.pushnotifications.DISPLAY_MESSAGE";

	static final String EXTRA_MESSAGE = "message";

	/**
	 * Notifies UI to display a message.
	 * <p>
	 * This method is defined in the common helper because it's used both by the
	 * UI and the background service.
	 * 
	 * @param context
	 *            application's context.
	 * @param message
	 *            message to be displayed.
	 */
	static void displayMessage(Context context, String message) {
		Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
		intent.putExtra(EXTRA_MESSAGE, message);
		context.sendBroadcast(intent);
	}
}
