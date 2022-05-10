package com.sindiealexandra.clinicalappointments;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AccountTypeActivity extends AppCompatActivity {

    private Button mPatientButton;
    private Button mDoctorButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_type);

        mPatientButton = findViewById(R.id.patientButton);
        mDoctorButton = findViewById(R.id.doctorButton);

        // When user clicks the Patient button
        mPatientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                intent.putExtra("ACCOUNT_TYPE", "PATIENT");
                startActivity(intent);
            }
        });

        // When user clicks the Doctor button
        mDoctorButton.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
            intent.putExtra("ACCOUNT_TYPE", "DOCTOR");
            startActivity(intent);
        });
    }
}