package com.example.adil.blogapp;


import java.sql.Date;
import java.sql.Timestamp;

public class BlogPost extends BlogPostId {

    private String UserId,Post_image,description;





    public BlogPost()
    {

    }



    public BlogPost(String userId, String post_image, String description) {
        UserId = userId;
        Post_image = post_image;
        this.description = description;


    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getPost_image() {
        return Post_image;
    }

    public void setPost_image(String post_image) {
        Post_image = post_image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }






}
