package com.example.mobile_basedsaccomanagement.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.example.mobile_basedsaccomanagement.data.dao.UserDao;
import com.example.mobile_basedsaccomanagement.data.entity.UserEntity;
import com.example.mobile_basedsaccomanagement.data.local.AppDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserRepository {

    private final UserDao userDao;
    private final ExecutorService executorService;

    public interface UserCallback<T> {
        void onResult(T result);
    }

    public UserRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        userDao = db.userDao();
        executorService = Executors.newFixedThreadPool(2);
    }

    public void insertUser(UserEntity user, UserCallback<Long> callback) {
        executorService.execute(() -> {
            long id = userDao.insertUser(user);
            if (callback != null) callback.onResult(id);
        });
    }

    public void findByEmail(String email, UserCallback<UserEntity> callback) {
        executorService.execute(() -> {
            UserEntity user = userDao.findByEmail(email);
            if (callback != null) callback.onResult(user);
        });
    }

    public void findById(long id, UserCallback<UserEntity> callback) {
        executorService.execute(() -> {
            UserEntity user = userDao.findById(id);
            if (callback != null) callback.onResult(user);
        });
    }

    public LiveData<UserEntity> findByIdLiveData(long id) {
        return userDao.findByIdLiveData(id);
    }

    public void updatePassword(long userId, String newHash, String newSalt, UserCallback<Boolean> callback) {
        executorService.execute(() -> {
            int rows = userDao.updatePassword(userId, newHash, newSalt);
            if (callback != null) callback.onResult(rows > 0);
        });
    }
}
