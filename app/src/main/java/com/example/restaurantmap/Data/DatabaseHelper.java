package com.example.restaurantmap.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.restaurantmap.Model.Restaurant;
import com.example.restaurantmap.Util.Util;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    public DatabaseHelper(@Nullable Context context) {
        super(context, Util.DATABASE_NAME, null, Util.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Query
        String CREATE_RES_TABLE = "CREATE TABLE " + Util.TABLE_NAME + "(" +
                Util.RES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                Util.RES_NAME + " TEXT," +
                Util.RES_LAT + " REAL," +
                Util.RES_LNG + " REAL)";

        // Execute the query above to create the table
        db.execSQL(CREATE_RES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Query to drop table
        String DROP_TABLE = "DROP TABLE IF EXISTS " + Util.DATABASE_NAME;

        // Execute the query above and create a new one (table)
        db.execSQL(DROP_TABLE);
        this.onCreate(db);
    }

    // Method to insert a restaurant to the database table. Returns new row id
    public long insertRestaurant(Restaurant restaurant) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues val = new ContentValues();

        // Put all values to be inserted to ContentValues
        val.put(Util.RES_NAME, restaurant.getName());
        val.put(Util.RES_LAT, restaurant.getLat());
        val.put(Util.RES_LNG, restaurant.getLng());

        long newRowId = db.insert(Util.TABLE_NAME, null, val);
        db.close(); // Close SQLiteDatabase to free up memory/prevent memory leak

        return newRowId;    // Return the newly updated row id
    }

    // Method to get all restaurants from the database table. Returns list of restaurant
    public List<Restaurant> getRestaurants() {
        SQLiteDatabase db = getReadableDatabase();
        List<Restaurant> restaurants = new ArrayList<>();   // The list that will store all restaurants

        // Select all fields from the table
        String QUERY = "SELECT * FROM " + Util.TABLE_NAME;
        Cursor cursor = db.rawQuery(QUERY, null);

        // Get and add each row to the list
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                // Get restaurant id, name, lat, lng from the table
                int id = cursor.getInt(cursor.getColumnIndex(Util.RES_ID));
                String name = cursor.getString(cursor.getColumnIndex(Util.RES_NAME));
                double lat = cursor.getDouble(cursor.getColumnIndex(Util.RES_LAT));
                double lng = cursor.getDouble(cursor.getColumnIndex(Util.RES_LNG));

                // Add it to the list
                restaurants.add(new Restaurant(id, name, lat, lng));

                cursor.moveToNext();
            }
        }

        // Close both db and cursor to free up memory
        db.close(); cursor.close();

        // Return the list
        return restaurants;
    }
}
