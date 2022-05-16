package com.sindiealexandra.clinicalappointments.models;

import java.util.Date;

public class Administrator extends User {
    public Date dateOfBirth;

    public Administrator() {
    }

    public Administrator(String firstName, String lastName, String phone, Date dateOfBirth) {
        super(firstName, lastName, phone);
        dateOfBirth = dateOfBirth;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        dateOfBirth = dateOfBirth;
    }

    @Override
    public String toString() {
        return "Administrator{" +
                "dateOfBirth=" + dateOfBirth +
                '}';
    }
}
