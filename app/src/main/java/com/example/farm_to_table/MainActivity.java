package com.example.farm_to_table;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private FirebaseAnalytics mFirebaseAnalytics;
    private Button btnSignOut, btnaccount, haveaccount;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase Auth and Analytics
        firebaseAuth = FirebaseAuth.getInstance();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // Initialize UI elements
        //btnSignOut = findViewById(R.id.btnSignOut);
        ImageView back = findViewById(R.id.product);
        TextView farm = findViewById(R.id.farm);
        TextView motto = findViewById(R.id.motto);
        btnaccount = findViewById(R.id.btnaccount);
        haveaccount = findViewById(R.id.haveaccount);

        btnaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CreateAccountActivity.class));
                finish();
            }
        });

        haveaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SignInActivity.class));
                finish();
            }
        });



//        btnSignOut.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                firebaseAuth.signOut();
//                startActivity(new Intent(MainActivity.this, CreateAccountActivity.class));
//                toast("signed out");
//                finish();
//            }
//        });
//    }

//    private void toast(String message) {
//        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
//    }
    }
}