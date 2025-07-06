package com.abhi.ltcecampuscare.ui.event;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.abhi.ltcecampuscare.R;

import java.text.DateFormat;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {
    private List<EventModel> eventList;

    public EventAdapter(List<EventModel> eventList) {
        this.eventList = eventList;
    }


    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        EventModel event = eventList.get(position);
        holder.subject.setText(event.getSubject());
        holder.body.setText(event.getBody());
        holder.time.setText(DateFormat.getDateTimeInstance().format(event.getTimestamp()));
        holder.author.setText(event.getPostedBy() + " (" + event.getRole() + ")");
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView subject, body, time;
        TextView author;


        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            author = itemView.findViewById(R.id.eventAuthor);
            subject = itemView.findViewById(R.id.eventSubject);
            body = itemView.findViewById(R.id.eventBody);
            time = itemView.findViewById(R.id.eventTime);
        }
    }
}
