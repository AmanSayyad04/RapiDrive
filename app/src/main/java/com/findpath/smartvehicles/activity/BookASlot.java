package com.findpath.smartvehicles.activity;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;


import com.findpath.smartvehicles.R;
import com.findpath.smartvehicles.adapter.NewTimeSlotAdapter;
import com.findpath.smartvehicles.adapter.TimeSlotAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class BookASlot extends AppCompatActivity {

    private TextView textViewSelectedDate;
    private String selectedSlotId;
    private RecyclerView recyclerViewTimeSlots;
    private List<Boolean> booleanValues = new ArrayList<>();
    private FirebaseFirestore db;
    private String chargingStationId;
    private NewTimeSlotAdapter adapter;
    private int selectedItemPosition = RecyclerView.NO_POSITION;
    private List<String> timeSlots;




//    private Spinner timeSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_aslot);

        //adapter = new NewTimeSlotAdapter(new ArrayList<>());

        textViewSelectedDate = findViewById(R.id.textViewSelectedDate);
        //timeSpinner = findViewById(R.id.timeSpinner);
        recyclerViewTimeSlots = findViewById(R.id.recyclerViewTimeSlots);

        db = FirebaseFirestore.getInstance();

        timeSlots = new ArrayList<>();

        selectedSlotId = getIntent().getStringExtra("slotId");
        chargingStationId = getIntent().getStringExtra("chargingId");
        Log.d(TAG, "chargId" + chargingStationId);
        Toast.makeText(this, selectedSlotId, Toast.LENGTH_SHORT).show();
        // Set today's date in the TextView
        setCurrentDate();



        fetchTimeSlots(selectedSlotId);

        // Set prompt and items for the Spinner
//        setTimeSpinner();

        // Set OnClickListener to open DatePickerDialog
        textViewSelectedDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        Button bookSlotButton = findViewById(R.id.bookSlotButton);
        bookSlotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle button click
                // Update status in Firebase
                Log.d(TAG, "Button clicked");
                Log.d(TAG, "Selected item position123a: " + selectedItemPosition);
                updateStatusInFirebase(selectedItemPosition);
                // Update status in adapter
                adapter.updateSlotStatus(selectedItemPosition);
            }
        });


    }

    private void updateStatusInFirebase(int position) {
        // Check if the position is valid
        if (position >= 0 && position < timeSlots.size()) {
            // Extract the selected time slot from the timeSlots list
            String selectedTimeSlot = timeSlots.get(position);

            // Extract the slot ID from the selected time slot
            String slotId = selectedTimeSlot.split(":")[0];

            // Update the status from "Available" to "Occupied" for the selected item
            timeSlots.set(position, slotId + ":Occupied");

            // Update the status of the selected time slot in Firestore
            Map<String, Object> updateData = new HashMap<>();
            for (String timeSlot : timeSlots) {
                String[] parts = timeSlot.split(":");
                int slot = Integer.parseInt(parts[0]);
                boolean isOccupied = parts[1].equalsIgnoreCase("Occupied");
                updateData.put("times." + slot, isOccupied);
            }

            db.collection("owners")
                    .document(chargingStationId)
                    .collection("slots")
                    .document(selectedSlotId)
                    .update(updateData)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Handle success
                            Toast.makeText(BookASlot.this, "Slot booked successfully", Toast.LENGTH_SHORT).show();
                            // Optionally, you can update the local data and UI if needed
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Handle failure
                            Toast.makeText(BookASlot.this, "Failed to book slot", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "Failed to update slot status in Firestore", e);
                        }
                    });
        } else {
            // If the position is invalid, show a message indicating that the slot is not available
            Toast.makeText(BookASlot.this, "Slot is not available", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "updateStatusInFirebase: Slot is not available");
        }
    }




    private void fetchTimeSlots(String selectedSlotId) {
        db.collection("owners")
                .document(chargingStationId)
                .collection("slots")
                .document(selectedSlotId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            List<String> timeSlots = new ArrayList<>();
                            Map<String, Boolean> timesMap = (Map<String, Boolean>) documentSnapshot.get("times");
                            if (timesMap != null) {
                                for (Map.Entry<String, Boolean> entry : timesMap.entrySet()) {
                                    String time = entry.getKey();
                                    Boolean isOccupied = entry.getValue();
                                    String status = isOccupied ? "Occupied" : "Available";
                                    timeSlots.add(time + ":" + status);
                                }
                            }

                            // Verify the data
                            Log.d(TAG, "Fetched time slots: " + timeSlots);

                            adapter = new NewTimeSlotAdapter(timeSlots, new NewTimeSlotAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(int position) {
                                    // Handle item click here
                                    selectedItemPosition = position; // Update selected item position
                                    adapter.setSelectedItem(position);
                                    adapter.notifyDataSetChanged();
                                }
                            });

                            recyclerViewTimeSlots.setLayoutManager(new GridLayoutManager(BookASlot.this, 4));
                            recyclerViewTimeSlots.setAdapter(adapter);

                            // Update the global timeSlots variable
                            BookASlot.this.timeSlots = timeSlots; // Ensure you update the global variable
                        } else {
                            Log.d(TAG, "Document does not exist");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle failure
                        Log.e(TAG, "Failed to fetch time slots", e);
                    }
                });
    }







    private void setCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String currentDate = sdf.format(calendar.getTime());
        textViewSelectedDate.setText(currentDate);
    }
    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        // Format the selected date and set it to the TextView
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        String selectedDate = sdf.format(calendar.getTime());
                        textViewSelectedDate.setText(selectedDate);
                    }
                }, year, month, dayOfMonth);

        datePickerDialog.show();
    }

    // Method to set prompt and items for the Spinner
//    private void setTimeSpinner() {
//        // Create an ArrayAdapter using the string array resource and a default spinner layout
//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
//                R.array.timing_options, android.R.layout.simple_spinner_item);
//        // Specify the layout to use when the list of choices appears
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        // Apply the adapter to the spinner
//        timeSpinner.setAdapter(adapter);
//    }
}