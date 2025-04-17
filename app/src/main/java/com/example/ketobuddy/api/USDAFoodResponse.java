package com.example.ketobuddy.api;

import java.util.List;

public class USDAFoodResponse {
    public List<Food> foods;

    public static class Food {
        public String description;
        public List<Nutrient> foodNutrients;
    }

    public static class Nutrient {
        public String nutrientName;
        public float value;
    }
}
