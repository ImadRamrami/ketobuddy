package com.example.ketobuddy.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ketobuddy.R;
import com.example.ketobuddy.api.USDAFoodResponse;

import java.util.List;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> {

    public interface OnFoodClickListener {
        void onFoodClick(USDAFoodResponse.Food food);
    }

    private final List<USDAFoodResponse.Food> foodList;
    private final OnFoodClickListener listener;

    public FoodAdapter(List<USDAFoodResponse.Food> foodList, OnFoodClickListener listener) {
        this.foodList = foodList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_item, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        USDAFoodResponse.Food food = foodList.get(position);

        holder.nameText.setText(food.description);
        holder.macroText.setText(
                String.format("Per 100g: %.0f kcal | P: %.1fg | F: %.1fg | C: %.1fg",
                        food.getCalories(), food.getProtein(), food.getFat(), food.getCarbs())
        );

        holder.itemView.setOnClickListener(v -> {
            Context context = holder.itemView.getContext();

            EditText input = new EditText(context);
            input.setHint("Quantity in grams");
            input.setInputType(InputType.TYPE_CLASS_NUMBER);

            new AlertDialog.Builder(context)
                    .setTitle(food.description)
                    .setMessage("Enter amount in grams:")
                    .setView(input)
                    .setPositiveButton("Add", (dialog, which) -> {
                        String quantityText = input.getText().toString().trim();
                        if (!quantityText.isEmpty()) {
                            float quantity = Float.parseFloat(quantityText);
                            float factor = quantity / 100f;

                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("foodName", food.description + " (" + quantity + "g)");
                            resultIntent.putExtra("calories", food.getCalories() * factor);
                            resultIntent.putExtra("protein", food.getProtein() * factor);
                            resultIntent.putExtra("fat", food.getFat() * factor);
                            resultIntent.putExtra("carbs", food.getCarbs() * factor);

                            ((android.app.Activity) context).setResult(android.app.Activity.RESULT_OK, resultIntent);
                            ((android.app.Activity) context).finish();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    public static class FoodViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, macroText;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.foodNameText);
            macroText = itemView.findViewById(R.id.foodMacroText);
        }
    }
}

