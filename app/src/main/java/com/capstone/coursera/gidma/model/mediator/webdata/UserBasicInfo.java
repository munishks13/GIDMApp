package com.capstone.coursera.gidma.model.mediator.webdata;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class UserBasicInfo {

    private long followerUniqueid;

    private String userId;

    private String firstName;
    private String lastName;
    private String dateOfBirth;

    private String medicalRecordNumber;
    private boolean followed;
    private boolean confirmed;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

    public long getFollowerUniqueid() {
        return followerUniqueid;
    }

    public void setFollowerUniqueid(long followerUniqueid) {
        this.followerUniqueid = followerUniqueid;
    }

    /**
     * @return the userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     * @param userId the userId to set
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * @return the firstName
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @param firstName the firstName to set
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * @return the lastName
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * @param lastName the lastName to set
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * @return the dateOfBirth
     */
    public String getDateOfBirth() {
        return dateOfBirth;
    }

    /**
     * @param dateOfBirth the dateOfBirth to set
     */
    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    /**
     * @return the medicalRecordNumber
     */
    public String getMedicalRecordNumber() {
        return medicalRecordNumber;
    }

    /**
     * @param medicalRecordNumber the medicalRecordNumber to set
     */
    public void setMedicalRecordNumber(String medicalRecordNumber) {
        this.medicalRecordNumber = medicalRecordNumber;
    }

    /**
     * @return the followed
     */
    public boolean isFollowed() {
        return followed;
    }

    /**
     * @param followed the followed to set
     */
    public void setFollowed(boolean followed) {
        this.followed = followed;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }
}
