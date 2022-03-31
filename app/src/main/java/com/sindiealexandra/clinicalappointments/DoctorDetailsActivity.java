package com.sindiealexandra.clinicalappointments;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sindiealexandra.clinicalappointments.models.Doctor;

import java.util.Objects;

public class DoctorDetailsActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private Toolbar mToolbar;
    private ProgressBar mProgressBar;
    private TextView mNameTextView;
    private Button mMakeAppointmentButton;
    private String mDoctorID;
    private Doctor mDoctor;
    private static final String TAG = "Doctor Details Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_details);

        mToolbar = findViewById(R.id.toolbar);

        mProgressBar = findViewById(R.id.progressBar);

        mNameTextView = findViewById(R.id.nameTextView);
        mMakeAppointmentButton = findViewById(R.id.makeAppointmentButton);

        Intent intent = getIntent();
        mDoctorID = intent.getStringExtra("DOCTOR_ID");

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        // Load Doctor from Firestore
        mFirestore.collection("Users").document(mDoctorID).get().addOnSuccessListener(documentSnapshot -> {
            mDoctor = documentSnapshot.toObject(Doctor.class);
            mNameTextView.setText(Objects.requireNonNull(mDoctor).getFirstName());

            // Configure Toolbar
            setSupportActionBar(mToolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(Objects.requireNonNull(mDoctor).getFirstName());
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
            case R.id.mainPageButton:
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