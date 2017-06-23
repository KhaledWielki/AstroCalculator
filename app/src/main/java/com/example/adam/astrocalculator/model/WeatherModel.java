package com.example.adam.astrocalculator.model;

/**
 * Created by Adam on 2017-06-23.
 */

public class WeatherModel {

    private String windSpeed;
    private String humidity;
    private String pressure;
    private String sunrise;
    private String sunset;
    private String date;
    private String currentTemp;
    private String highTemp;
    private String lowTemp;
    private String text;

    public WeatherModel(String windSpeed, String humidity, String pressure, String sunrise, String sunset, String date, String currentTemp, String highTemp, String lowTemp, String text) {
        this.windSpeed = windSpeed;
        this.humidity = humidity;
        this.pressure = pressure;
        this.sunrise = sunrise;
        this.sunset = sunset;
        this.date = date;
        this.currentTemp = currentTemp;
        this.highTemp = highTemp;
        this.lowTemp = lowTemp;
        this.text = text;
    }

    public WeatherModel(String date, String highTemp, String lowTemp, String text) {
        this.date = date;
        this.highTemp = highTemp;
        this.lowTemp = lowTemp;
        this.text = text;
    }

    public String getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(String windSpeed) {
        this.windSpeed = windSpeed;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getPressure() {
        return pressure;
    }

    public void setPressure(String pressure) {
        this.pressure = pressure;
    }

    public String getSunrise() {
        return sunrise;
    }

    public void setSunrise(String sunrise) {
        this.sunrise = sunrise;
    }

    public String getSunset() {
        return sunset;
    }

    public void setSunset(String sunset) {
        this.sunset = sunset;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCurrentTemp() {
        return currentTemp;
    }

    public void setCurrentTemp(String currentTemp) {
        this.currentTemp = currentTemp;
    }

    public String getHighTemp() {
        return highTemp;
    }

    public void setHighTemp(String highTemp) {
        this.highTemp = highTemp;
    }

    public String getLowTemp() {
        return lowTemp;
    }

    public void setLowTemp(String lowTemp) {
        this.lowTemp = lowTemp;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}

