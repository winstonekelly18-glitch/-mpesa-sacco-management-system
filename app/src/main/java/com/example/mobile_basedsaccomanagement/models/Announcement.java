package com.example.mobile_basedsaccomanagement.models;

public class Announcement {
    private long id;
    private String title;
    private String content;
    private String datePosted;
    private String postedBy;

    public Announcement() {}

    public Announcement(long id, String title, String content, String datePosted, String postedBy) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.datePosted = datePosted;
        this.postedBy = postedBy;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getDatePosted() { return datePosted; }
    public void setDatePosted(String datePosted) { this.datePosted = datePosted; }

    public String getPostedBy() { return postedBy; }
    public void setPostedBy(String postedBy) { this.postedBy = postedBy; }
}
