package com.findpath.smartvehicles.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.findpath.smartvehicles.R;
import com.google.firebase.auth.FirebaseAuth;

public class SplashScreen extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        mAuth = FirebaseAuth.getInstance();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(mAuth.getCurrentUser()!=null){
                    startActivity(new Intent(SplashScreen.this, home.class));
                }else{
                    startActivity(new Intent(SplashScreen.this, entermobilenumberone.class));
                }
//                startActivity(new Intent(SplashScreen.this,entermobilenumberone.class));
//                finish();
            }
        },3000);
    }
}