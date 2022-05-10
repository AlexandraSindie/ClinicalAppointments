package com.sindiealexandra.clinicalappointments;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sindiealexandra.clinicalappointments.models.Doctor;
import com.sindiealexandra.clinicalappointments.models.Patient;
import com.sindiealexandra.clinicalappointments.models.User;

import java.util.Objects;

public class AccountActivity extends AppCompatActivity {
    private static final String TAG = "Account Activity";
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private FirebaseUser mFirebaseUser;
    private SharedPreferences mPreferences;
    private ProgressBar mProgressBar;
    private Toolbar mToolbar;
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
    private Button mSaveButton;
    private Button mDeleteButton;
    private boolean mIsEnabled = false;
    private static String mSpecialization = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();

        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Configure Toolbar
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.account_settings));
        }

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
        mSaveButton = findViewById(R.id.saveButton);
        mDeleteButton = findViewById(R.id.deleteButton);

        // Set user specialization from Firestore
        setUserSpecialization();

        fillForm();

        // When user clicks the Register button
        mSaveButton.setOnClickListener(view -> {
            // Get user input from form
            final String firstName = mFirstNameEditText.getText().toString().trim();
            final String lastName = mLastNameEditText.getText().toString().trim();
            final String phone = mPhoneEditText.getText().toString().trim();
            final String email = mEmailEditText.getText().toString().trim();
            final String specialization = mSpecializationEditText.getText().toString().trim();

            // Clear errors
            mFirstNameTextInputLayout.setError(null);
            mLastNameTextInputLayout.setError(null);
            mPhoneTextInputLayout.setError(null);
            mEmailTextInputLayout.setError(null);
            mSpecializationTextInputLayout.setError(null);

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
            } else if (TextUtils.isEmpty(phone)) {
                mPhoneTextInputLayout.setError(getString(R.string.phone_required_error));
                mPhoneEditText.requestFocus();
            } else if (TextUtils.isEmpty(email)) {
                mEmailTextInputLayout.setError(getString(R.string.email_required_error));
                mEmailEditText.requestFocus();
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                mEmailTextInputLayout.setError(getString(R.string.email_format_error));
                mEmailEditText.requestFocus();
            } else if (mSpecialization != null && TextUtils.isEmpty(specialization)) {
                mSpecializationTextInputLayout.setError(getString(R.string.specialization_required_error));
                mSpecializationEditText.requestFocus();
            } else {
                mProgressBar.setVisibility(View.VISIBLE);

                // Change user info in database
                modifyUser(firstName, lastName, phone, email, specialization);
            }
        });

        mDeleteButton.setOnClickListener(view -> {
            DialogInterface.OnClickListener dialogClickListener = (dialog, option) -> {
                switch (option) {
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        deleteUser();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            };

            AlertDialog.Builder builder = new MaterialAlertDialogBuilder(AccountActivity.this);
            builder.setTitle(getString(R.string.delete_account_message_title));
            builder.setMessage(getString(R.string.delete_message)).setPositiveButton(getString(R.string.yes), dialogClickListener)
                    .setNegativeButton(getString(R.string.no), dialogClickListener).show();
        });

    }

    // Inflate toolbar menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.account_menu, menu);
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

    public void setUserSpecialization() {
        // Get user info from database
        mFirestore.collection("Users").document(mFirebaseUser.getUid()).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot != null) {
                mSpecialization = documentSnapshot.getString("specialization");
                // If doctor show specialization field
                if(mSpecialization != null) {
                    mSpecializationTextInputLayout.setVisibility(View.VISIBLE);
                    mSpecializationEditText.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    // Get user data and fill form
    public void fillForm() {
        // Get user info from database
        mFirebaseUser = mAuth.getCurrentUser();
        mFirestore.collection("Users").document(mFirebaseUser.getUid()).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot != null) {
                mFirstNameEditText.setText(documentSnapshot.getString("firstName"));
                mLastNameEditText.setText(documentSnapshot.getString("lastName"));
                mPhoneEditText.setText(documentSnapshot.getString("phone"));
                if(mSpecialization != null) {
                    mSpecializationEditText.setText(documentSnapshot.getString("specialization"));
                }
                mIsEnabled = Boolean.TRUE.equals(documentSnapshot.getBoolean("enabled"));
            }

            mProgressBar.setVisibility(View.INVISIBLE);
            mSaveButton.setEnabled(true);
        });

        // Get email from Authentication
        mEmailEditText.setText(mFirebaseUser.getEmail());
    }

    // Update user in Authentication / Firestore
    public void modifyUser(final String firstName, final String lastName, final String phone, final String email, final String specialization) {
        // Create new user object
        User user;
        if(mSpecialization == null) {
            user = new Patient(firstName, lastName, phone);
        } else {
            user = new Doctor(firstName, lastName, phone, specialization);
        }
        user.setEnabled(mIsEnabled);

        // Update user in the Users collection
        if (mFirebaseUser != null) {
            mFirestore.collection("Users").document(mFirebaseUser.getUid()).set(user)
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully written!"))
                    .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));

            // Get email and password form shared preferences
            String userEmail = mPreferences.getString(getString(R.string.user_email), ""); // user actual email
            String password = mPreferences.getString(getString(R.string.user_password), "");

            AuthCredential credential = EmailAuthProvider.getCredential(userEmail, password);

            // Re-authenticate user
            mFirebaseUser.reauthenticate(credential)
                    .addOnCompleteListener(task -> {
                        Log.d(TAG, "User re-authenticated.");
                        // Update email in Authentication
                        mFirebaseUser.updateEmail(email)
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        Log.d(TAG, "User email address updated.");
                                        // Update shared preferences
                                        mPreferences.edit().putString(getString(R.string.user_email), email).apply();
                                    } else {
                                        Log.w(TAG, "User email address not updated.");
                                        try {
                                            throw Objects.requireNonNull(task1.getException());
                                        } catch (Exception e) {
                                            // Display a message to the user.
                                            Toast.makeText(getApplicationContext(), getString(R.string.update_failed),
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    });
        }

        mProgressBar.setVisibility(View.INVISIBLE);

        // Start Main Activity
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    // Delete user from Firestore / Authentication
    public void deleteUser() {
        // Delete user from Firestore
        mFirestore.collection("Users").document(mFirebaseUser.getUid())
                .delete()
                .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully deleted!"))
                .addOnFailureListener(e -> Log.w(TAG, "Error deleting document", e));

        // Delete user from Authentication
        mFirebaseUser = mAuth.getCurrentUser();
        if(mFirebaseUser != null) {
            mFirebaseUser.delete()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User account deleted.");
                        }
                    });
        }

        // Clear shared preferences
        mPreferences.edit().putBoolean("checked", false).apply();
        mPreferences.edit().putString(getString(R.string.user_email), "").apply();
        mPreferences.edit().putString(getString(R.string.user_password), "").apply();

        // Start Main Activity
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }
}