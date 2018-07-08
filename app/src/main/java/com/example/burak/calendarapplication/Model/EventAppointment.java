package com.example.burak.calendarapplication.Model;

/**
 * Created by Burak on 9.07.2018.
 */

public class EventAppointment {

    private String operation;
    private Appointment appointment;

    public EventAppointment(String operation, Appointment appointment) {
        this.operation = operation;
        this.appointment = appointment;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }
}
