package com.dylan.mediadiary;

import java.util.Date;
import java.util.UUID;

public class Diary {
    private UUID id;
    private String title;
    private Date date;
    private String content;

    public Diary() {
        this(UUID.randomUUID());
    }

    public Diary(UUID id) {
        this.id = id;
        this.date = new Date();
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPhotoFilename() {
        return "IMG_" + getId().toString() + ".jpg";
    }
}
