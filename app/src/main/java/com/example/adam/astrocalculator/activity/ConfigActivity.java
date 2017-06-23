package com.example.adam.astrocalculator.activity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.adam.astrocalculator.R;
import com.example.adam.astrocalculator.async.PlaceWeatherLoad;
import com.example.adam.astrocalculator.db.AstroDb;
import com.example.adam.astrocalculator.model.PlaceModel;
import com.example.adam.astrocalculator.util.AstroStatuses;
import com.example.adam.astrocalculator.util.AstroTools;
import com.google.gson.Gson;

import java.util.List;

public class ConfigActivity extends Activity implements View.OnClickListener  {

    private static final String PREFERENCES_NAME = "astroPreferences";
    private static final String PREFERENCES_PLACE = "place";
    private static final String PREFERENCES_UNIT = "unit";
    private static final String GET_PLACE = "place";
    private static final String CONFIG_TAG = "CONFIG";
    private SharedPreferences config;

    private EditText placeText;
    private Spinner placeSpinner;
    private Spinner unitSpinner;
    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        config = getSharedPreferences(PREFERENCES_NAME, Activity.MODE_PRIVATE);
        Button okButton = (Button) findViewById(R.id.okButton);
        okButton.setOnClickListener(this);
        Button addButton = (Button) findViewById(R.id.addButton);
        addButton.setOnClickListener(this);
        Button removeButton = (Button) findViewById(R.id.removeButton);
        removeButton.setOnClickListener(this);
        placeText = (EditText) findViewById(R.id.placeText);;
        placeSpinner = (Spinner) findViewById(R.id.placeSpinner);
        unitSpinner = (Spinner) findViewById(R.id.unitSpinner);
        context = getApplicationContext();
        loadConfig();
    }

    private void loadConfig() {
        setPlaceSpinner();
        setUnitSpinner();
    }

    private void saveConfig() {
        savePlace((PlaceModel) placeSpinner.getSelectedItem());
        saveUnit();
    }

    @Override
    protected void onPause() {
        saveConfig();
        AstroTools.refreshWeatherData(context);
        super.onPause();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.okButton:
                finish();
                break;
            case R.id.addButton:
                addPlace();
                break;
            case R.id.removeButton:
                removePlace();
                break;
        }
    }

    private void addPlace() {
        String place = placeText.getText().toString();
        if(place.isEmpty()) {
            toast("You have to type something!");
            return;
        }
        try {
            int result = new PlaceWeatherLoad(context).execute(GET_PLACE, place).get();
            if(result != AstroStatuses.OK)
                toast(AstroTools.getToastMsg(result));
            else {
                refreshPlaceSpinner();
                placeText.setText("");
                toast("Success!");
            }
        } catch(Exception e) {
            toast(AstroTools.getToastMsg(AstroStatuses.ERROR));
            Log.d(CONFIG_TAG, e.toString());
        }
    }

    private void removePlace() {
        PlaceModel toRemove = (PlaceModel) placeSpinner.getSelectedItem();
        if(toRemove == null)
            return;
        try {
            AstroDb astro = new AstroDb(context);
            astro.open();
            boolean result = astro.deletePlace(toRemove.getWoeid());
            astro.close();
            if(result) {
                refreshPlaceSpinner();
                toast("Success!");
            } else {
                toast(AstroTools.getToastMsg(AstroStatuses.ERROR));
            }
        } catch(Exception e) {
            toast(AstroTools.getToastMsg(AstroStatuses.ERROR));
            Log.d(CONFIG_TAG, e.toString());
        }
    }

    private void toast(String msg) {
        Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        toast.show();
    }

    private void savePlace(PlaceModel place) {
        SharedPreferences.Editor preferencesEditor = config.edit();
        String jsonPlace = "";
        if(place != null) {
            Gson gson = new Gson();
            jsonPlace = gson.toJson(place);
        }
        preferencesEditor.putString(PREFERENCES_PLACE, jsonPlace);
        preferencesEditor.apply();
    }

    private void saveUnit() {
        int position = unitSpinner.getSelectedItemPosition();
        if (position == 0 || position == 1) {
            SharedPreferences.Editor preferencesEditor = config.edit();
            preferencesEditor.putInt(PREFERENCES_UNIT, position);
            preferencesEditor.apply();
        }
    }

    private void setPlaceSpinner() {
        AstroDb astro = new AstroDb(context);
        astro.open();
        List<PlaceModel> placeModelList = astro.getAllPlaces();
        astro.close();
        ArrayAdapter<PlaceModel> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, placeModelList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        placeSpinner.setAdapter(dataAdapter);
        PlaceModel currentPlace =  AstroTools.getCurrentPlace(context);
        for(int i = 0; i < dataAdapter.getCount(); i++) {
            if(dataAdapter.getItem(i).getWoeid().equals(currentPlace.getWoeid())) {
                placeSpinner.setSelection(i);
                break;
            }
        }
    }

    private void refreshPlaceSpinner() {
        int currentPosition = placeSpinner.getSelectedItemPosition();
        AstroDb astro = new AstroDb(context);
        astro.open();
        List<PlaceModel> placeModelList = astro.getAllPlaces();
        astro.close();
        ArrayAdapter<PlaceModel> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, placeModelList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        placeSpinner.setAdapter(dataAdapter);
        if(currentPosition < dataAdapter.getCount())
            placeSpinner.setSelection(currentPosition);
    }

    private void setUnitSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.unit_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        unitSpinner.setAdapter(adapter);
        unitSpinner.setSelection(config.getInt(PREFERENCES_UNIT, 0));
    }
}
