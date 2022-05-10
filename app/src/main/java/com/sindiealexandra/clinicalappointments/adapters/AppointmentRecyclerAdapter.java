package com.sindiealexandra.clinicalappointments.adapters;

import android.content.Intent;
import android.os.Build;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sindiealexandra.clinicalappointments.AppointmentActivity;
import com.sindiealexandra.clinicalappointments.R;
import com.sindiealexandra.clinicalappointments.models.Appointment;

import java.util.List;

public class AppointmentRecyclerAdapter extends RecyclerView.Adapter<AppointmentRecyclerAdapter.ViewHolder> {

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private FirebaseUser mFirebaseUser;

    private TextView mSpecializationTextView;
    private TextView mDateTextView;

    private List<Appointment> mAppointments;
    private List<String> mAppointmentIDs;
    private String mUserType;

    private static final String TAG = "Appointment Rec. A.";

    public AppointmentRecyclerAdapter(List<Appointment> appointments) {
        mAppointments = appointments;
    }

    @NonNull
    @Override
    public AppointmentRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.appointments_item, parent, false);
        return new AppointmentRecyclerAdapter.ViewHolder(view);
    }

    // Fill card with data from Firestore
    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void onBindViewHolder(@NonNull final AppointmentRecyclerAdapter.ViewHolder holder, final int position) {
        String specialization = mAppointments.get(position).getSpecialization();
        mSpecializationTextView.setText(specialization);
        String date = DateFormat.format("MM/dd/yyyy HH:mm", mAppointments.get(position).getDate()).toString();
        mDateTextView.setText(date);

        holder.itemView.setOnLongClickListener(view -> {
            Intent intent = new Intent(holder.itemView.getContext(), AppointmentActivity.class);
            intent.putExtra("APPOINTMENT_ID", mAppointmentIDs.get(position));
            intent.putExtra("USER_TYPE", mUserType);
            holder.itemView.getContext().startActivity(intent);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return mAppointments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            mAuth = FirebaseAuth.getInstance();
            mFirestore = FirebaseFirestore.getInstance();
            mFirebaseUser = mAuth.getCurrentUser();
            mSpecializationTextView = itemView.findViewById(R.id.specializationTextView);
            mDateTextView = itemView.findViewById(R.id.dateTextView);
        }
    }

    // Refresh fragment when something changes in the Recycler view
    public void updateAppointments(List<Appointment> appointments, List<String> appointmentIDs, String userType) {
        mAppointments = appointments;
        mAppointmentIDs = appointmentIDs;
        mUserType = userType;
        notifyDataSetChanged();
    }
}
