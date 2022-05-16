package com.sindiealexandra.clinicalappointments;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.Magnifier;
import android.widget.TextView;

public class VisuallyImpairedActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private Button mOphthalmologyButton;
    private Button mMainRedirectButton;
    private TextView mOphthalmologyTextView;
    private TextView mMagnifierTextView;
    private TextView mDisableMagnifierTextView;

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visually_impaired);

        mToolbar = findViewById(R.id.toolbar);
        mOphthalmologyButton = findViewById(R.id.ophthalmologyButton);
        mMainRedirectButton = findViewById(R.id.mainRedirectButton);
        mOphthalmologyTextView = findViewById(R.id.ophthalmologyTextView);
        mMagnifierTextView = findViewById(R.id.magnifierTextView);
        mDisableMagnifierTextView = findViewById(R.id.disableMagnifierTextView);

        // Configure Toolbar
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.app_name));
        }

        // When user clicks the Ophthalmology Button
        mOphthalmologyButton.setOnClickListener(view -> {
            Intent intent = new Intent(VisuallyImpairedActivity.this, DoctorsActivity.class);
            intent.putExtra("SPECIALIZATION","OPHTHALMOLOGY");
            startActivity(intent);
        });

        // When user clicks the Redirect to Main Button
        mMainRedirectButton.setOnClickListener(view -> {
            Intent intent = new Intent(VisuallyImpairedActivity.this, MainActivity.class);
            startActivity(intent);
        });

        // Enable magnifier
        Magnifier magnifier = new Magnifier(mOphthalmologyTextView);
        mOphthalmologyTextView.setOnTouchListener((v, event) -> {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    // Fall through.
                case MotionEvent.ACTION_MOVE: {
                    final int[] viewPosition = new int[2];
                    v.getLocationOnScreen(viewPosition);
                    magnifier.show(event.getRawX() - viewPosition[0],
                            event.getRawY() - viewPosition[1]);
                    break;
                }
                case MotionEvent.ACTION_CANCEL:
                    // Fall through.
                case MotionEvent.ACTION_UP: {
                    magnifier.dismiss();
                }
            }
            return true;
        });

    }
}