package com.example.adam.astrocalculator.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.adam.astrocalculator.R;
import com.example.adam.astrocalculator.util.AstroTools;

public class MenuActivity extends Activity implements View.OnClickListener {

    private static final String PREFERENCES_NAME = "astroPreferences";
    private static final String PREFERENCES_PLACE = "place";
    private SharedPreferences config;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        config = getSharedPreferences(PREFERENCES_NAME, Activity.MODE_PRIVATE);
        Button astroCalcButton = (Button) findViewById(R.id.mainButton);
        astroCalcButton.setOnClickListener(this);
        Button configButton = (Button) findViewById(R.id.configButton);
        configButton.setOnClickListener(this);
        Button refreshButton = (Button) findViewById(R.id.refreshButton);
        refreshButton.setOnClickListener(this);
        if(checkConfig()) {
            if(!isOnline()) {
                Toast toast = Toast.makeText(getApplicationContext(), "No internet connection! Data could be outdated!", Toast.LENGTH_SHORT);
                toast.show();
            } else {
                AstroTools.shouldRefresh(getApplicationContext());
            }
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mainButton:
                if(checkConfig()) {
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                    break;
                }
            case R.id.configButton:
                Intent intent = new Intent(this, ConfigActivity.class);
                startActivity(intent);
                break;
            case R.id.refreshButton:
                AstroTools.refreshWeatherData(getApplicationContext());
                break;
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private boolean checkConfig() {
        return config.getString(PREFERENCES_PLACE, "").length() > 0;
    }
}
