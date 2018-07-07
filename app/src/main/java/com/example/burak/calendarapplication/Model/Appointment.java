package com.example.burak.calendarapplication.Model;

import java.util.Date;

/**
 * Created by Burak on 23.06.2018.
 */

public class Appointment {

    private String id;
    private String description;
    private Date date;
    private boolean recursive;
    private Integer recurseDays;
    private Date createdAt;
    private Date updatedAt;
    private String url;

    public Appointment() {
    }

    public Appointment(String id, String description, Date date, boolean recursive, Integer recurseDays, Date createdAt, Date updatedAt, String url) {
        this.id = id;
        this.description = description;
        this.date = date;
        this.recursive = recursive;
        this.recurseDays = recurseDays;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isRecursive() {
        return recursive;
    }

    public void setRecursive(boolean recursive) {
        this.recursive = recursive;
    }

    public Integer getRecurseDays() {
        return recurseDays;
    }

    public void setRecurseDays(Integer recurseDays) {
        this.recurseDays = recurseDays;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
