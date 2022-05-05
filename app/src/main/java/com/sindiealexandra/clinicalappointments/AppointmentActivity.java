package com.sindiealexandra.clinicalappointments;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sindiealexandra.clinicalappointments.models.Appointment;
import com.sindiealexandra.clinicalappointments.models.User;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

@RequiresApi(api = Build.VERSION_CODES.P)
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
    private Button mEditAppointmentButton;
    private Button mCancelAppointmentButton;
    private static final String TAG = "Appointment Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);

        mToolbar = findViewById(R.id.toolbar);
        mProgressBar = findViewById(R.id.progressBar);
        mUserTextView = findViewById(R.id.userTextView);
        mDateTextView = findViewById(R.id.dateTextView);
        mEditAppointmentButton = findViewById(R.id.editAppointmentButton);
        mCancelAppointmentButton = findViewById(R.id.cancelAppointmentButton);

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

        // When user clicks the Edit Button
        mEditAppointmentButton.setOnClickListener(view -> {
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
                    editAppointment(calendar.getTime());
                });
            });
        });

        // When user clicks the Cancel Button
        mCancelAppointmentButton.setOnClickListener(view -> {
//            Intent intent = new Intent(SpecializationsActivity.this, DoctorsActivity.class);
//            intent.putExtra("SPECIALIZATION","CARDIOLOGY");
//            startActivity(intent);
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
    public void editAppointment(final Date date) {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        // Create new appointment
        assert firebaseUser != null;

        mFirestore.collection("Appointments").document(mAppointmentID)
                .update("date", date)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "DocumentSnapshot successfully updated!");
                    Toast.makeText(this, getString(R.string.appointment_edited), Toast.LENGTH_LONG).show();

                })
                .addOnFailureListener(e -> Log.w(TAG, "Error updating document", e));

        mProgressBar.setVisibility(View.INVISIBLE);

        // Start Main Activity
        Intent intent = new Intent(getApplicationContext(), AppointmentsActivity.class);
        startActivity(intent);
    }
}