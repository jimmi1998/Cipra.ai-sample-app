package com.cipra.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
    private ApiService apiService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = findViewById(R.id.email_input);
        password = findViewById(R.id.password_input);
        loginButton = findViewById(R.id.sign_in_button);

        apiService = ApiClient.getClient().create(ApiService.class);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performLogin();
            }
        });
    }

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
