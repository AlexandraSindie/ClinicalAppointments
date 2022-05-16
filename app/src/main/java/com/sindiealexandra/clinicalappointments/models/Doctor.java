package com.sindiealexandra.clinicalappointments.models;

public class Doctor extends User {
    public String specialization;

    public Doctor() {
    }

    public Doctor(String firstName, String lastName, String phone, String specialization) {
        super(firstName, lastName, phone);
        super.setEnabled(false);
        this.specialization = specialization;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    @Override
    public String toString() {
        return "Doctor{" +
                "specialization='" + specialization + '\'' +
                '}';
    }
}
