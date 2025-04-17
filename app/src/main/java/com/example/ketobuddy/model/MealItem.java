package com.example.ketobuddy.model;

public class MealItem {
    public String name;
    public float baseCalories, baseProtein, baseFat, baseCarbs;
    private float quantity = 100f;

    public MealItem(String name, float calories, float protein, float fat, float carbs) {
        this.name = name;
        this.baseCalories = calories;
        this.baseProtein = protein;
        this.baseFat = fat;
        this.baseCarbs = carbs;
        this.quantity = 100f;
    }

    public float getQuantity() {
        return quantity;
    }

    public void setQuantity(float quantity) {
        this.quantity = quantity;
    }

    public float getCalories() {
        return baseCalories * (quantity / 100f);
    }

    public float getProtein() {
        return baseProtein * (quantity / 100f);
    }

    public float getFat() {
        return baseFat * (quantity / 100f);
    }

    public float getCarbs() {
        return baseCarbs * (quantity / 100f);
    }
}



