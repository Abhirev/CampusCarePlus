package com.abhi.ltcecampuscare.ui.lost_and_found;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.abhi.ltcecampuscare.R;


import java.text.DateFormat;
import java.util.List;

public class LostFoundAdapter extends RecyclerView.Adapter<LostFoundAdapter.LostFoundViewHolder> {

    private List<LostFoundModel> lostFoundList;

    public LostFoundAdapter(List<LostFoundModel> lostFoundList) {
        this.lostFoundList = lostFoundList;
    }

    @NonNull
    @Override
    public LostFoundViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event, parent, false); // reusing same layout for now
        return new LostFoundViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LostFoundViewHolder holder, int position) {
        LostFoundModel item = lostFoundList.get(position);

        holder.subject.setText(item.getSubject());
        holder.body.setText(item.getBody());
        holder.time.setText(DateFormat.getDateTimeInstance().format(item.getTimestamp()));
        holder.author.setText(item.getPostedBy() + " (" + item.getRole() + ")");
    }

    @Override
    public int getItemCount() {
        return lostFoundList.size();
    }

    public static class LostFoundViewHolder extends RecyclerView.ViewHolder {
        TextView subject, body, time, author;

        public LostFoundViewHolder(@NonNull View itemView) {
            super(itemView);
            subject = itemView.findViewById(R.id.eventSubject);
            body = itemView.findViewById(R.id.eventBody);
            time = itemView.findViewById(R.id.eventTime);
            author = itemView.findViewById(R.id.eventAuthor);
        }
    }
}
