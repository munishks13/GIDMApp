package com.capstone.coursera.gidma.model.mediator.webdata;

import java.util.ArrayList;
import java.util.List;

public class AverageVideoRating {

    private final double rating;

    private final long videoId;

    private final int totalRatings;

    private double currentUserRating;

    private List<String> likedByList = new ArrayList<String>();

    public AverageVideoRating(double rating, long videoId, int totalRatings) {
        super();
        this.rating = rating;
        this.videoId = videoId;
        this.totalRatings = totalRatings;
    }

    public double getRating() {
        return rating;
    }

    public long getVideoId() {
        return videoId;
    }

    public int getTotalRatings() {
        return totalRatings;
    }

    public List<String> getLikedByList() {
        return likedByList;
    }

    public void setLikedByList(List<String> likedByList) {
        this.likedByList = likedByList;
    }

    public double getCurrentUserRating() {
        return currentUserRating;
    }

    public void setCurrentUserRating(double currentUserRating) {
        this.currentUserRating = currentUserRating;
    }

    @Override
    public String toString() {
        return "{" +
                "videoId: " + videoId + ", " +
                "rating: " + rating + ", " +
                "totalRatings: " + totalRatings + ", " +
                "currentUserRating: " + currentUserRating + ", " +
                "likedByList: " + likedByList +
                "}";
    }

}
