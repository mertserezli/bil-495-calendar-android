package com.example.burak.calendarapplication.Model;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.burak.calendarapplication.R;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by Burak on 7.07.2018.
 */

public class AppointmentAdapter extends ArrayAdapter<Appointment> {

    private Context mContext;
    private List<Appointment> appointments;
    public AppointmentAdapter(@NonNull Context context, int resource, @NonNull List<Appointment> objects) {
        super(context, resource, objects);
        mContext=context;
        appointments=objects;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.list_appointment,parent,false);

        Appointment appointment=appointments.get(position);

        TextView id =listItem.findViewById(R.id.appointmentId);
        id.setText("Id:"+appointment.getId());

        TextView date = (TextView)listItem.findViewById(R.id.appointmentDate);
        date.setText("Date:"+appointment.getDate().toString().substring(0,20));

        TextView description = (TextView)listItem.findViewById(R.id.appointmentDescription);
        description.setText("Description:"+appointment.getDescription());

        TextView isRecursive = (TextView)listItem.findViewById(R.id.appointmentIsRecursive);
        isRecursive.setText("IsRecursive:"+appointment.isRecursive()+"");

        TextView recurseDays = listItem.findViewById(R.id.appointmentRecurseDays);
        recurseDays.setText("RecurseDays:"+appointment.getRecurseDays());

        return listItem;
    }
    public Appointment getItem(int position){
        return appointments.get(position);
    }
}
