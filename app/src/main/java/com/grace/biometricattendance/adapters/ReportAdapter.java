package com.grace.biometricattendance.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.grace.biometricattendance.R;
import com.grace.biometricattendance.models.Class;
import com.grace.biometricattendance.models.Student;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.MyViewHolder> {
    List<Student> students;
    Calendar calendar = Calendar.getInstance();

    public ReportAdapter(List<Student> students) {
        this.students = students;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.report_adapter, parent, false);
        return new MyViewHolder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Student student = students.get(position);
        calendar.setTime(student.getDate());

        holder.firstName.setText(student.getFirstName());
        holder.lastName.setText(student.getLastName());
        holder.matriculationNumber.setText(student.getMatriculationNumber());
        holder.date.setText(calendar.getTime().toString());

    }


    @Override
    public int getItemCount() {
        return students.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView matriculationNumber, firstName, lastName, date;
        View itemView;// init the item view's


        public MyViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            // get the reference of item view's

            matriculationNumber = (TextView) itemView.findViewById(R.id.matriculation_number_report);
            firstName = (TextView) itemView.findViewById(R.id.first_name_report);
            lastName = (TextView) itemView.findViewById(R.id.last_name_report);
            date = (TextView) itemView.findViewById(R.id.date);

        }
    }
}
