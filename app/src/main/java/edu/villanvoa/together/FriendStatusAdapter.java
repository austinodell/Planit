package edu.villanvoa.together;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by wildcat on 4/8/2015.
 */
public class FriendStatusAdapter extends BaseAdapter {
    final static String TAG = "Debugging";

    private final LayoutInflater mInflater;
    private ImageLib imgLib = null;
    private ArrayList<FriendStatus> friendStatusList;

    public FriendStatusAdapter(Context context, ImageLib imgLib, ArrayList<FriendStatus> friendStatusList) {
        mInflater = LayoutInflater.from(context);
        this.imgLib = imgLib;
        this.friendStatusList = friendStatusList;
    }

    @Override
    public int getCount() {
        return friendStatusList.size();
    }

    @Override
    public FriendStatus getItem(int position) {
        return friendStatusList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        TextView friendName, friendStatusTV;
        final SquareImage picture;


        if (v == null) {
            v = mInflater.inflate(R.layout.friend_status, parent, false);
            v.setTag(R.id.friend_status_friendName, v.findViewById(R.id.friend_status_friendName));
            v.setTag(R.id.friend_status_friendStatus, v.findViewById(R.id.friend_status_friendStatus));
            v.setTag(R.id.friend_status_imageView, v.findViewById(R.id.friend_status_imageView));

        }

        friendName = (TextView) v.getTag(R.id.friend_status_friendName);
        friendStatusTV = (TextView) v.getTag(R.id.friend_status_friendStatus);
        picture = (SquareImage) v.getTag(R.id.friend_status_imageView);

        FriendStatus friendStatus;
        friendStatus = getItem(position);

        if (friendStatus != null) {
            if (friendName != null && friendStatus.getFriendName() != null) {
                friendName.setText(friendStatus.getFriendName() + ": ");
            }
            if (friendStatusTV!= null && friendStatus.getFriendStatus() != null) {
                friendStatusTV.setText(friendStatus.getFriendStatus());
            }
            if (friendStatus.getFriendId() != null) {
                String friendId = friendStatus.getFriendId();
                final String img_url = "https://graph.facebook.com/" + friendId + "/picture?type=normal";
                imgLib.imageLoader.displayImage(img_url, picture, imgLib.imageOptions); // Display Image
            }

        }

        notifyDataSetChanged();

        return v;
    }
}
