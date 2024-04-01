package com.findpath.smartvehicles.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.findpath.smartvehicles.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class OwnerPage extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;

    private EditText editTextChargingStationName, editTextLatitude, editTextLongitude, editTextWhatsAppNumber, editTextEmail, editTextSlotNumber, editTextPricePerUnit;
    private Spinner spinnerOptions;
    private FirebaseFirestore db;
    //private DatabaseReference mDatabase;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private Button submit,addSlotButton, btnFinish ;
    private ImageView map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_page);

        // Initialize Firesto
        db = FirebaseFirestore.getInstance();
        //mDatabase = FirebaseDatabase.getInstance().getReference();

        // Initialize EditText fields
        editTextChargingStationName = findViewById(R.id.editTextChargingStationName);
        editTextLatitude = findViewById(R.id.editTextLatitude);
        editTextLongitude = findViewById(R.id.editTextLongitude);
        editTextWhatsAppNumber = findViewById(R.id.editTextWhatsAppNumber);
        editTextEmail = findViewById(R.id.editTextEmail);
        submit = findViewById(R.id.buttonSubmit);
        editTextSlotNumber = findViewById(R.id.editTextSlotNumber);
        editTextPricePerUnit = findViewById(R.id.editTextPricePerUnit);
        spinnerOptions = findViewById(R.id.spinnerOptions);
        addSlotButton = findViewById(R.id.buttonAddSlot);
        btnFinish = findViewById(R.id.btnFinish);

        map = findViewById(R.id.imageViewLocationButton);

        Spinner spinnerOptions = findViewById(R.id.spinnerOptions);
        spinnerOptions.setSelection(1); // Replace DEFAULT_SELECTION_INDEX with the index of the default selection (0 for Option 1, 1 for Option 2, and so on)


        // Initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Call method to submit charging station data when button is clicked
       submit.setOnClickListener(view -> submitChargingStationData());
        Toast.makeText(this, "id"+ chargingStationId, Toast.LENGTH_SHORT).show();
        Log.d("SlotInfo", "Charging Station ID: " + chargingStationId);
        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle the click event for the Finish button here
                Intent intent = new Intent(OwnerPage.this, SlotInfo.class);
                intent.putExtra("chargingStationId", chargingStationId);
                startActivity(intent);
            }
        });

        addSlotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Call addSlot() method with the chargingStationId obtained previously
                addSlot(chargingStationId);
            }
        });


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            // Retrieve chargingStationId
            chargingStationId = extras.getString("chargingStationId");
            // Check if hideFields flag is set
            if (extras.getBoolean("hideFields", false)) {
                // Hide fields and buttons if the flag is set
                hideFields();
            } else {
                // Show fields and buttons if the flag is not set
                showSlotFields();
            }
        }


//        Button addSlotButton = findViewById(R.id.buttonAddSlot);
//        addSlotButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                addSlot();
//            }
//        });

        // Set up location callback
        setupLocationCallback();
    }

