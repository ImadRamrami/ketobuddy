package com.example.ketobuddy;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface FoodApiService {
    @GET("fdc/v1/foods/search")
    Call<USDAFoodResponse> searchFoods(
            @Query("query") String query,
            @Query("api_key") String apiKey
    );
}
