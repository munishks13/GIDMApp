package com.capstone.coursera.gidma.model.mediator.webdata;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

public class Follower {

    private long id;

    private String userId;
    private String followerUserId;

    private boolean confirmed;
    private Date lastUpdatedDateTime;

    private boolean bloodSugarLvlFasting;
    private boolean bloodSugarLvlMT;
    private boolean bloodSugarLvlBT;
    private boolean bloodSugarLvlTime;

    private boolean eatMT;
    private boolean insulin;
    private boolean whoWithYou;
    private boolean whereWereYou;
    private boolean mood;
    private boolean stress;
    private boolean energyLvl;
    private boolean bloodSugarLvlTimeEvent;

    public Follower() {
        // TODO Auto-generated constructor stub
    }

    public Follower(String userId, String followerUserId) {
        this.userId = userId;
        this.followerUserId = followerUserId;
    }

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

    public String getFollowerUserId() {
        return followerUserId;
    }

    public void setFollowerUserId(String followerUserId) {
        this.followerUserId = followerUserId;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }


    public Date getLastUpdatedDateTime() {
        return lastUpdatedDateTime;
    }

    public void setLastUpdatedDateTime(Date lastUpdatedDateTime) {
        this.lastUpdatedDateTime = lastUpdatedDateTime;
    }

    public boolean isBloodSugarLvlFasting() {
        return bloodSugarLvlFasting;
    }

    public void setBloodSugarLvlFasting(boolean bloodSugarLvlFasting) {
        this.bloodSugarLvlFasting = bloodSugarLvlFasting;
    }

    public boolean isBloodSugarLvlMT() {
        return bloodSugarLvlMT;
    }

    public void setBloodSugarLvlMT(boolean bloodSugarLvlMT) {
        this.bloodSugarLvlMT = bloodSugarLvlMT;
    }

    public boolean isBloodSugarLvlBT() {
        return bloodSugarLvlBT;
    }

    public void setBloodSugarLvlBT(boolean bloodSugarLvlBT) {
        this.bloodSugarLvlBT = bloodSugarLvlBT;
    }

    public boolean isBloodSugarLvlTime() {
        return bloodSugarLvlTime;
    }

    public void setBloodSugarLvlTime(boolean bloodSugarLvlTime) {
        this.bloodSugarLvlTime = bloodSugarLvlTime;
    }

    public boolean isEatMT() {
        return eatMT;
    }

    public void setEatMT(boolean eatMT) {
        this.eatMT = eatMT;
    }

    public boolean isInsulin() {
        return insulin;
    }

    public void setInsulin(boolean insulin) {
        this.insulin = insulin;
    }

    public boolean isWhoWithYou() {
        return whoWithYou;
    }

    public void setWhoWithYou(boolean whoWithYou) {
        this.whoWithYou = whoWithYou;
    }

    public boolean isWhereWereYou() {
        return whereWereYou;
    }

    public void setWhereWereYou(boolean whereWereYou) {
        this.whereWereYou = whereWereYou;
    }

    public boolean isMood() {
        return mood;
    }

    public void setMood(boolean mood) {
        this.mood = mood;
    }

    public boolean isStress() {
        return stress;
    }

    public void setStress(boolean stress) {
        this.stress = stress;
    }

    public boolean isEnergyLvl() {
        return energyLvl;
    }

    public void setEnergyLvl(boolean energyLvl) {
        this.energyLvl = energyLvl;
    }

    public boolean isBloodSugarLvlTimeEvent() {
        return bloodSugarLvlTimeEvent;
    }

    public void setBloodSugarLvlTimeEvent(boolean bloodSugarLvlTimeEvent) {
        this.bloodSugarLvlTimeEvent = bloodSugarLvlTimeEvent;
    }
}
