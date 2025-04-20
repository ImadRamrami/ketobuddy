package com.example.ketobuddy.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.app.AlertDialog;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ketobuddy.R;
import com.example.ketobuddy.model.MealItem;
import com.example.ketobuddy.adapters.MealItemAdapter;
import com.github.lzyzsd.circleprogress.DonutProgress;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.*;

public class HomeActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_ADD_MEAL = 100;
    private static final String PREFS_NAME = "KetoBuddyPrefs";
    private static final String MEAL_MAP_KEY = "mealsByDate";

    private Map<String, List<MealItem>> mealsByDate = new HashMap<>();
    private MealItemAdapter mealAdapter;
    private String currentDateKey;

    private TextView dateText, calorieProgressText;
    private ListView mealListView;

    private ProgressBar calorieProgressBar;
    private DonutProgress proteinCircle, carbsCircle, fatCircle;

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

        calorieProgressText = findViewById(R.id.calorieProgressText);
        calorieProgressBar = findViewById(R.id.calorieProgressBar);

        proteinCircle = findViewById(R.id.proteinCircle);
        carbsCircle = findViewById(R.id.carbsCircle);
        fatCircle = findViewById(R.id.fatCircle);

        Button prevDayButton = findViewById(R.id.prevDayButton);
        Button nextDayButton = findViewById(R.id.nextDayButton);

        loadMealsFromPrefs();

        currentDateKey = getDateKey(new Date());
        updateDateLabel();
        setupMealListView();

        prevDayButton.setOnClickListener(v -> changeDateBy(-1));
        nextDayButton.setOnClickListener(v -> changeDateBy(1));

        findViewById(R.id.addMealButton).setOnClickListener(v ->
                startActivityForResult(new Intent(this, SearchFoodActivity.class), REQUEST_CODE_ADD_MEAL)
        );

        findViewById(R.id.profileButton).setOnClickListener(v ->
                startActivity(new Intent(this, ProfileActivity.class))
        );
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

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        float currentWeight = parseFloatSafe(prefs.getString("weight", "0"));
        float targetWeight = parseFloatSafe(prefs.getString("targetGoal", "0"));
        float weeks = parseFloatSafe(prefs.getString("weeksToGoal", "1"));
        String sex = prefs.getString("sex", "Male");
        String activity = prefs.getString("activity", "Moderately active (3–5 days/week)");

        float baseCalories = currentWeight * 22;
        float activityFactor = getActivityMultiplier(activity);
        float tdee = baseCalories * activityFactor;

        float deltaKg = targetWeight - currentWeight;
        float kcalChange = (deltaKg * 7700f) / (weeks * 7f);
        float dailyGoal = tdee + kcalChange;

        float proteinGoal = currentWeight * 1.9f;
        float carbGoal = (dailyGoal * 0.2f) / 4f;
        float fatGoal = (dailyGoal - (proteinGoal * 4f + carbGoal * 4f)) / 9f;

        float calPercent = (dailyGoal > 0) ? (totalCalories / dailyGoal) * 100f : 0f;
        calorieProgressBar.setProgress((int) Math.min(calPercent, 100));
        calorieProgressText.setText(String.format(Locale.getDefault(), "%.0f / %.0f kcal (%.0f%%)", totalCalories, dailyGoal, calPercent));

        float pPercent = (proteinGoal > 0) ? (totalProtein / proteinGoal) * 100f : 0f;
        float cPercent = (carbGoal > 0) ? (totalCarbs / carbGoal) * 100f : 0f;
        float fPercent = (fatGoal > 0) ? (totalFat / fatGoal) * 100f : 0f;

        proteinCircle.setProgress(Math.min(pPercent, 100));
        proteinCircle.setText(String.format(Locale.getDefault(), "%.0fg", totalProtein));

        carbsCircle.setProgress(Math.min(cPercent, 100));
        carbsCircle.setText(String.format(Locale.getDefault(), "%.0fg", totalCarbs));

        fatCircle.setProgress(Math.min(fPercent, 100));
        fatCircle.setText(String.format(Locale.getDefault(), "%.0fg", totalFat));
    }

    private float parseFloatSafe(String input) {
        try { return Float.parseFloat(input); } catch (Exception e) { return 0f; }
    }

    private float getActivityMultiplier(String level) {
        if (level.contains("Sedentary")) return 1.2f;
        if (level.contains("Lightly")) return 1.375f;
        if (level.contains("Moderately")) return 1.55f;
        if (level.contains("Very active")) return 1.725f;
        if (level.contains("Super active")) return 1.9f;
        return 1.2f;
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
                    try {
                        float newQuantity = Float.parseFloat(input.getText().toString().trim());
                        meal.setQuantity(newQuantity);

                        List<MealItem> meals = mealsByDate.getOrDefault(currentDateKey, new ArrayList<>());
                        meals.set(position, meal);
                        mealsByDate.put(currentDateKey, meals);

                        mealAdapter.clear();
                        mealAdapter.addAll(meals);
                        mealAdapter.notifyDataSetChanged();
                        saveMealsToPrefs();
                        updateNutrientTotals();
                    } catch (Exception e) {
                        e.printStackTrace();
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
        String todayKey = getDateKey(new Date());
        String label = currentDateKey.equals(todayKey)
                ? "Today (" + formatDisplayDate(currentDateKey) + ")"
                : formatDisplayDate(currentDateKey);
        dateText.setText(label);
    }

    private String formatDisplayDate(String dateKey) {
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dateKey);
            return new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(date);
        } catch (Exception e) {
            return dateKey;
        }
    }

    private String getDateKey(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date);
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

    private void saveMealsToPrefs() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        prefs.edit().putString(MEAL_MAP_KEY, new Gson().toJson(mealsByDate)).apply();
    }

    private void loadMealsFromPrefs() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String json = prefs.getString(MEAL_MAP_KEY, null);
        if (json != null) {
            java.lang.reflect.Type type = new TypeToken<Map<String, List<MealItem>>>() {}.getType();
            mealsByDate = new Gson().fromJson(json, type);
        }
    }
}

