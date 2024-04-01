package com.findpath.smartvehicles.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.findpath.smartvehicles.R;
import com.findpath.smartvehicles.adapter.ReviewAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;

public class Mechanic_info extends AppCompatActivity {

    private TextView textViewTitle;
    private TextView textViewAddress;
    private double destinationLatitude;
    private double destinationLongitude;
    private RatingBar ratingBar;
    private EditText reviewEditText;
    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private ReviewAdapter adapter;
    private List<Review> reviewList;
    private String mobileNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mechanic_info);

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize UI components
        textViewTitle = findViewById(R.id.Marker_title);
        textViewAddress = findViewById(R.id.address);
        ratingBar = findViewById(R.id.ratingBar);
        reviewEditText = findViewById(R.id.reviewEditText);

        recyclerView = findViewById(R.id.reviewsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        reviewList = new ArrayList<>();
        adapter = new ReviewAdapter(reviewList);
        recyclerView.setAdapter(adapter);

        loadReviews();

        // Retrieve mechanic details from intent
        Intent intent = getIntent();
        if (intent != null) {
            destinationLatitude = intent.getDoubleExtra("latitude", 0.0);
            destinationLongitude = intent.getDoubleExtra("longitude", 0.0);
            String title = intent.getStringExtra("title");
            String address = intent.getStringExtra("address");
            textViewTitle.setText(title);
            textViewAddress.setText(address);
            mobileNumber = intent.getStringExtra("mobileNumber");
        }

        // Button click listeners
        findViewById(R.id.whatsappButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWhatsAppChat(mobileNumber);
            }
        });

        findViewById(R.id.callButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialPhoneNumber(mobileNumber);
            }
        });

//        findViewById(R.id.directionsButton).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                getDirections();
//            }
//        });

        findViewById(R.id.submitReviewButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitReview();
            }
        });

        // Get FCM token for device
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            String fcmToken = task.getResult();
                            Log.d("FCM Token", fcmToken);
                            // Now you have the FCM token which can be used as a unique device ID
                            // You can store this token in Firestore along with the user's data
                        } else {
                            Log.e("FCM Token", "Failed to get FCM token", task.getException());
                        }
                    }
                });
    }
    private void loadReviews() {
        db.collection("reviews")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Review review = document.toObject(Review.class);
                                reviewList.add(review);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
    }

//    private void loadReviews() {
//        // Retrieve garage name from UI
//        final String garageName = textViewTitle.getText().toString();
//
//        db.collection("reviews")
//                .whereEqualTo("garageName", garageName)
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            reviewList.clear(); // Clear the previous reviews
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                Review review = document.toObject(Review.class);
//                                reviewList.add(review);
//                            }
//                            adapter.notifyDataSetChanged(); // Update the RecyclerView
//                        } else {
//                            Toast.makeText(Mechanic_info.this, "Failed to load reviews", Toast.LENGTH_SHORT).show();
//                            Log.e("Firestore", "Error loading reviews", task.getException());
//                        }
//                    }
//                });
//    }


    private void submitReview() {
        final String reviewText = reviewEditText.getText().toString().trim();
        final float rating = ratingBar.getRating();

        // Retrieve garage name from UI
        final String garageName = textViewTitle.getText().toString();

        // Get the unique user ID (replace getUserId() with your method to get the user ID)
        final String userId = getUserId();

        // Check if the review text is not empty
        if (!reviewText.isEmpty()) {
            // Check the number of reviews submitted by the user for this garage
            db.collection("reviews")
                    .whereEqualTo("garageName", garageName)
                    .whereEqualTo("userId", userId)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                int reviewCount = task.getResult().size();
                                if (reviewCount == 0) {
                                    // User is submitting the first review for this garage
                                    submitNewReview(reviewText, rating, garageName, userId);
                                } else {
                                    // User has already submitted a review for this garage
                                    Toast.makeText(Mechanic_info.this, "You have already submitted a review for this garage", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(Mechanic_info.this, "Failed to check existing reviews", Toast.LENGTH_SHORT).show();
                                Log.e("Firestore", "Error checking existing reviews", task.getException());
                            }
                        }
                    });
        } else {
            Toast.makeText(this, "Please enter a review", Toast.LENGTH_SHORT).show();
        }
    }

    private void submitNewReview(String reviewText, float rating, String garageName, String userId) {
        // Create a new review document in Firestore
        Review review = new Review(reviewText, rating, garageName, userId);
        db.collection("reviews")
                .add(review)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(Mechanic_info.this, "Review submitted successfully", Toast.LENGTH_SHORT).show();
                            // Clear the review EditText after submission
                            reviewEditText.getText().clear();
                            // Optionally, update UI to reflect the new review
                        } else {
                            Toast.makeText(Mechanic_info.this, "Failed to submit review", Toast.LENGTH_SHORT).show();
                            Log.e("Firestore", "Error adding review", task.getException());
                        }
                    }
                });
    }



    // Method to get unique user ID (replace with your implementation)
    private String getUserId() {
        // Implement your logic to retrieve the unique user ID, e.g., Firebase Auth UID
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }


    private void dialPhoneNumber(String phoneNumber) {
        // Retrieve phone number from intent or UI element

        Intent dialIntent = new Intent(Intent.ACTION_DIAL);
        dialIntent.setData(Uri.parse("tel:" + phoneNumber));
        startActivity(dialIntent);
    }

//    private void getDirections() {
//        // Open Google Maps with the destination location
//        String geoUri = "http://maps.google.com/maps?q=loc:" + destinationLatitude + "," + destinationLongitude;
//        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
//        intent.setPackage("com.google.android.apps.maps");
//        if (intent.resolveActivity(getPackageManager()) != null) {
//            startActivity(intent);
//        } else {
//            Toast.makeText(this, "Google Maps app not found", Toast.LENGTH_SHORT).show();
//        }
//    }


    private void openWhatsAppChat(String phoneNumber) {
        // Retrieve phone number from intent or UI element
        try {
            Intent whatsappIntent = new Intent(Intent.ACTION_VIEW);
            String url = "https://api.whatsapp.com/send?phone=" + phoneNumber;
            whatsappIntent.setData(Uri.parse(url));
            startActivity(whatsappIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getDirections(View view) {
        String geoUri = "http://maps.google.com/maps?q=loc:" + destinationLatitude + "," + destinationLongitude;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
        intent.setPackage("com.google.android.apps.maps");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "Google Maps app not found", Toast.LENGTH_SHORT).show();
        }
    }
}
