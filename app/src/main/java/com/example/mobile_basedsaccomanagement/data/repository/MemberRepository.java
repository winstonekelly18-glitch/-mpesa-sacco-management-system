package com.example.mobile_basedsaccomanagement.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.example.mobile_basedsaccomanagement.data.dao.MemberDao;
import com.example.mobile_basedsaccomanagement.data.entity.MemberEntity;
import com.example.mobile_basedsaccomanagement.data.local.AppDatabase;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MemberRepository {

    private final MemberDao memberDao;
    private final ExecutorService executorService;

    public interface MemberCallback<T> {
        void onResult(T result);
    }

    public MemberRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        memberDao = db.memberDao();
        executorService = Executors.newFixedThreadPool(2);
    }

    public void insertMember(MemberEntity member, MemberCallback<Long> callback) {
        executorService.execute(() -> {
            long id = memberDao.insertMember(member);
            if (callback != null) callback.onResult(id);
        });
    }

    public void updateMember(MemberEntity member, MemberCallback<Boolean> callback) {
        executorService.execute(() -> {
            int rows = memberDao.updateMember(member);
            if (callback != null) callback.onResult(rows > 0);
        });
    }

    public void findByUserId(long userId, MemberCallback<MemberEntity> callback) {
        executorService.execute(() -> {
            MemberEntity member = memberDao.findByUserId(userId);
            if (callback != null) callback.onResult(member);
        });
    }

    public LiveData<MemberEntity> findByUserIdLiveData(long userId) {
        return memberDao.findByUserIdLiveData(userId);
    }

    public LiveData<List<MemberEntity>> getAllMembersLiveData() {
        return memberDao.getAllMembersLiveData();
    }

    public void updateMemberStatus(long memberId, String status, MemberCallback<Boolean> callback) {
        executorService.execute(() -> {
            int rows = memberDao.updateMemberStatus(memberId, status);
            if (callback != null) callback.onResult(rows > 0);
        });
    }

    public LiveData<Integer> getPendingMembersCountLiveData() {
        return memberDao.getPendingMembersCountLiveData();
    }
}
