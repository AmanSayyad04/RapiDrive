package com.findpath.smartvehicles.adapter;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.findpath.smartvehicles.fragment.cancelled;
import com.findpath.smartvehicles.fragment.confirmed;
import com.findpath.smartvehicles.fragment.history;

public class VPadapter extends FragmentStateAdapter {

    public VPadapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0: return new confirmed();
            case 1: return new cancelled();
            case 2: return new history();
            default: return new confirmed();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }


//  private final ArrayList<Fragment> fragmentArrayList = new ArrayList<>();
//  private final ArrayList<String> fragmentData = new ArrayList<>();
//    public VPadapter(@NonNull FragmentManager fm, int behavior) {
//
//        super(fm, behavior);
//    }
//
//    @NonNull
//    @Override
//    public Fragment getItem(int position) {
//
//        return fragmentArrayList.get(position);
//    }
//
//    @Override
//    public int getCount() {
//        return fragmentArrayList.size();
//    }
//
//    public void addFragment(Fragment fragment,String data){
//        fragmentArrayList.add(fragment);
//        fragmentData.add(data);
//    }
//
//    @Nullable
//    @Override
//    public CharSequence getPageTitle(int position) {
//        return fragmentData.get(position);
//    }
}