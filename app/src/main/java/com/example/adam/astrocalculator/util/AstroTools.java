package com.example.adam.astrocalculator.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.example.adam.astrocalculator.async.PlaceWeatherLoad;
import com.example.adam.astrocalculator.model.PlaceModel;
import com.example.adam.astrocalculator.model.WeatherModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by Adam on 2017-06-23.
 */

public class AstroTools {

    private static final String PREFERENCES_NAME = "astroPreferences";
    private static final String PREFERENCES_WEATHER = "weather";
    private static final String PREFERENCES_PLACE = "place";
    private static final String PREFERENCES_UNIT = "unit";
    private static final String PREFERENCES_REFRESH_DATE = "refresh";
    private static final String GET_WEATHER = "weather";

    public static void saveRefreshDate(final Context context) {
        Date date = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, 1);
        date = c.getTime();
        SharedPreferences config = context.getSharedPreferences(PREFERENCES_NAME, Activity.MODE_PRIVATE);
        config.edit().putLong(PREFERENCES_REFRESH_DATE, date.getTime()).apply();
    }

    public static void shouldRefresh(final Context context) {
        long currentDate = new Date().getTime();
        SharedPreferences config = context.getSharedPreferences(PREFERENCES_NAME, Activity.MODE_PRIVATE);
        long refreshDate = config.getLong(PREFERENCES_REFRESH_DATE, 0);
        if (currentDate > refreshDate)
            refreshWeatherData(context);
    }

    public static void refreshWeatherData(final Context context) {
        String woeid = getCurrentWoeid(context);
        if(woeid == null)
            return;
        try {
            int result = new PlaceWeatherLoad(context).execute(GET_WEATHER, woeid).get();
            if(result == AstroStatuses.OK)
                saveRefreshDate(context);
            else
                toast(getToastMsg(result), context);
        } catch(Exception e) {
            Log.d("REFRESH", e.toString());
        }
    }

    private static void toast(String msg, final Context context) {
        Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        toast.show();
    }

    public static List<WeatherModel> getWeatherList(final Context context) {
        SharedPreferences config = context.getSharedPreferences(PREFERENCES_NAME, Activity.MODE_PRIVATE);
        String temp = config.getString(PREFERENCES_WEATHER, "");
        if(temp.length() == 0)
            return null;
        Type type = new TypeToken<List<WeatherModel>>(){}.getType();
        return new Gson().fromJson(temp, type);
    }

    public static PlaceModel getCurrentPlace(final Context context) {
        SharedPreferences config = context.getSharedPreferences(PREFERENCES_NAME, Activity.MODE_PRIVATE);
        String temp = config.getString(PREFERENCES_PLACE, "");
        if(temp.length() == 0)
            return null;
        Type type = new TypeToken<PlaceModel>(){}.getType();
        return new Gson().fromJson(temp, type);
    }

    public static String getCurrentWoeid(final Context context) {
        PlaceModel placeModel = getCurrentPlace(context);
        if(placeModel != null)
            return placeModel.getWoeid();
        return null;
    }

    public static int getCurrentUnits(final Context context) {
        SharedPreferences config = context.getSharedPreferences(PREFERENCES_NAME, Activity.MODE_PRIVATE);
        return config.getInt(PREFERENCES_UNIT, 0);
    }

    public static String getCurrentTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new GregorianCalendar().getTime());
    }

    /*
    public static String dateFormat(AstroDateTime dateTime) {
        GregorianCalendar cal = astroDateTimeToGregorianCalendar(dateTime);
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(cal.getTime());
    }
    */

    /*
    private static GregorianCalendar astroDateTimeToGregorianCalendar(AstroDateTime dateTime) {
        int year = dateTime.getYear();
        int month = dateTime.getMonth();
        int day = dateTime.getDay();
        int hour = dateTime.getHour();
        int minute = dateTime.getMinute();
        int second = dateTime.getSecond();
        return new GregorianCalendar(year, month, day, hour, minute, second);
    }
    */

    public static String getToastMsg(int errorCode) {
        switch(errorCode) {
            case AstroStatuses.CONNECTION_ERROR:
                return "Internet connection problem, try again later!";
            case AstroStatuses.DB_ERROR:
                return "You already added this place";
            case AstroStatuses.ERROR:
                return "Oops! Something goes wrong :(";
            case AstroStatuses.PLACE_NOT_FOUND:
                return "Can't find typed place :(";
            case AstroStatuses.YAHOO_ERROR:
                return "Yahoo weather service error :(";
            default:
                return "Error";
        }
    }
}
