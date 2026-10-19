package com.example.mobile_basedsaccomanagement.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.mobile_basedsaccomanagement.data.entity.MemberEntity;
import com.example.mobile_basedsaccomanagement.data.entity.UserEntity;
import com.example.mobile_basedsaccomanagement.data.local.AppDatabase;
import com.example.mobile_basedsaccomanagement.util.PasswordUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProfileViewModel extends AndroidViewModel {

    private final AppDatabase db;
    private final ExecutorService executorService;

    private final MutableLiveData<String> actionMessage = new MutableLiveData<>();

    public ProfileViewModel(@NonNull Application application) {
        super(application);
        db = AppDatabase.getInstance(application);
        executorService = Executors.newFixedThreadPool(2);
    }

    public LiveData<String> getActionMessage() { return actionMessage; }

    public LiveData<MemberEntity> getMemberByUserIdLiveData(long userId) {
        return db.memberDao().findByUserIdLiveData(userId);
    }

    public void updateMemberProfile(MemberEntity member) {
        executorService.execute(() -> {
            int rows = db.memberDao().updateMember(member);
            if (rows > 0) {
                actionMessage.postValue("Profile updated successfully!");
            } else {
                actionMessage.postValue("Failed to update profile!");
            }
        });
    }

    public void changePassword(long userId, String currentPassword, String newPassword) {
        executorService.execute(() -> {
            UserEntity user = db.userDao().findById(userId);
            if (user == null) {
                actionMessage.postValue("User account not found!");
                return;
            }

            boolean match = PasswordUtils.verifyPassword(currentPassword, user.getPasswordHash(), user.getSalt());
            if (!match) {
                actionMessage.postValue("Incorrect current password!");
                return;
            }

            String newSalt = PasswordUtils.generateSalt();
            String newHash = PasswordUtils.hashPassword(newPassword, newSalt);

            int rows = db.userDao().updatePassword(userId, newHash, newSalt);
            if (rows > 0) {
                actionMessage.postValue("Password changed successfully!");
            } else {
                actionMessage.postValue("Failed to update password!");
            }
        });
    }
}
