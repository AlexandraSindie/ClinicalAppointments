package com.sindiealexandra.clinicalappointments;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sindiealexandra.clinicalappointments.models.Appointment;
import com.sindiealexandra.clinicalappointments.models.User;

import java.util.Objects;

public class AppointmentActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private Toolbar mToolbar;
    private ProgressBar mProgressBar;
    private TextView mUserTextView;
    private TextView mDateTextView;
    private String mAppointmentID;
    private String mUserType;
    private Appointment mAppointment;
    private static final String TAG = "Appointment Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);

        mToolbar = findViewById(R.id.toolbar);
        mProgressBar = findViewById(R.id.progressBar);
        mUserTextView = findViewById(R.id.userTextView);
        mDateTextView = findViewById(R.id.dateTextView);

        Intent intent = getIntent();
        mAppointmentID = intent.getStringExtra("APPOINTMENT_ID");
        mUserType = intent.getStringExtra("USER_TYPE");

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        // Load Doctor from Firestore
        mFirestore.collection("Appointments").document(mAppointmentID).get().addOnSuccessListener(appointmentDocumentSnapshot -> {
            mAppointment = appointmentDocumentSnapshot.toObject(Appointment.class);
            assert mAppointment != null;
            String date = DateFormat.format("MM/dd/yyyy HH:mm", mAppointment.getDate()).toString();
            mDateTextView.setText(date);

            if(mUserType.equals("DOCTOR")) {
                mFirestore.collection("Users").document(mAppointment.getPatientId()).get().addOnSuccessListener(documentSnapshot -> {
                    User user = documentSnapshot.toObject(User.class);
                    mUserTextView.setText(String.format("%s %s", Objects.requireNonNull(user).getFirstName(), Objects.requireNonNull(user).getLastName()));
                });
            } else if(mUserType.equals("PATIENT")){
                mFirestore.collection("Users").document(mAppointment.getDoctorId()).get().addOnSuccessListener(documentSnapshot -> {
                    User user = documentSnapshot.toObject(User.class);
                    mUserTextView.setText(String.format("%s %s", Objects.requireNonNull(user).getFirstName(), Objects.requireNonNull(user).getLastName()));
                });
            }

            // Configure Toolbar
            setSupportActionBar(mToolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(Objects.requireNonNull(mAppointment).getSpecialization());
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
            // Go to MainActivity
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
                // Start the Login Activity
                intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
                return true;
            default:
                return false;
        }
    }
}