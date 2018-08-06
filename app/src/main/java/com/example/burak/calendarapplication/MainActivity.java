package com.example.burak.calendarapplication;



import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException;
import com.applandeo.materialcalendarview.listeners.OnCalendarPageChangeListener;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.example.burak.calendarapplication.Model.Appointment;
import com.example.burak.calendarapplication.Model.AppointmentEventDay;
import com.example.burak.calendarapplication.Model.EventAppointment;
import com.example.burak.calendarapplication.Request.JsonParser;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.joda.time.Days;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.joda.time.LocalDate;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Appointment> appointments;
    private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext=getApplicationContext();
        getAppointments();

    }
    public void showAppointments(View v){
        Intent intent = new Intent(this,AppointmentsActivity.class);

       // intent.putExtra("Appointments",appointments);
        intent.putExtra("list", appointments);
        startActivity(intent);
    }

    public void getAppointments(){
        final CalendarView calendarView = findViewById(R.id.calendarView);
        try {
            calendarView.setDate(new Date());
        } catch (OutOfDateRangeException e) {
            e.printStackTrace();
        }

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://guarded-bayou-90785.herokuapp.com/appointments.json";
        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    ArrayList<Appointment> receivedAppointments = JsonParser.readJson(response);
                    MainActivity.this.appointments = receivedAppointments;
                    List<EventDay> events = getAllEvents(calendarView);
                    calendarView.setEvents(events);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getApplicationContext(), "Some error occurred!!", Toast.LENGTH_SHORT).show();

            }
        });
        queue.add(request);
    }

    @Override
    public void onStart(){
        super.onStart();
        final CalendarView calendarView = (CalendarView) findViewById(R.id.calendarView);
        PageChangeListener listener = new PageChangeListener(calendarView);
        calendarView.setOnForwardPageChangeListener(listener);
        calendarView.setOnPreviousPageChangeListener(listener);
        calendarView.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {
                AppointmentEventDay appintmentEvent = (AppointmentEventDay) eventDay;
                new AlertDialog.Builder(MainActivity.this).setMessage(appintmentEvent.getDescription() + "\n" + appintmentEvent.getDate()).create().show();
            }
        });
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEvent(EventAppointment eventAppointment) {
        String operation=eventAppointment.getOperation();
        if(operation.equals("delete")){
            getAppointments();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    private List<EventDay> getEvents(){
        List<EventDay> events = new ArrayList<>();
        for(Appointment appointment: appointments) {
            Date date=appointment.getDate();
            Calendar calendar=Calendar.getInstance();
            calendar.set(date.getYear(),date.getMonth(),date.getDate(),date.getHours(),date.getMinutes(),date.getSeconds());
            events.add(new AppointmentEventDay(calendar, R.mipmap.ic_launcher, appointment.getDescription(), appointment.getDate()));
        }
        return events;
    }

    private List<EventDay> getRecurringEvents(Appointment appointment, Calendar currentDate, int days, int recurseDays){
        int excessDays = days % recurseDays;
        int curDay = excessDays;
        Date appointmentDate = appointment.getDate();
        if(appointmentDate.getMonth() == currentDate.get(Calendar.DAY_OF_MONTH)){
            curDay = appointmentDate.getDay();
        }
        List<EventDay> eventList = new ArrayList<>();
        for(int i = curDay; i <= currentDate.getActualMaximum(Calendar.DAY_OF_MONTH); i += recurseDays){
            Calendar recurringEventDate=Calendar.getInstance();
            recurringEventDate.set(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), i, appointmentDate.getHours(), appointmentDate.getMinutes(), appointmentDate.getSeconds());
            eventList.add(new AppointmentEventDay(recurringEventDate, R.mipmap.ic_launcher, appointment.getDescription(), appointmentDate));
        }
        return eventList;
    }

    private List<EventDay> getAllRecurringEvents(CalendarView calendarView){
        Calendar currentPage = calendarView.getCurrentPageDate();
        List<EventDay> events = new ArrayList<>();
        for( Appointment appointment : appointments ){
            if(appointment.isRecursive()){
                Date appointmentDate = appointment.getDate();
                LocalDate localAppointmentDate = new LocalDate(appointmentDate.getYear(), appointmentDate.getMonth() + 1, appointmentDate.getDay());
                LocalDate localCurrentDate = new LocalDate(currentPage.get(Calendar.YEAR), currentPage.get(Calendar.MONTH) + 1, currentPage.getActualMaximum(Calendar.DAY_OF_MONTH));

                int days = Days.daysBetween(localAppointmentDate, localCurrentDate).getDays();
                boolean willRecur = days > 0;
                if (willRecur){
                    events.addAll( getRecurringEvents(appointment, currentPage, days, appointment.getRecurseDays()) );
                }
            }
        }
        return events;
    }

    private List<EventDay> getAllEvents(CalendarView calendarView){
        List<EventDay> allEvents = getEvents();
        allEvents.addAll(getAllRecurringEvents(calendarView));
        return allEvents;
    }

    private class PageChangeListener implements OnCalendarPageChangeListener{

        CalendarView calendarView;

        PageChangeListener(CalendarView calendarView){
            this.calendarView = calendarView;
        }

        @Override
        public void onChange() {
            calendarView.setEvents(getAllEvents(calendarView));
        }
    }
}
