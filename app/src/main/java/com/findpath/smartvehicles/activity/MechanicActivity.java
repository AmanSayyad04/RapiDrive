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
import com.findpath.smartvehicles.databinding.ActivityMechanicBinding;
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

public class MechanicActivity extends FragmentActivity implements OnMapReadyCallback
, GoogleMap.OnMapClickListener,
        LocationListener {

    private GoogleMap mMap;
    private @NonNull ActivityMechanicBinding binding;
    private EditText locationSearch;
    private ImageView searchButton;
    private Marker currentUserLocationMarker;


    private List<Marker> customMarkers;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMechanicBinding.inflate(getLayoutInflater());
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


//        ImageView chargingNearby = findViewById(R.id.charging_nearby);
//        chargingNearby.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//
//                showAllCustomMarkers();
//            }
//        });
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
                String mobileNumber = findMobileNumber(markerLatLng);

                // Handle marker click event
                // You can perform actions like showing an info window, opening a new activity, etc.
                Intent intent = new Intent(MechanicActivity.this, Mechanic_info.class);
                intent.putExtra("title", title);
                intent.putExtra("address", address);
                intent.putExtra("latitude", marker.getPosition().latitude);
                intent.putExtra("longitude", marker.getPosition().longitude);
                intent.putExtra("mobileNumber", mobileNumber);
                startActivity(intent);
                // Example: Display a toast with the marker title
                Toast.makeText(MechanicActivity.this, marker.getTitle(), Toast.LENGTH_SHORT).show();

                return true; // Return true to indicate that the click event has been handled
            }
        });

    }

    private String findMobileNumber(LatLng markerLatLng) {
        for (Marker marker : customMarkers) {
            if (marker.getPosition().equals(markerLatLng)) {
                // Assuming you've set the mobile number as the snippet of the marker
                return marker.getSnippet();
            }
        }
        return "";
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
        addCustomMarker(new LatLng(17.69114708	,74.02417668), "Akshada Auto Garage","+91 8805521557");
        addCustomMarker(new LatLng(17.69334744	,74.02432103), "Mobil Car Care - Samarth Garage & Spares","+91 7447792779");
        addCustomMarker(new LatLng(17.70262988	,74.02489843), "VAJRESHWRI AUTO GARAGE","+91 9503300760");
        addCustomMarker(new LatLng(17.6778907	,73.98673199), "ANIKET AUTO GARAGE","+91 7420901319");
        addCustomMarker(new LatLng(17.67928091	,73.98904942), "SHREE GANESH AUTO GARAGE Car Service Station","+91 9975900706");
        addCustomMarker(new LatLng(17.70122478	,73.99387687), "CHINTAMANI AUTO GARAGE","+91 7841812477");
        addCustomMarker(new LatLng(17.67983084	,74.01378366), "MAULI DNYANRAJ AUTO GARAGE","+91 8552034030");

        addCustomMarker(new LatLng(18.45681415	,73.83107813), "Best Car Mechanic & Repair Services - Shree Samarth Car Care","+91 8856980721");
        addCustomMarker(new LatLng(18.46560683	,73.82249506), "Ghare Motors","+91 9822887455");
        addCustomMarker(new LatLng(18.49035417	,73.80155237), "GoMechanic - Auto Assist","+91 8398970970");
        addCustomMarker(new LatLng(18.57140922	,73.77112111), "Auto Solutions","+91 9623333999");
        addCustomMarker(new LatLng(18.58052144	,73.77867422), "GoMechanic - Shetty's Multicar Service","+91 8398970970");
        addCustomMarker(new LatLng(18.60004598	,73.75670156), "The Motor Works : Car Garage Car Service & Repairing Car Denting Painting Car Service Center In Wakad | Hinjewadi","+91 8087020003");
        addCustomMarker(new LatLng(18.47633461	,73.85268508), "SHABARI MOTORS-CAR REPAIR AND SERVICE CAR MECHANIC","+91 9822850838");
        addCustomMarker(new LatLng(18.53208763	,73.86575009), "GoMechanic - Car Breakdown Helps | Towing | Road Side Assistance","+91 8398970970");
        addCustomMarker(new LatLng(18.58163062	,73.83962006), "GoMechanic - Car Service Center Khadki","+91 8398970970");
        addCustomMarker(new LatLng(18.7380774	,73.43261543), "GoMechanic - Automobile Repair Workshop","+91 8398970970");
        addCustomMarker(new LatLng(18.76489814	,73.42420403), "Sai Motors Garage","+91 9637720786");
        addCustomMarker(new LatLng(18.75561804	,73.40619485), "Engineer's Autogarage","+91 9370726441");
        addCustomMarker(new LatLng(18.76122578	,73.41718118), "A1 Car Garage And Mechanic","+91 9767210044");
        addCustomMarker(new LatLng(18.76163213	,73.41829698), "om sai Enterprices","+91 7507427663");
        addCustomMarker(new LatLng(18.78607116	,73.34542605), "Al Baksh Auto Garage","+91 9834789515");
        addCustomMarker(new LatLng(18.79513121	,73.33358142), "Rajesh Auto Garage","+91 8087296363");
        addCustomMarker(new LatLng(19.21361577	,72.9782264), "Royal Car Care-Car Service Center In Thane West-Car Cleaning Services In Thane West-Car Repair/Car AC Repair In Thane West","+91 8929763071");
        addCustomMarker(new LatLng(19.26548253	,72.9620699), "Fix My Car-Car Repair center Near Me-Car Garage Near Thane-Premium Car Audi , VW , BMW, Mercedes Car Repair Services In Thane","+91 9619571110");
        addCustomMarker(new LatLng(19.03984029	,73.0765711), "Sharhols Garage, Kharghar, Navi Mumbai - Car Repair Workshop","+91 8879348168");
        addCustomMarker(new LatLng(19.0570324	,73.06098136), "Complete Car Care","+91 8652865211");
        addCustomMarker(new LatLng(16.7451028	,74.28135399), "Ratnatreya Automobile ","+91 9527652716");
        addCustomMarker(new LatLng(16.70653084	,74.22181338), "SHRI MAHALAXMI AUTO","+91 9822681789");
        addCustomMarker(new LatLng(16.70228614	,74.23742073), "Hindustan Auto","+91 8421712670");
        addCustomMarker(new LatLng(16.71095999	,74.23029144), "Shums Auto Care","+91 9922333011");
        addCustomMarker(new LatLng(17.09838205	,74.24629211), "S M MOTOR GARAGE AND AUTOMOBILE","+91 8830439513");
        addCustomMarker(new LatLng(16.88956321	,74.56626891), "Zhakir Auto Mechanic","+91 7498080601");
        addCustomMarker(new LatLng(16.85145173	,74.59373473), "Siddhivinayak Autoworks","+91 9422408193");
        addCustomMarker(new LatLng(17.4076669	,74.10175581), "Aaba Welding And Repair Works","+91 9881693970");
        addCustomMarker(new LatLng(17.40493494	,74.10209264), "Badshah Auto Garage","+91 9850911562");
        addCustomMarker(new LatLng(17.40340824	,74.10172774), "Shree Jugai Devi Garage","+91 9518535562");
        addCustomMarker(new LatLng(17.40228329	,74.10169967), "Siddhanat Auto Garej","+91 7057673790");
        addCustomMarker(new LatLng(17.29775033	,74.16760428), "Pk car care","+91 8888376562");
        addCustomMarker(new LatLng(17.28121108	,74.17337826), "Shivsai Auto Garage","+91 9960276461");
        addCustomMarker(new LatLng(17.2762989	,74.17952309), "Om Sai Auto","+91 7058356614");
        addCustomMarker(new LatLng(17.27361106	,74.178657), "Shri Sidhnath Auto Service","+91 9359166034");
        addCustomMarker(new LatLng(18.15681133	,73.9631474), "Sahyadri Auto","+91 9766880032");
        addCustomMarker(new LatLng(18.13897242	,73.97875886), "Pawar Garage, shirwal NH 4","+91 7350507256");
        addCustomMarker(new LatLng(18.13113123	,73.98301878), "Trimurti Garage","+91 8856980721");
        addCustomMarker(new LatLng(18.14141956	,73.97493038), "Moraya Auto Guarage","+91 7350112335");
        addCustomMarker(new LatLng(18.44159809	,73.85973842), "Mulani Motors","+91 9422502510");

        addCustomMarker(new LatLng(17.67742372	,73.98659259), "ANIKET AUTO GARAGE","+91 7420901319");
        addCustomMarker(new LatLng(17.67848683	,73.98899585), "SHREE GANESH AUTO GARAGE","+91 9975900706");
        addCustomMarker(new LatLng(17.68012237	,73.98925334), "GURUKRUPA AUTO GARAGE","+91 9850364622");
        addCustomMarker(new LatLng(17.68012237	,73.98749381), "S AUTO GARAGE","+91 8805252787");
        addCustomMarker(new LatLng(17.68257565	,73.98702174), "Shivam Auto Garage","+91 8788283106");
        addCustomMarker(new LatLng(17.68547866	,73.98573428), "JAGDAMB AUTO GARAGE","+91 9890974562");
        addCustomMarker(new LatLng(17.6881363	,73.98749381), "Mahrashtra Auto Garage","+91 8668245102");
        addCustomMarker(new LatLng(17.68895403	,74.00045425), "Harish Garage","+91 9850636343");
        addCustomMarker(new LatLng(17.69303818	,73.9923213), "Suraj Auto Garage","+91 9552411328");
        addCustomMarker(new LatLng(17.69083036	,74.01622515), "Majgaonkar Garage","+91 9422403181");
        addCustomMarker(new LatLng(17.72019052	,74.00474852), "Khan Auto Shop","+91 9975213376");
        addCustomMarker(new LatLng(17.65549189	,73.94704681), "Shivaji Auto Garage","+91 9834901903");
        addCustomMarker(new LatLng(17.65640814	,73.94284105), "SHREE SWAMI SAMARTH AUTO GARAGE","+91 9766554417");
        addCustomMarker(new LatLng(17.66036456	,73.93818408), "Sadguru Krupa Auto Garage","+91 9011775536");
        addCustomMarker(new LatLng(17.66105974	,73.93277675), "Vighnesh Auto garage","+91 9730835017");
        addCustomMarker(new LatLng(17.67030124	,73.92985851), "Shubham Auto Garage","+91 8378039248");
        addCustomMarker(new LatLng(17.373251	,73.92038448), "Sai Samrat Motars","+91 9637249331");
        addCustomMarker(new LatLng(17.37487767	,73.9129445), "AJ AUTOMOBILES","+91 7709887495");
        addCustomMarker(new LatLng(17.37538963	,73.9108631), "Shidhivinayak Auto Garage","+91 9881577462");
        addCustomMarker(new LatLng(17.37257637	,73.90589833), "HEM AGENCY PATAN","+91 8888837655");
        addCustomMarker(new LatLng(17.36960948	,73.89870465), "Koyana Baike Garage","+91 9822773590");
        addCustomMarker(new LatLng(17.3713502	,73.89797509), "Shree Samarth Krupa Auto Garage","+91 8956339278");
        addCustomMarker(new LatLng(17.36799161	,73.89643013), " Dosti Auto Garrage","+91 7558605754");
        addCustomMarker(new LatLng(17.37926131	,73.89774039), "Sagar Auto Garage","+91 9689135106");
        addCustomMarker(new LatLng(17.67447714	,74.03448343), "Om Sai Ram Auto Garage","+91 7248968057");
        addCustomMarker(new LatLng(17.67365934	,74.03791666), "RAJMUDRA AUTO GARAGE","+91 9579362693");
        addCustomMarker(new LatLng(17.69473679	,74.14916933), "Gurukrupa Auto","+91 9822806361");
        addCustomMarker(new LatLng(17.69745563	,74.15317302), "R K MOTORS","+91 7397865027");
        addCustomMarker(new LatLng(17.69837552	,74.15461069), "SHRI DATTA TRACTOR GARAGE","+91 8600398226");
        addCustomMarker(new LatLng(17.69843756	,74.15490891), "Sahyadri Automobiles","+91 9730494030");
        addCustomMarker(new LatLng(17.69905911	,74.15584963), "Shivaparvati Motors","+91 9923035135");
        addCustomMarker(new LatLng(17.6933497	,74.16448871), "BHOSALE GARAGE","+91 9921594632");
        addCustomMarker(new LatLng(17.69702835	,74.16726743), "Yashwant Auto garage","+91 9767628041");
        addCustomMarker(new LatLng(17.70184143	,74.16553524), "N.N.Dias Motor Garage, Koregaon","+91 9028407308");
        addCustomMarker(new LatLng(17.70191019	,74.16683439), "Shivshakti Auto Garage","+91 9923477450");
        addCustomMarker(new LatLng(17.70246025	,74.17322185), "Vaishanavi Auto Garage","+91 7517990884");
        addCustomMarker(new LatLng(17.70406885	,74.17532352), "Lucky Auto Garage and Servicing Cente","+91 9623182312");
        addCustomMarker(new LatLng(17.27642477	,74.18405007), "BABA Auto Garage","+91 9096027717");
        addCustomMarker(new LatLng(17.27757218	,74.18233346), "JYORTILING AUTO GARAGE","+91 8007541170");
        addCustomMarker(new LatLng(17.28093242	,74.19228982), "Royal Auto Garage","+91 9172248693");
        addCustomMarker(new LatLng(17.28091878	,74.19044795), "Super Auto Garage","+91 7758019539");
        addCustomMarker(new LatLng(17.98247702	,74.42967423), "Shree krushna Auto Garage","+91 7972626526");
        addCustomMarker(new LatLng(17.99450353	,74.43583914), "Ring Road automobiles and garage","+91 7020438181");
        addCustomMarker(new LatLng(17.98847326	,74.44010914), "Ghorpade Garege","+91 8767644851");
        addCustomMarker(new LatLng(17.99241481	,74.44030529), "kanifnath auto garage","+91 9552455059");
        addCustomMarker(new LatLng(17.99253923	,74.44046932), "Jay Ganesh Auto Garage","+91 9689409873");
        addCustomMarker(new LatLng(17.99344116	,74.44077278), "Samarth Auto Garage","+91 7745017657");
        addCustomMarker(new LatLng(17.99340464	,74.44448643), "Shiv Auto Garaj","+91 9552110054");
        addCustomMarker(new LatLng(17.66366018	,75.89732334), "Prakash motor garage","+91 9423264041");
        addCustomMarker(new LatLng(17.67007921	,75.89720196), "Imran Auto Garage","+91 9130900506");
        addCustomMarker(new LatLng(17.67036835	,75.90072207), "City Garage","+91 9730362775");
        addCustomMarker(new LatLng(17.68725332	,75.8947743), "Shri Ganesh Motor Garage","+91 9860153893");
        addCustomMarker(new LatLng(17.58307689	,74.2093667), "Shrikushna Auto Garage And Car Wash","+91 9975998019");
        addCustomMarker(new LatLng(17.58487694	,74.20387354), "Puna car cear Rahimatpur","+91 9552303868");
        addCustomMarker(new LatLng(17.58585877	,74.20219984), "National Tyre Works & Truck Garage","+91 7385923504");
        addCustomMarker(new LatLng(17.58700424	,74.20099821), "Mirage Garage","+91 8080914894");
        addCustomMarker(new LatLng(17.58536786	,74.19971075), "Harun Motar Garage","+91 9860290652");



        showAllCustomMarkers();
    }

    private void addCustomMarker(LatLng latLng, String title, String mobile) {
        Bitmap customMarkerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.mech_ic);
        BitmapDescriptor customMarkerIcon = BitmapDescriptorFactory.fromBitmap(customMarkerBitmap);

        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(title)
                .snippet(mobile)
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
