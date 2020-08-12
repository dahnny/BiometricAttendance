package com.grace.biometricattendance;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.grace.biometricattendance.adapters.ReportAdapter;
import com.grace.biometricattendance.models.Class;
import com.grace.biometricattendance.models.Student;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ReportActivity extends AppCompatActivity {
    TextView reportTitle;
    FirebaseFirestore firestore;
    FirestoreRecyclerAdapter<Class, MyViewHolder> firestoreRecyclerAdapter;
    private RecyclerView recyclerView;
    private Query query;
    private List<Student> students;
    String course_code;
    String course_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        firestore = FirebaseFirestore.getInstance();

        reportTitle = (TextView)findViewById(R.id.report_title);

        Intent intent = getIntent();
        String classId = intent.getStringExtra("classId");

        recyclerView = (RecyclerView) findViewById(R.id.reportRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));



        getListOfStudents(classId);
    }

    private void getListOfStudents(String classId) {
        firestore.collection("class").document(classId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Class newClass = documentSnapshot.toObject(Class.class);
                course_code = newClass.getCourse_code();
                course_title = newClass.getCourse_title();

                students = newClass.getListOfStudents();

                ReportAdapter reportAdapter = new ReportAdapter(students);
                recyclerView.setAdapter(reportAdapter);

                reportTitle.setText(course_code + " - "+ course_title);

            }
        });
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {

        View itemView;// init the item view's

        public MyViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            // get the reference of item view's


        }
    }
}