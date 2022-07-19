package com.example.newsapp;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

public class BlogRecyclerAdapter extends RecyclerView.Adapter<BlogRecyclerAdapter.ViewHolder> {

    public List<BlogPost> blog_list;
    public Context context;
    public BlogRecyclerAdapter(List<BlogPost> blog_list){
        this.blog_list=blog_list;
    }

    public void setBlog_list(List<BlogPost> blog_list){
        this.blog_list=blog_list;
    }
    @Override
    public ViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_list_item, parent, false);

        context = parent.getContext();

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder( BlogRecyclerAdapter.ViewHolder holder, int position) {
        String desc_data = blog_list.get(position).getTitle();
        holder.setDescText(desc_data);

        String thumbImg = blog_list.get(position).getThumb_img();
        holder.setBlogImage(thumbImg);
        String ago = "";
        //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        //sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        try {
            long millisecond = blog_list.get(position).getTimestamp().getTime();
            long now = System.currentTimeMillis();
            ago = DateUtils.getRelativeTimeSpanString(millisecond, now, DateUtils.MINUTE_IN_MILLIS).toString();
            String dateString = DateFormat.getDateInstance().format(millisecond).toString();

        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.setTime(ago);

    }

    @Override
    public int getItemCount() {
        return blog_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private View mView;
        private TextView descView;
        private TextView postDate;
        private ImageView postImageView;
        public ViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
        }
        public void setDescText(String descText){
            descView= mView.findViewById(R.id.post_title);
            descView.setText(descText);
        }

        public void setBlogImage(String downloadUrl){
            postImageView = mView.findViewById(R.id.post_img);
            Glide.with(context).load(downloadUrl).into(postImageView);
        }

        public void setTime(String date){
            postDate = mView.findViewById(R.id.post_date);
            postDate.setText(date);
        }
    }
}
