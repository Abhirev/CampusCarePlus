package com.abhi.ltcecampuscare.ui.report;


import java.util.Date;

public class ReportModel {
    private String subject;
    private String body;
    private Date timestamp;
    private String postedBy;
    private String role;


    public ReportModel() {}

    public ReportModel(String subject, String body, Date timestamp, String postedBy, String role) {
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
