package com.example.ketobuddy.activities;
import com.example.ketobuddy.utils.ThemeHelper;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ketobuddy.R;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {


    private static final int REQUEST_CODE_ADD_MEAL = 100;
    private ArrayList<String> mealList = new ArrayList<>();
    private ArrayAdapter<String> mealAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Get user info from SharedPreferences
        SharedPreferences preferences = getSharedPreferences("KetoBuddyPrefs", MODE_PRIVATE);
        String name = preferences.getString("name", "User");

        // Set welcome message
        TextView welcomeText = findViewById(R.id.welcomeText);
        welcomeText.setText("Welcome, " + name + "!");

        // Meal List setup
        ListView mealListView = findViewById(R.id.mealListView);
        mealAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mealList);
        mealListView.setAdapter(mealAdapter);

        // Add Meal Button
        Button addMealButton = findViewById(R.id.addMealButton);
        addMealButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, SearchFoodActivity.class);
            startActivityForResult(intent, REQUEST_CODE_ADD_MEAL);
        });

        // Logout Button
        Button logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(v -> {
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear(); // Clear user data
            editor.apply();

            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        // Profile Button
        Button profileButton = findViewById(R.id.profileButton);
        profileButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_ADD_MEAL && resultCode == RESULT_OK && data != null) {
            String meal = data.getStringExtra("meal");
            if (meal != null) {
                mealList.add(meal);
                mealAdapter.notifyDataSetChanged();
            }
        }
    }
}
