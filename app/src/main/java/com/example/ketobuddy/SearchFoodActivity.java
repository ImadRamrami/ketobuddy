package com.example.ketobuddy;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFoodActivity extends AppCompatActivity {

    private static final String API_KEY = "aifPgBqC4sC2rThadfW055IRt9zOSw2OvGGym1pb";

    private EditText searchInput;
    private Button searchButton;
    private TextView resultMessage;
    private RecyclerView resultsRecyclerView;

    private FoodAdapter foodAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_food);

        searchInput = findViewById(R.id.searchInput);
        searchButton = findViewById(R.id.searchButton);
        resultMessage = findViewById(R.id.resultMessage);
        resultsRecyclerView = findViewById(R.id.resultsRecyclerView);
        resultsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        searchButton.setOnClickListener(v -> {
            String query = searchInput.getText().toString().trim();
            if (!query.isEmpty()) {
                searchFood(query);
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
                    List<USDAFoodResponse.Food> foodList = response.body().foods;

                    if (foodList.isEmpty()) {
                        resultMessage.setText("No results found.");
                        resultsRecyclerView.setAdapter(null);
                    } else {
                        resultMessage.setText("");

                        foodAdapter = new FoodAdapter(foodList, food -> {
                            // When a food is clicked, return its name to HomeActivity
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("foodName", food.description);
                            setResult(RESULT_OK, resultIntent);
                            finish();
                        });

                        resultsRecyclerView.setAdapter(foodAdapter);
                    }
                } else {
                    resultMessage.setText("No results found.");
                    Log.e("SearchFood", "API error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<USDAFoodResponse> call, Throwable t) {
                resultMessage.setText("Search failed.");
                Log.e("SearchFood", "API failure: " + t.getMessage());
            }
        });
    }
}