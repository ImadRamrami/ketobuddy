package com.example.ketobuddy.activities;
import com.example.ketobuddy.utils.ThemeHelper;

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

        loginEmailInput = findViewById(R.id.loginEmailInput);
        loginPasswordInput = findViewById(R.id.loginPasswordInput);
        loginButton = findViewById(R.id.loginButton);
        goToRegisterButton = findViewById(R.id.goToRegisterButton);

        loginButton.setOnClickListener(v -> {
            String email = loginEmailInput.getText().toString();
            String password = loginPasswordInput.getText().toString();

            SharedPreferences preferences = getSharedPreferences("KetoBuddyPrefs", MODE_PRIVATE);
            String savedEmail = preferences.getString("email", "");
            String savedPassword = preferences.getString("password", "");

            if (email.equals(savedEmail) && password.equals(savedPassword)) {
                // Set isLoggedIn = true
                preferences.edit().putBoolean("isLoggedIn", true).apply();

                Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                finish();
            } else {
                Toast.makeText(LoginActivity.this, "Wrong credentials!", Toast.LENGTH_SHORT).show();
            }
        });

        goToRegisterButton.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            finish();
        });
    }
}
