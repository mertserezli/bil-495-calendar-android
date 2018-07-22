package com.example.burak.calendarapplication;

import android.content.Context;
import android.os.Bundle;
import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

public class SearchAppointmentActivity extends AppCompatActivity {

    Context mContext;
    JSONArray found;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_appointment);
        mContext = this;
    }

    public void searchAppointment(View view) throws IOException {
        //onclick
        EditText searchInput=findViewById(R.id.searchInput);
        String searchKeyword = searchInput.getText().toString();

        //send post request to find appointments

        final JSONObject json = new JSONObject();
        try {
            json.put("description",searchKeyword);
            //dummy values
            json.put("date","2018-06-07T09:31:00.000Z");
            json.put("recursive",false);
            json.put("recurseDays",null);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Thread t = new Thread() {

            public void run() {
                try {
                    URL url = new URL("https://guarded-bayou-90785.herokuapp.com/appointmentsSearch.json");
                    URLConnection con = url.openConnection();
                    HttpURLConnection http = (HttpURLConnection)con;
                    http.setRequestMethod("POST"); // PUT is another valid option
                    http.setDoOutput(true);
                    byte[] out = json.toString().getBytes("utf-8");
                    int length = out.length;
                    http.setFixedLengthStreamingMode(length);
                    http.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    http.connect();
                    OutputStream os = http.getOutputStream();
                    os.write(out);
                    InputStream is = http.getInputStream();
                    Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
                    found = new JSONArray(s.hasNext() ? s.next() : "");
                }catch (Exception e){ }
            }
        };
        t.start();
        try {
            t.join();
        }catch (Exception e){ }

        String message = "";

        for (int i = 0; i < found.length(); i++) {
            try{
                JSONObject j =found.getJSONObject(i);
                message += "Date: " + j.getString("date") + " Description: " + j.getString("description") + "\n";
            }catch (Exception e){}
        }

        new AlertDialog.Builder(this).setMessage(message).create().show();

    }
}
