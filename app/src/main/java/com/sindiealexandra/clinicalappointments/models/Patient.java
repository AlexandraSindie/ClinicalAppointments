package com.sindiealexandra.clinicalappointments.models;

public class Patient extends User {
    public int noOfAppointments;
    private boolean isVisuallyImpaired;

    public Patient() {
    }

    public Patient(String firstName, String lastName, String phone, boolean isVisuallyImpaired) {
        super(firstName, lastName, phone);
        super.setEnabled(true);
        noOfAppointments = 0;
        this.isVisuallyImpaired = isVisuallyImpaired;
    }

    public int getNoOfAppointments() {
        return noOfAppointments;
    }

    public void setNoOfAppointments(int noOfAppointments) {
        this.noOfAppointments = noOfAppointments;
    }

    public boolean isVisuallyImpaired() {
        return isVisuallyImpaired;
    }

    public void setVisuallyImpaired(boolean visuallyImpaired) {
        isVisuallyImpaired = visuallyImpaired;
    }


    @Override
    public String toString() {
        return "Patient{" +
                "noOfAppointments=" + noOfAppointments +
                '}';
    }
}
