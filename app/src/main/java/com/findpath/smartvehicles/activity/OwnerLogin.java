package com.findpath.smartvehicles.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.findpath.smartvehicles.R;

import com.findpath.smartvehicles.adapter.Owner;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class OwnerLogin extends AppCompatActivity {

    private EditText editTextEmail, editTextPassword;
    private Button buttonLogin;
    private TextView textViewContactUs;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_login);

        // Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();
        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize views
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        textViewContactUs = findViewById(R.id.contact_us);

        // Set click listener for login button
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform login
                loginUser();
            }
        });

        textViewContactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open dial pad with the specified phone number
                openDialPad("+919604097964");
            }
        });
    }

    private void loginUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        // Check if email and password are not empty
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        // Authenticate user with Firebase
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                checkUserType(user.getUid());
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(OwnerLogin.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void checkUserType(String userId) {
        // Check if the owner exists in Firestore
        db.collection("owners").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Existing owner, navigate to SlotInfo activity
                        openSlotInfo();
                    } else {
                        // New owner, save user data and navigate to OwnerPage activity
                        saveUserData(userId);
                        openOwnerPage();
                    }
                } else {
                    Log.e("OwnerLogin", "Error checking user type: ", task.getException());
                }
            }
        });
    }

    private void saveUserData(String userId) {
        // Get user details
        String email = editTextEmail.getText().toString().trim();
        // Save owner data to Firestore
        Owner owner = new Owner(userId, email);
        db.collection("owners").document(userId).set(owner);
    }

    private void openOwnerPage() {
        Intent intent = new Intent(this, OwnerPage.class);
        startActivity(intent);
        finish(); // Finish the current activity so the user cannot go back to login screen
    }

    private void openSlotInfo() {
        Intent intent = new Intent(this, SlotInfo.class);
        startActivity(intent);
        finish(); // Finish the current activity so the user cannot go back to login screen
    }

    private void openDialPad(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        startActivity(intent);
    }
}
