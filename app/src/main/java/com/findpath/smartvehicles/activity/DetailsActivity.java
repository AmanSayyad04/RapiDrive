package com.findpath.smartvehicles.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.widget.TextView;

import com.findpath.smartvehicles.R;
import com.findpath.smartvehicles.adapter.SotAdapter;
import com.findpath.smartvehicles.adapter.VPadapter;
import com.google.android.material.tabs.TabLayout;

public class DetailsActivity extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager2 viewPager2;
    TextView markertext;
    TextView address;
    SotAdapter viewPagerAdapter2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        String title = getIntent().getStringExtra("title");
        String address = getIntent().getStringExtra("address");

        // Now you can use 'title' and 'address' to display in your activity
        // For example, if you have TextViews with ids 'textViewTitle' and 'textViewAddress':
        TextView textViewTitle = findViewById(R.id.Marker_title);
        TextView textViewAddress = findViewById(R.id.address);

        textViewTitle.setText(title);
        textViewAddress.setText(address);

        tabLayout=findViewById(R.id.tabLayout2);
        viewPager2=findViewById(R.id.viewPager2);
        viewPagerAdapter2 = new SotAdapter(this);
        viewPager2.setAdapter(viewPagerAdapter2);
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

    }
}