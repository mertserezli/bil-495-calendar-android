package com.example.burak.calendarapplication;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.burak.calendarapplication.Model.Appointment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CreateAppointmentActivity extends AppCompatActivity {

    Context mContext;
    String operation="create";
    Appointment appointment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_appointment);
        mContext=getApplicationContext();
        operation=getIntent().getExtras().getString("operation");
        if(operation.equals("update")){
            appointment=(Appointment)getIntent().getExtras().get("Appointment");

            EditText textDescription=findViewById(R.id.createDescription);
            textDescription.setText(appointment.getDescription());

            Button button=findViewById(R.id.createAppointmentButton);
            button.setText("Update Appointment");

            TextView createDate=findViewById(R.id.createDate);
            createDate.setText(appointment.getDate().toString());

        }


    }

    public void showDatePickerDialog(View view) {
        final TextView createDate=findViewById(R.id.createDate);
        Calendar mcurrentTime = Calendar.getInstance();
        int year = mcurrentTime.get(Calendar.YEAR);//Güncel Yılı alıyoruz
        int month = mcurrentTime.get(Calendar.MONTH);//Güncel Ayı alıyoruz
        int day = mcurrentTime.get(Calendar.DAY_OF_MONTH);//Güncel Günü alıyoruz

        DatePickerDialog datePicker;//Datepicker objemiz
        datePicker = new DatePickerDialog(CreateAppointmentActivity.this, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                monthOfYear++;
                String day=dayOfMonth+"";
                String month=monthOfYear+"";
                if(month.length()==1)
                    month="0"+month;
                if(day.length()==1)
                    day="0"+day;

                createDate.setText(year+"-"+month+"-"+day);//Ayarla butonu tıklandığında textview'a yazdırıyoruz

            }
        },year,month,day);//başlarken set edilcek değerlerimizi atıyoruz
        datePicker.setTitle("Tarih Seçiniz");
        datePicker.setButton(DatePickerDialog.BUTTON_POSITIVE, "Ayarla", datePicker);
        datePicker.setButton(DatePickerDialog.BUTTON_NEGATIVE, "İptal", datePicker);

        datePicker.show();

    }

    public void showTimePickerDialog(View view) {
        Calendar mcurrentTime = Calendar.getInstance();//
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);//Güncel saati aldık
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog timePicker; //Time Picker referansımızı oluşturduk

        final TextView createTime=findViewById(R.id.createTime);
        //TimePicker objemizi oluşturuyor ve click listener ekliyoruz
        timePicker = new TimePickerDialog(CreateAppointmentActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                String hour=selectedHour+"";
                String minute=selectedMinute+"";
                if(hour.length()==1)
                    hour="0"+hour;
                if(minute.length()==1)
                    minute="0"+minute;

                createTime.setText( hour+ ":" + minute);//Ayarla butonu tıklandığında textview'a yazdırıyoruz
            }
        }, hour, minute, true);//true 24 saatli sistem için
        timePicker.setTitle("Saat Seçiniz");
        timePicker.setButton(DatePickerDialog.BUTTON_POSITIVE, "Ayarla", timePicker);
        timePicker.setButton(DatePickerDialog.BUTTON_NEGATIVE, "İptal", timePicker);

        timePicker.show();
    }


    public void createAppointment(View view) throws IOException {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://guarded-bayou-90785.herokuapp.com/appointments";
        int requestType=Request.Method.POST;
        if(operation.equals("update")){
            url+="/"+appointment.getId();
            requestType=Request.Method.PUT;
        }

        EditText textDescription=findViewById(R.id.createDescription);
        TextView textDate=findViewById(R.id.createDate);
        TextView textTime=findViewById(R.id.createTime);
        CheckBox checkIsRecursive=findViewById(R.id.checkIsRecursive);
        EditText checkRecurseDays=findViewById(R.id.checkRecurseDays);

        String description= textDescription.getText().toString();
        String date=textDate.getText()+"T"+textTime.getText()+".000Z";
        boolean isRecursive=checkIsRecursive.isChecked();
        String recurseDaysString=checkRecurseDays.getText().toString();
        if(recurseDaysString.length()==0)
            recurseDaysString="0";
        int recurseDays=Integer.parseInt(recurseDaysString);
        JSONObject json = new JSONObject();
        try {
            json.put("description",description);
            json.put("date",date);
            json.put("recursive",isRecursive);
            json.put("recurseDays",recurseDays);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(requestType, url, json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(mContext,"Appointment basariyla olusturuldu:"+response.toString(),Toast.LENGTH_SHORT);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(mContext,"HATA:Appointment olusturulamadi:"+error.toString(),Toast.LENGTH_SHORT);
            }
        });
        queue.add(jsonObjectRequest);

    }






}




