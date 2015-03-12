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

/**
 * Created by aodell on 3/12/15.
 */
public class IdeaListAdapter extends BaseAdapter {
    private List<Idea> mIdea;
    private final LayoutInflater mInflater;

    public IdeaListAdapter(Context context, ArrayList<Idea> list) {
        mInflater = LayoutInflater.from(context);
        mIdea = list;
    }

    @Override
    public int getCount() {
        return mIdea.size();
    }

    @Override
    public Idea getItem(int i) {
        return mIdea.get(i);
    }

    @Override
    public long getItemId(int i) {
        return mIdea.get(i).getId();
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

        Home.Event event = getItem(i);

        picture.setImageResource(event.drawableId);
        name.setText(event.name);

        return v;
    }
