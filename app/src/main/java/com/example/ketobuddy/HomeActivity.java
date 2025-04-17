package com.example.ketobuddy;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_ADD_MEAL = 100;
    private ArrayList<String> mealList = new ArrayList<>();
    private ArrayAdapter<String> mealAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ListView mealListView = findViewById(R.id.mealListView);
        mealAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mealList);
        mealListView.setAdapter(mealAdapter);

        Button addMealButton = findViewById(R.id.addMealButton);
        addMealButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, SearchFoodActivity.class);
            startActivityForResult(intent, REQUEST_CODE_ADD_MEAL);
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
