package com.example.ketobuddy.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class LauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if user is already logged in
        SharedPreferences preferences = getSharedPreferences("KetoBuddyPrefs", MODE_PRIVATE);
        String savedEmail = preferences.getString("email", null);
        String savedPassword = preferences.getString("password", null);

        boolean isLoggedIn = (savedEmail != null && savedPassword != null);

        Intent intent;
        if (isLoggedIn) {
            intent = new Intent(this, HomeActivity.class);
        } else {
            intent = new Intent(this, LoginActivity.class);
        }

        startActivity(intent);
        finish(); // Close this activity so user can't go "back" to it
    }
}
