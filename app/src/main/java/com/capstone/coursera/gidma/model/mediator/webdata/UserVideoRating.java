package com.capstone.coursera.gidma.model.mediator.webdata;

import com.google.common.base.Objects;

// You might want to annotate this with Jpa annotations, add an id field,
// and store it in the database...
//
// There are also plenty of other solutions that do not require
// persisting instances of this...
public class UserVideoRating {

    private long id;


    private long videoId;

    private double rating;

    private String user;

    public UserVideoRating() {
    }

    public UserVideoRating(long videoId, double rating, String user) {
        super();
        this.videoId = videoId;
        this.rating = rating;
        this.user = user;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getVideoId() {
        return videoId;
    }

    public void setVideoId(long videoId) {
        this.videoId = videoId;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(videoId, user, rating);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof UserVideoRating) {
            UserVideoRating other = (UserVideoRating) obj;
            return Objects.equal(videoId, other.videoId)
                    && Objects.equal(user, other.user)
                    && Objects.equal(rating, other.rating);
        } else {
            return false;
        }
    }

}
