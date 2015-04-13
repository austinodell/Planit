package edu.villanvoa.together;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class TimeGridAdapter extends BaseAdapter {
    private static final String TAG = "Debugging";

    private List<User> mEvent;
    private final LayoutInflater mInflater;
    private ImageLib imgLib;
    private Context mContext;
    private String startTime, endTime;

    public TimeGridAdapter(Context context, ArrayList<User> list, ImageLib imgLib, String startTime, String endTime) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mEvent = list;
        this.imgLib = imgLib;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    @Override
    public int getCount() {
        return mEvent.size();
    }

    @Override
    public User getItem(int i) {
        return mEvent.get(i);
    }

    @Override
    public long getItemId(int i) {
        //return Integer.parseInt(mEvent.get(i).getId());
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = view;
        ImageView picture;
        TextView name;
        TextView times;
        View bar, space;

        if (v == null) {
            v = mInflater.inflate(R.layout.fragment_user_time_span, viewGroup, false);
            v.setTag(R.id.friend_img, v.findViewById(R.id.friend_img));
            v.setTag(R.id.friend_name_tv, v.findViewById(R.id.friend_name_tv));
            v.setTag(R.id.time_available_tv, v.findViewById(R.id.time_available_tv));
            v.setTag(R.id.time_bar, v.findViewById(R.id.time_bar));
            v.setTag(R.id.spacer, v.findViewById(R.id.spacer));
        }

        picture = (ImageView) v.getTag(R.id.friend_img);
        name = (TextView) v.getTag(R.id.friend_name_tv);
        times = (TextView) v.getTag(R.id.time_available_tv);
        bar = (View) v.getTag(R.id.time_bar);
        space = (View) v.getTag(R.id.spacer);

        final User user = getItem(i);

        imgLib.imageLoader.displayImage(user.getImageURL(), picture, imgLib.imageOptions); // Display Image

        Time timeObj = new Time();

        int wStartTime = timeObj.timeToInt(startTime) - 60;
        int wEndTime = timeObj.timeToInt(endTime) + 60;

        if(wStartTime < 0) {
            wStartTime = 0;
        }

        int sStartTime = timeObj.timeToInt(startTime) - 60;
        int sEndTime = timeObj.timeToInt(endTime) + 60;

        if(sStartTime < 0) {
            sStartTime = 0;
        }

        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        name.setText(user.getFirstName());
        times.setText(user.getStartTime() + " - " + user.getEndTime());

        int pStartTime = timeObj.timeToInt(user.getStartTime());
        if(pStartTime < wStartTime) {
            pStartTime = wStartTime;
        }

        int pEndTime = timeObj.timeToInt(user.getEndTime());
        if(pEndTime > wEndTime) {
            pEndTime = wEndTime;
        }

        int barSize = pEndTime - pStartTime + (width - wEndTime - wStartTime) - picture.getLayoutParams().width;

        if(barSize < 0) {
            barSize = 0;
        }

        int spaceSize = pStartTime - wStartTime - picture.getLayoutParams().width;

        int oldSpaceSize = timeObj.timeToInt(user.getStartTime()) / 2;

        int newSpaceSize = sStartTime - oldSpaceSize;

        bar.getLayoutParams().width = barSize;
        space.getLayoutParams().width = spaceSize;

        return v;
    }



}