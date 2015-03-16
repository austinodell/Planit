package edu.villanvoa.together;

import android.content.Context;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

/**
 * Created by aodell on 3/16/15.
 */
public class ImageLib {

    /* UniversalImageLoader library variables */
    static public ImageLoader imageLoader = null;
    static public DisplayImageOptions imageOptions = null;
    static public ImageLoaderConfiguration imageConfig = null;

    public ImageLib(Context mContext) {
        setupIfNeeded(mContext);
    }

    public void setupIfNeeded(Context mContext) {
        if(imageConfig == null) {
            imageConfig = new ImageLoaderConfiguration.Builder(mContext)
                    .build();
            imageLoader = ImageLoader.getInstance();
            imageLoader.init(imageConfig);

            imageOptions = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.drawable.ic_launcher)
                    .showImageForEmptyUri(R.drawable.ic_launcher)
                    .showImageOnFail(R.drawable.ic_launcher)
                    .cacheOnDisk(true)
                    .build();
        }
    }
}
