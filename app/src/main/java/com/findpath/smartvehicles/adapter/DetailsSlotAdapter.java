package com.findpath.smartvehicles.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.findpath.smartvehicles.R;
import com.findpath.smartvehicles.activity.Slot;
import java.util.List;

public class DetailsSlotAdapter extends RecyclerView.Adapter<DetailsSlotAdapter.DetailsSlotViewHolder> {

    private Context context;
    private List<Slot> slotList;
    private OnSlotClickListener onSlotClickListener;

    public DetailsSlotAdapter(Context context, List<Slot> slotList) {
        this.context = context;
        this.slotList = slotList;
    }

    public interface OnSlotClickListener {
        void onSlotClick(Slot slot);
    }

    public void setOnSlotClickListener(OnSlotClickListener listener) {
        this.onSlotClickListener = listener;
    }

    @NonNull
    @Override
    public DetailsSlotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_slot2, parent, false);
        return new DetailsSlotViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DetailsSlotViewHolder holder, int position) {
        Slot slot = slotList.get(position);
        holder.bind(slot);
    }

    @Override
    public int getItemCount() {
        return slotList.size();
    }

    public class DetailsSlotViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView textViewSlotNumber;
        private TextView textViewPricePerUnit;
        private TextView textViewOption;
        private RadioButton radioButtonSlot;

        public DetailsSlotViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewSlotNumber = itemView.findViewById(R.id.textViewSlotNumber);
            textViewPricePerUnit = itemView.findViewById(R.id.textViewPricePerUnit);
            textViewOption = itemView.findViewById(R.id.textViewOption);
            radioButtonSlot = itemView.findViewById(R.id.radioButtonSlot);

            itemView.setOnClickListener(this);
            radioButtonSlot.setOnClickListener(this);
        }

        public void bind(Slot slot) {
            textViewSlotNumber.setText(slot.getSlotNumber());
            textViewPricePerUnit.setText(slot.getPricePerUnit());
            textViewOption.setText(slot.getSelectedOption());
            radioButtonSlot.setChecked(slot.isSelected());
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Slot slot = slotList.get(position);
                slot.setSelected(!slot.isSelected()); // Toggle selection
                notifyDataSetChanged();

                if (onSlotClickListener != null) {
                    onSlotClickListener.onSlotClick(slot);
                }
            }
        }
    }
}
