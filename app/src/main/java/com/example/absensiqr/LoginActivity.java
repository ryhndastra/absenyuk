package com.example.absensiqr;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.splashscreen.SplashScreen;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.absensiqr.apihandler.LoginHandler;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private boolean isLoading = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen sp = SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);
        sp.setKeepOnScreenCondition(() -> isLoading);

        // LANGSUNG CEK LOGIN DI AWAL
        LoginHandler.CheckLogin(LoginActivity.this, new LoginHandler.CallBack() {
            @Override
            public void onSuccess(JSONObject data) {
                runOnUiThread(() -> {
                    isLoading = false;
                    Toast.makeText(LoginActivity.this, "Kamu sudah login", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    isLoading = false;
                    Log.e("CheckLogin", "Error : " + message);
                    // TAMPILKAN UI LOGIN KARENA BELUM LOGIN
                    setContentView(R.layout.activity_login);
                    setupLoginForm();
                });
            }
        });
    }

    private void setupLoginForm() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button btnLogin = findViewById(R.id.btnLogin);
        EditText txtEmail = findViewById(R.id.txtEmail);
        EditText txtPassword = findViewById(R.id.txtPassword);

        btnLogin.setOnClickListener(v -> {
            String email = txtEmail.getText().toString();
            String password = txtPassword.getText().toString();

            LoginHandler.Login(LoginActivity.this, email, password, new LoginHandler.CallBack() {
                @Override
                public void onSuccess(JSONObject data) {
                    runOnUiThread(() -> {
                        Log.d("LoginActivity", "Login success: " + data);
                        try {
                            String id = data.getString("id");
                            String userEmail = data.getString("email");

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra("user id", id);
                            intent.putExtra("user email", userEmail);
                            startActivity(intent);
                            finish();
                        } catch (JSONException e) {
                            Toast.makeText(LoginActivity.this, "Error Parsing JSON", Toast.LENGTH_LONG).show();
                            throw new RuntimeException(e);
                        }
                    });
                }

                @Override
                public void onError(String message) {
                    runOnUiThread(() -> {
                        Log.e("LoginError", message);
                        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
                    });
                }
            });
        });
    }
}