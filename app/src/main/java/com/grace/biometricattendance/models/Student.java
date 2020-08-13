package com.grace.biometricattendance.models;

import java.util.Date;

public class Student {
    String id;
    String level;
    String gender;
    String firstName;
    String LastName;
    String matriculationNumber;
    Date date;


    public Student() {
    }

    public Student(String id, String level, String gender, String firstName, String lastName, String matriculationNumber) {
        this.id = id;
        this.level = level;
        this.gender = gender;
        this.firstName = firstName;
        LastName = lastName;
        this.matriculationNumber = matriculationNumber;
    }
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getMatriculationNumber() {
        return matriculationNumber;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }
}
