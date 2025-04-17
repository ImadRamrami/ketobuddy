package com.example.ketobuddy.model;

public class MealItem {
    public String name;
    public float calories, protein, fat, carbs;

    public MealItem(String name, float calories, float protein, float fat, float carbs) {
        this.name = name;
        this.calories = calories;
        this.protein = protein;
        this.fat = fat;
        this.carbs = carbs;
    }
}

