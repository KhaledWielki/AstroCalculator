package com.example.adam.astrocalculator.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.adam.astrocalculator.model.PlaceModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Adam on 2017-06-23.
 */

public class AstroDb {
    private static final String DEBUG_TAG = "AstroDb";

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "database.db";
    private static final String DB_PLACE_TABLE = "place";

    public static final String KEY_ID = "id";
    public static final String ID_OPTIONS = "INTEGER PRIMARY KEY AUTOINCREMENT";
    public static final int ID_COLUMN = 0;
    public static final String KEY_WOEID = "woeid";
    public static final String WOEID_OPTIONS = "TEXT NOT NULL UNIQUE";
    public static final int WOEID_COLUMN = 1;
    public static final String KEY_CONTENT = "content";
    public static final String CONTENT_OPTIONS = "TEXT NOT NULL";
    public static final int CONTENT_COLUMN = 2;
    public static final String KEY_LATITUDE = "latitude";
    public static final String LATITUDE_OPTIONS = "TEXT NOT NULL";
    public static final int LATITUDE_COLUMN = 3;
    public static final String KEY_LONGITUDE = "longitude";
    public static final String LONGITUDE_OPTIONS = "TEXT NOT NULL";
    public static final int LONGITUDE_COLUMN = 4;
    public static final String KEY_TIMEZONE = "timeZone";
    public static final String TIMEZONE_OPTIONS = "TEXT NOT NULL";
    public static final int TIMEZONE_COLUMN = 4;

    private static final String DB_CREATE_PLACE_TABLE =
            "CREATE TABLE " + DB_PLACE_TABLE + "( " +
                    KEY_ID + " " + ID_OPTIONS + ", " +
                    KEY_WOEID + " " + WOEID_OPTIONS + ", " +
                    KEY_CONTENT + " " + CONTENT_OPTIONS + ", " +
                    KEY_LATITUDE + " " + LATITUDE_OPTIONS + ", " +
                    KEY_LONGITUDE + " " + LONGITUDE_OPTIONS + ", " +
                    KEY_TIMEZONE + " " + TIMEZONE_OPTIONS +
                    ");";

    private static final String DROP_TODO_TABLE =
            "DROP TABLE IF EXISTS " + DB_CREATE_PLACE_TABLE;

    private SQLiteDatabase db;
    private Context context;
    private DatabaseHelper dbHelper;

    private static class DatabaseHelper extends SQLiteOpenHelper {
        public DatabaseHelper(Context context, String name,
                              SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DB_CREATE_PLACE_TABLE);

            Log.d(DEBUG_TAG, "Database creating...");
            Log.d(DEBUG_TAG, "Table " + DB_PLACE_TABLE + " ver." + DB_VERSION + " created");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(DROP_TODO_TABLE);

            Log.d(DEBUG_TAG, "Database updating...");
            Log.d(DEBUG_TAG, "Table " + DB_PLACE_TABLE + " updated from ver." + oldVersion + " to ver." + newVersion);
            Log.d(DEBUG_TAG, "All data is lost.");

            onCreate(db);
        }
    }

    public AstroDb(Context context) {
        this.context = context;
    }

    public AstroDb open(){
        dbHelper = new DatabaseHelper(context, DB_NAME, null, DB_VERSION);
        try {
            db = dbHelper.getWritableDatabase();
        } catch (SQLException e) {
            db = dbHelper.getReadableDatabase();
        }
        return this;
    }

    public void close() {
        dbHelper.close();
    }


    public boolean insertPlace(PlaceModel place) {
        ContentValues contentValues = getContentValues(place);
        return db.insert(DB_PLACE_TABLE, null, contentValues) > 0;
    }

    public boolean deletePlace(String woeid){
        String where = KEY_WOEID + "=" + woeid;
        return db.delete(DB_PLACE_TABLE, where, null) > 0;
    }

    public List<PlaceModel> getAllPlaces() {
        List<PlaceModel> places = new ArrayList<>();
        Cursor c = db.rawQuery("SELECT * FROM " + DB_PLACE_TABLE, null);
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            int id = c.getInt(ID_COLUMN);
            String woeid = c.getString(WOEID_COLUMN);
            String content = c.getString(CONTENT_COLUMN);
            String latitude = c.getString(LATITUDE_COLUMN);
            String longitude = c.getString(LONGITUDE_COLUMN);
            String timeZone = c.getString(TIMEZONE_COLUMN);
            places.add(new PlaceModel(id, woeid, content, latitude, longitude, timeZone));
        }
        c.close();
        return places;
    }

    private ContentValues getContentValues(PlaceModel place) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_WOEID, place.getWoeid());
        contentValues.put(KEY_CONTENT, place.getContent());
        contentValues.put(KEY_LATITUDE, place.getLatitude());
        contentValues.put(KEY_LONGITUDE, place.getLongitude());
        contentValues.put(KEY_TIMEZONE, place.getTimeZone());
        return contentValues;
    }

}
