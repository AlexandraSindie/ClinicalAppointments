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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specializations);

        mAuth = FirebaseAuth.getInstance();
        mToolbar = findViewById(R.id.toolbar);
        mCardiologyButton = findViewById(R.id.cardiologyButton);
        mSurgeryButton = findViewById(R.id.surgeryButton);

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