package com.findpath.smartvehicles.activity;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.findpath.smartvehicles.R;
import com.findpath.smartvehicles.adapter.TimeSlotAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TimeActivity extends AppCompatActivity {

    private EditText editTextStartTime;
    private Button buttonAddSlot;
    private String slotId, ownerId, chargingStationId;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Get the slotId from the intent
        slotId = getIntent().getStringExtra("slotId");
        ownerId = getIntent().getStringExtra("ownerId");
        chargingStationId = getIntent().getStringExtra("chargingStationId");
        Log.d(TAG, "ownerid" + ownerId);
        Log.d(TAG, "chargingid" + chargingStationId);
        Toast.makeText(this, slotId, Toast.LENGTH_SHORT).show();

        // Initialize views
        editTextStartTime = findViewById(R.id.editTextStartTime);
        buttonAddSlot = findViewById(R.id.buttonAddSlot);

        // Set click listener for the "Add Slot" button
        // Set click listener for the "Add Slot" button
        buttonAddSlot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the selected time from the EditText
                String startTime = editTextStartTime.getText().toString().trim();

                // Check if startTime is not empty
                if (!startTime.isEmpty()) {
                    // Create a list to store the start times
                    List<String> startTimes = new ArrayList<>();
                    // Add the selected start time to the list
                    startTimes.add(startTime);

                    // Call the method to update slot start times with the list of start times
                    updateSlotStartTime(startTimes);
                } else {
                    // Show a toast message if startTime is empty
                    Toast.makeText(TimeActivity.this, "Please enter a valid time", Toast.LENGTH_SHORT).show();
                }
            }
        });
        // Set up RecyclerView
        setUpRecyclerView();

    }

    private void setUpRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerViewTimeSlots);

        // Use GridLayoutManager with span count 4
        GridLayoutManager layoutManager = new GridLayoutManager(this, 4);
        recyclerView.setLayoutManager(layoutManager);

        // Fetch data from Firestore
        DocumentReference slotRef = db.document("owners/" + chargingStationId + "/slots/" + slotId);
        slotRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    // Retrieve the times map from Firestore
                    Map<String, Object> timesMap = documentSnapshot.contains("times") ?
                            (Map<String, Object>) documentSnapshot.get("times") : new HashMap<>();

                    // Convert the times map to a list of strings for the RecyclerView
                    List<String> timeSlotsList = new ArrayList<>();
                    for (Map.Entry<String, Object> entry : timesMap.entrySet()) {
                        String timeSlot = entry.getKey() + ":" + entry.getValue();
                        timeSlotsList.add(timeSlot);
                    }

                    // Create and set the adapter for the RecyclerView
                    TimeSlotAdapter adapter = new TimeSlotAdapter(timeSlotsList);
                    recyclerView.setAdapter(adapter);
                } else {
                    Log.d(TAG, "No such document");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Failed to fetch document: " + e.getMessage());
            }
        });
    }



    // Method to update the slot document in Firestore with the selected time
    // Method to update the slot document in Firestore with the selected time
    private void updateSlotStartTime(List<String> startTimes) {
        // Construct the path to the slot document in Firestore
        String slotPath = "owners/" + chargingStationId + "/slots/" + slotId;

        // Get the Firestore document reference
        DocumentReference slotRef = db.document(slotPath);

        // Fetch the existing times map from Firestore
        slotRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    // Get the existing times map
                    Map<String, Object> existingTimes = documentSnapshot.contains("times") ?
                            (Map<String, Object>) documentSnapshot.getData().get("times") : new HashMap<>();

                    // Add each new start time to the times map with a default value of false
                    for (int i = 0; i < startTimes.size(); i++) {
                        String startTime = startTimes.get(i);
                        existingTimes.put(startTime, false);
                    }

                    // Create a map to store the updated data
                    Map<String, Object> slotData = new HashMap<>();
                    slotData.put("times", existingTimes);

                    // Update the slot document in Firestore
                    slotRef.set(slotData, SetOptions.merge())
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // Slot document updated successfully
                                    Toast.makeText(TimeActivity.this, "Slot start times updated successfully", Toast.LENGTH_SHORT).show();
                                    // Finish the activity
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(Exception e) {
                                    // Failed to update slot document
                                    Toast.makeText(TimeActivity.this, "Failed to update slot start times", Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, "time failed: " + e.getMessage());
                                }
                            });
                } else {
                    // Document does not exist
                    Log.d(TAG, "No such document");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Failed to fetch document
                Log.d(TAG, "Failed to fetch document: " + e.getMessage());
            }
        });
    }

}
