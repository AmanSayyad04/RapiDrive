package com.findpath.smartvehicles.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.findpath.smartvehicles.R;
import com.findpath.smartvehicles.activity.Slot;
import com.findpath.smartvehicles.adapter.DetailsSlotAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class DetailsActivity extends AppCompatActivity implements SlotAdapter.OnSlotStatusChangedListener {

    private RecyclerView recyclerView;
    private DetailsSlotAdapter slotAdapter;
    private List<Slot> slotList;
    private FirebaseFirestore db;
    private Button bookSlot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        String title = getIntent().getStringExtra("title");
        String address = getIntent().getStringExtra("address");
        String chargingStationId = getIntent().getStringExtra("chargingStationId");

        // Set the title and address
        TextView textViewTitle = findViewById(R.id.Marker_title);
        TextView textViewAddress = findViewById(R.id.address);
        textViewTitle.setText(title);
        textViewAddress.setText(address);
        bookSlot = findViewById(R.id.bookSlotButton);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerViewSlots);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        slotList = new ArrayList<>();
        slotAdapter = new DetailsSlotAdapter(this, slotList);
        recyclerView.setAdapter(slotAdapter);

        fetchChargingStationId();

        // Fetch slot data from Firestore
        fetchSlotData(chargingStationId);

        bookSlot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailsActivity.this,BookASlot.class);
                startActivity(intent);
            }
        });
    }

    private void fetchChargingStationId() {
        String ownerId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Query Firestore to retrieve the charging station document associated with the owner
        db.collection("owners")
                .document(ownerId)
                .collection("chargingStations")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Get the first document (assuming there's only one charging station per owner)
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                        // Retrieve the charging station ID
                        String chargingStationId = documentSnapshot.getId();
                        // Now that we have the charging station ID, we can fetch slot data
                        fetchSlotData(chargingStationId);
                    } else {
                        // No charging station document found
                        Log.d("DetailsActivity", "No charging station document found for owner ID: " + ownerId);
                        Toast.makeText(DetailsActivity.this, "No charging station found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("DetailsActivity", "Error fetching charging station data: " + e.getMessage());
                    Toast.makeText(DetailsActivity.this, "Failed to fetch charging station data", Toast.LENGTH_SHORT).show();
                });
    }

    private void fetchSlotData(String chargingStationId) {
        if (chargingStationId != null) {
            db.collection("owners").document(chargingStationId).collection("slots")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            slotList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Retrieve slot data from Firestore and instantiate Slot objects
                                String slotNumber = document.getString("slotNumber");
                                String pricePerUnit = document.getString("pricePerUnit");
                                String selectedOption = document.getString("selectedOption");
                                Boolean isOccupied = document.getBoolean("isOccupied");
                                boolean isOccupiedValue = false; // Default value
                                if (isOccupied != null) {
                                    isOccupiedValue = isOccupied.booleanValue();
                                }


                                Slot slot = new Slot(slotNumber, pricePerUnit, selectedOption, isOccupiedValue);

                                slot.setSlotId(document.getId());
                                slotList.add(slot);
                            }
                            slotAdapter.notifyDataSetChanged();
                        } else {
                            // Handle errors
                        }
                    });
        }
    }

    @Override
    public void onSlotStatusChanged(int position, boolean isChecked) {
        // Handle slot status changes here if needed
    }
}
