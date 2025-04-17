package com.example.ketobuddy.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

public class ThemeHelper {
    public static void applyTheme(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("KetoBuddyPrefs", Context.MODE_PRIVATE);
        boolean darkMode = preferences.getBoolean("darkMode", false);
        AppCompatDelegate.setDefaultNightMode(
                darkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );
    }
}
