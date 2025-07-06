package com.abhi.ltcecampuscare.ui.event;

import java.util.Date;

public class EventModel {
    private String subject;
    private String body;
    private Date timestamp;
    private String postedBy;
    private String role;


    public EventModel() {}

    public EventModel(String subject, String body, Date timestamp, String postedBy, String role) {
        this.subject = subject;
        this.body = body;
        this.timestamp = timestamp;
        this.postedBy = postedBy;
        this.role = role;
    }

    public String getSubject() { return subject; }
    public String getPostedBy() { return postedBy; }
    public String getRole() { return role; }
    public String getBody() { return body; }
    public Date getTimestamp() { return timestamp; }
}
