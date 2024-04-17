package com.findpath.smartvehicles.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.findpath.smartvehicles.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MyProfile extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView profileImageView;
    private Button uploadImageButton;
    private EditText firstNameEditText, lastNameEditText, emailEditText;

    private Uri selectedImageUri;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        profileImageView = findViewById(R.id.profileImageView);
        uploadImageButton = findViewById(R.id.uplod_img);
        firstNameEditText = findViewById(R.id.firstNameEditText);
        lastNameEditText = findViewById(R.id.lastNameEditText);
        emailEditText = findViewById(R.id.emailEditText);

        db = FirebaseFirestore.getInstance();

        // Initialize Firebase Storage
        storageReference = FirebaseStorage.getInstance().getReference();

        // Initialize Firebase Realtime Database
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        uploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Collect user information
                String firstName = firstNameEditText.getText().toString().trim();
                String lastName = lastNameEditText.getText().toString().trim();
                String email = emailEditText.getText().toString().trim();

                if (selectedImageUri != null && !firstName.isEmpty() && !lastName.isEmpty() && !email.isEmpty()) {
                    uploadUserDataToFirebase(firstName, lastName, email, selectedImageUri);
                } else {
                    Toast.makeText(MyProfile.this, "Please fill all the fields and select an image", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void openGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(galleryIntent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                profileImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadUserDataToFirebase(String firstName, String lastName, String email, Uri imageUri) {
        // Create a reference to "images/profile.jpg"
        StorageReference profileImageRef = storageReference.child("images/profile.jpg");

        // Get the bitmap from imageUri
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Compress bitmap to PNG format
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();

        // Upload byte[] data to Firebase Storage
        profileImageRef.putBytes(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get the download URL of the uploaded image
                        String imageUrl = taskSnapshot.getUploadSessionUri().toString();

                        // Save user information to Firebase Realtime Database
                        saveUserInfoToDatabase(firstName, lastName, email, imageUrl);

                        Toast.makeText(MyProfile.this, "User data uploaded successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MyProfile.this, "Failed to upload user data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserInfoToDatabase(String firstName, String lastName, String email, String imageUrl) {
        // Check if a document with the user's email exists
        db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            // If a document with the user's email exists, update it
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                document.getReference().update("firstName", firstName,
                                                "lastName", lastName,
                                                "email", email,
                                                "imageUrl", imageUrl)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(MyProfile.this, "User data updated successfully", Toast.LENGTH_SHORT).show();
                                                // Start the new activity here
                                                Intent intent = new Intent(MyProfile.this, UserActivity.class);
                                                startActivity(intent);
                                                // Finish the current activity to prevent going back to it when pressing back button
                                                finish();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(MyProfile.this, "Failed to update user data", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                return;
                            }
                            // If no document with the user's email exists, create a new document
                            createNewUserDocument(firstName, lastName, email, imageUrl);
                        } else {
                            Toast.makeText(MyProfile.this, "Error checking user data", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private void createNewUserDocument(String firstName, String lastName, String email, String imageUrl) {
        // Create a new document in the "users" collection
        Map<String, Object> user = new HashMap<>();
        user.put("firstName", firstName);
        user.put("lastName", lastName);
        user.put("email", email);
        user.put("imageUrl", imageUrl);

        db.collection("users")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(MyProfile.this, "User data uploaded successfully", Toast.LENGTH_SHORT).show();
                        // Start the new activity here
                        Intent intent = new Intent(MyProfile.this, UserActivity.class);
                        startActivity(intent);
                        // Finish the current activity to prevent going back to it when pressing back button
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MyProfile.this, "Failed to upload user data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
