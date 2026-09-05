package com.example.mycalendar2026sar;

import java.io.Serializable;

/**
 * Represents a Notification Event in the calendar.
 */
public class NotificationEvent implements Serializable {

    private long id;
    private String title;
    private String notes;
    private String date; // dd/MM/yyyy
    private String startTime;
    private String endTime;
    private String priority; // High, Medium, Low
    private String status;   // Pending, Completed, Snoozed, Overdue
    private String repeat;   // None, Daily, Weekly, Monthly, Yearly, Custom
    private String reminder; // At event time, 5m, 10m, 15m, 30m, 1h, 1d, Custom
    private String location;
    private String attachments; // JSON array of file paths
    private String voiceNotePath;
    private String history; // JSON array of action logs

    public NotificationEvent(long id, String title, String notes, String date, String startTime, String endTime,
                             String priority, String status, String repeat, String reminder, String location,
                             String attachments, String voiceNotePath, String history) {
        this.id = id;
        this.title = title;
        this.notes = notes;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.priority = priority;
        this.status = status;
        this.repeat = repeat;
        this.reminder = reminder;
        this.location = location;
        this.attachments = attachments;
        this.voiceNotePath = voiceNotePath;
        this.history = history;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getRepeat() { return repeat; }
    public void setRepeat(String repeat) { this.repeat = repeat; }

    public String getReminder() { return reminder; }
    public void setReminder(String reminder) { this.reminder = reminder; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getAttachments() { return attachments; }
    public void setAttachments(String attachments) { this.attachments = attachments; }

    public String getVoiceNotePath() { return voiceNotePath; }
    public void setVoiceNotePath(String voiceNotePath) { this.voiceNotePath = voiceNotePath; }

    public String getHistory() { return history; }
    public void setHistory(String history) { this.history = history; }
}
