package like.app;

import android.support.v4.app.FragmentActivity;

import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 
 * @author hetpin
 * 
 */
public abstract class BaseActivity extends FragmentActivity {

	protected ImageLoader imageLoader = ImageLoader.getInstance();

	// public ImageLoader getImageLoader() {
	// return this.imageLoader;
	// }
}
