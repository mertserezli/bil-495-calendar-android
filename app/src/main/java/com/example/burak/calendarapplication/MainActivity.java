package com.example.burak.calendarapplication;



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
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.example.burak.calendarapplication.Model.Appointment;
import com.example.burak.calendarapplication.Request.JsonParser;

import org.json.JSONException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Appointment> appointments;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


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
            public void onResponse(String string) {
                try {
                    ArrayList<Appointment> array= JsonParser.readJson(string);
                    List<EventDay> events = new ArrayList<>();
                    for(Appointment appointment:array) {
                        Date date=appointment.getDate();
                        Calendar calendar=Calendar.getInstance();
                        calendar.set(date.getYear(),date.getMonth(),date.getDate(),date.getHours(),date.getMinutes(),date.getSeconds());
                        events.add(new EventDay(calendar, R.mipmap.ic_launcher));
                        calendarView.setEvents(events);
                    }
                    //Button showAppointmentsButton =(Button)findViewById(R.id.showAppointments);
                    appointments=array;
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
        calendarView.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {

            }
        });

    }
    public void showAppointments(View v){
        Intent intent = new Intent(this,AppointmentsActivity.class);

       // intent.putExtra("Appointments",appointments);
        intent.putExtra("list", appointments);
        startActivity(intent);
    }
}
