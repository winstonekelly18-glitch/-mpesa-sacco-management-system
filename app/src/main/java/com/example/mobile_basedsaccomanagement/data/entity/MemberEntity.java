package com.example.mobile_basedsaccomanagement.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "members",
    foreignKeys = @ForeignKey(
        entity = UserEntity.class,
        parentColumns = "id",
        childColumns = "user_id",
        onDelete = ForeignKey.CASCADE
    ),
    indices = {
        @Index(value = {"user_id"}),
        @Index(value = {"member_number"}, unique = true),
        @Index(value = {"national_id"}, unique = true)
    }
)
public class MemberEntity {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "user_id")
    private long userId;

    @ColumnInfo(name = "member_number")
    private String memberNumber;

    @ColumnInfo(name = "full_name")
    private String fullName;

    @ColumnInfo(name = "national_id")
    private String nationalId;

    @ColumnInfo(name = "phone_number")
    private String phoneNumber;

    @ColumnInfo(name = "gender")
    private String gender;

    @ColumnInfo(name = "date_of_birth")
    private String dateOfBirth;

    @ColumnInfo(name = "address")
    private String address;

    @ColumnInfo(name = "profile_image_path")
    private String profileImagePath;

    @ColumnInfo(name = "status")
    private String status; // "PENDING", "APPROVED", "REJECTED"

    public MemberEntity() {}

    public MemberEntity(long userId, String memberNumber, String fullName, String nationalId,
                        String phoneNumber, String gender, String dateOfBirth, String address,
                        String profileImagePath, String status) {
        this.userId = userId;
        this.memberNumber = memberNumber;
        this.fullName = fullName;
        this.nationalId = nationalId;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.profileImagePath = profileImagePath;
        this.status = status;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }

    public String getMemberNumber() { return memberNumber; }
    public void setMemberNumber(String memberNumber) { this.memberNumber = memberNumber; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getNationalId() { return nationalId; }
    public void setNationalId(String nationalId) { this.nationalId = nationalId; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getProfileImagePath() { return profileImagePath; }
    public void setProfileImagePath(String profileImagePath) { this.profileImagePath = profileImagePath; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
