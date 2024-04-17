//package com.findpath.smartvehicles.activity;
//
//import android.content.Context;
//import android.content.SharedPreferences;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.CompoundButton;
//import android.widget.Switch;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.findpath.smartvehicles.R;
//
//import java.util.List;
//
//public class SlotAdapter extends RecyclerView.Adapter<SlotAdapter.SlotViewHolder> {
//
//    private Context context;
//    private List<Slot> slotList;
//    private OnSlotStatusChangedListener statusChangedListener;
//    private SharedPreferences sharedPreferences;
//
//    public SlotAdapter(Context context, List<Slot> slotList, OnSlotStatusChangedListener statusChangedListener) {
//        this.context = context;
//        this.slotList = slotList;
//        this.statusChangedListener = statusChangedListener;
//        sharedPreferences = context.getSharedPreferences("slot_status", Context.MODE_PRIVATE);
//    }
//
//    @NonNull
//    @Override
//    public SlotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(context).inflate(R.layout.item_slot, parent, false);
//        return new SlotViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull SlotViewHolder holder, int position) {
//        Slot slot = slotList.get(position);
//        holder.bind(slot, position);
//        // Restore switch state
//        boolean isSlotOn = sharedPreferences.getBoolean("slot_" + position, false);
//        holder.switchSlot.setChecked(isSlotOn);
//
//    }
//
//    @Override
//    public int getItemCount() {
//        return slotList.size();
//    }
//
//    public class SlotViewHolder extends RecyclerView.ViewHolder {
//
//        private TextView textViewSlotNumber, textViewPricePerUnit, textViewSelectedOption;
//        private Button buttonSlot;
//        private Switch switchSlot;
//
//        public SlotViewHolder(@NonNull View itemView) {
//            super(itemView);
//            textViewSlotNumber = itemView.findViewById(R.id.textViewSlotNumber);
//            textViewPricePerUnit = itemView.findViewById(R.id.textViewPricePerUnit);
//            textViewSelectedOption = itemView.findViewById(R.id.textViewSelectedOption);
//            //buttonSlot = itemView.findViewById(R.id.buttonSlot);
//
//            switchSlot = itemView.findViewById(R.id.switchSlot);
//            switchSlot.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                @Override
//                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                    // Save switch state when changed
//                    SharedPreferences.Editor editor = sharedPreferences.edit();
//                    editor.putBoolean("slot_" + getAdapterPosition(), isChecked);
//                    editor.apply();
//
//                    // Notify listener about status change
//                    statusChangedListener.onSlotStatusChanged(getAdapterPosition(), isChecked);
//                }
//            });
//        }
//
//        public void bind(final Slot slot, final int position) {
//            String slotNumberText = "Slot Number: " + slot.getSlotNumber();
//            String price = "Price per unit: " + slot.getPricePerUnit();
//            String selectedOptions = "Selected Options: " + slot.getSelectedOption();
//            textViewSlotNumber.setText(slotNumberText);
//            textViewPricePerUnit.setText(price);
//            textViewSelectedOption.setText(selectedOptions);
//            // Set switch state based on the value of isOccupied
//            switchSlot.setChecked(slot.isOccupied());
//        }
//    }
//
//
//    public interface OnSlotStatusChangedListener {
//        void onSlotStatusChanged(int position, boolean isChecked);
//    }
//}


package com.findpath.smartvehicles.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.findpath.smartvehicles.R;

import java.util.List;

public class SlotAdapter extends RecyclerView.Adapter<SlotAdapter.SlotViewHolder> {

    private Context context;
    private List<Slot> slotList;
    private OnSlotStatusChangedListener statusChangedListener;
    private SharedPreferences sharedPreferences;
    private OnSlotClickListener slotClickListener;
    private String chargingStationId;
    private String ownerId;

    public void setChargingStationId(String chargingStationId) {
        this.chargingStationId = chargingStationId;
    }

    public SlotAdapter(Context context, List<Slot> slotList, OnSlotStatusChangedListener statusChangedListener, OnSlotClickListener slotClickListener, String chargingStationId, String ownerId) {
        this.context = context;
        this.slotList = slotList;
        this.statusChangedListener = statusChangedListener;
        this.slotClickListener = slotClickListener;
        this.chargingStationId = chargingStationId;
        this.ownerId = ownerId;
        sharedPreferences = context.getSharedPreferences("slot_status", Context.MODE_PRIVATE);

    }

    @NonNull
    @Override
    public SlotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_slot, parent, false);
        return new SlotViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SlotViewHolder holder, int position) {
        Slot slot = slotList.get(position);
        holder.bind(slot, position);
        // Restore switch state
        boolean isSlotOn = sharedPreferences.getBoolean("slot_" + position, false);
        holder.switchSlot.setChecked(isSlotOn);

        // Set click listener for the slot item
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the slot ID of the clicked item
                String slotId = slotList.get(holder.getAdapterPosition()).getSlotId();
                // Notify the click listener with the slot ID
                slotClickListener.onSlotClick(slotId);
            }
        });
    }


    @Override
    public int getItemCount() {
        return slotList.size();
    }

    public class SlotViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewSlotNumber, textViewPricePerUnit, textViewSelectedOption;
        private Button buttonSlot;
        private Switch switchSlot;

        public SlotViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewSlotNumber = itemView.findViewById(R.id.textViewSlotNumber);
            textViewPricePerUnit = itemView.findViewById(R.id.textViewPricePerUnit);
            textViewSelectedOption = itemView.findViewById(R.id.textViewSelectedOption);
            //buttonSlot = itemView.findViewById(R.id.buttonSlot);

            Button buttonAddTimeSlot = itemView.findViewById(R.id.buttonAddTimeSlot);
            buttonAddTimeSlot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Get the slot ID of the clicked item
                    String slotId = slotList.get(getAdapterPosition()).getSlotId();

                    // Create an intent to open TimeActivity
                    Intent intent = new Intent(context, TimeActivity.class);

                    // Pass the selected slot ID to TimeActivity
                    intent.putExtra("slotId", slotId);
                    intent.putExtra("chargingStationId", chargingStationId);
                    intent.putExtra("ownerId", ownerId);

                    // Start TimeActivity
                    context.startActivity(intent);

                }
            });

            switchSlot = itemView.findViewById(R.id.switchSlot);
            switchSlot.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    // Save switch state when changed
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("slot_" + getAdapterPosition(), isChecked);
                    editor.apply();

                    // Notify listener about status change
                    statusChangedListener.onSlotStatusChanged(getAdapterPosition(), isChecked);
                }
            });
        }

        public void bind(final Slot slot, final int position) {
            String slotNumberText = "Slot Number: " + slot.getSlotNumber();
            String price = "Price per unit: " + slot.getPricePerUnit();
            String selectedOptions = "Selected Options: " + slot.getSelectedOption();
            textViewSlotNumber.setText(slotNumberText);
            textViewPricePerUnit.setText(price);
            textViewSelectedOption.setText(selectedOptions);
            // Set switch state based on the value of isOccupied
            switchSlot.setChecked(slot.isOccupied());
        }
    }

    public interface OnSlotStatusChangedListener {
        void onSlotStatusChanged(int position, boolean isChecked);
    }

    public interface OnSlotClickListener {
        void onSlotClick(String slotId);
    }
}
