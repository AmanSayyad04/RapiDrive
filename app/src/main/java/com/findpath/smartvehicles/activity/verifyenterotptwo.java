package com.findpath.smartvehicles.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.MediaRouteButton;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.findpath.smartvehicles.R;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class verifyenterotptwo extends AppCompatActivity {

    private OTP_Receiver otp_receiver;

    EditText inputnumber1, inputnumber2, inputnumber3, inputnumber4, inputnumber5, inputnumber6;
    EditText inputotp1, inputotp2, inputotp3, inputotp4, inputotp5, inputotp6;
    String getotpbackend;
    private ProgressBar progressBarverifyotp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verifyenterotptwo);

        progressBarverifyotp = findViewById(R.id.progressbar_verify_otp);

       // final Button verifybuttonclick = findViewById(R.id.buttongetotp);

        inputnumber1 = findViewById(R.id.inputotp1);
        inputnumber2 = findViewById(R.id.inputotp2);
        inputnumber3 = findViewById(R.id.inputotp3);
        inputnumber4 = findViewById(R.id.inputotp4);
        inputnumber5 = findViewById(R.id.inputotp5);
        inputnumber6 = findViewById(R.id.inputotp6);




        inputotp1 = findViewById(R.id.inputotp1);
        inputotp2 = findViewById(R.id.inputotp2);
        inputotp3 = findViewById(R.id.inputotp3);
        inputotp4 = findViewById(R.id.inputotp4);
        inputotp5 = findViewById(R.id.inputotp5);
        inputotp6 = findViewById(R.id.inputotp6);

        TextView textView = findViewById(R.id.textmobileshownumber);
        textView.setText(String.format("+91.%s",getIntent().getStringExtra("mobile")));

        getotpbackend = getIntent().getStringExtra("backendotp");

        final ProgressBar progressBarverifyotp = findViewById(R.id.progressbar_verify_otp);

//        verifybuttonclick.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(!inputnumber1.getText().toString().trim().isEmpty() && !inputnumber2.getText().toString().trim().isEmpty() && !inputnumber3.getText().toString().trim().isEmpty() && !inputnumber4.getText().toString().trim().isEmpty() && !inputnumber5.getText().toString().trim().isEmpty() && !inputnumber6.getText().toString().trim().isEmpty()){
//
//                    String entercodeotp = inputnumber1.getText().toString() +
//                            inputnumber2.getText().toString() +
//                            inputnumber3.getText().toString() +
//                            inputnumber4.getText().toString() +
//                            inputnumber5.getText().toString() +
//                            inputnumber6.getText().toString();
//
//                    if (getotpbackend != null){
//                        progressBarverifyotp.setVisibility(View.VISIBLE);
//                        verifybuttonclick.setVisibility(View.INVISIBLE);
//
//                        PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(
//                                getotpbackend,entercodeotp
//                        );
//                        FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
//                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<AuthResult> task) {
//                                        progressBarverifyotp.setVisibility(View.GONE);
//                                        verifybuttonclick.setVisibility(View.VISIBLE);
//
//                                        if (task.isSuccessful()){
//                                            Intent intent = new Intent(getApplicationContext(), home.class);
//                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                            startActivity(intent);
//                                        }else {
//                                            Toast.makeText(verifyenterotptwo.this, "Enter the correct OTP", Toast.LENGTH_SHORT).show();
//                                        }
//                                    }
//                                });
//
//                    }else {
//                        Toast.makeText(verifyenterotptwo.this, "Please check internet connection", Toast.LENGTH_SHORT).show();
//                    }
//
//                    //Toast.makeText(verifyenterotptwo.this, "otp verify", Toast.LENGTH_SHORT).show();
//                }else{
//                    Toast.makeText(verifyenterotptwo.this, "Please enter all numbers", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });

        numberotpmove();

        TextView resendlabel = findViewById(R.id.textresendotp);
        resendlabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        "+91" + getIntent().getStringExtra("mobile"),
                        60,
                        TimeUnit.SECONDS,
                        verifyenterotptwo.this,
                        new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {



                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {

                                Toast.makeText(verifyenterotptwo.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                            }

                            @Override
                            public void onCodeSent(@NonNull String newbackendotp, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {

                                getotpbackend = newbackendotp;
                                Toast.makeText(verifyenterotptwo.this, "OTP sended successfully", Toast.LENGTH_SHORT).show();

                            }
                        }
                );
            }
        });
        autoOtpReceiver();
    }

    private void autoOtpReceiver() {
        otp_receiver = new OTP_Receiver();
        this.registerReceiver(otp_receiver, new IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION));
        otp_receiver.initListener(new OTP_Receiver.OtpReceiverListener() {
            @Override
            public void onOtpSuccess(String otp) {
                int o1 = Character.getNumericValue(otp.charAt(0));
                int o2 = Character.getNumericValue(otp.charAt(1));
                int o3 = Character.getNumericValue(otp.charAt(2));
                int o4 = Character.getNumericValue(otp.charAt(3));
                int o5 = Character.getNumericValue(otp.charAt(4));
                int o6 = Character.getNumericValue(otp.charAt(5));

                inputotp1.setText(String.valueOf(o1));
                inputotp2.setText(String.valueOf(o2));
                inputotp3.setText(String.valueOf(o3));
                inputotp4.setText(String.valueOf(o4));
                inputotp5.setText(String.valueOf(o5));
                inputotp6.setText(String.valueOf(o6));

            }

            @Override
            public void onOtpTimeout() {
                Toast.makeText(verifyenterotptwo.this, "Something went wrong!", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void numberotpmove() {
        inputnumber1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!charSequence.toString().trim().isEmpty()){
                    inputnumber2.requestFocus();
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        inputnumber2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!charSequence.toString().trim().isEmpty()){
                    inputnumber3.requestFocus();
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        inputnumber3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!charSequence.toString().trim().isEmpty()){
                    inputnumber4.requestFocus();
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        inputnumber4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!charSequence.toString().trim().isEmpty()){
                    inputnumber5.requestFocus();
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        inputnumber5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!charSequence.toString().trim().isEmpty()){
                    inputnumber6.requestFocus();
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        inputnumber6.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().trim().isEmpty()) {
                    // All OTP fields are filled, automatically trigger verification
                    verifyOtp();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

    }

    private void verifyOtp() {
        String entercodeotp = inputnumber1.getText().toString() +
                inputnumber2.getText().toString() +
                inputnumber3.getText().toString() +
                inputnumber4.getText().toString() +
                inputnumber5.getText().toString() +
                inputnumber6.getText().toString();

        if (getotpbackend != null) {
            progressBarverifyotp.setVisibility(View.VISIBLE);

            PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(
                    getotpbackend, entercodeotp
            );
            FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressBarverifyotp.setVisibility(View.GONE);

                            if (task.isSuccessful()) {
                                // Move to the next activity
                                Intent intent = new Intent(getApplicationContext(), NavigationActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            } else {
                                Toast.makeText(verifyenterotptwo.this, "Enter the correct OTP", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        } else {
            Toast.makeText(verifyenterotptwo.this, "Please check internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (otp_receiver != null){
            unregisterReceiver(otp_receiver);
        }
    }
}