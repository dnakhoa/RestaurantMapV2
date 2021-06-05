package com.example.restaurantmap;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.restaurantmap.Data.DatabaseHelper;
import com.example.restaurantmap.Model.Restaurant;
import com.example.restaurantmap.Util.Util;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.restaurantmap.databinding.ActivityMapsBinding;

import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private String name;
    private double lat, lng;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Get intent extras (name, latitude, longitude)
        intent = getIntent();
        name = intent.getStringExtra(Util.KEY_RES_NAME);                // location name
        lat = intent.getDoubleExtra(Util.KEY_RES_LAT, 0);   // location latitude
        lng = intent.getDoubleExtra(Util.KEY_RES_LNG, 0);   // location longitude
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Check whether there is an extra with key Util.KEY_SHOW_ALL_RES
        // If true, then show all markers in the map. Else, just show one marker
        if (intent.hasExtra(Util.KEY_SHOW_ALL_RES)) {
            DatabaseHelper db = new DatabaseHelper(this);
            List<Restaurant> restaurants = db.getRestaurants();     // Get all restaurants and store it in the list

            // If there is no saved location, tell the user
            if (restaurants.size() == 0)
                Toast.makeText(MapsActivity.this, "No saved location. Please add one", Toast.LENGTH_SHORT).show();
            else {
                // Iterate through the list and add marker for each restaurant
                for (int i = 0; i < restaurants.size(); i++) {
                    LatLng restaurant = new LatLng(restaurants.get(i).getLat(), restaurants.get(i).getLng());
                    mMap.addMarker(new MarkerOptions().position(restaurant).title(restaurants.get(i).getName()));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(restaurant, 10));
                }
            }
        }
        else {
            // Create the marker and show it on the map
            LatLng restaurant = new LatLng(lat, lng);
            mMap.addMarker(new MarkerOptions().position(restaurant).title(name));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(restaurant, 15));
        }
    }
}