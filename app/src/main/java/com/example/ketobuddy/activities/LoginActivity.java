package com.example.ketobuddy.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.content.SharedPreferences;

import com.example.ketobuddy.R;

public class LoginActivity extends AppCompatActivity {

    EditText loginEmailInput, loginPasswordInput;
    Button loginButton, goToRegisterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Link XML views to Java
        loginEmailInput = findViewById(R.id.loginEmailInput);
        loginPasswordInput = findViewById(R.id.loginPasswordInput);
        loginButton = findViewById(R.id.loginButton);
        goToRegisterButton = findViewById(R.id.goToRegisterButton);

        // Login button logic
        loginButton.setOnClickListener(v -> {
            String email = loginEmailInput.getText().toString().trim();
            String password = loginPasswordInput.getText().toString().trim();

            // Basic validation
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter both email and password", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                return;
            }

            // Get saved credentials
            SharedPreferences preferences = getSharedPreferences("KetoBuddyPrefs", MODE_PRIVATE);
            String savedEmail = preferences.getString("email", "");
            String savedPassword = preferences.getString("password", "");

            // Compare entered with saved
            if (email.equals(savedEmail) && password.equals(savedPassword)) {
                Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();

                // Optionally store login status
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("isLoggedIn", true);
                editor.apply();

                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(LoginActivity.this, "Wrong credentials!", Toast.LENGTH_SHORT).show();
            }

        });

        // Go to Register screen
        goToRegisterButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
