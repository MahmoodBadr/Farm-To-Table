package com.example.farm_to_table;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.MapView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignInActivity extends AppCompatActivity {

    private String signInEmail;
    private String signInPassword;
    private EditText[] signInInputsArray;
    private EditText etSignInEmail, etSignInPassword;
    private Button btnCreateAccount2, btnSignIn;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();

        // Initialize UI elements
        etSignInEmail = findViewById(R.id.etSignInEmail);
        etSignInPassword = findViewById(R.id.etSignInPassword);
        btnCreateAccount2 = findViewById(R.id.btnCreateAccount2);
        btnSignIn = findViewById(R.id.btnSignIn);

        signInInputsArray = new EditText[]{etSignInEmail, etSignInPassword};

        btnCreateAccount2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignInActivity.this, CreateAccountActivity.class));
                finish();
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInUser();
            }
        });
    }

    private boolean notEmpty() {
        return !signInEmail.isEmpty() && !signInPassword.isEmpty();
    }

    private void signInUser() {
        signInEmail = etSignInEmail.getText().toString().trim();
        signInPassword = etSignInPassword.getText().toString().trim();

        if (notEmpty()) {
            firebaseAuth.signInWithEmailAndPassword(signInEmail, signInPassword)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> signIn) {
                            if (signIn.isSuccessful()) {
                                startActivity(new Intent(SignInActivity.this, MapViewActivity.class));
                                toast("signed in successfully");
                                finish();
                            } else {
                                toast("The account does not exists, please create account");
                            }

                        }
                    });
        } else {
            for (EditText input : signInInputsArray) {
                if (input.getText().toString().trim().isEmpty()) {
                    input.setError(input.getHint() + " is required");
                }
            }
        }
    }

    private void toast(String message) {
        Toast.makeText(SignInActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}