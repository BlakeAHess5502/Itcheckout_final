package com.example.itcheckout;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.itcheckout.DatabaseHelper;
import com.example.itcheckout.userDatabase;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        EditText usernameEditText = findViewById(R.id.usernameEditText);
        EditText passwordEditText = findViewById(R.id.passwordEditText);
        Button loginButton = findViewById(R.id.loginButton);
        Button signupButton = findViewById(R.id.signupButton);

        DatabaseHelper dbHelper = new DatabaseHelper(this);

        // Ensure admin account exists
        if (!dbHelper.checkUser("admin", "admin123")) {
            dbHelper.addUser("admin", "admin123", "admin@admin.com");
        }

        loginButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            if (username.equals("admin") && password.equals("admin123")) {
                Intent adminIntent = new Intent(MainActivity.this, userDatabase.class);
                startActivity(adminIntent);
            } else if (dbHelper.checkUser(username, password)) {
                Toast.makeText(MainActivity.this, "Logged in as " + username, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
            }
        });

        signupButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SignUpPage.class);
            startActivityForResult(intent, 1);
        });

        //Button databaseButton = findViewById(R.id.databaseButton);
        //if (databaseButton != null) {
         //   databaseButton.setOnClickListener(v -> {
          //     Intent intent = new Intent(MainActivity.this, userDatabase.class);
            //    startActivity(intent);
           // });
        //}
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            String email = data.getStringExtra("email");
            String username = data.getStringExtra("username");
            Toast.makeText(this, "Account created for " + username, Toast.LENGTH_SHORT).show();
        }
    }
}