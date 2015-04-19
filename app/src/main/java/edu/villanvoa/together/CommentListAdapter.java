package edu.villanvoa.together;

import android.content.Context;
import android.content.SharedPreferences;
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
    private String userFbId;

    public CommentListAdapter(Context context, ArrayList<Comment> list){
        mInflater = LayoutInflater.from(context);
        commentList = list;

        //Get userId from shared preferences
        SharedPreferences sharedPreferences;
        sharedPreferences = context.getSharedPreferences("UserDetails", MainActivity.MODE_PRIVATE);
        userFbId = sharedPreferences.getString("UserFbId", null);

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
        boolean isUserComment = false;

        Comment com = getItem(position);

        if (v == null) {

            if(userFbId.equals(com.getUserID())){

                isUserComment = true;

                v = mInflater.inflate(R.layout.user_comment_item, parent, false);
                v.setTag(R.id.ucomment_list_user_name, v.findViewById(R.id.ucomment_list_user_name));
                v.setTag(R.id.ucomment_list_comment, v.findViewById(R.id.ucomment_list_comment));
                v.setTag(R.id.ucomment_time_stamp, v.findViewById(R.id.ucomment_time_stamp));

            }else {

                isUserComment = false;

                v = mInflater.inflate(R.layout.comment_item, parent, false);
                v.setTag(R.id.comment_list_user_name, v.findViewById(R.id.comment_list_user_name));
                v.setTag(R.id.comment_list_comment, v.findViewById(R.id.comment_list_comment));
                v.setTag(R.id.comment_time_stamp, v.findViewById(R.id.comment_time_stamp));
            }

        }

        if(isUserComment){
            name = (TextView) v.getTag(R.id.ucomment_list_user_name);
            comment = (TextView) v.getTag(R.id.ucomment_list_comment);
            time = (TextView) v.getTag(R.id.ucomment_time_stamp);
        }else {
            name = (TextView) v.getTag(R.id.comment_list_user_name);
            comment = (TextView) v.getTag(R.id.comment_list_comment);
            time = (TextView) v.getTag(R.id.comment_time_stamp);
        }

        name.setText(com.getName());
        comment.setText(com.getComment());
        time.setText(com.getTimeStamp());

        String commenterID = com.getUserID();
        final String img_url = "https://graph.facebook.com/" + commenterID + "/picture?type=large";

        notifyDataSetChanged();

        return v;
    }
}
