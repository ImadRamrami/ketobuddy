package com.example.ketobuddy.api;

import java.util.List;

public class USDAFoodResponse {

    public List<Food> foods;

    public static class Food {
        public String description;
        public List<Nutrient> foodNutrients;

        public float getCalories() {
            return getNutrientValue("Energy");
        }

        public float getProtein() {
            return getNutrientValue("Protein");
        }

        public float getFat() {
            return getNutrientValue("Total lipid (fat)");
        }

        public float getCarbs() {
            return getNutrientValue("Carbohydrate, by difference");
        }

        private float getNutrientValue(String name) {
            if (foodNutrients == null) return 0f;
            for (Nutrient n : foodNutrients) {
                if (n.nutrientName.equalsIgnoreCase(name)) {
                    return n.value;
                }
            }
            return 0f;
        }
    }

    public static class Nutrient {
        public String nutrientName;
        public float value;
    }
}

