package com.example.mobile_basedsaccomanagement.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "announcements")
public class AnnouncementEntity {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "content")
    private String content;

    @ColumnInfo(name = "date_posted")
    private String datePosted;

    @ColumnInfo(name = "posted_by")
    private String postedBy;

    public AnnouncementEntity() {}

    public AnnouncementEntity(String title, String content, String datePosted, String postedBy) {
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
