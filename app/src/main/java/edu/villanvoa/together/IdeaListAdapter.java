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

public class IdeaListAdapter extends BaseAdapter {
    private List<Idea> mIdeaList;
    private final LayoutInflater mInflater;

    public IdeaListAdapter(Context context, ArrayList<Idea> list) {
        mInflater = LayoutInflater.from(context);
        mIdeaList = list;
    }

    @Override
    public int getCount() {
        return mIdeaList.size();
    }

    @Override
    public Idea getItem(int i) {
        return mIdeaList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return mIdeaList.get(i).getId();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = view;
        TextView name, rank;

        if (v == null) {
            v = mInflater.inflate(R.layout.fragment_idea_list_item, viewGroup, false);
            v.setTag(R.id.idea_list_item_name, v.findViewById(R.id.idea_list_item_name));
            v.setTag(R.id.idea_list_item_rank, v.findViewById(R.id.idea_list_item_rank));
        }

        name = (TextView) v.getTag(R.id.idea_list_item_name);
        rank = (TextView) v.getTag(R.id.idea_list_item_rank);

        Idea idea = getItem(i);

        name.setText(idea.getName());
        rank.setText("+" + idea.getId());

        return v;
    }
}