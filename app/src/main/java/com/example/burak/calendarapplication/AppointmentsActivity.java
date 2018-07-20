package com.example.burak.calendarapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.burak.calendarapplication.Model.Appointment;
import com.example.burak.calendarapplication.Model.AppointmentAdapter;
import com.example.burak.calendarapplication.Model.EventAppointment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AppointmentsActivity extends AppCompatActivity {

    Context mContext;

    String baseUrl="https://guarded-bayou-90785.herokuapp.com/appointments";
    ArrayList<Appointment> appointments=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointments);
        mContext=getApplicationContext();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext,CreateAppointmentActivity.class);
                intent.putExtra("operation","create");
                startActivity(intent);
            }
        });


        Bundle extras = getIntent().getExtras();
        if(extras!=null){
            appointments=(ArrayList<Appointment>)extras.getSerializable("list");
        }

        final AppointmentAdapter appointmentAdapter = new AppointmentAdapter(this,0,appointments);
        final ListView listAppointments = (ListView)findViewById(R.id.listAppointments);
        listAppointments.setAdapter(appointmentAdapter);

        listAppointments.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> adapterView, View view, int i, long l) {
                final Appointment appointment=appointmentAdapter.getItem(i);
                AlertDialog.Builder builder = new AlertDialog.Builder(AppointmentsActivity.this);
                builder.setTitle("Delete Dialog");
                builder.setMessage("Options:");
                final int position=i;
                builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        RequestQueue queue = Volley.newRequestQueue(mContext);
                        JSONObject json = new JSONObject();
                        String url=baseUrl+"/"+appointment.getId();
                        try {
                            json.put("id",appointment.getId());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE,url, json,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {

                                    }
                        });
                        queue.add(jsonObjectRequest);
                        EventBus.getDefault().postSticky(new EventAppointment("delete",appointment));
                        appointments.remove(position);
                        appointmentAdapter.notifyDataSetChanged();
                    }
                });
                builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(mContext,CreateAppointmentActivity.class);
                        intent.putExtra("operation","update");
                        intent.putExtra("Appointment",appointment);
                        startActivity(intent);
                    }
                });
                builder.show();
            }
        });

    }

    public void createListView(){

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
