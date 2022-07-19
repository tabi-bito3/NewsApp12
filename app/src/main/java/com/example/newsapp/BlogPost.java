package com.example.newsapp;
import java.util.Date;


public class BlogPost extends BlogPostId{
    public String user_id,thumb_img,desc, title;
    public Date timestamp;
    public BlogPost(){};
    public BlogPost(String user_id, String thumb_img, String title,String desc, Date timestamp) {
        this.user_id = user_id;
        this.thumb_img = thumb_img;
        this.title = title;
        this.desc = desc;
        this.timestamp = timestamp;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getThumb_img() {
        return thumb_img;
    }

    public void setThumb_img(String thumb_img) {
        this.thumb_img = thumb_img;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }



}
