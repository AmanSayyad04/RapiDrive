package com.findpath.smartvehicles.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.findpath.smartvehicles.R;

import java.util.List;

public class NewTimeSlotAdapter extends RecyclerView.Adapter<NewTimeSlotAdapter.TimeSlotViewHolder> {

    private List<String> timeSlots;
    private OnItemClickListener listener;
    private int selectedItem = RecyclerView.NO_POSITION;

    public NewTimeSlotAdapter(List<String> timeSlots, OnItemClickListener listener) {
        this.timeSlots = timeSlots;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TimeSlotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_time_slot, parent, false);
        return new TimeSlotViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimeSlotViewHolder holder, final int position) {
        String timeSlot = timeSlots.get(position);
        holder.bind(timeSlot);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(position);
                }
                setSelectedItem(position); // Highlight selected item
            }
        });

        // Change the background color based on the selected item
        if (selectedItem == position) {
            holder.itemView.setBackgroundColor(Color.RED); // Change to your desired color
        } else {
            holder.itemView.setBackgroundColor(Color.WHITE); // Change to your desired color
        }
    }



    @Override
    public int getItemCount() {
        return timeSlots.size();
    }

    public static class TimeSlotViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewTime;
        private TextView textViewStatus;

        public TimeSlotViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTime = itemView.findViewById(R.id.textViewTime);
            textViewStatus = itemView.findViewById(R.id.textViewStatus);
        }

        public void bind(String timeSlot) {
            // Split the time slot string into time and status
            String[] parts = timeSlot.split(":");
            String time = parts[0];
            String status = parts[1];

            textViewTime.setText(time);
            textViewStatus.setText(status);
        }
    }

    // Method to set the selected item
    public void setSelectedItem(int position) {
        selectedItem = position;
        notifyDataSetChanged();
    }

    // Interface for item click listener
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    // Method to update the status of the time slot
    public void updateSlotStatus(int position) {
        // Get the time slot string at the selected position
        String timeSlot = timeSlots.get(position);

        // Update the status from "Available" to "Occupied" for the selected item
        timeSlot = timeSlot.replace("Available", "Occupied");

        // Update the timeSlots list with the modified time slot
        timeSlots.set(position, timeSlot);

        // Notify the adapter about the data change
        notifyDataSetChanged();
    }



    // Method to set the data for the adapter
    public void setData(List<String> data) {
        this.timeSlots = data;
        notifyDataSetChanged();
    }
}
