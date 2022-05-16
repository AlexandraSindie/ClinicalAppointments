package com.sindiealexandra.clinicalappointments;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Magnifier;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sindiealexandra.clinicalappointments.models.Appointment;
import com.sindiealexandra.clinicalappointments.models.Doctor;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class DoctorActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private Toolbar mToolbar;
    private ProgressBar mProgressBar;
    private TextView mSpecializationTextView;
    private Button mMakeAppointmentButton;
    private String mDoctorID;
    private Doctor mDoctor;
    private FirebaseUser mFirebaseUser;
    private static final String TAG = "Doctor Activity";

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor);

        mToolbar = findViewById(R.id.toolbar);
        mProgressBar = findViewById(R.id.progressBar);
        mSpecializationTextView = findViewById(R.id.specializationTextView);
        mMakeAppointmentButton = findViewById(R.id.makeAppointmentButton);

        Intent intent = getIntent();
        mDoctorID = intent.getStringExtra("DOCTOR_ID");

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();

        // Get user info from database
        if (mFirebaseUser != null) {
            mFirestore.collection("Users").document(mFirebaseUser.getUid()).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // If visually impaired
                        if (Boolean.TRUE.equals(document.getBoolean("visuallyImpaired"))) {
                            // Enable magnifier
                            Magnifier magnifier = new Magnifier(mSpecializationTextView);
                            mSpecializationTextView.setOnTouchListener((v, event) -> {
                                switch (event.getActionMasked()) {
                                    case MotionEvent.ACTION_DOWN:
                                        // Fall through.
                                    case MotionEvent.ACTION_MOVE: {
                                        final int[] viewPosition = new int[2];
                                        v.getLocationOnScreen(viewPosition);
                                        magnifier.show(event.getRawX() - viewPosition[0],
                                                event.getRawY() - viewPosition[1]);
                                        break;
                                    }
                                    case MotionEvent.ACTION_CANCEL:
                                        // Fall through.
                                    case MotionEvent.ACTION_UP: {
                                        magnifier.dismiss();
                                    }
                                }
                                return true;
                            });
                        }
                    }
                }
            });
        }



        // Load Doctor from Firestore
        mFirestore.collection("Users").document(mDoctorID).get().addOnSuccessListener(documentSnapshot -> {
            mDoctor = documentSnapshot.toObject(Doctor.class);
            mSpecializationTextView.setText(Objects.requireNonNull(mDoctor).getSpecialization());

            // Configure Toolbar
            setSupportActionBar(mToolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(String.format("%s %s", Objects.requireNonNull(mDoctor).getFirstName(), Objects.requireNonNull(mDoctor).getLastName()));
            }
        });

        // Get appointment date and time;
        mMakeAppointmentButton.setOnClickListener(view -> {
            // Show date picker
            MaterialDatePicker<Long> datePicker =
                    MaterialDatePicker.Builder.datePicker()
                            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                            .build();
            datePicker.show(getSupportFragmentManager(), "datePicker");

            // On date set
            datePicker.addOnPositiveButtonClickListener(dateSelection -> {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(dateSelection);

                // Show time picker
                MaterialTimePicker timePicker =
                        new MaterialTimePicker.Builder()
                                .setTimeFormat(TimeFormat.CLOCK_24H)
                                .setHour(12)
                                .setMinute(10)
                                .build();
                timePicker.show(getSupportFragmentManager(), "timePicker");
                // On time set
                timePicker.addOnPositiveButtonClickListener(dialog -> {
                    calendar.set(Calendar.HOUR, timePicker.getHour());
                    calendar.set(Calendar.MINUTE, timePicker.getMinute());
                    // Send data to Firestore
                    addAppointment(calendar.getTime());
                });
            });
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

    // Add user to Firestore
    public void addAppointment(final Date date) {

        // Create new appointment
        Appointment appointment = new Appointment(mDoctor.getSpecialization(), date, mDoctorID, mFirebaseUser.getUid());

        // Add user in the Users collection
        mFirestore.collection("Appointments").document().set(appointment)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully written!"))
                .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));

        mProgressBar.setVisibility(View.INVISIBLE);

        // Start Main Activity
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }
}