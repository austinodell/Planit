package edu.villanvoa.together;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class HomeGridAdapter extends BaseAdapter {
    private static final String TAG = "Debugging";

    private List<Event> mEvent;
    private final LayoutInflater mInflater;
    private ImageLib imgLib;
    private Context mContext;

    public HomeGridAdapter(Context context, ArrayList<Event> list, ImageLib imgLib) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mEvent = list;
        this.imgLib = imgLib;
    }

    @Override
    public int getCount() {
        return mEvent.size();
    }

    @Override
    public Event getItem(int i) {
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

        if (v == null) {
            v = mInflater.inflate(R.layout.fragment_event, viewGroup, false);
            v.setTag(R.id.event_img, v.findViewById(R.id.event_img));
            v.setTag(R.id.event_tv, v.findViewById(R.id.event_tv));
        }

        picture = (ImageView) v.getTag(R.id.event_img);
        name = (TextView) v.getTag(R.id.event_tv);

        final Event event = getItem(i);

        if(!event.imgIsURL()) {
            picture.setImageResource(event.getImageResource());
        } else {
            imgLib.imageLoader.displayImage(event.getImageURL(), picture, imgLib.imageOptions); // Display Image
        }

        name.setText(event.getName());

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Event (onItemClick): " + event.getName());

                Intent viewEventIntent = new Intent(mContext,ViewEvent.class);
                viewEventIntent.putExtra("EventObjectId", event.getId());
                mContext.startActivity(viewEventIntent);
            }
        });

        return v;
    }



}