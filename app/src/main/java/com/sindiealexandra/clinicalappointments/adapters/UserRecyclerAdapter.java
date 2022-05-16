package com.sindiealexandra.clinicalappointments.adapters;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sindiealexandra.clinicalappointments.DoctorActivity;
import com.sindiealexandra.clinicalappointments.R;
import com.sindiealexandra.clinicalappointments.models.Doctor;
import com.sindiealexandra.clinicalappointments.models.User;

import java.util.List;

public class UserRecyclerAdapter extends RecyclerView.Adapter<UserRecyclerAdapter.ViewHolder> {

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    private TextView mNameTextView;
    private TextView mPhoneTextView;
    private TextView mUserTypeTextView;

    private List<User> mUsers;
    private List<String> mUserIDs;

    private SwitchCompat mStatusSwitch;

    private static final String TAG = "User Recycler Adapter";

    public UserRecyclerAdapter(List<User> users) {
        mUsers = users;
    }

    @NonNull
    @Override
    public UserRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_item, parent, false);
        return new UserRecyclerAdapter.ViewHolder(view);
    }

    // Fill card with data from Firestore
    @Override
    public void onBindViewHolder(@NonNull final UserRecyclerAdapter.ViewHolder holder, final int position) {
        // Fill cards with data
        String firstName = mUsers.get(position).getFirstName();
        String lastName = mUsers.get(position).getLastName();
        mNameTextView.setText(String.format("%s %s", firstName, lastName));

        String phone = mUsers.get(position).getPhone();
        mPhoneTextView.setText(phone);

        // If user enabled
        boolean enabled = mUsers.get(position).isEnabled();
        if(enabled) {
            mStatusSwitch.setChecked(true);
        }

        // Fill user type
        User user = mUsers.get(position);
        if(user instanceof Doctor) {
            mUserTypeTextView.setText(R.string.doctor);
            mUserTypeTextView.setTextColor(holder.itemView.getResources().getColor(R.color.primary_light));
        } else {
            mUserTypeTextView.setText(R.string.patient);
        }

        mStatusSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            setUser(mUsers.get(position), mUserIDs.get(position));
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull final View itemView) {
            super(itemView);

            mAuth = FirebaseAuth.getInstance();
            mFirestore = FirebaseFirestore.getInstance();
            mNameTextView = itemView.findViewById(R.id.nameTextView);
            mPhoneTextView = itemView.findViewById(R.id.phoneTextView);
            mUserTypeTextView = itemView.findViewById(R.id.userTypeTextView);
            mStatusSwitch = itemView.findViewById(R.id.statusSwitch);
        }
    }

    // Refresh fragment when something changes in the Recycler view
    public void updateUsers(List<User> users, List<String> userIDs) {
        mUsers = users;
        mUserIDs = userIDs;
        notifyDataSetChanged();
    }

    // Set enabled / disabled
    public void setUser(User user, String userID) {
        user.setEnabled(!user.isEnabled());
        mFirestore.collection("Users").document(userID)
                .set(user)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully written!"))
                .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));
    }
}

