package com.cipra.app;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.text.InputType;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.cipra.app.network.ApiClient;
import com.cipra.app.network.ApiService;
import com.cipra.app.network.LoginResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText username, password;
    private Button loginButton;
    private ImageView userIcon; // User icon
    private ApiService apiService;
    private boolean isPasswordVisible = false; // To track password visibility state

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Find views by ID
        username = findViewById(R.id.email_input);
        password = findViewById(R.id.password_input);
        loginButton = findViewById(R.id.sign_in_button);
        userIcon = findViewById(R.id.user_icon); // Initialize the user icon ImageView

        apiService = ApiClient.getClient().create(ApiService.class);

        // Setup text watchers and listeners
        setupTextWatchers();
        setupPasswordVisibilityToggle(); // Call the method here

        // Set login button click listener
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performLogin();
            }
        });
    }

    // Method to set up TextWatchers for dynamic icon changes
    private void setupTextWatchers() {
        // Email input listener for changing user icon
        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    userIcon.setImageResource(R.drawable.user_solid); // Change to solid icon
                } else {
                    userIcon.setImageResource(R.drawable.user_hollow); // Change back to hollow icon
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Password input listener for changing key icon and hidden icon
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ImageView keyIcon = findViewById(R.id.key_icon);

                if (s.length() > 0) {
                    keyIcon.setImageResource(R.drawable.key_solid); // Change key icon to solid

                    // If password is masked
                    if (!isPasswordVisible) {
                        password.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.hidden_solid, 0);
                    } else {
                        // If password is visible
                        password.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.unhide_solid, 0);
                    }
                } else {
                    keyIcon.setImageResource(R.drawable.key_hollow); // Change key icon to hollow

                    // No characters in password field
                    if (!isPasswordVisible) {
                        password.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.hidden_grey, 0);
                    } else {
                        password.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.unhide_hollow, 0);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    // Method to toggle password visibility and change the eye/hidden icon
    private void setupPasswordVisibilityToggle() {
        password.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2; // Index for the right drawable (the eye icon)

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (password.getRight() - password.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // Call method to toggle password visibility
                        togglePasswordVisibility();
                        return true;
                    }
                }
                return false;
            }
        });
    }

    private void togglePasswordVisibility() {
        if (!isPasswordVisible) {
            // Show password
            password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);

            if (password.getText().toString().isEmpty()) {
                // If password is empty, set to unhide_hollow
                password.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.unhide_hollow, 0);
            } else {
                // Otherwise, set to unhide_solid
                password.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.unhide_solid, 0);
            }
            isPasswordVisible = true;
        } else {
            // Hide password
            password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

            if (password.getText().toString().isEmpty()) {
                // If password is empty, set to hidden_grey
                password.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.hidden_grey, 0);
            } else {
                // Otherwise, set to hidden_solid
                password.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.hidden_solid, 0);
            }
            isPasswordVisible = false;
        }

        // Keep cursor at the end of the text
        password.setSelection(password.getText().length());
    }

    // Method to handle the login action
    private void performLogin() {
        String user = username.getText().toString();
        String pass = password.getText().toString();

        Call<LoginResponse> call = apiService.login(user, pass);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    Intent intent = new Intent(LoginActivity.this, ImageDisplayActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}