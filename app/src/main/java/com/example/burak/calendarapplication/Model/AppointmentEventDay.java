package com.example.burak.calendarapplication.Model;

import com.applandeo.materialcalendarview.EventDay;

import java.util.Calendar;
import java.util.Date;

public class AppointmentEventDay extends EventDay{

    private String description = "";
    private Date date = null;

    public AppointmentEventDay(Calendar day, int imageResource, String description, Date date){
        super(day, imageResource);
        this.description = description;
        this.date = date;
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
}
