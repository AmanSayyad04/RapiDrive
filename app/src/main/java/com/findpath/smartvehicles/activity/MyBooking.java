package com.findpath.smartvehicles.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import com.findpath.smartvehicles.R;
import com.findpath.smartvehicles.adapter.VPadapter;
import com.google.android.material.tabs.TabLayout;

public class MyBooking extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager2 viewPager2;

    VPadapter viewPagerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_booking);
        tabLayout=findViewById(R.id.tabLayout);
        viewPager2=findViewById(R.id.viewPager);
        viewPagerAdapter = new VPadapter(this);
        viewPager2.setAdapter(viewPagerAdapter);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tabLayout.getTabAt(position).select();
            }
        });

//        tablayout.findViewById(R.id.tabLayout);
//        viewPager.findViewById(R.id.viewpager);
//
//        tablayout.setupWithViewPager(viewPager);
//
//        VPadapter vPadapter= new VPadapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
//        vPadapter.addFragment(new confirmed(),"No Confirmed Bookings");
//        vPadapter.addFragment(new cancelled(),"No Cancelled Bookings");
//        vPadapter.addFragment(new history(),"No History");
//        viewPager.setAdapter(vPadapter);
    }
}