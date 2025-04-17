package com.example.ketobuddy.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ketobuddy.R;
import com.example.ketobuddy.model.MealItem;
import com.example.ketobuddy.adapters.MealItemAdapter;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_ADD_MEAL = 100;
    private List<MealItem> mealList = new ArrayList<>();
    private MealItemAdapter mealAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ListView mealListView = findViewById(R.id.mealListView);
        mealAdapter = new MealItemAdapter(this, mealList);
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
            String name = data.getStringExtra("foodName");
            float calories = data.getFloatExtra("calories", 0);
            float protein = data.getFloatExtra("protein", 0);
            float fat = data.getFloatExtra("fat", 0);
            float carbs = data.getFloatExtra("carbs", 0);

            MealItem meal = new MealItem(name, calories, protein, fat, carbs);
            mealList.add(meal);
            mealAdapter.notifyDataSetChanged();
        }
    }
}

