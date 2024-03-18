package com.findpath.smartvehicles.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.findpath.smartvehicles.R;
import com.google.firebase.auth.FirebaseAuth;

public class UserActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        mAuth = FirebaseAuth.getInstance();

        Intent intent = getIntent();
        String firstName = intent.getStringExtra("firstName");
        String lastName = intent.getStringExtra("lastName");
        String email = intent.getStringExtra("email");
        // ... other data

        // Update UI with received data
        TextView firstNameTextView = findViewById(R.id.firstName);
        TextView lastNameTextView = findViewById(R.id.lastName);
        TextView emailTextView = findViewById(R.id.emailId);

        firstNameTextView.setText(firstName);
        lastNameTextView.setText(lastName);
        emailTextView.setText(email);

        TextView myProfile = findViewById(R.id.myProfile);
        TextView myFavouriteSpot = findViewById(R.id.myFavouriteSpot);
        TextView myBooking = findViewById(R.id.myBookings);
        TextView myEv = findViewById(R.id.myEv);
        TextView wallet = findViewById(R.id.myWallet);
        TextView notification = findViewById(R.id.notification);
        TextView privacy = findViewById(R.id.privacy);
        TextView contactUs = findViewById(R.id.contactUs);
        Button addMoney = findViewById(R.id.addMoney);
        TextView help = findViewById(R.id.help);
        Button logOut = findViewById(R.id.btnLogout);

        myProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserActivity.this, MyProfile.class);
                startActivity(intent);
            }
        });

        addMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserActivity.this, MyWallet.class);
                startActivity(intent);
            }
        });

        myFavouriteSpot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserActivity.this, MyFavouriteSpot.class);
                startActivity(intent);
            }
        });
        myBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserActivity.this, MyBooking.class);
                startActivity(intent);
            }
        });

        myEv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserActivity.this, EVInfo.class);
                startActivity(intent);
            }
        });

        wallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserActivity.this, MyWallet.class);
                startActivity(intent);
            }
        });

        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserActivity.this, Notification.class);
                startActivity(intent);
            }
        });

        privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserActivity.this, Privacy.class);
                startActivity(intent);
            }
        });

        contactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserActivity.this, Contact_Us.class);
                startActivity(intent);
            }
        });

        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserActivity.this, HelpActivity.class);
                startActivity(intent);
            }
        });

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Intent intent = new Intent(UserActivity.this, entermobilenumberone.class);
                // Clear the back stack so that pressing back after logout doesn't take the user back to the home page
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish(); // Optional, to close the current activity
            }
        });


    }
    @Override
    public void onBackPressed() {
        // Do nothing or handle differently as per your app's requirement
        super.onBackPressed();
    }
}