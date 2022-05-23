package com.sindiealexandra.clinicalappointments;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
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
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sindiealexandra.clinicalappointments.models.Appointment;
import com.sindiealexandra.clinicalappointments.models.User;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

@RequiresApi(api = Build.VERSION_CODES.P)
public class AppointmentActivity extends AppCompatActivity {

    private static final String TAG = "Appointment Activity";
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private StorageReference mStorageRef;
    private Toolbar mToolbar;
    private ProgressBar mProgressBar;
    private TextView mUserTextView;
    private TextView mDateTextView;
    private String mAppointmentID;
    private String mUserType;
    private Appointment mAppointment;
    private Button mEditAppointmentButton;
    private Button mCancelAppointmentButton;
    private Button mUploadResultsButton;
    private Button mDownloadResultsButton;
    private Uri mPDFUri;

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
        mUploadResultsButton = findViewById(R.id.uploadPDFButton);
        mDownloadResultsButton = findViewById(R.id.downloadPDFButton);

        Intent intent = getIntent();
        mAppointmentID = intent.getStringExtra("APPOINTMENT_ID");
        mUserType = intent.getStringExtra("USER_TYPE");

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        // Load Doctor from Firestore
        mFirestore.collection("Appointments").document(mAppointmentID).get().addOnSuccessListener(appointmentDocumentSnapshot -> {
            mAppointment = appointmentDocumentSnapshot.toObject(Appointment.class);
            assert mAppointment != null;
            String date = DateFormat.format("MM/dd/yyyy HH:mm", mAppointment.getDate()).toString();
            mDateTextView.setText(date);

            if (mUserType.equals("DOCTOR")) {
                mFirestore.collection("Users").document(mAppointment.getPatientId()).get().addOnSuccessListener(documentSnapshot -> {
                    User user = documentSnapshot.toObject(User.class);
                    mUserTextView.setText(String.format("%s %s", Objects.requireNonNull(user).getFirstName(), Objects.requireNonNull(user).getLastName()));
                    mUploadResultsButton.setVisibility(View.VISIBLE);
                });
            } else if (mUserType.equals("PATIENT")) {
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

        // When user clicks the Upload Results Button
        mUploadResultsButton.setOnClickListener(view -> {
            Intent galleryIntent = new Intent();
            galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

            // We will be redirected to choose pdf
            galleryIntent.setType("application/pdf");
            startActivityForResult(galleryIntent, 1);
        });

        // When user clicks the Download Results Button
        mDownloadResultsButton.setOnClickListener(view -> {
            final StorageReference ref = mStorageRef.child(mAppointmentID + "." + "pdf");
            ref.getDownloadUrl().addOnSuccessListener(uri -> {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri.toString()));
                startActivity(browserIntent);
            }).addOnFailureListener(exception -> {
                Toast.makeText(this, getString(R.string.results_not_ready), Toast.LENGTH_LONG).show();
            });
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
            DialogInterface.OnClickListener dialogClickListener = (dialog, option) -> {
                switch (option) {
                    case DialogInterface.BUTTON_POSITIVE:
                        // Yes button clicked
                        cancelAppointment();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        // No button clicked
                        break;
                }
            };

            AlertDialog.Builder builder = new MaterialAlertDialogBuilder(AppointmentActivity.this);
            builder.setTitle(getString(R.string.cancel_appointment_message_title));
            builder.setMessage(getString(R.string.delete_message)).setPositiveButton(getString(R.string.yes), dialogClickListener)
                    .setNegativeButton(getString(R.string.no), dialogClickListener).show();
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

    // Edit appointment in Firestore
    public void editAppointment(final Date date) {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        // Create new appointment
        assert firebaseUser != null;

        // Check if later than 24 hours
        DocumentReference docRef = mFirestore.collection("Appointments").document(mAppointmentID);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Date appointmentDate = document.getDate("date");
                    Date currentTime = Calendar.getInstance().getTime();
                    long diff =   appointmentDate.getTime() - currentTime.getTime();
                    int hours = (int) (diff / (1000 * 60 * 60));
                    Log.e(TAG, String.valueOf(hours));
                    if(hours < 24) {
                        Toast.makeText(this, getString(R.string.appointment_modification_denied),
                                Toast.LENGTH_LONG).show();
                    } else {
                        mFirestore.collection("Appointments").document(mAppointmentID)
                                .update("date", date)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d(TAG, "DocumentSnapshot successfully updated!");
                                    Toast.makeText(this, getString(R.string.appointment_edited), Toast.LENGTH_LONG).show();

                                })
                                .addOnFailureListener(e -> Log.w(TAG, "Error updating document", e));
                    }
                } else {
                    Log.d(TAG, "No such document");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });

        mProgressBar.setVisibility(View.INVISIBLE);

        // Start Main Activity
        Intent intent = new Intent(getApplicationContext(), AppointmentsActivity.class);
        startActivity(intent);
    }

    // Cancel appointment in Firestore
    public void cancelAppointment() {
        mFirestore.collection("Appointments").document(mAppointmentID)
                .delete()
                .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully deleted!"))
                .addOnFailureListener(e -> Log.w(TAG, "Error deleting document", e));
        mProgressBar.setVisibility(View.INVISIBLE);

        // Start Main Activity
        Intent intent = new Intent(getApplicationContext(), AppointmentsActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            mPDFUri = data.getData();
            final StorageReference filepath = mStorageRef.child(mAppointmentID + "." + "pdf");
            filepath.putFile(mPDFUri).continueWithTask((Continuation) task -> {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return filepath.getDownloadUrl();
            }).addOnCompleteListener((OnCompleteListener<Uri>) task -> {
                if (task.isSuccessful()) {
                    // After uploading is done it progress
                    Toast.makeText(AppointmentActivity.this, "Uploaded Successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AppointmentActivity.this, "UploadedFailed", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}