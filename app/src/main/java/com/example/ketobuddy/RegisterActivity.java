package com.example.ketobuddy;  // <-- Keep your real package here
//can register twice w same info
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.content.SharedPreferences;

public class RegisterActivity extends AppCompatActivity {

    EditText registerEmailInput, registerPasswordInput, registerConfirmPasswordInput;
    Button registerButton, goToLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        registerEmailInput = findViewById(R.id.registerEmailInput);
        registerPasswordInput = findViewById(R.id.registerPasswordInput);
        registerConfirmPasswordInput = findViewById(R.id.registerConfirmPasswordInput);
        registerButton = findViewById(R.id.registerButton);
        goToLoginButton = findViewById(R.id.goToLoginButton);

        // Handle Register Button
        registerButton.setOnClickListener(v -> {
            String email = registerEmailInput.getText().toString();
            String password = registerPasswordInput.getText().toString();
            String confirmPassword = registerConfirmPasswordInput.getText().toString();

            if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else if (!password.equals(confirmPassword)) {
                Toast.makeText(RegisterActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            } else {
                // Save email & password in SharedPreferences
                SharedPreferences preferences = getSharedPreferences("KetoBuddyPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("email", email);
                editor.putString("password", password);
                editor.apply(); // ✅ Save

                Toast.makeText(RegisterActivity.this, "Registered successfully!", Toast.LENGTH_SHORT).show();

// Go to Login screen
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Handle "Already have an account?" Button
        goToLoginButton.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
