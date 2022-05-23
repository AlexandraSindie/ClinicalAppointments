package com.sindiealexandra.clinicalappointments;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class SpecializationsActivity extends AppCompatActivity {

    private static final String TAG = "Specializations Activity";
    private FirebaseAuth mAuth;
    private Toolbar mToolbar;
    private Button mCardiologyButton;
    private Button mSurgeryButton;
    private Button mOphthalmologyButton;
    private Button mdDermatologyButton;
    private Button mNeurologyButton;
    private Button mPediatricsButton;
    private Button mPlasticSurgeryButton;
    private Button mPsychiatryButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specializations);

        mAuth = FirebaseAuth.getInstance();
        mToolbar = findViewById(R.id.toolbar);
        mCardiologyButton = findViewById(R.id.cardiologyButton);
        mSurgeryButton = findViewById(R.id.surgeryButton);
        mOphthalmologyButton = findViewById(R.id.ophthalmologyButton);
        mdDermatologyButton = findViewById(R.id.dermatologyButton);
        mNeurologyButton = findViewById(R.id.neurologyButton);
        mPediatricsButton = findViewById(R.id.pediatricsButton);
        mPlasticSurgeryButton = findViewById(R.id.plasticSurgeryButton);
        mPsychiatryButton = findViewById(R.id.psychiatryButton);

        // Configure Toolbar
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.specializations));
        }

        // When user clicks the Surgery Button
        mCardiologyButton.setOnClickListener(view -> {
            Intent intent = new Intent(SpecializationsActivity.this, DoctorsActivity.class);
            intent.putExtra("SPECIALIZATION","CARDIOLOGY");
            startActivity(intent);
        });

        // When user clicks the Surgery Button
        mSurgeryButton.setOnClickListener(view -> {
            Intent intent = new Intent(SpecializationsActivity.this, DoctorsActivity.class);
            intent.putExtra("SPECIALIZATION","GENERAL SURGERY");
            startActivity(intent);
        });

        // When user clicks the Ophthalmology Button
        mOphthalmologyButton.setOnClickListener(view -> {
            Intent intent = new Intent(SpecializationsActivity.this, DoctorsActivity.class);
            intent.putExtra("SPECIALIZATION","OPHTHALMOLOGY");
            startActivity(intent);
        });

        // When user clicks the Dermatology Button
        mdDermatologyButton.setOnClickListener(view -> {
            Intent intent = new Intent(SpecializationsActivity.this, DoctorsActivity.class);
            intent.putExtra("SPECIALIZATION","DERMATOLOGY");
            startActivity(intent);
        });

        // When user clicks the Neurology Button
        mNeurologyButton.setOnClickListener(view -> {
            Intent intent = new Intent(SpecializationsActivity.this, DoctorsActivity.class);
            intent.putExtra("SPECIALIZATION","NEUROLOGY");
            startActivity(intent);
        });

        // When user clicks the Pediatrics Button
        mPediatricsButton.setOnClickListener(view -> {
            Intent intent = new Intent(SpecializationsActivity.this, DoctorsActivity.class);
            intent.putExtra("SPECIALIZATION","PEDIATRICS");
            startActivity(intent);
        });

        // When user clicks the Plastic Surgery Button
        mPlasticSurgeryButton.setOnClickListener(view -> {
            Intent intent = new Intent(SpecializationsActivity.this, DoctorsActivity.class);
            intent.putExtra("SPECIALIZATION","PLASTIC SURGERY");
            startActivity(intent);
        });

        // When user clicks the Psychiatry Button
        mPsychiatryButton.setOnClickListener(view -> {
            Intent intent = new Intent(SpecializationsActivity.this, DoctorsActivity.class);
            intent.putExtra("SPECIALIZATION","PSYCHIATRY");
            startActivity(intent);
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