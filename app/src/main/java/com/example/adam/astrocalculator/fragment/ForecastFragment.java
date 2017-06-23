package com.example.adam.astrocalculator.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.adam.astrocalculator.R;
import com.example.adam.astrocalculator.model.WeatherModel;
import com.example.adam.astrocalculator.util.AstroTools;

import java.util.List;

/**
 * Created by Adam on 2017-06-23.
 */

public class ForecastFragment extends Fragment {

    private TextView temp1;
    private TextView temp2;
    private TextView temp3;
    private TextView temp4;
    private TextView temp5;
    private TextView temp6;
    private TextView text1;
    private TextView text2;
    private TextView text3;
    private TextView text4;
    private TextView text5;
    private TextView text6;
    private TextView textView1;
    private TextView textView2;
    private TextView textView3;
    private TextView textView4;
    private TextView textView5;
    private TextView textView6;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forecast, container, false);
        temp1 = (TextView) view.findViewById(R.id.temp1);
        temp2 = (TextView) view.findViewById(R.id.temp2);
        temp3 = (TextView) view.findViewById(R.id.temp3);
        temp4 = (TextView) view.findViewById(R.id.temp4);
        temp5 = (TextView) view.findViewById(R.id.temp5);
        temp6 = (TextView) view.findViewById(R.id.temp6);
        text1 = (TextView) view.findViewById(R.id.text1);
        text2 = (TextView) view.findViewById(R.id.text2);
        text3 = (TextView) view.findViewById(R.id.text3);
        text4 = (TextView) view.findViewById(R.id.text4);
        text5 = (TextView) view.findViewById(R.id.text5);
        text6 = (TextView) view.findViewById(R.id.text6);
        textView1 = (TextView) view.findViewById(R.id.textView1);
        textView2 = (TextView) view.findViewById(R.id.textView2);
        textView3 = (TextView) view.findViewById(R.id.textView3);
        textView4 = (TextView) view.findViewById(R.id.textView4);
        textView5 = (TextView) view.findViewById(R.id.textView5);
        textView6 = (TextView) view.findViewById(R.id.textView6);
        setInfo();
        return view;
    }

    private void setInfo() {
        List<WeatherModel> weatherList = AstroTools.getWeatherList(getContext());
        if(weatherList == null)
            return;
        int i = 0;
        for(WeatherModel weather : weatherList) {
            String tempLow = weather.getLowTemp();
            String tempHigh = weather.getHighTemp();
            String text = weather.getText();
            String date = weather.getDate();
            switch(i) {
                case 1:
                    temp1.setText(tempLow + " - " + tempHigh);
                    text1.setText(text);
                    textView1.setText(date);
                    break;
                case 2:
                    temp2.setText(tempLow + " - " + tempHigh);
                    text2.setText(text);
                    textView2.setText(date);
                    break;
                case 3:
                    temp3.setText(tempLow + " - " + tempHigh);
                    text3.setText(text);
                    textView3.setText(date);
                    break;
                case 4:
                    temp4.setText(tempLow + " - " + tempHigh);
                    text4.setText(text);
                    textView4.setText(date);
                    break;
                case 5:
                    temp5.setText(tempLow + " - " + tempHigh);
                    text5.setText(text);
                    textView5.setText(date);
                    break;
                case 6:
                    temp6.setText(tempLow + " - " + tempHigh);
                    text6.setText(text);
                    textView6.setText(date);
                    break;
            }
            i++;
        }
    }

}
