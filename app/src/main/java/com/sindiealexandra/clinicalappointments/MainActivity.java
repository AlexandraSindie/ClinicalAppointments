package com.sindiealexandra.clinicalappointments;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Main Activity";
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private FirebaseUser mFirebaseUser;
    private Toolbar mToolbar;
    private Button mSpecializationsButton;
    private Button mAppointmentsButton;
    private Button mUsersButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();
        mToolbar = findViewById(R.id.toolbar);
        mSpecializationsButton = findViewById(R.id.specializationsButton);
        mAppointmentsButton = findViewById(R.id.appointmentsButton);
        mUsersButton = findViewById(R.id.usersButton);

        // Configure Toolbar
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.app_name));
        }

        // Get user info from database
        if (mFirebaseUser != null) {
            mFirestore.collection("Users").document(mFirebaseUser.getUid()).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // If doctor
                        if (document.getString("specialization") != null) {
                            mSpecializationsButton.setVisibility(View.GONE);
                        }
                        if (document.getDate("dateOfBirth") == null) {
                            mUsersButton.setVisibility(View.GONE);
                        }
                    }
                }
            });
        }

        // When user clicks the Specializations Button
        mAppointmentsButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AppointmentsActivity.class);
            startActivity(intent);
        });

        // When user clicks the Appointments Button
        mSpecializationsButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, SpecializationsActivity.class);
            startActivity(intent);
        });

        // When user clicks the Users Button
        mUsersButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, UsersActivity.class);
            startActivity(intent);
        });
    }

    // Inflate toolbar menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    // When the user clicks a button in the toolbar menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
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

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in
        if (mFirebaseUser == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

}