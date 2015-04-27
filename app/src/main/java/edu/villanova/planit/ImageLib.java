package edu.villanova.planit;

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

    private Context mContext;

    public ImageLib(Context mContext) {
        setupIfNeeded(mContext);
        this.mContext = mContext;
    }

    public void setupIfNeeded(Context mContext) {
        if(imageConfig == null) {
            imageConfig = new ImageLoaderConfiguration.Builder(mContext)
                    .build();
            imageLoader = ImageLoader.getInstance();
            imageLoader.init(imageConfig);

            imageOptions = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.drawable.picnic)
                    .showImageForEmptyUri(R.drawable.picnic)
                    .showImageOnFail(R.drawable.picnic)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .build();
        }
    }

    public int getResId(String name) {
        int resid = mContext.getResources().getIdentifier(name.toLowerCase(), "drawable", mContext.getPackageName());

        return resid;
    }
}
