package com.example.absensiqr;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.absensiqr.apihandler.AbsenHandler;
import com.example.absensiqr.apihandler.LoginHandler;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private int idUser = 0;
    private int npm = 0;

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

        LoginHandler.CheckLogin(this, new LoginHandler.CallBack() {
            @Override
            public void onSuccess(JSONObject data) {
                if (data != null) {
                    idUser = data.optInt("id");
                    npm = data.optInt("npm");
                }
            }

            @Override
            public void onError(String message) {
                Toast.makeText(MainActivity.this, "Session Error: " + message, Toast.LENGTH_LONG).show();
            }
        });

        Button btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            LoginHandler.Logout(MainActivity.this, new LoginHandler.CallBack() {
                @Override
                public void onSuccess(JSONObject data) {
                    runOnUiThread(() -> {
                        Toast.makeText(MainActivity.this, "Kamu berhasil logout", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    });
                }

                @Override
                public void onError(String message) {
                    runOnUiThread(() -> {
                        Toast.makeText(MainActivity.this, "Error : " + message, Toast.LENGTH_LONG).show();
                    });
                }
            });
        });

        Button btnScan = findViewById(R.id.btnScan);
        btnScan.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, QRCodeScanner.class);
            startActivityForResult(intent, 995);
        });

        Button btnLihatAbsen =findViewById(R.id.btnLihatAbsen);
        btnLihatAbsen.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, DataAbsenActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 995 && resultCode == RESULT_OK && data != null) {
            String scannedResult = data.getStringExtra("scanned result");

            try {
                int scannedNpm = Integer.parseInt(scannedResult);
                AbsenHandler.Absen(MainActivity.this, idUser, scannedNpm, new AbsenHandler.CallBack() {
                    @Override
                    public void onSuccess(JSONObject responseData) {
                        int status = responseData.optInt("status", -1);
                        runOnUiThread(() -> {
                            if (status == 1) {
                                Toast.makeText(MainActivity.this, "Absen berhasil masuk", Toast.LENGTH_LONG).show();
                            } else if (status == 0) {
                                Toast.makeText(MainActivity.this, "Absen berhasil keluar", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(MainActivity.this, "Absen tidak diketahui", Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                    @Override
                    public void onError(String message) {
                        runOnUiThread(() -> Toast.makeText(MainActivity.this, "Gagal absen: " + message, Toast.LENGTH_LONG).show());
                    }
                });
            } catch (NumberFormatException e) {
                Toast.makeText(MainActivity.this, "QR Code tidak valid", Toast.LENGTH_LONG).show();
            }

        }
    }



}
