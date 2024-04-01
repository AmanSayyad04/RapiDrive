package com.findpath.smartvehicles.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.findpath.smartvehicles.R;

import java.util.List;

public class SlotAdapter extends RecyclerView.Adapter<SlotAdapter.SlotViewHolder> {

    private Context context;
    private List<Slot> slotList;
    private OnSlotStatusChangedListener statusChangedListener;

    public SlotAdapter(Context context, List<Slot> slotList, OnSlotStatusChangedListener statusChangedListener) {
        this.context = context;
        this.slotList = slotList;
        this.statusChangedListener = statusChangedListener;
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

    }

    @Override
    public int getItemCount() {
        return slotList.size();
    }

    public class SlotViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewSlotNumber, textViewPricePerUnit, textViewSelectedOption;
        private Button buttonSlot;

        public SlotViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewSlotNumber = itemView.findViewById(R.id.textViewSlotNumber);
            textViewPricePerUnit = itemView.findViewById(R.id.textViewPricePerUnit);
            textViewSelectedOption = itemView.findViewById(R.id.textViewSelectedOption);
            buttonSlot = itemView.findViewById(R.id.buttonSlot);
        }

        public void bind(final Slot slot, final int position) {
            String slotNumberText = "Slot Number: " + slot.getSlotNumber();
            String price = "Price per unit: " + slot.getPricePerUnit();
            String selectedOptions = "Selected Options: " + slot.getSelectedOption();
            textViewSlotNumber.setText(slotNumberText);
            textViewPricePerUnit.setText(price);
            textViewSelectedOption.setText(selectedOptions);
            // Set switch state based on the value of isOccupied
            if (slot.isOccupied()) {
                buttonSlot.setText("Slot On");
            } else {
                buttonSlot.setText("Slot Off");
            }

            // Add click listener to toggle slot status
            buttonSlot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Toggle slot status
                    boolean newStatus = !slot.isOccupied();
                    slot.setOccupied(newStatus);
                    // Update button text
                    if (newStatus) {
                        buttonSlot.setText("Slot On");
                    } else {
                        buttonSlot.setText("Slot Off");
                    }
                    // Notify listener about status change
                    statusChangedListener.onSlotStatusChanged(getAdapterPosition(), newStatus);
                }
            });
        }
    }


    public interface OnSlotStatusChangedListener {
        void onSlotStatusChanged(int position, boolean isChecked);
    }
}
