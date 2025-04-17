package com.example.ketobuddy.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.ketobuddy.R;
import com.example.ketobuddy.model.MealItem;

import java.util.List;

public class MealItemAdapter extends ArrayAdapter<MealItem> {

    private final Context context;
    private final List<MealItem> mealList;

    public MealItemAdapter(@NonNull Context context, @NonNull List<MealItem> mealList) {
        super(context, 0, mealList);
        this.context = context;
        this.mealList = mealList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        MealItem meal = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_meal, parent, false);
        }

        TextView nameText = convertView.findViewById(R.id.foodName);
        TextView macrosText = convertView.findViewById(R.id.macros);

        nameText.setText(meal.name);
        macrosText.setText(String.format("Kcal: %.0f | P: %.1fg | F: %.1fg | C: %.1fg",
                meal.calories, meal.protein, meal.fat, meal.carbs));

        // 🗑️ Long press to delete
        convertView.setOnLongClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Delete Item")
                    .setMessage("Remove " + meal.name + " from your dashboard?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        mealList.remove(position);
                        notifyDataSetChanged();
                    })
                    .setNegativeButton("No", null)
                    .show();
            return true;
        });

        return convertView;
    }
}

