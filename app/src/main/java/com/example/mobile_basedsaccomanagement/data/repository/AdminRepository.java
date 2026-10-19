package com.example.mobile_basedsaccomanagement.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.example.mobile_basedsaccomanagement.data.dao.AdminDao;
import com.example.mobile_basedsaccomanagement.data.dao.AnnouncementDao;
import com.example.mobile_basedsaccomanagement.data.dao.SaccoProductDao;
import com.example.mobile_basedsaccomanagement.data.entity.AnnouncementEntity;
import com.example.mobile_basedsaccomanagement.data.entity.SaccoProductEntity;
import com.example.mobile_basedsaccomanagement.data.local.AppDatabase;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AdminRepository {

    private final AdminDao adminDao;
    private final SaccoProductDao productDao;
    private final AnnouncementDao announcementDao;
    private final ExecutorService executorService;

    public interface AdminCallback<T> {
        void onResult(T result);
    }

    public AdminRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        adminDao = db.adminDao();
        productDao = db.saccoProductDao();
        announcementDao = db.announcementDao();
        executorService = Executors.newFixedThreadPool(2);
    }

    public LiveData<List<SaccoProductEntity>> getAllProductsLiveData() {
        return productDao.getAllProductsLiveData();
    }

    public void insertProduct(SaccoProductEntity product, AdminCallback<Long> callback) {
        executorService.execute(() -> {
            long id = productDao.insertProduct(product);
            if (callback != null) callback.onResult(id);
        });
    }

    public void updateProduct(SaccoProductEntity product, AdminCallback<Boolean> callback) {
        executorService.execute(() -> {
            int rows = productDao.updateProduct(product);
            if (callback != null) callback.onResult(rows > 0);
        });
    }

    public void deleteProduct(SaccoProductEntity product, AdminCallback<Boolean> callback) {
        executorService.execute(() -> {
            int rows = productDao.deleteProduct(product);
            if (callback != null) callback.onResult(rows > 0);
        });
    }

    public LiveData<List<AnnouncementEntity>> getAllAnnouncementsLiveData() {
        return announcementDao.getAllAnnouncementsLiveData();
    }

    public void insertAnnouncement(AnnouncementEntity announcement, AdminCallback<Long> callback) {
        executorService.execute(() -> {
            long id = announcementDao.insertAnnouncement(announcement);
            if (callback != null) callback.onResult(id);
        });
    }
}
