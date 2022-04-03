package com.sindiealexandra.clinicalappointments.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sindiealexandra.clinicalappointments.DoctorActivity;
import com.sindiealexandra.clinicalappointments.R;
import com.sindiealexandra.clinicalappointments.models.Doctor;

import java.util.List;

public class DoctorRecyclerAdapter extends RecyclerView.Adapter<DoctorRecyclerAdapter.ViewHolder> {

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private FirebaseUser mFirebaseUser;

    private TextView mNameTextView;
    private TextView mPhoneTextView;

    private List<Doctor> mDoctors;
    private List<String> mDoctorIDs;

    private static final String TAG = "User Recycler Adapter";

    public DoctorRecyclerAdapter(List<Doctor> doctors) {
        mDoctors = doctors;
    }

    @NonNull
    @Override
    public DoctorRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.doctors_item, parent, false);
        return new DoctorRecyclerAdapter.ViewHolder(view);
    }

    // Fill card with data from Firestore
    @Override
    public void onBindViewHolder(@NonNull final DoctorRecyclerAdapter.ViewHolder holder, final int position) {
        // Fill cards with data
        String firstName = mDoctors.get(position).getFirstName();
        String lastName = mDoctors.get(position).getLastName();
        mNameTextView.setText(String.format("%s %s", firstName, lastName));

        String phone = mDoctors.get(position).getPhone();
        mPhoneTextView.setText(phone);

        holder.itemView.setOnLongClickListener(view -> {
            Intent intent = new Intent(holder.itemView.getContext(), DoctorActivity.class);
            intent.putExtra("DOCTOR_ID", mDoctorIDs.get(position));
            holder.itemView.getContext().startActivity(intent);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return mDoctors.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull final View itemView) {
            super(itemView);

            mAuth = FirebaseAuth.getInstance();
            mFirestore = FirebaseFirestore.getInstance();
            mFirebaseUser = mAuth.getCurrentUser();
            mNameTextView = itemView.findViewById(R.id.nameTextView);
            mPhoneTextView = itemView.findViewById(R.id.phoneTextView);
        }
    }

    // Refresh fragment when something changes in the Recycler view
    public void updateDoctors(List<Doctor> doctors, List<String> doctorIDs) {
        mDoctors = doctors;
        mDoctorIDs = doctorIDs;
        notifyDataSetChanged();
    }
}
