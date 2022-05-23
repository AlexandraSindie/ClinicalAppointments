package com.sindiealexandra.clinicalappointments;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class ReportsActivity extends AppCompatActivity {

    private static final String TAG = "Reports Activity";
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private FirebaseUser mFirebaseUser;
    private Toolbar mToolbar;
    private ProgressBar mProgressBar;
    private TextView mTextView1;
    private TextView mTextView2;
    private TextView mTextView3;
    private TextView mTextView4;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();
        mToolbar = findViewById(R.id.toolbar);
        mProgressBar = findViewById(R.id.progressBar);
        mTextView1 = findViewById(R.id.textView1);
        mTextView2 = findViewById(R.id.textView2);
        mTextView3 = findViewById(R.id.textView3);
        mTextView4 = findViewById(R.id.textView4);

        // Configure Toolbar
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.reports);
        }

        // Get user info from database
        if (mFirebaseUser != null) {
            mFirestore.collection("Users").document(mFirebaseUser.getUid()).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // If doctor
                        if (document.getString("specialization") != null) {
                            // TextView1
                            mFirestore.collection("Appointments")
                                    .get()
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            int count = 0;
                                            for (QueryDocumentSnapshot document1 : task1.getResult()) {
                                                Log.d(TAG, document1.getId() + " => " + document1.getData());
                                                if(Objects.equals(document1.getString("doctorId"), mFirebaseUser.getUid())) {
                                                    count += 1;
                                                }
                                            }
                                            mTextView1.setText(String.format("%s: %d", getString(R.string.no_of_appointments), count));
                                        } else {
                                            Log.d(TAG, "Error getting documents: ", task1.getException());
                                        }
                                    });
                        }
                        // If admin
                        else if (document.getDate("dateOfBirth") != null) {
                            // TextView1
                            mFirestore.collection("Appointments")
                                    .get()
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            int count = 0;
                                            for (QueryDocumentSnapshot document1 : task1.getResult()) {
                                                Log.d(TAG, document1.getId() + " => " + document1.getData());
                                                count += 1;
                                            }
                                            mTextView1.setText(String.format("%s: %d", getString(R.string.no_of_appointments), count));
                                        } else {
                                            Log.d(TAG, "Error getting documents: ", task1.getException());
                                        }
                                    });
                            // TextView2
                            // TextView3
                            // TextView4
                            mFirestore.collection("Users")
                                    .get()
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            int countDoctors = 0;
                                            int countPatients = 0;
                                            for (QueryDocumentSnapshot document1 : task1.getResult()) {
                                                Log.d(TAG, document1.getId() + " => " + document1.getData());
                                                if(document1.getString("specialization") != null) {
                                                    countDoctors += 1;
                                                }
                                                else if(document1.getDate("dateOfBirth") == null ) {
                                                    countPatients += 1;
                                                }
                                            }
                                            mTextView2.setText(String.format("%s: %d", getString(R.string.no_of_users), countDoctors + countPatients));
                                            mTextView3.setText(String.format("%s: %d", getString(R.string.no_of_doctors), countDoctors));
                                            mTextView4.setText(String.format("%s: %d", getString(R.string.no_of_patients), countPatients));
                                        } else {
                                            Log.d(TAG, "Error getting documents: ", task1.getException());
                                        }
                                    });
                        } else { // If patient
                            // TextView1
                            mFirestore.collection("Appointments")
                                    .get()
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            int count = 0;
                                            List<String> appointments = new ArrayList<>();
                                            for (QueryDocumentSnapshot document1 : task1.getResult()) {
                                                Log.d(TAG, document1.getId() + " => " + document1.getData());
                                                if(document1.getString("patientId").equals(mFirebaseUser.getUid())) {
                                                    count += 1;
                                                    appointments.add(document1.getString("specialization"));
                                                }
                                            }
                                            mTextView1.setText(String.format("%s: %d", getString(R.string.no_of_appointments), count));
                                            // TextView2
                                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                                                String mostRepeatedSpecialization
                                                        = appointments.stream()
                                                        .collect(Collectors.groupingBy(w -> w, Collectors.counting()))
                                                        .entrySet()
                                                        .stream()
                                                        .max(Map.Entry.comparingByValue())
                                                        .get()
                                                        .getKey();
                                                mTextView2.setText(String.format("%s: %s", getString(R.string.most_used_specialization), mostRepeatedSpecialization));
                                            }
                                        } else {
                                            Log.d(TAG, "Error getting documents: ", task1.getException());
                                        }
                                    });
                        }

                    }
                }
            });
        }
    }
}