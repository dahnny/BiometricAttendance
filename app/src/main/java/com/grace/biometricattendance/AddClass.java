package com.grace.biometricattendance;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import asia.kanopi.fingerscan.Status;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.Blob;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.grace.biometricattendance.models.Class;
import com.grace.biometricattendance.models.ProfileDetails;
import com.grace.biometricattendance.models.Student;
import com.grace.biometricattendance.sourceafis.FingerprintMatcher;
import com.grace.biometricattendance.sourceafis.FingerprintTemplate;

import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddClass extends AppCompatActivity {
    EditText course_title, course_code;
    Button addClass, attendance, generateReport;
    FirebaseAuth auth;
    FirebaseFirestore firestore;
    private static final int REQUEST_CODE = 0;
    byte[] img;
    FingerprintTemplate template;
    ProfileDetails match;
    List<ProfileDetails> detailsList;
    ProfileDetails details;
    List<Student> students = new ArrayList<>();
    String classId;
    ProgressBar simpleProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_class);

        course_code = (EditText) findViewById(R.id.edit_course_code);
        course_title = (EditText) findViewById(R.id.edit_course_title);
        firestore = FirebaseFirestore.getInstance();


        auth = FirebaseAuth.getInstance();

        addClass = (Button) findViewById(R.id.add_class);
        attendance = (Button) findViewById(R.id.attendance);
        generateReport = (Button) findViewById(R.id.generate_report);

        retrieveStudents();
        Intent intent = getIntent();
        classId = intent.getStringExtra("classId");
        String path = intent.getStringExtra("path");

        Toast.makeText(this, classId + "classId", Toast.LENGTH_SHORT).show();

        generateReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddClass.this, ReportActivity.class);
                intent.putExtra("classId", classId);
//                finish();
                startActivity(intent);
            }
        });

        addClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (course_code.getText().toString().isEmpty() || course_title.getText().toString().isEmpty()) {
                    Toast.makeText(AddClass.this, "creating...",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                String id = auth.getCurrentUser().getUid();
                Toast.makeText(AddClass.this, id, Toast.LENGTH_SHORT).show();
                String classId = Timestamp.now().toString();
                Class classes = new Class(id, classId, course_title.getText().toString(), course_code.getText().toString(), students);
                firestore.collection("class").document().set(classes)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d("TAG", "created class successfully");
                                    Toast.makeText(AddClass.this, "created class successfully",
                                            Toast.LENGTH_SHORT).show();
                                    addClass.setEnabled(false);
                                    finish();

                                } else {
                                    Toast.makeText(AddClass.this, "created class failed",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        attendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddClass.this, FingerPrintPage.class);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

    }

    private void retrieveStudents() {
        detailsList = new ArrayList<>();
        firestore.collection("Student").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        Blob fingerprint = ((Blob) documentSnapshot.get("fingerprint"));
                        String studentId = documentSnapshot.get("studentId").toString();

                        byte[] img = fingerprint.toBytes();
                        FingerprintTemplate template = new FingerprintTemplate()
                                .dpi(500)
                                .create(img);
                        Log.i("TAG", studentId);
                        details = new ProfileDetails(studentId, template);
                        detailsList.add(details);
                        Log.i("TAG", detailsList.toString());
                        Toast.makeText(AddClass.this, "Working...", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AddClass.this, "Not Working", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        int status;
        String errorMessage;
        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {
//                    simpleProgressBar = (ProgressBar)findViewById(R.id.simpleProgressBar);
//                    simpleProgressBar.setVisibility(View.VISIBLE);

                    Toast.makeText(this, classId + "classId", Toast.LENGTH_SHORT).show();
                    status = data.getIntExtra("status", Status.ERROR);

                    if (status == Status.SUCCESS) {
                        img = data.getByteArrayExtra("img");

                        template = new FingerprintTemplate()
                                .dpi(500)
                                .create(img);

                        match = find(template, detailsList);
//                      simpleProgressBar.setVisibility(View.GONE);

                        if (match != null) {
                            Intent intent = new Intent(AddClass.this, StudentProfile.class);
                            intent.putExtra("id", match.getId());

                            intent.putExtra("classId", classId);
                            startActivity(intent);

                            Toast.makeText(this, "Match found", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        }
    }

    private ProfileDetails find(FingerprintTemplate probe, List<ProfileDetails> candidates) {
        FingerprintMatcher matcher = new FingerprintMatcher()
                .index(probe);
        ProfileDetails match = null;
        double high = 0;
        for (ProfileDetails candidate : candidates) {
            //go through all the list and then match
            double score = matcher.match(candidate.getImageTemplate());
            if (score > high) {
                high = score;
                match = candidate;
            }
        }
        double threshold = 40;
        if (high >= threshold) {

            return match;
        } else {
            return null;
        }

    }
}