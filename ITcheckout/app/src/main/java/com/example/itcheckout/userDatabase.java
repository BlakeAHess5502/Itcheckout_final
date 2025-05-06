package com.example.itcheckout;

import android.widget.Button;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.ArrayList;
import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.app.AlertDialog;
import android.widget.EditText;
import android.widget.LinearLayout;

// DatabaseHelper class definition
class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ITCheckout.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE users (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT, password TEXT, email TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS users");
        onCreate(db);
    }

    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = { "id" };
        String selection = "username=? AND password=?";
        String[] selectionArgs = { username, password };
        Cursor cursor = db.query("users", columns, selection, selectionArgs, null, null, null);
        boolean exists = cursor.moveToFirst();
        cursor.close();
        db.close();
        return exists;
    }

    public boolean addUser(String username, String password, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("password", password);
        values.put("email", email);
        long result = db.insert("users", null, values);
        db.close();
        return result != -1;
    }

    public void deleteUser(String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("users", "username=?", new String[]{username});
        db.close();
    }

    public ArrayList<String> getAllUsers() {
        ArrayList<String> users = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT username, email FROM users", null);
        if (cursor.moveToFirst()) {
            do {
                String entry = cursor.getString(0) + " (" + cursor.getString(1) + ")";
                users.add(entry);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return users;
    }

    public boolean updateUser(String username, String newUsername, String newPassword, String newEmail) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", newUsername);
        values.put("password", newPassword);
        values.put("email", newEmail);
        int result = db.update("users", values, "username=?", new String[]{username});
        db.close();
        return result > 0;
    }
}

public class userDatabase extends AppCompatActivity {

    ListView userListView;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_database);
        userListView = findViewById(R.id.userListView);
        dbHelper = new DatabaseHelper(this);

        ArrayList<String> userList = dbHelper.getAllUsers();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, userList);
        userListView.setAdapter(adapter);

        Button addUserButton = findViewById(R.id.addUserButton);
        Button deleteUserButton = findViewById(R.id.deleteUserButton);
        Button editUserButton = findViewById(R.id.editUserButton);


        addUserButton.setOnClickListener(v -> {
            dbHelper.addUser("newuser", "password", "new@user.com");
            ArrayList<String> updatedList = dbHelper.getAllUsers();
            ArrayAdapter<String> newAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, updatedList);
            userListView.setAdapter(newAdapter);
        });

        deleteUserButton.setOnClickListener(v -> {
            ArrayList<String> currentUsers = dbHelper.getAllUsers();
            if (!currentUsers.isEmpty()) {
                String[] userArray = currentUsers.toArray(new String[0]);

                new AlertDialog.Builder(userDatabase.this)
                    .setTitle("Select a user to delete")
                    .setItems(userArray, (dialog, which) -> {
                        String selectedEntry = userArray[which];
                        String usernameToDelete = selectedEntry.split(" ")[0];
                        dbHelper.deleteUser(usernameToDelete);
                        ArrayList<String> updatedList = dbHelper.getAllUsers();
                        ArrayAdapter<String> newAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, updatedList);
                        userListView.setAdapter(newAdapter);
                    })
                    .show();
            }
        });

        editUserButton.setOnClickListener(v -> {
            ArrayList<String> currentUsers = dbHelper.getAllUsers();
            if (!currentUsers.isEmpty()) {
                String[] userArray = currentUsers.toArray(new String[0]);

                new AlertDialog.Builder(userDatabase.this)
                    .setTitle("Select a user to edit")
                    .setItems(userArray, (dialog, which) -> {
                        String selectedEntry = userArray[which];
                        String usernameToEdit = selectedEntry.split(" ")[0];

                        LinearLayout layout = new LinearLayout(userDatabase.this);
                        layout.setOrientation(LinearLayout.VERTICAL);

                        final EditText newUsernameInput = new EditText(userDatabase.this);
                        newUsernameInput.setHint("New Username");
                        layout.addView(newUsernameInput);

                        final EditText newPasswordInput = new EditText(userDatabase.this);
                        newPasswordInput.setHint("New Password");
                        layout.addView(newPasswordInput);

                        final EditText newEmailInput = new EditText(userDatabase.this);
                        newEmailInput.setHint("New Email");
                        layout.addView(newEmailInput);

                        new AlertDialog.Builder(userDatabase.this)
                            .setTitle("Edit User: " + usernameToEdit)
                            .setView(layout)
                            .setPositiveButton("Save", (d, w) -> {
                                String newUsername = newUsernameInput.getText().toString();
                                String newPassword = newPasswordInput.getText().toString();
                                String newEmail = newEmailInput.getText().toString();
                                if (!newUsername.isEmpty() && !newPassword.isEmpty() && !newEmail.isEmpty()) {
                                    dbHelper.updateUser(usernameToEdit, newUsername, newPassword, newEmail);
                                    ArrayList<String> updatedList = dbHelper.getAllUsers();
                                    ArrayAdapter<String> newAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, updatedList);
                                    userListView.setAdapter(newAdapter);
                                }
                            })
                            .setNegativeButton("Cancel", null)
                            .show();
                    })
                    .show();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
