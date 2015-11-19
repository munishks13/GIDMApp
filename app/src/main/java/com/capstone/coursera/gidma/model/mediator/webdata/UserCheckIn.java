package com.capstone.coursera.gidma.model.mediator.webdata;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

public class UserCheckIn {

    private long id;

    private String userId;
    private Date checkInDateTime;

    private String bloodSugarLvlFasting;
    private String bloodSugarLvlMT;
    private String bloodSugarLvlBT;
    private String bloodSugarLvlTime;

    private String eatMT;
    private String insulin;
    private String whoWithYou;
    private String whereWereYou;
    private String mood;
    private String stress;
    private String energyLvl;
    private String bloodSugarLvlTimeEvent;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getCheckInDateTime() {
        return checkInDateTime;
    }

    public void setCheckInDateTime(Date checkInDateTime) {
        this.checkInDateTime = checkInDateTime;
    }

    public String getBloodSugarLvlFasting() {
        return bloodSugarLvlFasting;
    }

    public void setBloodSugarLvlFasting(String bloodSugarLvlFasting) {
        this.bloodSugarLvlFasting = bloodSugarLvlFasting;
    }

    public String getBloodSugarLvlMT() {
        return bloodSugarLvlMT;
    }

    public void setBloodSugarLvlMT(String bloodSugarLvlMT) {
        this.bloodSugarLvlMT = bloodSugarLvlMT;
    }

    public String getBloodSugarLvlBT() {
        return bloodSugarLvlBT;
    }

    public void setBloodSugarLvlBT(String bloodSugarLvlBT) {
        this.bloodSugarLvlBT = bloodSugarLvlBT;
    }

    public String getBloodSugarLvlTime() {
        return bloodSugarLvlTime;
    }

    public void setBloodSugarLvlTime(String bloodSugarLvlTime) {
        this.bloodSugarLvlTime = bloodSugarLvlTime;
    }

    public String getEatMT() {
        return eatMT;
    }

    public void setEatMT(String eatMT) {
        this.eatMT = eatMT;
    }

    public String getInsulin() {
        return insulin;
    }

    public void setInsulin(String insulin) {
        this.insulin = insulin;
    }

    public String getWhoWithYou() {
        return whoWithYou;
    }

    public void setWhoWithYou(String whoWithYou) {
        this.whoWithYou = whoWithYou;
    }

    public String getWhereWereYou() {
        return whereWereYou;
    }

    public void setWhereWereYou(String whereWereYou) {
        this.whereWereYou = whereWereYou;
    }

    public String getMood() {
        return mood;
    }

    public void setMood(String mood) {
        this.mood = mood;
    }

    public String getStress() {
        return stress;
    }

    public void setStress(String stress) {
        this.stress = stress;
    }

    public String getEnergyLvl() {
        return energyLvl;
    }

    public void setEnergyLvl(String energyLvl) {
        this.energyLvl = energyLvl;
    }

    public String getBloodSugarLvlTimeEvent() {
        return bloodSugarLvlTimeEvent;
    }

    public void setBloodSugarLvlTimeEvent(String bloodSugarLvlTimeEvent) {
        this.bloodSugarLvlTimeEvent = bloodSugarLvlTimeEvent;
    }
}
