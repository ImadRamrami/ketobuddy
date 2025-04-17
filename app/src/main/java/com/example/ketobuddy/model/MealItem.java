public class MealItem {
    public String name;
    public float calories, protein, fat, carbs;
    private float quantity = 100f; // default is per 100g

    public MealItem(String name, float calories, float protein, float fat, float carbs) {
        this.name = name;
        this.calories = calories;
        this.protein = protein;
        this.fat = fat;
        this.carbs = carbs;
        this.quantity = 100f;
    }

    public float getQuantity() {
        return quantity;
    }

    public void setQuantity(float quantity) {
        this.quantity = quantity;
    }
}


