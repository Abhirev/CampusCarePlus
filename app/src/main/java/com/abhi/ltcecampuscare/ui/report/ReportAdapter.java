package com.abhi.ltcecampuscare.ui.report;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.abhi.ltcecampuscare.R;

import java.text.DateFormat;
import java.util.List;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ReportViewHolder> {

    private List<ReportModel> reportList;

    public ReportAdapter(List<ReportModel> reportList) {
        this.reportList = reportList;
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event, parent, false); // ✅ reusing item_event layout
        return new ReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        ReportModel item = reportList.get(position);

        holder.subject.setText(item.getSubject());
        holder.body.setText(item.getBody());
        holder.time.setText(DateFormat.getDateTimeInstance().format(item.getTimestamp()));
        holder.author.setText(item.getPostedBy() + " (" + item.getRole() + ")");
    }

    @Override
    public int getItemCount() {
        return reportList.size();
    }

    public static class ReportViewHolder extends RecyclerView.ViewHolder {
        TextView subject, body, time, author;

        public ReportViewHolder(@NonNull View itemView) {
            super(itemView);
            subject = itemView.findViewById(R.id.eventSubject);
            body = itemView.findViewById(R.id.eventBody);
            time = itemView.findViewById(R.id.eventTime);
            author = itemView.findViewById(R.id.eventAuthor);
        }
    }
}
