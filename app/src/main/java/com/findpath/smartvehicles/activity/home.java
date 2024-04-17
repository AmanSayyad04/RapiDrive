package com.findpath.smartvehicles.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.findpath.smartvehicles.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class home extends AppCompatActivity {
    RecyclerView rv;
    ArrayList<String> dataSource;
    LinearLayoutManager linearLayoutManager;
    MyRvAdapter myRvAdapter;
    private AdView mAdView;
    private BottomNavigationView bottomNavigationView;
    private FrameLayout frameLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        //
        rv=findViewById(R.id.horizontalRv);//

        ImageView drawerImageView = findViewById(R.id.user);

        //Data Source
        dataSource=new ArrayList<>();
        dataSource.add("First station...");
        dataSource.add("second station...");
        dataSource.add("Third station...");
        dataSource.add("Fourth station...");
        dataSource.add("Fifth station...");
        dataSource.add("Sixth station...");
        dataSource.add("Seventh station...");
        dataSource.add("Eighth station...");

        linearLayoutManager = new LinearLayoutManager(home.this,LinearLayoutManager.HORIZONTAL,false);
        myRvAdapter = new MyRvAdapter(dataSource);
        rv.setLayoutManager(linearLayoutManager);
        rv.setAdapter(myRvAdapter);
        drawerImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start the new activity
                Intent intent = new Intent(home.this, UserActivity.class);

                // Apply a left-to-right animation
                Animation slideInAnimation = AnimationUtils.loadAnimation(home.this, R.anim.slide_in_from_left);
                drawerImageView.startAnimation(slideInAnimation);

                // Start the new activity after the animation completes
                slideInAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {}

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_left);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                });
            }
        });

        ImageSlider imageSlider = findViewById(R.id.imageSlider);
        ArrayList<SlideModel> slideModels = new ArrayList<>();

        slideModels.add(new SlideModel(R.drawable.image1, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.image2, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.image3, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.image4, ScaleTypes.FIT));

        imageSlider.setImageList(slideModels, ScaleTypes.FIT);

        bottomNavigationView = findViewById(R.id.bottomNavView);
        frameLayout = findViewById(R.id.frameLayout);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navHome:
                        // You can choose to stay in the current activity or start a new instance.
                        // If you want to stay, you can remove the startActivity line.
                        startActivity(new Intent(home.this, home.class));
                        return true;
                    case R.id.navNavigator:
                        startActivity(new Intent(home.this, NavigatorActivity.class));
                        return true;
                    case R.id.navMech:
                        startActivity(new Intent(home.this, MechanicActivity.class));
                        return true;
                }
                return false;
            }
        });

//        bottomNavigationView.findViewById(R.id.bottomNavView);
//        frameLayout.findViewById(R.id.frameLayout);
//
//        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                switch (item.getItemId()) {
//                    case R.id.navHome:
//                        startActivity(new Intent(home.this, home.class));
//                        return true;
//                    case R.id.navNavigator:
//                        startActivity(new Intent(home.this, NavigatorActivity.class));
//                        return true;
//                    case R.id.navMech:
//                        startActivity(new Intent(home.this, MechanicActivity.class));
//                        return true;
//                }
//                return false;
//            }
//        });

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {

            }
        });

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


    }

    class MyRvAdapter extends  RecyclerView.Adapter<MyRvAdapter.MyHolder>{
        ArrayList<String> data;

        public MyRvAdapter(ArrayList<String> data) {
            this.data=data;
        }

        @NonNull
        @Override
        public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(home.this).inflate(R.layout.rv_items,parent,false);
            return new MyHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyHolder holder, int position) {
            holder.tvTitle.setText(data.get(position));
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        class MyHolder extends RecyclerView.ViewHolder{
            TextView tvTitle;
            public MyHolder(@NonNull View itemView) {
                super(itemView);
                tvTitle=itemView.findViewById(R.id.tvTitle);
            }
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(home.this);
        alertDialog.setTitle("Exit App");
        alertDialog.setMessage("Do you want to exit app?");
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finishAffinity();
            }
        });
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.show();


    }
}