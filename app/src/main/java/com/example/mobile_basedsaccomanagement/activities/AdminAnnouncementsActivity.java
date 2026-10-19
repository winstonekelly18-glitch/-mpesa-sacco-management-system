package com.example.mobile_basedsaccomanagement.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.mobile_basedsaccomanagement.adapters.AnnouncementAdapter;
import com.example.mobile_basedsaccomanagement.data.entity.AnnouncementEntity;
import com.example.mobile_basedsaccomanagement.databinding.ActivityAdminAnnouncementsBinding;
import com.example.mobile_basedsaccomanagement.models.Announcement;
import com.example.mobile_basedsaccomanagement.viewmodel.AdminViewModel;

import java.util.ArrayList;
import java.util.List;

public class AdminAnnouncementsActivity extends AppCompatActivity {

    private ActivityAdminAnnouncementsBinding binding;
    private AdminViewModel adminViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminAnnouncementsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        adminViewModel = new ViewModelProvider(this).get(AdminViewModel.class);

        binding.rvAdminAnnouncements.setLayoutManager(new LinearLayoutManager(this));

        observeViewModel();

        binding.btnPostAnnouncement.setOnClickListener(v -> postAnnouncement());
    }

    private void observeViewModel() {
        adminViewModel.getAllAnnouncementsLiveData().observe(this, annEntities -> {
            List<Announcement> list = new ArrayList<>();
            if (annEntities != null) {
                for (AnnouncementEntity e : annEntities) {
                    list.add(new Announcement(
                            e.getId(), e.getTitle(), e.getContent(), e.getDatePosted(), e.getPostedBy()
                    ));
                }
            }
            AnnouncementAdapter adapter = new AnnouncementAdapter(this, list);
            binding.rvAdminAnnouncements.setAdapter(adapter);
        });

        adminViewModel.getActionResult().observe(this, msg -> {
            if (msg != null) {
                Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void postAnnouncement() {
        String title = binding.etAnnTitle.getText().toString().trim();
        String content = binding.etAnnContent.getText().toString().trim();

        if (TextUtils.isEmpty(title)) {
            binding.etAnnTitle.setError("Title is required");
            return;
        }

        if (TextUtils.isEmpty(content)) {
            binding.etAnnContent.setError("Content is required");
            return;
        }

        adminViewModel.broadcastAnnouncement(title, content, "SACCO Administration");
        binding.etAnnTitle.setText("");
        binding.etAnnContent.setText("");
    }
}
