package com.example.ketobuddy.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ketobuddy.R;
import com.example.ketobuddy.adapters.FoodAdapter;
import com.example.ketobuddy.api.FoodApiService;
import com.example.ketobuddy.api.RetrofitClient;
import com.example.ketobuddy.api.USDAFoodResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFoodActivity extends AppCompatActivity {

    private static final String API_KEY = "aifPgBqC4sC2rThadfW055IRt9zOSw2OvGGym1pb";

    private EditText searchInput;
    private TextView resultMessage;
    private RecyclerView resultsRecyclerView;
    private FoodAdapter foodAdapter;

    private final Handler handler = new Handler();
    private Runnable searchRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_food);

        searchInput = findViewById(R.id.searchInput);
        resultMessage = findViewById(R.id.resultMessage);
        resultsRecyclerView = findViewById(R.id.resultsRecyclerView);
        resultsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (searchRunnable != null) handler.removeCallbacks(searchRunnable);

                searchRunnable = () -> {
                    String query = s.toString().trim();
                    if (query.length() > 2) {
                        searchFood(query);
                    } else {
                        resultMessage.setText("Type at least 3 characters...");
                        resultsRecyclerView.setAdapter(null);
                    }
                };

                handler.postDelayed(searchRunnable, 500);
            }
        });
    }

    private void searchFood(String query) {
        resultMessage.setText("Searching...");

        FoodApiService api = RetrofitClient.getClient().create(FoodApiService.class);
        api.searchFoods(query, API_KEY).enqueue(new Callback<USDAFoodResponse>() {
            @Override
            public void onResponse(Call<USDAFoodResponse> call, Response<USDAFoodResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().foods != null) {
                    List<USDAFoodResponse.Food> rawList = response.body().foods;

                    List<USDAFoodResponse.Food> filtered = new ArrayList<>();
                    for (USDAFoodResponse.Food f : rawList) {
                        if (f.description.toLowerCase().contains(query.toLowerCase())) {
                            filtered.add(f);
                        }
                    }

                    if (filtered.isEmpty()) {
                        resultMessage.setText("No results found.");
                        resultsRecyclerView.setAdapter(null);
                        return;
                    }

                    resultMessage.setText("");
                    foodAdapter = new FoodAdapter(filtered, food -> {
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("foodName", food.description);
                        resultIntent.putExtra("calories", food.getCalories());
                        resultIntent.putExtra("protein", food.getProtein());
                        resultIntent.putExtra("fat", food.getFat());
                        resultIntent.putExtra("carbs", food.getCarbs());
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    });
                    resultsRecyclerView.setAdapter(foodAdapter);
                } else {
                    resultMessage.setText("No results found.");
                    resultsRecyclerView.setAdapter(null);
                }
            }

            @Override
            public void onFailure(Call<USDAFoodResponse> call, Throwable t) {
                Log.e("SearchFood", "API call failed", t);
                resultMessage.setText("Error fetching results.");
            }
        });
    }
}
