package com.example.restaurantmap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.restaurantmap.Data.DatabaseHelper;
import com.example.restaurantmap.Model.Restaurant;
import com.example.restaurantmap.Util.Util;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class AddPlaceActivity extends AppCompatActivity {
    private double lat, lng;
    private EditText nameEditText, locationEditText;

    public static final int AC_REQ_CODE = 14;
    public static final int LOC_PERM_REQ_CODE = 15;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_place);

        nameEditText = findViewById(R.id.nameEditText);
        locationEditText = findViewById(R.id.locationEditText);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AC_REQ_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                locationEditText.setText(place.getName());  // Set locationEditText text to the name of the location
                lat = place.getLatLng().latitude;           // Set lat to the latitude of the location
                lng = place.getLatLng().longitude;          // Set lng to the longitude of the location
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // Show error message (toast) if something goes wrong
                Status status = Autocomplete.getStatusFromIntent(data);
                Toast.makeText(AddPlaceActivity.this, status.getStatusMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    // OnClick method that will be called when clicking the location edittext. New intent will be started
    public void openPlacesAutocomplete(View view) {
        // Initialise the Places SDK & create a new Places client instance
        Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));
        PlacesClient pc = Places.createClient(this);

        // Start the auto complete intent
        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.OVERLAY,
                Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG))
                .setCountries(Arrays.asList("AU"))
                .build(this);
        startActivityForResult(intent, AC_REQ_CODE);
    }

    // OnClick method that will be called when clicking the getLocButton. Will ask permission for using gps
    public void getCurrentLocation(View view) {
        // Declare and initialise FusedLocationProviderClient
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);

        // Start by checking whether the app has permission. Ask if the app has no required permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AddPlaceActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOC_PERM_REQ_CODE);
            return;
        }

        // Get the current location
        client.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                if (location != null) {
                    try {
                        Geocoder geocoder = new Geocoder(AddPlaceActivity.this, Locale.getDefault());
                        // Initialise address list
                        List<Address> addressList = geocoder.getFromLocation(location.getLatitude(),
                                location.getLongitude(), 1);

                        // Set both latitude and longitude from current location
                        lat = addressList.get(0).getLatitude();
                        lng = addressList.get(0).getLongitude();

                        // Set location edit text box text to current location
                        locationEditText.setText(addressList.get(0).getAddressLine(0));
                    } catch (IOException e) {
                        Toast.makeText(AddPlaceActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    // OnClick method that will be called when clicking the showMapButton. Will open Google Maps
    public void showLocation(View view) {
        // Get the value from the nameEditText (name of the restaurant) and the locationEditText (location of the restaurant)
        String name = nameEditText.getText().toString();
        String location = locationEditText.getText().toString();

        // Check whether the restaurant name or location are empty
        if (name.equals("") || location.equals(""))
            Toast.makeText(AddPlaceActivity.this, "Please enter the name and the location", Toast.LENGTH_SHORT).show();
        else {
            Intent intent = new Intent(AddPlaceActivity.this, MapsActivity.class);

            // Put extras to the intent (latitude and longitude for the maps) and then start activity
            intent.putExtra(Util.KEY_RES_NAME, name);
            intent.putExtra(Util.KEY_RES_LAT, lat);
            intent.putExtra(Util.KEY_RES_LNG, lng);
            startActivity(intent);
        }
    }

    // OnClick method that will be called when clicking the saveButton. Will insert the restaurant to the database
    public void saveRestaurant(View view) {
        // Get the value from the nameEditText (name of the restaurant) and the locationEditText (location of the restaurant)
        String name = nameEditText.getText().toString();
        String location = locationEditText.getText().toString();

        // Check whether the restaurant name or location are empty
        if (name.equals("") || location.equals(""))
            Toast.makeText(AddPlaceActivity.this, "Please enter the name and the location", Toast.LENGTH_SHORT).show();
        else {
            // Declare and initialise database
            DatabaseHelper db = new DatabaseHelper(this);

            // Create new Restaurant object, passing name, latitude and longitude to the constructor
            Restaurant restaurant = new Restaurant(name, lat, lng);

            // Insert the object to the database
            long result = db.insertRestaurant(restaurant);

            // Show toast to notify whether the restaurant has been successfully saved. result > 0 means success
            if (result > 0) {
                Toast.makeText(AddPlaceActivity.this, "Restaurant saved", Toast.LENGTH_SHORT).show();
                finish();   // Since it's saved, close/finish this activity
            } else Toast.makeText(AddPlaceActivity.this, "Error: failed to save the restaurant",
                    Toast.LENGTH_SHORT).show();
        }
    }
}