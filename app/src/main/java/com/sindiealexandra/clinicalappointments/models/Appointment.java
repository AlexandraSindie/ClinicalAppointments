package com.sindiealexandra.clinicalappointments.models;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Appointment {
    public String specialization;
    private Date date;
    private String doctorId;
    private String patientId;
    private @ServerTimestamp
    Date timestamp;

    public Appointment() {
    }

    public Appointment(String specialization, Date date, String doctorId, String patientId) {
        this.specialization = specialization;
        this.date = date;
        this.doctorId = doctorId;
        this.patientId = patientId;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    @NonNull
    @Override
    public String toString() {
        return "Appointment{" +
                "specialization='" + specialization + '\'' +
                ", date=" + date +
                ", doctorId='" + doctorId + '\'' +
                ", patientId='" + patientId + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
