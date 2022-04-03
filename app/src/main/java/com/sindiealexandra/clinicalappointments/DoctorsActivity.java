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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.sindiealexandra.clinicalappointments.adapters.DoctorRecyclerAdapter;
import com.sindiealexandra.clinicalappointments.models.Doctor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DoctorsActivity extends AppCompatActivity {

    private static final String TAG = "Doctors Activity";
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private Toolbar mToolbar;
    private ProgressBar mProgressBar;
    private RecyclerView mRecyclerView;
    private DoctorRecyclerAdapter mDoctorRecyclerAdapter;
    private List<Doctor> mDoctors;
    private List<String> mDoctorIDs;
    private String mSpecialization;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctors);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mToolbar = findViewById(R.id.toolbar);
        mProgressBar = findViewById(R.id.progressBar);
        mRecyclerView = findViewById(R.id.recyclerView);
        mDoctors = new ArrayList<>();
        mDoctorIDs = new ArrayList<>();

        Intent intent = getIntent();
        mSpecialization = intent.getStringExtra("SPECIALIZATION");

        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mDoctorRecyclerAdapter = new DoctorRecyclerAdapter(mDoctors);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mDoctorRecyclerAdapter);

        mProgressBar.setVisibility(View.VISIBLE);

        // Configure Toolbar
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.available_doctors));
        }

        mDoctors = new ArrayList<>();
        mDoctorIDs = new ArrayList<>();

        mFirestore.collection("Users").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Load users
                for (QueryDocumentSnapshot document : task.getResult()) {
                    if (document.getBoolean("admin") == null && document.getString("specialization") != null) {
                        if (Objects.equals(Objects.requireNonNull(document.getString("specialization")).toUpperCase(), mSpecialization)) {
                            mDoctors.add(document.toObject(Doctor.class));
                            mDoctorIDs.add(document.getId());
                        }
                    }
                }
                mDoctorRecyclerAdapter.updateDoctors(mDoctors, mDoctorIDs);
                mProgressBar.setVisibility(View.INVISIBLE);
            } else {
                Log.d(TAG, "Error getting documents: ", task.getException());
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