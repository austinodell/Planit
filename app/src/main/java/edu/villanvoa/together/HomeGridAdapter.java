package edu.villanvoa.together;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class HomeGridAdapter extends BaseAdapter {
    private List<Event> mEvent;
    private final LayoutInflater mInflater;
    private ImageLib imgLib;

    public HomeGridAdapter(Context context, ArrayList<Event> list, ImageLib imgLib) {
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
        return mEvent.get(i).getId();
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

        Event event = getItem(i);

        if(!event.imgIsURL()) {
            picture.setImageResource(event.getImageResource());
        } else {
            imgLib.imageLoader.displayImage(event.getImageURL(), picture, imgLib.imageOptions); // Display Image
        }

        name.setText(event.getName());

        return v;
    }
}