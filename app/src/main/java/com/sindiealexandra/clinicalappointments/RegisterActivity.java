package com.sindiealexandra.clinicalappointments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sindiealexandra.clinicalappointments.models.Doctor;
import com.sindiealexandra.clinicalappointments.models.Patient;
import com.sindiealexandra.clinicalappointments.models.User;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "Register Activity";
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private SharedPreferences mPreferences;
    private ProgressBar mProgressBar;
    private TextInputLayout mFirstNameTextInputLayout;
    private EditText mFirstNameEditText;
    private TextInputLayout mLastNameTextInputLayout;
    private EditText mLastNameEditText;
    private TextInputLayout mPhoneTextInputLayout;
    private EditText mPhoneEditText;
    private TextInputLayout mEmailTextInputLayout;
    private EditText mEmailEditText;
    private TextInputLayout mSpecializationTextInputLayout;
    private EditText mSpecializationEditText;
    private TextInputLayout mPasswordTextInputLayout;
    private EditText mPasswordEditText;
    private TextInputLayout mConfirmPasswordTextInputLayout;
    private EditText mConfirmPasswordEditText;
    private Button mRegisterButton;
    private Button mLoginRedirectButton;
    private CheckBox mVisuallyImpairedCheckBox;
    private boolean mIsDoctor = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Check if user selected doctor account type
        Intent intent = getIntent();
        if(Objects.equals(intent.getStringExtra("ACCOUNT_TYPE"), "DOCTOR")) {
            mIsDoctor = true;
        }

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        mProgressBar = findViewById(R.id.progressBar);
        mFirstNameTextInputLayout = findViewById(R.id.firstNameTextInputLayout);
        mFirstNameEditText = findViewById(R.id.firstNameEditText);
        mLastNameTextInputLayout = findViewById(R.id.lastNameTextInputLayout);
        mLastNameEditText = findViewById(R.id.lastNameEditText);
        mPhoneTextInputLayout = findViewById(R.id.phoneTextInputLayout);
        mPhoneEditText = findViewById(R.id.phoneEditText);
        mEmailTextInputLayout = findViewById(R.id.emailTextInputLayout);
        mEmailEditText = findViewById(R.id.emailEditText);
        mSpecializationTextInputLayout = findViewById(R.id.specializationTextInputLayout);
        mSpecializationEditText = findViewById(R.id.specializationEditText);
        mPasswordTextInputLayout = findViewById(R.id.passwordTextInputLayout);
        mPasswordEditText = findViewById(R.id.passwordEditText);
        mConfirmPasswordTextInputLayout = findViewById(R.id.confirmPasswordTextInputLayout);
        mConfirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        mRegisterButton = findViewById(R.id.registerButton);
        mLoginRedirectButton = findViewById(R.id.loginRedirectButton);
        mVisuallyImpairedCheckBox = findViewById(R.id.visuallyImpairedCheckBox);

        // If doctor show specialization field
        if(mIsDoctor) {
            mSpecializationTextInputLayout.setVisibility(View.VISIBLE);
            mSpecializationEditText.setVisibility(View.VISIBLE);
        }

        // When the user clicks the Register button
        mRegisterButton.setOnClickListener(view -> {
            // Get user input from form
            final String firstName = mFirstNameEditText.getText().toString().trim();
            final String lastName = mLastNameEditText.getText().toString().trim();
            final String phone = mPhoneEditText.getText().toString().trim();
            final String email = mEmailEditText.getText().toString().trim();
            final String specialization = mSpecializationEditText.getText().toString().trim();
            final String password = mPasswordEditText.getText().toString().trim();
            final String confirmPassword = mConfirmPasswordEditText.getText().toString().trim();
            final boolean isVisuallyImpaired = mVisuallyImpairedCheckBox.isChecked();

            // Clear errors
            mFirstNameTextInputLayout.setError(null);
            mLastNameTextInputLayout.setError(null);
            mPhoneTextInputLayout.setError(null);
            mEmailTextInputLayout.setError(null);
            mSpecializationTextInputLayout.setError(null);
            mPasswordTextInputLayout.setError(null);
            mConfirmPasswordTextInputLayout.setError(null);

            // Check user input
            if (TextUtils.isEmpty(firstName)) {
                mFirstNameTextInputLayout.setError(getString(R.string.first_name_required_error));
                mFirstNameEditText.requestFocus();
            } else if (TextUtils.isEmpty(lastName)) {
                mLastNameTextInputLayout.setError(getString(R.string.last_name_required_error));
                mLastNameEditText.requestFocus();
            } else if (TextUtils.isEmpty(phone)) {
                mPhoneTextInputLayout.setError(getString(R.string.phone_required_error));
                mPhoneEditText.requestFocus();
            } else if (TextUtils.isEmpty(email)) {
                mEmailTextInputLayout.setError(getString(R.string.email_required_error));
                mEmailEditText.requestFocus();
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                mEmailTextInputLayout.setError(getString(R.string.email_format_error));
                mEmailEditText.requestFocus();
            } else if (mIsDoctor && TextUtils.isEmpty(specialization)) {
                mSpecializationTextInputLayout.setError(getString(R.string.specialization_required_error));
                mSpecializationEditText.requestFocus();
            } else if (TextUtils.isEmpty(password)) {
                mPasswordTextInputLayout.setError(getString(R.string.password_required_error));
                mPasswordEditText.requestFocus();
            } else if (TextUtils.isEmpty(confirmPassword)) {
                mConfirmPasswordTextInputLayout.setError(getString(R.string.password_confirmation_error));
                mConfirmPasswordEditText.requestFocus();
            } else if (!password.equals(confirmPassword)) {
                mConfirmPasswordTextInputLayout.setError(getString(R.string.passwords_do_not_match_error));
                mConfirmPasswordEditText.requestFocus();
            } else {
                mProgressBar.setVisibility(View.VISIBLE);

                // Create user
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(RegisterActivity.this, task -> {
                            // Sign up success
                            if (task.isSuccessful()) {
                                // Add user to database
                                addUser(firstName, lastName, phone, specialization, isVisuallyImpaired);
                                setLoginCredentials(email, password);
                            } else {
                                try {
                                    throw Objects.requireNonNull(task.getException());
                                }
                                // If password format is not correct.
                                catch (FirebaseAuthWeakPasswordException weakPassword) {
                                    mPasswordTextInputLayout.setError(getString(R.string.password_not_strong_enough_error));
                                    mPasswordEditText.requestFocus();
                                    Log.d(TAG, "onComplete: weak_password");
                                }
                                // If email is already registered
                                catch (FirebaseAuthUserCollisionException existEmail) {
                                    mEmailTextInputLayout.setError(getString(R.string.email_already_in_use_error));
                                    mEmailEditText.requestFocus();
                                    Log.d(TAG, "onComplete: exist_email");
                                } catch (Exception e) {
                                    // Otherwise display a message to the user.
                                    Toast.makeText(getApplicationContext(), getString(R.string.failed_registration),
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                            mProgressBar.setVisibility(View.INVISIBLE);
                        });
            }
        });

        // When the user clicks the Login Redirect button
        mLoginRedirectButton.setOnClickListener(view -> {
            Intent intent1 = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent1);
        });
    }

    // Add user to Firestore
    public void addUser(final String firstName, final String lastName, final String phone, final String specialization, boolean isVisuallyImpaired) {
        // Create new user
        User user;
        if(!mIsDoctor) {
            user = new Patient(firstName, lastName, phone, isVisuallyImpaired);
        } else {
            user = new Doctor(firstName, lastName, phone, specialization);
        }

        // Add user in the Users collection
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null) {
            mFirestore.collection("Users").document(firebaseUser.getUid()).set(user)
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully written!"))
                    .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));
        }

        mProgressBar.setVisibility(View.INVISIBLE);

        // Start Main Activity
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    // Uses shared preferences to remember user login info
    public void setLoginCredentials(String email, String password) {
        mPreferences.edit().putString(getString(R.string.user_email), email).apply();
        mPreferences.edit().putString(getString(R.string.user_password), password).apply();
    }
}