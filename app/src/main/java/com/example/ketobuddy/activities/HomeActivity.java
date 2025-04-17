package com.example.ketobuddy.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.app.AlertDialog;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ketobuddy.R;
import com.example.ketobuddy.model.MealItem;
import com.example.ketobuddy.adapters.MealItemAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_ADD_MEAL = 100;
    private static final String PREFS_NAME = "KetoBuddyPrefs";
    private static final String MEAL_MAP_KEY = "mealsByDate";

    private Map<String, List<MealItem>> mealsByDate = new HashMap<>();
    private MealItemAdapter mealAdapter;
    private String currentDateKey;

    private TextView dateText;
    private ListView mealListView;

    private TextView totalCaloriesText;
    private TextView totalProteinText;
    private TextView totalFatText;
    private TextView totalCarbsText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String name = preferences.getString("name", "User");

        TextView welcomeText = findViewById(R.id.welcomeText);
        welcomeText.setText("Welcome, " + name + "!");

        dateText = findViewById(R.id.dateText);
        mealListView = findViewById(R.id.mealListView);

        totalCaloriesText = findViewById(R.id.totalCalories);
        totalProteinText = findViewById(R.id.totalProtein);
        totalFatText = findViewById(R.id.totalFat);
        totalCarbsText = findViewById(R.id.totalCarbs);

        Button prevDayButton = findViewById(R.id.prevDayButton);
        Button nextDayButton = findViewById(R.id.nextDayButton);

        loadMealsFromPrefs();

        currentDateKey = getDateKey(new Date());
        updateDateLabel();
        setupMealListView();

        prevDayButton.setOnClickListener(v -> changeDateBy(-1));
        nextDayButton.setOnClickListener(v -> changeDateBy(1));

        Button addMealButton = findViewById(R.id.addMealButton);
        addMealButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, SearchFoodActivity.class);
            startActivityForResult(intent, REQUEST_CODE_ADD_MEAL);
        });

        // ✅ New Profile button logic
        Button profileButton = findViewById(R.id.profileButton);
        profileButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
            startActivity(intent);
        });
    }

    private void setupMealListView() {
        List<MealItem> currentMeals = mealsByDate.getOrDefault(currentDateKey, new ArrayList<>());

        if (mealAdapter == null) {
            mealAdapter = new MealItemAdapter(this, new ArrayList<>());
            mealListView.setAdapter(mealAdapter);
        }

        mealAdapter.clear();
        mealAdapter.addAll(currentMeals);
        mealAdapter.notifyDataSetChanged();

        updateNutrientTotals();

        mealListView.setOnItemLongClickListener((parent, view, position, id) -> {
            MealItem meal = mealAdapter.getItem(position);

            new AlertDialog.Builder(this)
                    .setTitle("Edit or Delete")
                    .setMessage("What would you like to do with \"" + meal.name + "\"?")
                    .setPositiveButton("Edit Quantity", (dialog, which) -> {
                        showEditMealDialog(position, meal);
                    })
                    .setNegativeButton("Delete", (dialog, which) -> {
                        currentMeals.remove(position);
                        mealAdapter.clear();
                        mealAdapter.addAll(currentMeals);
                        mealAdapter.notifyDataSetChanged();
                        mealsByDate.put(currentDateKey, currentMeals);
                        saveMealsToPrefs();
                        updateNutrientTotals();
                    })
                    .setNeutralButton("Cancel", null)
                    .show();
            return true;
        });
    }

    private void showEditMealDialog(int position, MealItem meal) {
        EditText input = new EditText(this);
        input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        input.setHint("Quantity in grams");
        input.setText(String.format(Locale.getDefault(), "%.0f", meal.getQuantity()));

        new AlertDialog.Builder(this)
                .setTitle("Update quantity for " + meal.name)
                .setView(input)
                .setPositiveButton("Update", (dialog, which) -> {
                    String inputText = input.getText().toString().trim();
                    if (!inputText.isEmpty()) {
                        try {
                            float newQuantity = Float.parseFloat(inputText);
                            meal.setQuantity(newQuantity);

                            List<MealItem> meals = mealsByDate.getOrDefault(currentDateKey, new ArrayList<>());
                            meals.set(position, meal);
                            mealsByDate.put(currentDateKey, meals);

                            mealAdapter.clear();
                            mealAdapter.addAll(meals);
                            mealAdapter.notifyDataSetChanged();
                            saveMealsToPrefs();
                            updateNutrientTotals();
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void changeDateBy(int days) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date currentDate = sdf.parse(currentDateKey);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(currentDate);
            calendar.add(Calendar.DAY_OF_YEAR, days);
            currentDateKey = sdf.format(calendar.getTime());

            updateDateLabel();
            setupMealListView();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateDateLabel() {
        String label;
        String todayKey = getDateKey(new Date());
        if (currentDateKey.equals(todayKey)) {
            label = "Today (" + formatDisplayDate(currentDateKey) + ")";
        } else {
            label = formatDisplayDate(currentDateKey);
        }
        dateText.setText(label);
    }

    private String formatDisplayDate(String dateKey) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = sdf.parse(dateKey);
            SimpleDateFormat output = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
            return output.format(date);
        } catch (Exception e) {
            return dateKey;
        }
    }

    private String getDateKey(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(date);
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
            List<MealItem> meals = mealsByDate.getOrDefault(currentDateKey, new ArrayList<>());
            meals.add(meal);
            mealsByDate.put(currentDateKey, meals);

            mealAdapter.clear();
            mealAdapter.addAll(meals);
            mealAdapter.notifyDataSetChanged();

            saveMealsToPrefs();
            updateNutrientTotals();
        }
    }

    private void updateNutrientTotals() {
        List<MealItem> currentMeals = mealsByDate.getOrDefault(currentDateKey, new ArrayList<>());

        float totalCalories = 0, totalProtein = 0, totalFat = 0, totalCarbs = 0;

        for (MealItem meal : currentMeals) {
            totalCalories += meal.getCalories();
            totalProtein += meal.getProtein();
            totalFat += meal.getFat();
            totalCarbs += meal.getCarbs();
        }

        totalCaloriesText.setText(String.format("Calories: %.0f kcal", totalCalories));
        totalProteinText.setText(String.format("Protein: %.1f g", totalProtein));
        totalFatText.setText(String.format("Fat: %.1f g", totalFat));
        totalCarbsText.setText(String.format("Carbs: %.1f g", totalCarbs));
    }

    private void saveMealsToPrefs() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(mealsByDate);
        editor.putString(MEAL_MAP_KEY, json);
        editor.apply();
    }

    private void loadMealsFromPrefs() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String json = prefs.getString(MEAL_MAP_KEY, null);
        if (json != null) {
            Gson gson = new Gson();
            java.lang.reflect.Type type = new TypeToken<Map<String, List<MealItem>>>() {}.getType();
            mealsByDate = gson.fromJson(json, type);
        }
    }
}
