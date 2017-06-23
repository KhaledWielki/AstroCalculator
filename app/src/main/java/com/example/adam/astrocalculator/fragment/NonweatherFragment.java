package com.example.adam.astrocalculator.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.adam.astrocalculator.R;
import com.example.adam.astrocalculator.model.PlaceModel;
import com.example.adam.astrocalculator.model.WeatherModel;
import com.example.adam.astrocalculator.util.AstroTools;

import java.util.List;

/**
 * Created by Adam on 2017-06-23.
 */


public class NonweatherFragment extends Fragment {

    private TextView windspeed;
    private TextView humidity;
    private TextView sunriseTime;
    private TextView sunsetTime;
    private TextView latitude;
    private TextView longitude;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nonweather, container, false);
        windspeed = (TextView) view.findViewById(R.id.windspeed);
        humidity = (TextView) view.findViewById(R.id.humidity);
        sunriseTime = (TextView) view.findViewById(R.id.sunriseTime);
        sunsetTime = (TextView) view.findViewById(R.id.sunsetTime);
        latitude = (TextView) view.findViewById(R.id.latitude);
        longitude = (TextView) view.findViewById(R.id.longitude);
        setInfo();
        return view;
    }

    private void setInfo() {
        List<WeatherModel> weatherList = AstroTools.getWeatherList(getContext());
        if(weatherList == null)
            return;
        WeatherModel weather = weatherList.get(0);
        windspeed.setText(weather.getWindSpeed());
        humidity.setText(weather.getHumidity());
        sunriseTime.setText(weather.getSunrise());
        sunsetTime.setText(weather.getSunset());
        PlaceModel place = AstroTools.getCurrentPlace(getContext());
        if(place != null) {
            latitude.setText(place.getLatitude());
            longitude.setText(place.getLongitude());
        }
    }

}