package com.findpath.smartvehicles.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.findpath.smartvehicles.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SlotInfo extends AppCompatActivity implements SlotAdapter.OnSlotStatusChangedListener, SlotAdapter.OnSlotClickListener{

    private RecyclerView recyclerView;
    private SlotAdapter slotAdapter;
    private List<Slot> slotList;
    private FirebaseFirestore db;
    private String chargingStationId;
    private Button addmore, addTime;
    private String selectedSlotId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slot_info);

        db = FirebaseFirestore.getInstance();

        // Retrieve chargingStationId from Firebase
        getChargingStationIdFromFirebase();

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerViewSlots);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        slotList = new ArrayList<>();
        slotAdapter = new SlotAdapter(this, slotList, this, this, null, getOwnerId());
        recyclerView.setAdapter(slotAdapter);
        addmore = findViewById(R.id.buttonAddMore);
        //addTime = findViewById(R.id.buttonAddTimeSlot);


        addmore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SlotInfo.this, OwnerPage.class);
                intent.putExtra("hideFields", true);
                intent.putExtra("chargingStationId", chargingStationId);
                startActivity(intent);
            }
        });

//        addTime.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(SlotInfo.this, TimeActivity.class);
//                intent.putExtra("slotId", selectedSlotId);
//                startActivity(intent);
//            }
//        });
    }

    private String getOwnerId() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    private void getChargingStationIdFromFirebase() {
        String ownerId = getOwnerId();

        // Query Firestore to retrieve the charging station document associated with the owner
        db.collection("owners")
                .document(ownerId)
                .collection("chargingStations")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            // Get the first document (assuming there's only one charging station per owner)
                            DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                            // Retrieve the charging station ID
                            chargingStationId = documentSnapshot.getId();
                            Log.d("SlotInfoActivity", "Charging Station ID: " + chargingStationId);
                            // Fetch slot data after obtaining the charging station ID
                            updateAdapterWithChargingStationId();
                            
                            fetchSlotData();
                        } else {
                            // No charging station document found
                            Log.d("SlotInfoActivity", "No charging station document found for owner ID: " + ownerId);
                            Toast.makeText(SlotInfo.this, "No charging station found", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("SlotInfoActivity", "Error fetching charging station data: " + e.getMessage());
                        Toast.makeText(SlotInfo.this, "Failed to fetch charging station data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateAdapterWithChargingStationId() {
        // Update the slot adapter with the obtained chargingStationId
        if (chargingStationId != null) {
            slotAdapter.setChargingStationId(chargingStationId);
        }
    }


    private void fetchSlotData() {
        if (chargingStationId != null) {
            db.collection("owners").document(chargingStationId).collection("slots")
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            Log.d("SlotInfoActivity", "Number of slots: " + queryDocumentSnapshots.size());
                            // Clear the list before adding new data
                            slotList.clear();
                            // Loop through each document snapshot
                            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                // Convert document snapshot to Slot object
                                Slot slot = documentSnapshot.toObject(Slot.class);
                                // Set slot ID
                                slot.setSlotId(documentSnapshot.getId());
                                // Add slot to list
                                slotList.add(slot);
                            }
                            // Notify adapter about data changes
                            slotAdapter.notifyDataSetChanged();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("SlotInfoActivity", "Error fetching slot data: " + e.getMessage());
                            Toast.makeText(SlotInfo.this, "Failed to fetch slot data", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    @Override
    public void onSlotStatusChanged(int position, boolean isChecked) {
        Slot slot = slotList.get(position);
        slot.setOccupied(isChecked);
        updateSlotStatusInFirestore(slot);
    }

    // Implement the method from the interface to handle slot clicks
    @Override
    public void onSlotClick(String slotId) {
        // Handle slot click here
        selectedSlotId = slotId; // Update the selectedSlotId with the clicked slot ID
        Toast.makeText(this, "Slot ID: " + slotId + " clicked", Toast.LENGTH_SHORT).show();
    }

    private void updateSlotStatusInFirestore(Slot slot) {
        if (chargingStationId != null && slot != null) {
            // Ensure slot number is retrieved as a string
            String slotId = slot.getSlotId();

            // Construct the path to the slot document in Firestore
            String slotPath = "owners/" + chargingStationId + "/slots/" + slotId ;

            // Update Firestore with the new status of the slot
            db.document(slotPath)
                    .update("isOccupied", slot.isOccupied())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Update successful
                            Log.d("SlotInfoActivity", "Slot status updated successfully");
                            Log.d("SlotInfoActivity", "Slot info " + slot);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Update failed
                            Log.e("SlotInfoActivity", "Failed to update slot status: " + e.getMessage());
                            Log.e("SlotInfoActivity", "Slot Id:  " + slotId);
                            Toast.makeText(SlotInfo.this, "Failed to update slot status", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
