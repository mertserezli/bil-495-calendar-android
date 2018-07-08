package com.example.burak.calendarapplication.Request;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.burak.calendarapplication.Model.Appointment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Burak on 7.07.2018.
 */

public class JsonParser  {



    public static ArrayList<Appointment> readJson(String jsonString) throws JSONException {


        JSONArray jsonArray= new JSONArray(jsonString);
        ArrayList<Appointment> array = new ArrayList<>();

        for(int i=0;i<jsonArray.length();i++){
            JSONObject jAppointment = jsonArray.getJSONObject(i);
            String id=jAppointment.getString("id");
            String description=jAppointment.getString("description");
            Date date=parseDate(jAppointment.getString("date"));
            boolean recursive=jAppointment.getBoolean("recursive");
            Integer recurseDays=null;
            if(!jAppointment.get("recurseDays").toString().equals("null")){
                recurseDays=jAppointment.getInt("recurseDays");
            }
            Date createdAt=parseDate(jAppointment.getString("created_at"));
            Date updatedAt=parseDate(jAppointment.getString("updated_at"));
            String url=jAppointment.getString("url");
            Appointment appointment = new Appointment(id,description,date,recursive,recurseDays,createdAt,updatedAt,url);
            array.add(appointment);
        }
        return array;
    }

    private static Date parseDate(String date){
        int year=Integer.parseInt(date.substring(0,4));
        int month=Integer.parseInt(date.substring(5,7));
        int day=Integer.parseInt(date.substring(8,10));
        int hour=Integer.parseInt(date.substring(11,13));
        int minute=Integer.parseInt(date.substring(14,16));
        int second=Integer.parseInt(date.substring(17,19));

        return new Date(year,month,day,hour,minute,second);
    }

}
