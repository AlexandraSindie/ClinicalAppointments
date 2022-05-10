package com.sindiealexandra.clinicalappointments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.sindiealexandra.clinicalappointments.adapters.AppointmentRecyclerAdapter;
import com.sindiealexandra.clinicalappointments.models.Appointment;
import com.sindiealexandra.clinicalappointments.models.User;

import java.util.ArrayList;
import java.util.List;

public class AppointmentsActivity extends AppCompatActivity {

    private static final String TAG = "Appointments Activity";
    FirebaseUser mFirebaseUser;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private Toolbar mToolbar;
    private ProgressBar mProgressBar;
    private RecyclerView mRecyclerView;
    private AppointmentRecyclerAdapter mAppointmentRecyclerAdapter;
    private List<Appointment> mAppointments;
    private List<User> mUsers;
    private List<String> mAppointmentIDs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointments);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mToolbar = findViewById(R.id.toolbar);
        mProgressBar = findViewById(R.id.progressBar);
        mRecyclerView = findViewById(R.id.recyclerView);
        mAppointments = new ArrayList<>();
        mUsers = new ArrayList<>();
        mAppointmentIDs = new ArrayList<>();

        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mAppointmentRecyclerAdapter = new AppointmentRecyclerAdapter(mAppointments);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mAppointmentRecyclerAdapter);

        mProgressBar.setVisibility(View.VISIBLE);

        // Configure Toolbar
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.appointments));
        }

        mAppointments = new ArrayList<>();
        mAppointmentIDs = new ArrayList<>();
        mFirebaseUser = mAuth.getCurrentUser();

        mFirestore.collection("Users").document(mFirebaseUser.getUid()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // If doctor
                    if (document.getString("specialization") != null) {
                        mFirestore.collection("Appointments").whereEqualTo("doctorId", mFirebaseUser.getUid()).get().addOnCompleteListener(patientTask -> {
                            if (patientTask.isSuccessful()) {
                                // Load appointments
                                for (QueryDocumentSnapshot appointmentDocument : patientTask.getResult()) {
                                    Appointment appointment = appointmentDocument.toObject(Appointment.class);
                                    mAppointments.add(appointment);
                                    mAppointmentIDs.add(appointmentDocument.getId());
                                }
                                mAppointmentRecyclerAdapter.updateAppointments(mAppointments, mAppointmentIDs, "DOCTOR");
                                mProgressBar.setVisibility(View.INVISIBLE);
                            } else {
                                Log.d(TAG, "Error getting documents: ", patientTask.getException());
                            }
                        });
                        // If patient
                    } else {
                        mFirestore.collection("Appointments").whereEqualTo("patientId", mFirebaseUser.getUid()).get().addOnCompleteListener(doctorTask -> {
                            if (doctorTask.isSuccessful()) {
                                // Load appointments
                                for (QueryDocumentSnapshot appointmentDocument : doctorTask.getResult()) {
                                    Appointment appointment = appointmentDocument.toObject(Appointment.class);
                                    mAppointments.add(appointment);
                                    mAppointmentIDs.add(appointmentDocument.getId());
                                }
                                mAppointmentRecyclerAdapter.updateAppointments(mAppointments, mAppointmentIDs, "PATIENT");
                                mProgressBar.setVisibility(View.INVISIBLE);
                            } else {
                                Log.d(TAG, "Error getting documents: ", doctorTask.getException());
                            }
                        });
                    }
                } else {
                    Log.d(TAG, "No such document");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });
    }

    // Inflate toolbar menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.general_menu, menu);
        return true;
    }

    // When the user clicks a button in the toolbar menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            // Go to main screen
            case R.id.mainButton:
                intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                return true;
            // Start the Account Activity
            case R.id.accountButton:
                intent = new Intent(this, AccountActivity.class);
                startActivity(intent);
                return true;
            // Log out user
            case R.id.logoutButton:
                mAuth.signOut();
                intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
                return true;
            default:
                return false;
        }
    }
}
