package com.findpath.smartvehicles.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.findpath.smartvehicles.R;

public class Mechanic_info extends AppCompatActivity {

    TextView markertext;
    TextView address;
    double destinationLatitude;
    double destinationLongitude;
    TextView mobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mechanic_info);

        destinationLatitude = getIntent().getDoubleExtra("latitude", 0.0);
        destinationLongitude = getIntent().getDoubleExtra("longitude", 0.0);

        String title = getIntent().getStringExtra("title");
        String address = getIntent().getStringExtra("address");
        String mob = getIntent().getStringExtra("mobileNumber");

        // Now you can use 'title' and 'address' to display in your activity
        // For example, if you have TextViews with ids 'textViewTitle' and 'textViewAddress':
        TextView textViewTitle = findViewById(R.id.Marker_title);
        TextView textViewAddress = findViewById(R.id.address);
        mobile = findViewById(R.id.mobile_number);

        textViewTitle.setText(title);
        textViewAddress.setText(address);
        mobile.setText("Mob number " + mob);

        // Assuming you have a button with the id 'whatsappButton' in your layout
        findViewById(R.id.whatsappButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWhatsAppChat(mob);
            }
        });
    }
    public void getDirections(View view) {
        String uri = "http://maps.google.com/maps?saddr=" + "&daddr=" + destinationLatitude + "," + destinationLongitude;
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setPackage("com.google.android.apps.maps");
        startActivity(intent);
    }

    private void openWhatsAppChat(String phoneNumber) {
        try {
            Intent whatsappIntent = new Intent(Intent.ACTION_VIEW);
            String url = "https://api.whatsapp.com/send?phone=" + phoneNumber;
            whatsappIntent.setData(Uri.parse(url));
            startActivity(whatsappIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}