package com.thalessz.ratwitter.models;

import java.sql.Timestamp;

public class Post {
    private int id;
    private String content;
    private int userId;
    private int likeCount;
    private Timestamp createdAt;

    public Post(int id, String content, int userId, int likeCount, Timestamp createdAt) {
        this.id = id;
        this.content = content;
        this.userId = userId;
        this.likeCount = likeCount;
        this.createdAt = createdAt;
    }
    public Post() {}


    // Getters
    public int getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public int getUserId() {
        return userId;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
