package com.capstone.coursera.gidma.model.mediator.webdata;

import java.util.ArrayList;
import java.util.List;

public class User {

    List<Follower> followers = new ArrayList<Follower>();
    List<UserAlarmTimes> userAlarmTimes = new ArrayList<UserAlarmTimes>();
    private String userId;
    private String password;
    private String firstName;
    private String lastName;
    private String dateOfBirth;
    private String medicalRecordNumber;
    private boolean followed;

    @Override
    public String toString() {
        return "User [userId=" + userId
                + ", password=******"
                + ", firstName=" + firstName
                + ", lastName=" + lastName
                + ", dateOfBirth=" + dateOfBirth
                + ", medicalRecordNumber : " + medicalRecordNumber
                + ", followed : " + followed
                + ", followers : " + followers
                + ", userAlarmTimes : " + userAlarmTimes
                + "]";
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }


    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public List<Follower> getFollowers() {
        return followers;
    }

    public void setFollowers(List<Follower> followers) {
        this.followers = followers;
    }

    public List<UserAlarmTimes> getUserAlarmTimes() {
        return userAlarmTimes;
    }

    public void setUserAlarmTimes(List<UserAlarmTimes> userAlarmTimes) {
        this.userAlarmTimes = userAlarmTimes;
    }

    public String getMedicalRecordNumber() {
        return medicalRecordNumber;
    }

    public void setMedicalRecordNumber(String medicalRecordNumber) {
        this.medicalRecordNumber = medicalRecordNumber;
    }

    public boolean isFollowed() {
        return followed;
    }

    public void setFollowed(boolean followed) {
        this.followed = followed;
    }
}
