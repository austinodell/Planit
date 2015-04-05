package edu.villanvoa.together;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Matt_Wiedmeier on 3/15/15.
 */
public class CommentListAdapter extends BaseAdapter {

    private List<Comment> commentList;
    private final LayoutInflater mInflater;
    private SimpleDateFormat dateTimeStamp;

    public CommentListAdapter(Context context, ArrayList<Comment> list){
        mInflater = LayoutInflater.from(context);
        commentList = list;
    }

    public void addItems(ArrayList<Comment> updatedList){

        commentList.clear();
        commentList.addAll(updatedList);
    }
    @Override
    public int getCount() {
        return commentList.size();
    }

    @Override
    public Comment getItem(int position) {
        return commentList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;
        TextView name, comment,time;
        final ImageView picture;


        if (v == null) {
            v = mInflater.inflate(R.layout.comment_item, parent, false);
            v.setTag(R.id.comment_list_user_name, v.findViewById(R.id.comment_list_user_name));
            v.setTag(R.id.comment_list_comment, v.findViewById(R.id.comment_list_comment));
            v.setTag(R.id.comment_time_stamp, v.findViewById(R.id.comment_time_stamp));

        }

        name = (TextView) v.getTag(R.id.comment_list_user_name);
        comment = (TextView) v.getTag(R.id.comment_list_comment);
        time = (TextView) v.getTag(R.id.comment_time_stamp);

        Comment com = getItem(position);

        name.setText(com.getName() + ":");
        comment.setText(com.getComment());
        time.setText(com.getTimeStamp());

        String commenterID = com.getUserID();
        final String img_url = "https://graph.facebook.com/" + commenterID + "/picture?type=large";

        notifyDataSetChanged();

        return v;
    }
}
