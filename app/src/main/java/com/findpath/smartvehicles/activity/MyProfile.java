package com.findpath.smartvehicles.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.findpath.smartvehicles.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MyProfile extends AppCompatActivity {

    private final int GALLERY_REQ_CODE = 1000;
    private ImageView imageView;
    private EditText firstNameEditText, lastNameEditText, emailEditText;

    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        // Initialize Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        // Initialize UI components
        imageView = findViewById(R.id.profileImageView);
        firstNameEditText = findViewById(R.id.firstNameEditText);
        lastNameEditText = findViewById(R.id.lastNameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        Button btnGallery = findViewById(R.id.uplod_img);
        Button btnUpdate = findViewById(R.id.updateButton);

        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iGallery = new Intent(Intent.ACTION_PICK);
                iGallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(iGallery, GALLERY_REQ_CODE);
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == GALLERY_REQ_CODE) {
                imageView.setImageURI(data.getData());
            }
        }
    }

    private void updateProfile() {
        String firstName = firstNameEditText.getText().toString().trim();
        String lastName = lastNameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();

        if (!firstName.isEmpty() && !lastName.isEmpty() && !email.isEmpty()) {
            // Create a User object
            UserProfile userProfile = new UserProfile(firstName, lastName, email);

            // Upload data to Firebase Database
            DatabaseReference userRef = databaseReference.child(currentUser.getUid());
            userRef.setValue(userProfile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        // Data uploaded successfully
                        Intent intent = new Intent(MyProfile.this, UserActivity.class);

                        // Pass necessary data through Intent extras
                        intent.putExtra("firstName", firstName);
                        intent.putExtra("lastName", lastName);
                        intent.putExtra("email", email);
                        // ... other data you want to pass

                        startActivity(intent);
                    } else {
                        // Handle the case where data upload fails
                        Toast.makeText(MyProfile.this, "Failed to update profile.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }


    // Create a simple POJO class for user profile
    private static class UserProfile {
        public String firstName;
        public String lastName;
        public String email;

        public UserProfile() {
            // Default constructor required for DataSnapshot.getValue(User.class)
        }

        public UserProfile(String firstName, String lastName, String email) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
        }
    }
}
