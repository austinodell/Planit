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

        name.setText(user.getFirstName());
        times.setText(user.getStartTime() + " - " + user.getEndTime());

        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        /*int screenStart = 0, screenEnd = width;
        int timeSpan = timeObj.timeToInt(endTime) - timeObj.timeToInt(startTime) + 120;
        int screenSpan = screenEnd - screenStart;
        int diff = screenSpan - timeSpan;

        int pStartTime = timeObj.timeToInt(user.getStartTime()) - timeObj.timeToInt(startTime) + 60;
        int pEndTime = timeObj.timeToInt(user.getEndTime()) - timeObj.timeToInt(startTime) + 60;

        if(pEndTime < pStartTime) {
            pEndTime += timeObj.timeToInt("11:59 PM");
        }

        int pStart = pStartTime + 50;
        int pBarSize = pEndTime - pStartTime - picture.getLayoutParams().width;
        int pSpaceSize = pStart;

        if(pStart < 0) {
            pSpaceSize = 0;
            pBarSize += pStart;
        }

        bar.getLayoutParams().width = pBarSize;
        space.getLayoutParams().width = pStart;*/

        double tStartTime = timeObj.timeToInt(startTime);
        double tEndTime = timeObj.timeToInt(endTime);

        if(tStartTime < 0) {
            tStartTime += timeObj.timeToInt("11:59 PM");
        }

        if(tEndTime < tStartTime) {
            tEndTime += timeObj.timeToInt("11:59 PM");
        }

        tStartTime -= 60;
        tEndTime += 60;

        double pStartTime = timeObj.timeToInt(user.getStartTime());
        double pEndTime = timeObj.timeToInt(user.getEndTime());

        double rate = (double) ((width - 40) / (tEndTime - tStartTime));

        double cpStartTime = (pStartTime - tStartTime ) * rate + 20;
        double cpEndTime = (width - 20) - ((tEndTime - pEndTime) * rate);

        double pBarSize = cpEndTime - cpStartTime - 120;

        bar.getLayoutParams().width = (int) pBarSize;
        space.getLayoutParams().width = (int) cpStartTime;

        Log.i(TAG, "User: " + user.getFirstName() + ", tStartTime: " + tStartTime + ", tEndTime: " + tEndTime + ", barsize: " + pBarSize + ", spacesize: " +
                cpStartTime + ", cpEndTime: " + cpEndTime + ", pStartTime: " + pStartTime +
                ", pEndTime: " + pEndTime + ", rate: " + rate);

        /*Log.i(TAG,"Person: " + user.getFirstName() + ", timespan: " + timeSpan + ", screenSpan: " + screenSpan + ", diff: " + diff +
                ", pStart: " + pStart + ", pStartTime: " + pStartTime + ", pEndTime: " + pEndTime +
                ", barSize: " + pBarSize + ", pSpaceSize: " + pSpaceSize);*/

        return v;
    }



}