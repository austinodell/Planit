package edu.villanvoa.together;

import android.content.Context;
import android.content.res.Resources;
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

            name.setText(title);
            picture.setImageResource(resid);
        } else {
            v = lastItem;
        }

        return v;
    }

}
