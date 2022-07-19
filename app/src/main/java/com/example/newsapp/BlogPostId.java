package com.example.newsapp;

import com.google.firebase.database.Exclude;

public class BlogPostId {
    @Exclude
    public String blogPostId;

    public <T extends BlogPostId> T withId (final String id){
        this.blogPostId = id;
        return (T) this;
    }
}