//    private void addSlot() {
//        String slotNumber = editTextSlotNumber.getText().toString().trim();
//        String pricePerUnit = editTextPricePerUnit.getText().toString().trim();
//        String selectedOption = spinnerOptions.getSelectedItem().toString();
//
//        // Check if any of the fields are empty
//        if (slotNumber.isEmpty() || pricePerUnit.isEmpty()) {
//            Toast.makeText(this, "Please fill in all fields for the slot", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        // Create a new Slot object
//        Slot slot = new Slot(slotNumber, pricePerUnit, selectedOption);
//
//        // Add the slot data to Firebase
//        db.collection("slots")
//                .add(slot)
//                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
//                    @Override
//                    public void onComplete(@NonNull Task<DocumentReference> task) {
//                        if (task.isSuccessful()) {
//                            Toast.makeText(OwnerPage.this, "Slot added successfully", Toast.LENGTH_SHORT).show();
//                            // Optionally, clear the EditText fields after submission
//                            editTextSlotNumber.getText().clear();
//                            editTextPricePerUnit.getText().clear();
//                        } else {
//                            Toast.makeText(OwnerPage.this, "Failed to add slot", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//    }

    @Override
    protected void onResume() {
        super.onResume();
        if (checkLocationPermission() && locationCallback != null) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    private boolean checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request location permissions
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            return false;
        }
        return true;
    }

    private void setupLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    for (Location location : locationResult.getLocations()) {
                        // Update latitude and longitude EditText fields with current location
                        editTextLatitude.setText(String.valueOf(location.getLatitude()));
                        editTextLongitude.setText(String.valueOf(location.getLongitude()));
                        // Stop location updates after obtaining the current location
                        stopLocationUpdates();
                    }
                }
            }
        };
    }

    private void startLocationUpdates() {
        // Check for location permissions
        if (checkLocationPermission()) {
            // Create location request
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(10000); // Update location every 10 seconds

            // Start location updates
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
        }
    }


    private void stopLocationUpdates() {
        // Stop location updates
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    private String chargingStationId; // Class variable to store the ID of the charging station document

    private void submitChargingStationData() {
        // Retrieve data from EditText fields
        String chargingStationName = editTextChargingStationName.getText().toString().trim();
        String latitude = editTextLatitude.getText().toString().trim();
        String longitude = editTextLongitude.getText().toString().trim();
        String whatsappNumber = editTextWhatsAppNumber.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();

        // Check if any of the fields are empty
        if (chargingStationName.isEmpty() || latitude.isEmpty() || longitude.isEmpty() || whatsappNumber.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the current user's ID
        String ownerId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Create a new ChargingStation object
        Map<String, Object> chargingStation = new HashMap<>();
        chargingStation.put("chargingStationName", chargingStationName);
        chargingStation.put("latitude", latitude);
        chargingStation.put("longitude", longitude);
        chargingStation.put("whatsappNumber", whatsappNumber);
        chargingStation.put("email", email);

        // Add the charging station data to Firestore under the owner's node
        db.collection("owners").document(ownerId).collection("chargingStations").add(chargingStation)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(OwnerPage.this, "Charging station data submitted successfully", Toast.LENGTH_SHORT).show();

                            // Optionally, clear the EditText fields after submission
                            clearFields();
                            // Hide all fields except for slot fields
                            hideFields();
                            // Show slot fields
                            showSlotFields();
                            // Set chargingStationId before calling addSlot()
                            chargingStationId = task.getResult().getId();

                            // Call addSlot() method with the chargingStationId obtained previously
                            addSlot(chargingStationId);
                        } else {
                            Toast.makeText(OwnerPage.this, "Failed to submit charging station data", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void addSlot(String chargingStationId) {
        // Check if chargingStationId is null
        if (chargingStationId == null) {
            Toast.makeText(this, "Charging station ID is null", Toast.LENGTH_SHORT).show();
            return;
        }

        // Retrieve slot data from EditText and Spinner
        String slotNumber = editTextSlotNumber.getText().toString().trim();
        String pricePerUnit = editTextPricePerUnit.getText().toString().trim();
        String selectedOption = spinnerOptions.getSelectedItem().toString();

        // Check if any of the slot fields are empty
        if (slotNumber.isEmpty() || pricePerUnit.isEmpty() || selectedOption.isEmpty()) {
            Toast.makeText(this, "Please fill in all slot fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a new Slot object
        Map<String, Object> slot = new HashMap<>();
        slot.put("slotNumber", slotNumber);
        slot.put("pricePerUnit", pricePerUnit);
        slot.put("selectedOption", selectedOption);

        // Add the slot data to Firestore under the charging station document
        db.collection("owners").document(chargingStationId).collection("slots").add(slot)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(OwnerPage.this, "Slot added successfully", Toast.LENGTH_SHORT).show();
                            // Optionally, clear the EditText fields after adding the slot
                            editTextSlotNumber.getText().clear();
                            editTextPricePerUnit.getText().clear();
                            btnFinish.setVisibility(View.VISIBLE);
                        } else {
                            Toast.makeText(OwnerPage.this, "Failed to add slot", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }






    // Method to clear all EditText fields
    private void clearFields() {
        editTextChargingStationName.getText().clear();
        editTextLatitude.getText().clear();
        editTextLongitude.getText().clear();
        editTextWhatsAppNumber.getText().clear();
        editTextEmail.getText().clear();
    }

    // Method to hide all EditText fields and button
    private void hideFields() {
        editTextChargingStationName.setVisibility(View.GONE);
        editTextLatitude.setVisibility(View.GONE);
        editTextLongitude.setVisibility(View.GONE);
        editTextWhatsAppNumber.setVisibility(View.GONE);
        editTextEmail.setVisibility(View.GONE);
        submit.setVisibility(View.GONE);
        map.setVisibility(View.GONE);

    }

    private void showSlotFields() {
        editTextSlotNumber.setVisibility(View.VISIBLE);
        editTextPricePerUnit.setVisibility(View.VISIBLE);
        spinnerOptions.setVisibility(View.VISIBLE);
        addSlotButton.setVisibility(View.VISIBLE);
    }




    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, start location updates
                if (locationCallback != null) {
                    startLocationUpdates();
                }
            } else {
                // Permission denied, handle accordingly (e.g., show a message)
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
