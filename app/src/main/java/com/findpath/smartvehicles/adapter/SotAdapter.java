package com.findpath.smartvehicles.adapter;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;


import com.findpath.smartvehicles.fragment.Connectors;
import com.findpath.smartvehicles.fragment.Photos;
import com.findpath.smartvehicles.fragment.Reviews;


public class SotAdapter extends FragmentStateAdapter {

    public SotAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new Connectors();
            case 1:
                return new Reviews();
            case 2:
                return new Photos();
            default:
                return new Connectors();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }

}