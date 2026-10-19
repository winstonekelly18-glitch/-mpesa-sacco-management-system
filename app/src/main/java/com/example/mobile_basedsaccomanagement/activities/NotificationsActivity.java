package com.example.mobile_basedsaccomanagement.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.mobile_basedsaccomanagement.adapters.NotificationAdapter;
import com.example.mobile_basedsaccomanagement.data.entity.NotificationEntity;
import com.example.mobile_basedsaccomanagement.databinding.ActivityNotificationsBinding;
import com.example.mobile_basedsaccomanagement.models.NotificationModel;
import com.example.mobile_basedsaccomanagement.utils.SessionManager;
import com.example.mobile_basedsaccomanagement.viewmodel.NotificationsViewModel;

import java.util.ArrayList;
import java.util.List;

public class NotificationsActivity extends AppCompatActivity {

    private ActivityNotificationsBinding binding;
    private NotificationsViewModel notificationsViewModel;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNotificationsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);
        notificationsViewModel = new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding.rvNotificationsList.setLayoutManager(new LinearLayoutManager(this));

        long memberId = sessionManager.getUserId();

        observeViewModel(memberId);

        binding.btnMarkRead.setOnClickListener(v -> {
            notificationsViewModel.markAllAsRead(memberId);
            Toast.makeText(this, "All notifications marked as read", Toast.LENGTH_SHORT).show();
        });
    }

    private void observeViewModel(long memberId) {
        notificationsViewModel.getNotificationsForMemberLiveData(memberId).observe(this, notifEntities -> {
            List<NotificationModel> list = new ArrayList<>();
            if (notifEntities != null) {
                for (NotificationEntity e : notifEntities) {
                    list.add(new NotificationModel(
                            e.getId(), memberId, e.getTitle(), e.getMessage(), e.getTimestamp(), e.isRead()
                    ));
                }
            }
            NotificationAdapter adapter = new NotificationAdapter(this, list);
            binding.rvNotificationsList.setAdapter(adapter);
        });
    }
}
