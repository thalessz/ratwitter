package com.thalessz.ratwitter.models;

import java.io.Serializable;

public class Post implements Serializable {
    private int id;
    private String content;
    private int user_id;
    private int like_count;
    private String created_at;

    public Post(int id, String content, int user_id, int like_count, String createdAt) {
        this.id = id;
        this.content = content;
        this.user_id = user_id;
        this.like_count = like_count;
        this.created_at = createdAt;
    }
    public Post() {}


    // Getters
    public int getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public int getUser_id() {
        return user_id;
    }

    public int getLike_count() {
        return like_count;
    }

    public String getCreated_at() {
        return created_at;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public void setLike_count(int like_count) {
        this.like_count = like_count;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
}
