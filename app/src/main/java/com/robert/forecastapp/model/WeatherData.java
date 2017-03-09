package com.robert.forecastapp.model;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Rob on 3/8/2017.
 */

public class WeatherData {
    public static final String WEATHER_TYPE_CLOUDS = "Clouds";
    public static final String WEATHER_TYPE_CLEAR = "Clear";
    public static final String WEATHER_TYPE_RAIN = "Rain";
    public static final String WEATHER_TYPE_WIND = "Wind";
    public static final String WEATHER_TYPE_SNOW = "Snow";

    private String city;
    private String country;
    private int currentTemp;
    private int maxTemp;
    private int minTemp;
    private String weather;
    private String formattedDate;

    public WeatherData(String city, String country, int currentTemp, int maxTemp, int minTemp, String weather, String rawDate) {
        this.city = city;
        this.country = country;
        this.currentTemp = currentTemp;
        this.maxTemp = maxTemp;
        this.minTemp = minTemp;
        this.weather = weather;
        this.formattedDate = formatDate(rawDate);
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public int getCurrentTemp() {
        return currentTemp;
    }

    public int getMaxTemp() {
        return maxTemp;
    }

    public int getMinTemp() {
        return minTemp;
    }

    public String getWeather() {
        return weather;
    }

    public String getFormattedDate() {
        return formattedDate;
    }

    public String formatDate(String rawDate) {
        DateFormat outputFormat = new SimpleDateFormat("LLL dd, yyyy");
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date date = null;
        try {
            date = inputFormat.parse(rawDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return outputFormat.format(date);
    }
}