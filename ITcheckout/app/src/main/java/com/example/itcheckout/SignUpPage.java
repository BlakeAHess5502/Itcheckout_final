package com.example.itcheckout;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.constraintlayout.widget.ConstraintLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.itcheckout.DatabaseHelper;

public class SignUpPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        EditText emailField = findViewById(R.id.emailEditText);
        EditText usernameField = findViewById(R.id.usernameEditText);
        EditText passwordField = findViewById(R.id.passwordEditText);
        EditText confirmPasswordField = findViewById(R.id.confirmPasswordEditText);

        Button submitButton = findViewById(R.id.submitButton);

        DatabaseHelper dbHelper = new DatabaseHelper(this);

        submitButton.setOnClickListener(v -> {
            String email = emailField.getText().toString();
            String username = usernameField.getText().toString();
            String password = passwordField.getText().toString();
            String confirmPassword = confirmPasswordField.getText().toString();

            if (!password.equals(confirmPassword)) {
                Toast.makeText(SignUpPage.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            } else {
                boolean inserted = dbHelper.addUser(username, password, email);
                if (inserted) {
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("email", email);
                    returnIntent.putExtra("username", username);
                    setResult(RESULT_OK, returnIntent);
                    finish(); // go back to previous activity
                } else {
                    Toast.makeText(SignUpPage.this, "Error saving user", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}