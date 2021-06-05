package com.example.restaurantmap;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.restaurantmap.Util.Util;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }

    public void addPlace(View view) {
        // Create new intent to start AddPlaceActivity
        Intent intent = new Intent(HomeActivity.this, AddPlaceActivity.class);
        startActivity(intent);
    }

    public void showAllPlaces(View view) {
        // Create new intent to start MapsActivity to show the map
        Intent intent = new Intent(HomeActivity.this, MapsActivity.class);
        intent.putExtra(Util.KEY_SHOW_ALL_RES, true);
        startActivity(intent);
    }
}