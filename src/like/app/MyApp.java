package like.app;

import java.io.File;
import java.util.ArrayList;

import model.User;
import android.app.Application;
import android.content.Context;

import com.google.android.gms.maps.model.Marker;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

public class MyApp extends Application {
	private File file;
	private Marker current_marker;
	private User current_user;
	private int current_user_index = 0;

	private ArrayList<User> list_user = new ArrayList<User>();

	@Override
	public void onCreate() {
		super.onCreate();
		initImageLoader(getApplicationContext());
	}

	public static void initImageLoader(Context context) {
		// This configuration tuning is custom. You can tune every option, you
		// may tune some of them,
		// or you can create default configuration by
		// ImageLoaderConfiguration.createDefault(this);
		// method.
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context).threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.writeDebugLogs() // Remove for release app
				.build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
	}

	public void setCurrentMarker(Marker marker) {
		this.current_marker = marker;
	}

	public Marker getCurrentMarker() {
		return this.current_marker;
	}

	public void setCurrenUser(User user) {
		this.current_user = user;
	}

	public User getCurrentUser() {
		return this.current_user;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public File getFile() {
		return this.file;
	}

	public void setListUser(ArrayList<User> list_user) {
		this.list_user = list_user;
	}

	public ArrayList<User> getListUser() {
		return this.list_user;
	}

	public void setCurrentUserIndex(int index) {
		this.current_user_index = index;
	}

	public int getCurrentUserIndex() {
		return this.current_user_index;
	}

	public void clear() {
		current_user_index = 0;
		list_user.clear();
		this.file = null;
		this.current_marker = null;
		this.current_user = null;
	}
}
