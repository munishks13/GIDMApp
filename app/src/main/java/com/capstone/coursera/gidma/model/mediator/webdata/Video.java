package com.capstone.coursera.gidma.model.mediator.webdata;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Objects;

/**
 * A simple object to represent a video and its URL for viewing.
 * <p/>
 * You must annotate this object to make it a JPA entity.
 * <p/>
 * <p/>
 * Feel free to modify this with whatever other metadata that you want, such as
 * the
 *
 * @author jules, mitchell
 */
public class Video {

    private long id;

    private String title;
    private String url;
    private long duration;
    private String location;
    private String subject;
    private String contentType;


    // We don't want to bother unmarshalling or marshalling
    // any owner data in the JSON. Why? We definitely don't
    // want the client trying to tell us who the owner is.
    // We also might want to keep the owner secret.
    @JsonIgnore
    private String owner;

    public Video() {
    }

    public Video(String title, long duration,
                 String contentType, String location) {
        super();
        this.title = title;
        this.contentType = contentType;
        this.duration = duration;
        this.location = location;
    }


    /**
     * Constructor that initializes all the fields of interest.
     */
    public Video(long id,
                 String title,
                 long duration,
                 String contentType,
                 String url) {
        this.id = id;
        this.title = title;
        this.duration = duration;
        this.contentType = contentType;
        this.url = url;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * Two Videos will generate the same hashcode if they have exactly the same
     * values for their name, url, and duration.
     */
    @Override
    public int hashCode() {
        // Google Guava provides great utilities for hashing
        return Objects.hashCode(title, url, duration, owner);
    }

    /**
     * Two Videos are considered equal if they have exactly the same values for
     * their name, url, and duration.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Video) {
            Video other = (Video) obj;
            // Google Guava provides great utilities for equals too!
            return Objects.equal(title, other.title)
                    && Objects.equal(url, other.url)
                    && Objects.equal(owner, other.owner)
                    && duration == other.duration;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "{" +
                "Id: " + id + ", " +
                "Title: " + title + ", " +
                "Data URL: " + url + ", " +
                "Duration: " + duration + ", " +
                "location: " + location + ", " +
                "subject: " + subject + ", " +
                "ContentType: " + contentType +
                "}";
    }

}
