package com.example.adam.astrocalculator.model;

/**
 * Created by Adam on 2017-06-23.
 */

public class PlaceModel {
    private Integer id;
    private String woeid;
    private String content;
    private String latitude;
    private String longitude;
    private String timeZone;

    public PlaceModel(Integer id, String woeid, String content, String latitude, String longitude, String timeZone) {
        this.id = id;
        this.woeid = woeid;
        this.content = content;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timeZone = timeZone;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getWoeid() {
        return woeid;
    }

    public void setWoeid(String woeid) {
        this.woeid = woeid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    @Override
    public String toString() {
        return this.content;
    }

}

