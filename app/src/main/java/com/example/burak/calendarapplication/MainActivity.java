package com.example.burak.calendarapplication;



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
import com.example.burak.calendarapplication.Model.Appointment;
import com.example.burak.calendarapplication.Model.EventAppointment;
import com.example.burak.calendarapplication.Request.JsonParser;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
    }
    @Override
    public void onStart(){
        super.onStart();
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
}
