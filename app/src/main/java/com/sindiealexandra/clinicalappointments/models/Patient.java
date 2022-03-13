package com.sindiealexandra.clinicalappointments.models;

public class Patient extends User {
    public int noOfAppointments;

    public Patient() {
    }

    public Patient(String firstName, String lastName, String phone) {
        super(firstName, lastName, phone);
        super.setEnabled(true);
        noOfAppointments = 0;
    }

    public int getNoOfAppointments() {
        return noOfAppointments;
    }

    public void setNoOfAppointments(int noOfAppointments) {
        this.noOfAppointments = noOfAppointments;
    }

    @Override
    public String toString() {
        return "Patient{" +
                "noOfAppointments=" + noOfAppointments +
                '}';
    }
}
