package com.findpath.smartvehicles.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.findpath.smartvehicles.R;
import com.findpath.smartvehicles.databinding.ActivityNavigatorBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class NavigatorActivity extends FragmentActivity implements
        OnMapReadyCallback,
        GoogleMap.OnMapClickListener,
        LocationListener {

    private GoogleMap mMap;
    private ActivityNavigatorBinding binding;
    private EditText locationSearch;
    private ImageView searchButton;
    private Marker currentUserLocationMarker;


    private List<Marker> customMarkers;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityNavigatorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        locationSearch = findViewById(R.id.location_search);
        searchButton = findViewById(R.id.search_address);

        customMarkers = new ArrayList<>();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchLocation();
            }
        });


        ImageView chargingNearby = findViewById(R.id.charging_nearby);
        chargingNearby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                showAllCustomMarkers();
            }
        });
    }

    private void requestLocationUpdates() {
        // Check if location permission is granted
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (mMap != null) {
                mMap.setMyLocationEnabled(true);
                mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                    @Override
                    public boolean onMyLocationButtonClick() {
                        return false; // Return false so that default behavior (centering on location) works as well.
                    }
                });
                mMap.setOnMyLocationClickListener(new GoogleMap.OnMyLocationClickListener() {
                    @Override
                    public void onMyLocationClick(@NonNull Location location) {
                        LatLng myLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                        addMarker(myLatLng, "My Location");
                        moveCamera(myLatLng);
                    }
                });
            } else {
                Toast.makeText(this, "Map not available", Toast.LENGTH_SHORT).show();
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    private void getCurrentLocation() {
        // Check if location permission is granted
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Get last known location
            mMap.setMyLocationEnabled(true);
            mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {
                    return false; // Return false so that default behavior (centering on location) works as well.
                }
            });
            mMap.setOnMyLocationClickListener(new GoogleMap.OnMyLocationClickListener() {
                @Override
                public void onMyLocationClick(@NonNull Location location) {
                    LatLng myLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    addMarker(myLatLng, "My Location");
                    moveCamera(myLatLng);
                }
            });
        } else {
            // If permission is not granted, request it
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    private void searchLocation() {
        String address = locationSearch.getText().toString();

        if (!address.isEmpty()) {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());

            try {
                List<android.location.Address> addressList = geocoder.getFromLocationName(address, 1);

                if (!addressList.isEmpty()) {
                    android.location.Address foundAddress = addressList.get(0);
                    LatLng latLng = new LatLng(foundAddress.getLatitude(), foundAddress.getLongitude());
                    addMarker(latLng, address);
                    moveCamera(latLng);
                } else {
                    Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Please enter some text", Toast.LENGTH_SHORT).show();
        }
    }

    private void addMarker(LatLng latLng, String title) {
        if (currentUserLocationMarker != null) {
            currentUserLocationMarker.remove();
        }

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title(title);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        currentUserLocationMarker = mMap.addMarker(markerOptions);
    }

    private void moveCamera(LatLng latLng) {
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
    }

    private void showAllCustomMarkers() {
        for (Marker marker : customMarkers) {
            marker.setVisible(true);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Request location updates only when the map is ready
        requestLocationUpdates();

        // Automatically move to the user's current location
        getCurrentLocation();

        addCustomMarkers();

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                String title = marker.getTitle();
                LatLng markerLatLng = marker.getPosition();
                String address = getAddressFromLatLng(markerLatLng);
                // Handle marker click event
                // You can perform actions like showing an info window, opening a new activity, etc.
                Intent intent = new Intent(NavigatorActivity.this, DetailsActivity.class);
                intent.putExtra("title", title);
                intent.putExtra("address", address);
                startActivity(intent);
                // Example: Display a toast with the marker title
                Toast.makeText(NavigatorActivity.this, marker.getTitle(), Toast.LENGTH_SHORT).show();

                return true; // Return true to indicate that the click event has been handled
            }
        });

    }

    private String getAddressFromLatLng(LatLng latLng) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        try {
            List<android.location.Address> addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (!addressList.isEmpty()) {
                android.location.Address address = addressList.get(0);
                return address.getAddressLine(0); // You can customize this based on your needs
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "Address not found";
    }

    private void addCustomMarkers() {
        // Load custom marker icons from resources and add custom markers
        // ...

        // Example:
        addCustomMarker(new LatLng(18.538, 73.8485), "Synergy Solutions AC Charging Station 2");
        addCustomMarker(new LatLng(18.763, 73.4252), "Lonavla Wax Museum DC Charging Station");
        addCustomMarker(new LatLng(17.0749 , 74.2227), "Tellus Power DC Charging Station");
        addCustomMarker(new LatLng(16.8433 , 74.5859), "Ashnni Pilot Circle Station");
        addCustomMarker(new LatLng(18.5887678 , 73.7825231), "CopaCabana");
        addCustomMarker(new LatLng(18.9284545096609 , 72.8320303138997), "Kala Ghoda Cafe");
        addCustomMarker(new LatLng(18.11289 , 73.98985), "Vikarsh Nano technology, Dhangarwadi");
        addCustomMarker(new LatLng(17.92564438 , 73.65552014), "Hotel Dreamland, ST Main Market Mahabaleshwar");
        addCustomMarker(new LatLng(17.92655754 , 73.69586565), "The Grand Legacy, Panchgani-Mahabaleshwar Road");
        addCustomMarker(new LatLng(16.6869187 , 74.2703805), "McDonalds");
        addCustomMarker(new LatLng(17.63820763 , 74.01209174), "Amrai Hotel & Resort , NH 4 Shendre");
        addCustomMarker(new LatLng(19.25612 , 72.97145), "Heritage Motors, Ghodbunder");
        addCustomMarker(new LatLng(19.1972 , 72.96321), "Heritage Motors, Panchpakhadi");
        addCustomMarker(new LatLng(19.277922 , 72.880256), "Inderjit Cars, Mira Road");
        addCustomMarker(new LatLng(17.695980908204717, 74.01587009526581), "Kazam Charging Station");
        addCustomMarker(new LatLng(17.684123951030173, 74.02222156620564), "Electric Vehicle Charging Station");
        addCustomMarker(new LatLng(17.679626279824976, 74.02179241276376), "15A, Pune - Bengaluru Hwy");
        addCustomMarker(new LatLng(16.6869187 , 74.2703805), "E-Fill Charging Station");
        addCustomMarker(new LatLng(17.951666402421566, 73.93468913858099), "Electric Vehicle Charging Station");
        addCustomMarker(new LatLng(17.639396516542654, 74.01241593064613), "TATA Charging Station");
        addCustomMarker(new LatLng(17.76805895789883, 73.98867899995774), "Electric Vehicle Charging Station");
        addCustomMarker(new LatLng(17.694930378913735, 74.01227633858099), "PHENIX ELECTRIC VEHICLE SERVICE CENTRE");
        addCustomMarker(new LatLng(17.679311649467895, 74.02188937567911), "Electric Vehicle Charging Station");
        addCustomMarker(new LatLng(17.443403075597374, 74.09429083064612), "E-Fill Charging Station");

        // Add more custom markers as needed
    }

    private void addCustomMarker(LatLng latLng, String title) {
        Bitmap customMarkerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.charg_ic);
        BitmapDescriptor customMarkerIcon = BitmapDescriptorFactory.fromBitmap(customMarkerBitmap);

        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(title)
                .visible(false)
                .icon(customMarkerIcon));

        customMarkers.add(marker);
    }



    @Override
    public void onMapClick(LatLng latLng) {
        // Handle map click events, if needed
    }

    @Override
    public void onLocationChanged(@NonNull android.location.Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        addMarker(latLng, "Current Location");
        moveCamera(latLng);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            requestLocationUpdates();
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }
}
