package edu.villanvoa.together;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by aodell on 3/17/15.
 */
public class ImageSpinnerAdapter extends BaseAdapter {

    LayoutInflater inflator;
    ArrayList<String> mList;
    private Context mContext;
    private View lastItem;

    public ImageSpinnerAdapter(Context context, ArrayList<String> list,View lastItem)
    {
        mContext = context;
        inflator = LayoutInflater.from(context);
        mList=list;
        this.lastItem = lastItem;
    }

    @Override
    public int getCount()
    {
        return mList.size() + 1;
    }

    @Override
    public Object getItem(int position)
    {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent)
    {
        View v = view;

        if(position < mList.size()) {
            ImageView picture;
            TextView name;

            v = inflator.inflate(R.layout.choose_image_spinner_item, null);
            name = (TextView) v.findViewById(R.id.spinner_item_tv);
            picture = (ImageView) v.findViewById(R.id.spinner_item_img);

            String title = mList.get(position);
            int resid = mContext.getResources().getIdentifier(title.toLowerCase(), "drawable", mContext.getPackageName());

            Log.i("imageName","Image Name: " + title.toLowerCase());

            picture.setImageBitmap(
                    decodeSampledBitmapFromResource(mContext.getResources(), resid, 35, 35, mContext));

            name.setText(title);
            //picture.setImageResource(resid);
        } else {
            v = lastItem;
        }

        return v;
    }

    private static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        Log.i("sampleImage", "Displaying img at " + reqWidth + "px x " + reqHeight + "px (" + inSampleSize + " SampleSize)");

        return inSampleSize;
    }

    private static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         float dipWidth, float dipHeight, Context mContext) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        int reqWidth = dipToPixels(mContext,dipWidth);
        int reqHeight = dipToPixels(mContext,dipHeight);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    private static int dipToPixels(Context context, float dipValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }
}
