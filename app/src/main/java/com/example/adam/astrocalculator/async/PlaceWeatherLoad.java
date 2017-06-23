package com.example.adam.astrocalculator.async;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.example.adam.astrocalculator.db.AstroDb;
import com.example.adam.astrocalculator.model.PlaceModel;
import com.example.adam.astrocalculator.model.WeatherModel;
import com.example.adam.astrocalculator.util.AstroStatuses;
import com.example.adam.astrocalculator.util.AstroTools;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Adam on 2017-06-23.
 */

public class PlaceWeatherLoad extends AsyncTask<String, Void, Integer> {

    private static final String PREFERENCES_NAME = "astroPreferences";
    private static final String PREFERENCES_WEATHER = "weather";
    private static final String GET_PLACE = "place";
    private static final String GET_WEATHER = "weather";
    private static final int UNIT_C = 0;
    private static final int UNIT_F = 1;

    private Context context;
    private AstroDb astroDb;

    public PlaceWeatherLoad(Context context){
        this.context = context;
        astroDb = new AstroDb(context);
    }

    @Override
    protected Integer doInBackground(String... params) {
        if(params.length < 2)
            return AstroStatuses.ERROR;
        int result = -1;
        if(params[0].equals(GET_PLACE)) {
            String place = params[1];
            result = place(place);
        }
        else if(params[0].equals(GET_WEATHER)) {
            String woeid = params[1];
            result = weather(woeid, AstroTools.getCurrentUnits(context) == UNIT_C);
        }
        return result;
    }

    private int weather(String woeid, boolean c) {
        String query = "select * from weather.forecast where woeid=" + woeid;
        if(c)
            query += " and u='c'";
        String inputStream = connection(query);
        if(inputStream.length() == 0)
            return AstroStatuses.CONNECTION_ERROR;
        try {
            return saveWeather(inputStream);
        } catch (Exception e) {
            return AstroStatuses.ERROR;
        }
    }

    private int place(String place) {
        String query = "select * from geo.places(1) where text='" + place + "'";
        String inputStream = connection(query);
        if(inputStream.length() == 0)
            return AstroStatuses.CONNECTION_ERROR;
        astroDb.open();
        try {
            return savePlace(inputStream);
        } catch (Exception e) {
            return AstroStatuses.ERROR;
        } finally {
            astroDb.close();
        }
    }

    private int saveWeather(String inputStream) throws Exception {
        JSONObject input = new JSONObject(inputStream);
        if(input.isNull("query"))
            return AstroStatuses.YAHOO_ERROR;
        JSONObject query = input.getJSONObject("query");
        int count = query.getInt("count");
        if(count > 0) {
            JSONObject channel = query.getJSONObject("results").getJSONObject("channel");
            /* units */
            JSONObject units = channel.getJSONObject("units");
            String unitPressure = units.getString("pressure");
            String unitSpeed = units.getString("speed");
            String unitTemperature = units.getString("temperature");
            /* wind */
            String windSpeed = channel.getJSONObject("wind").getString("speed") + " " + unitSpeed;
            /* atmosphere */
            JSONObject atmosphere = channel.getJSONObject("atmosphere");
            String humidity = atmosphere.getString("humidity");
            String pressure = atmosphere.getString("pressure") + " " + unitPressure;
            /* astronomy */
            JSONObject astronomy = channel.getJSONObject("astronomy");
            String sunrise = astronomy.getString("sunrise");
            String sunset = astronomy.getString("sunset");
            /* weather */
            JSONObject today = channel.getJSONObject("item").getJSONObject("condition");
            Log.d("woeid", query.toString());
            JSONArray forecast = channel.getJSONObject("item").getJSONArray("forecast");
            List<WeatherModel> weatherModelList = new ArrayList<>();
            /* today */
            JSONObject weather = forecast.getJSONObject(0);
            String todayDate = today.getString("date");
            String todayTemp = today.getString("temp") + " \u00b0" + unitTemperature;
            String todayHighTemp = weather.getString("high") + " \u00b0"  + unitTemperature;
            String todayLowTemp = weather.getString("low") + " \u00b0"  + unitTemperature;
            String todayText = today.getString("text");
            WeatherModel todayWeather = new WeatherModel(windSpeed, humidity, pressure, sunrise, sunset, todayDate, todayTemp, todayHighTemp, todayLowTemp, todayText);
            weatherModelList.add(todayWeather);
            /* forecast */
            for(int i = 1; i < 7 ; i++) {
                if(forecast.isNull(i))
                    break;
                weather = forecast.getJSONObject(i);
                String date = weather.getString("date");
                String highTemp = weather.getString("high") + " \u00b0"  + unitTemperature;
                String lowTemp = weather.getString("low") + " \u00b0"  + unitTemperature;
                String text = weather.getString("text");
                WeatherModel weatherModel = new WeatherModel(date, highTemp, lowTemp, text);
                weatherModelList.add(weatherModel);
            }
            saveList(weatherModelList);
            return AstroStatuses.OK;
        }
        return AstroStatuses.YAHOO_ERROR;
    }

    private void saveList(List<WeatherModel> list) {
        Gson gson = new Gson();
        String jsonList = gson.toJson(list);
        SharedPreferences config = context.getSharedPreferences(PREFERENCES_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor preferencesEditor = config.edit();
        preferencesEditor.putString(PREFERENCES_WEATHER, jsonList);
        preferencesEditor.apply();
    }

    private int savePlace(String inputStream) throws Exception {
        JSONObject input = new JSONObject(inputStream);
        if(input.isNull("query"))
            return AstroStatuses.PLACE_NOT_FOUND;
        JSONObject query = input.getJSONObject("query");
        int count = query.getInt("count");
        if(count > 0) {
            JSONObject result;
            JSONObject place = query.getJSONObject("results").getJSONObject("place");
            if(!place.isNull("locality1"))
                result = place.getJSONObject("locality1");
            else if(!place.isNull("admin2"))
                result = place.getJSONObject("admin2");
            else if(!place.isNull("admin1"))
                result = place.getJSONObject("admin1");
            else if(!place.isNull("country"))
                result = place.getJSONObject("country");
            else
                return AstroStatuses.PLACE_NOT_FOUND;
            JSONObject centroid = place.getJSONObject("centroid");
            String woeid = result.getString("woeid");
            String content = result.getString("content");
            String latitude = centroid.getString("latitude");
            String longitude = centroid.getString("longitude");
            String timeZone = place.getJSONObject("timezone").getString("content");
            PlaceModel placeModel = new PlaceModel(null, woeid, content, latitude, longitude, timeZone);
            if(astroDb.insertPlace(placeModel))
                return AstroStatuses.OK;
            else
                return AstroStatuses.DB_ERROR;
        }
        return AstroStatuses.PLACE_NOT_FOUND;
    }

    private String connection(String query) {
        String address = "https://query.yahooapis.com/v1/public/yql?q=" + query + "&format=json";
        InputStream in = null;
        HttpsURLConnection urlConnection = null;
        try {
            URL url = new URL(address);
            urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(5000);
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            urlConnection.setRequestProperty("connection", "close");
            if (urlConnection.getResponseCode() != HttpsURLConnection.HTTP_ACCEPTED)
                in = new BufferedInputStream(urlConnection.getInputStream());
            return readStream(in);
        }
        catch (Exception e) {
            e.printStackTrace();
            return "";
        } finally {
            if (urlConnection != null) urlConnection.disconnect();
        }
    }

    private String readStream(InputStream in) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (int c; (c = in.read()) >= 0;) sb.append((char) c);
        return sb.toString();
    }
}
