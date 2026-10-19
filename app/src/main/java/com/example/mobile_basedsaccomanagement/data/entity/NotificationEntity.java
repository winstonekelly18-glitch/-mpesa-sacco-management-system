package com.example.mobile_basedsaccomanagement.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "notifications",
    foreignKeys = @ForeignKey(
        entity = MemberEntity.class,
        parentColumns = "id",
        childColumns = "member_id",
        onDelete = ForeignKey.CASCADE
    ),
    indices = {@Index(value = {"member_id"})}
)
public class NotificationEntity {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "member_id")
    private long memberId;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "message")
    private String message;

    @ColumnInfo(name = "timestamp")
    private String timestamp;

    @ColumnInfo(name = "is_read")
    private boolean isRead;

    public NotificationEntity() {}

    public NotificationEntity(long memberId, String title, String message, String timestamp, boolean isRead) {
        this.memberId = memberId;
        this.title = title;
        this.message = message;
        this.timestamp = timestamp;
        this.isRead = isRead;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getMemberId() { return memberId; }
    public void setMemberId(long memberId) { this.memberId = memberId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
}
