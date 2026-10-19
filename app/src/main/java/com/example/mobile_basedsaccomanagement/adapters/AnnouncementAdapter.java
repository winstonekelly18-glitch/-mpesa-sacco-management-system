package com.example.mobile_basedsaccomanagement.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile_basedsaccomanagement.R;
import com.example.mobile_basedsaccomanagement.models.Announcement;

import java.util.List;

public class AnnouncementAdapter extends RecyclerView.Adapter<AnnouncementAdapter.ViewHolder> {

    private final Context context;
    private final List<Announcement> announcementList;

    public AnnouncementAdapter(Context context, List<Announcement> announcementList) {
        this.context = context;
        this.announcementList = announcementList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_announcement, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Announcement ann = announcementList.get(position);

        holder.tvAnnTitle.setText(ann.getTitle());
        holder.tvAnnContent.setText(ann.getContent());
        holder.tvAnnDate.setText(ann.getDatePosted());
        holder.tvAnnPostedBy.setText("Posted by: " + ann.getPostedBy());
    }

    @Override
    public int getItemCount() {
        return announcementList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvAnnTitle, tvAnnContent, tvAnnDate, tvAnnPostedBy;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAnnTitle = itemView.findViewById(R.id.tvAnnTitle);
            tvAnnContent = itemView.findViewById(R.id.tvAnnContent);
            tvAnnDate = itemView.findViewById(R.id.tvAnnDate);
            tvAnnPostedBy = itemView.findViewById(R.id.tvAnnPostedBy);
        }
    }
}
