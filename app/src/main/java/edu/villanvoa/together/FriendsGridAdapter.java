package edu.villanvoa.together;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aodell on 3/10/15.
 */
public class FriendsGridAdapter extends BaseAdapter {
    private List<Friend> mFriend;
    private final LayoutInflater mInflater;
    private View btnView;
    private GridView gridView;

    public FriendsGridAdapter(Context context, ArrayList<Friend> list, View btnView, GridView gridView) {
        mInflater = LayoutInflater.from(context);
        mFriend = list;
        this.btnView = btnView;
        this.gridView = gridView;
    }

    public FriendsGridAdapter(Context context, ArrayList<Friend> list, GridView gridView) {
        mInflater = LayoutInflater.from(context);
        mFriend = list;
        this.btnView = null;
        this.gridView = gridView;
    }

    @Override
    public int getCount() {
        if(btnView == null) {
            return mFriend.size();
        }

        return mFriend.size() + 1;
    }

    @Override
    public Friend getItem(int i) {
        return mFriend.get(i);
    }

    @Override
    public long getItemId(int i) {
        if(mFriend.size() > 0) {
            return Long.parseLong(mFriend.get(i).id);
        } else {
            return 0;
        }
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = view;
        ImageView picture;
        TextView name;

        if(i == 0 && btnView != null) {

            v = btnView;

        } else {
            if(btnView != null) {
                i--;
            }

            Friend friend = getItem(i);

            if(friend.isReal) {

                if (v == null) {
                    v = mInflater.inflate(R.layout.fragment_grid_user, viewGroup, false);
                    v.setTag(R.id.friend_img, v.findViewById(R.id.friend_img));
                    v.setTag(R.id.friend_name_tv, v.findViewById(R.id.friend_name_tv));
                }

                picture = (ImageView) v.getTag(R.id.friend_img);
                name = (TextView) v.getTag(R.id.friend_name_tv);


                String img_url = "http://graph.facebook.com/" + friend.id + "/picture?type=large";
                AddFriends.imageLoader.displayImage(img_url, picture, AddFriends.imageOptions); // Display Image

                int spaceIndex = friend.name.indexOf(' ');
                String firstName = friend.name.substring(0, spaceIndex);

                name.setText(firstName); // Show First Name

            } else {

                if (v == null) {
                    v = mInflater.inflate(R.layout.fragment_grid_hint, viewGroup, false);
                    v.setTag(R.id.hint_add_user, v.findViewById(R.id.hint_add_user));
                }

                TextView hint = (TextView) v.getTag(R.id.hint_add_user);
                hint.setText(friend.name); // Show hint

            }

        }

        return v;
    }
}