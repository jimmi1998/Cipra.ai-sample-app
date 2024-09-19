package com.cipra.app;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.text.InputType;


import com.cipra.app.network.ApiClient;
import com.cipra.app.network.ApiService;


public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getName();
    private EditText uEmail, password;
    private Button signinButton;
    private ImageView userIcon;
    private ApiService apiService;
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        uEmail = findViewById(R.id.email_input);
        password = findViewById(R.id.password_input);
        signinButton = findViewById(R.id.sign_in_button);
        userIcon = findViewById(R.id.user_icon);

        apiService = ApiClient.getClient().create(ApiService.class);

        setupTextWatchers();
        setupPasswordVisibilityToggle();

        signinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performLogin();
            }
        });
    }

    private void setupTextWatchers() {
        uEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    userIcon.setImageResource(R.drawable.user_solid);
                } else {
                    userIcon.setImageResource(R.drawable.user_hollow);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ImageView keyIcon = findViewById(R.id.key_icon);

                if (s.length() > 0) {
                    keyIcon.setImageResource(R.drawable.key_solid);
                    if (!isPasswordVisible) {
                        password.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.hidden_solid, 0);
                    } else {
                        password.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.unhide_solid, 0);
                    }
                } else {
                    keyIcon.setImageResource(R.drawable.key_hollow);
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

    private void setupPasswordVisibilityToggle() {
        password.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (password.getRight() - password.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
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
            password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            password.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.unhide_solid, 0);
            isPasswordVisible = true;
        } else {
            password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            password.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.hidden_solid, 0);
            isPasswordVisible = false;
        }
        password.setSelection(password.getText().length());
    }

    private void performLogin() {
        String user = uEmail.getText().toString().trim();
        String pass = password.getText().toString().trim();

        if (user.isEmpty() || pass.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Call<ResponseBody> call = apiService.signin(user, pass);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String responseBody = response.body().string();
                        if (responseBody.equalsIgnoreCase("Valid Credentials")) {
                            Log.e(TAG, "Valid Credentials: Login Successful");
                            Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();


                            Intent intent = new Intent(LoginActivity.this, ImageDisplayActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "Login Failed: Invalid credentials", Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(LoginActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Login Failed: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}