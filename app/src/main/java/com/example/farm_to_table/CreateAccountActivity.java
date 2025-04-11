package com.example.farm_to_table;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class CreateAccountActivity extends AppCompatActivity {
    private EditText email, password, confirm_password;
    private Button btnCreateAccount, btnSignIn;
    private FirebaseAuth mAuth;
    private String userEmail, userPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_account);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize UI elements
        email = findViewById(R.id.etEmail);
        password = findViewById(R.id.etPassword);
        confirm_password = findViewById(R.id.etConfirmPassword);
        btnCreateAccount = findViewById(R.id.btnCreateAccount);
        btnSignIn = findViewById(R.id.btnSignIn2);

        // Set click listeners
        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();
            }
        });

        if (btnSignIn != null) {
            btnSignIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Navigate to sign-in page
                    startActivity(new Intent(CreateAccountActivity.this, SignInActivity.class));
                    finish();
                }
            });
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is already signed in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null && currentUser.isEmailVerified()) {
            // User already signed in and verified, navigate to MapView
            startActivity(new Intent(CreateAccountActivity.this, MapViewActivity.class));
            finish();
        }
    }

    private boolean validateInputs() {
        userEmail = email.getText().toString().trim();
        userPassword = password.getText().toString().trim();
        String confirmPass = confirm_password.getText().toString().trim();

        if (userEmail.isEmpty()) {
            email.setError("Email is required");
            return false;
        }

        if (userPassword.isEmpty()) {
            password.setError("Password is required");
            return false;
        }

        if (confirmPass.isEmpty()) {
            confirm_password.setError("Please confirm your password");
            return false;
        }

        if (!userPassword.equals(confirmPass)) {
            confirm_password.setError("Passwords do not match");
            return false;
        }

        if (userPassword.length() < 6) {
            password.setError("Password must be at least 6 characters");
            return false;
        }

        return true;
    }

    private void createAccount() {
        if (!validateInputs()) {
            return;
        }

        // Show progress dialog or loading indicator here if needed

        mAuth.createUserWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(CreateAccountActivity.this, "Account created successfully",
                                    Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(CreateAccountActivity.this, MapViewActivity.class));
                            finish();
                        } else {
                            String errorMessage = task.getException() != null ?
                                    task.getException().getMessage() : "Invalid email or password";
                            Toast.makeText(CreateAccountActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                            email.setError("Invalid email");
                            password.setError("Invalid password");
                        }
                    }
                });
    }


}