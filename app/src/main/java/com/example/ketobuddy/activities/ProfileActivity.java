package com.example.ketobuddy.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ketobuddy.R;

import java.util.Calendar;

public class ProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    EditText nameInput, emailInput, weightInput, targetGoalInput, dobInput, weeksInput;
    Spinner sexSpinner, goalSpinner, unitSpinner, activitySpinner;
    ImageView profileImage;
    Button saveButton, logoutButton;
    Uri selectedImageUri = null;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Bind views
        nameInput = findViewById(R.id.editNameInput);
        emailInput = findViewById(R.id.editEmailInput);
        weightInput = findViewById(R.id.weightInput);
        targetGoalInput = findViewById(R.id.targetGoalInput);
        dobInput = findViewById(R.id.dobInput);
        weeksInput = findViewById(R.id.weeksInput); // 🔥 New

        sexSpinner = findViewById(R.id.sexSpinner);
        goalSpinner = findViewById(R.id.goalSpinner);
        unitSpinner = findViewById(R.id.unitSpinner);
        activitySpinner = findViewById(R.id.activitySpinner); // 🔥 New

        profileImage = findViewById(R.id.profileImage);
        saveButton = findViewById(R.id.saveProfileButton);
        logoutButton = findViewById(R.id.logoutButton);

        preferences = getSharedPreferences("KetoBuddyPrefs", MODE_PRIVATE);
        editor = preferences.edit();

        // Load existing values
        nameInput.setText(preferences.getString("name", ""));
        emailInput.setText(preferences.getString("email", ""));
        weightInput.setText(preferences.getString("weight", ""));
        targetGoalInput.setText(preferences.getString("targetGoal", ""));
        dobInput.setText(preferences.getString("dob", ""));
        weeksInput.setText(preferences.getString("weeksToGoal", ""));

        loadSpinner(sexSpinner, R.array.sex_options, preferences.getString("sex", ""));
        loadSpinner(goalSpinner, R.array.goal_options, preferences.getString("goal", ""));
        loadSpinner(unitSpinner, R.array.unit_options, preferences.getString("units", ""));
        loadSpinner(activitySpinner, R.array.activity_levels, preferences.getString("activity", ""));

        // Load saved image
        String imageUri = preferences.getString("profileImageUri", null);
        if (imageUri != null) {
            profileImage.setImageURI(Uri.parse(imageUri));
        }

        // Image click
        profileImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "Select Profile Picture"), PICK_IMAGE_REQUEST);
        });

        // Date Picker
        dobInput.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int y = calendar.get(Calendar.YEAR);
            int m = calendar.get(Calendar.MONTH);
            int d = calendar.get(Calendar.DAY_OF_MONTH);
            new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                dobInput.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
            }, y, m, d).show();
        });

        // Save Profile
        saveButton.setOnClickListener(v -> {
            editor.putString("name", nameInput.getText().toString().trim());
            editor.putString("email", emailInput.getText().toString().trim());
            editor.putString("weight", weightInput.getText().toString().trim());
            editor.putString("targetGoal", targetGoalInput.getText().toString().trim());
            editor.putString("dob", dobInput.getText().toString().trim());
            editor.putString("weeksToGoal", weeksInput.getText().toString().trim());
            editor.putString("sex", sexSpinner.getSelectedItem().toString());
            editor.putString("goal", goalSpinner.getSelectedItem().toString());
            editor.putString("units", unitSpinner.getSelectedItem().toString());
            editor.putString("activity", activitySpinner.getSelectedItem().toString());
            if (selectedImageUri != null) {
                editor.putString("profileImageUri", selectedImageUri.toString());
            }
            editor.apply();
            Toast.makeText(this, "Profile saved", Toast.LENGTH_SHORT).show();
        });

        // Logout
        logoutButton.setOnClickListener(v -> {
            editor.putBoolean("isLoggedIn", false);
            editor.apply();
            startActivity(new Intent(this, LoginActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        });
    }

    private void loadSpinner(Spinner spinner, int arrayResId, String selected) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, arrayResId, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        if (!selected.isEmpty()) {
            int pos = adapter.getPosition(selected);
            spinner.setSelection(pos);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            profileImage.setImageURI(selectedImageUri);
        }
    }
}


