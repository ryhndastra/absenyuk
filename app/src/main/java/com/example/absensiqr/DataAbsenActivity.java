package com.example.absensiqr;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.absensiqr.apihandler.AbsenHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DataAbsenActivity extends AppCompatActivity {

    TableLayout tableLayout;
    Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_absen);

        tableLayout = findViewById(R.id.tableAbsen);
        btnBack = findViewById(R.id.btnBack);

        AbsenHandler.GetAllAbsen(this, new AbsenHandler.CallBackArray() {
            @Override
            public void onSuccess(JSONArray dataArray) {
                runOnUiThread(() -> {
                    addHeader();
                    for (int i = 0; i < dataArray.length(); i++) {
                        try {
                            JSONObject item = dataArray.getJSONObject(i);
                            addRow(item);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() ->
                        Toast.makeText(DataAbsenActivity.this, "Gagal mengambil data: " + message, Toast.LENGTH_LONG).show());
            }
        });

        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(DataAbsenActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
    }

    private void addHeader() {
        TableRow header = new TableRow(this);
        header.setBackgroundColor(0xFF0077CC);

        String[] titles = {"ID", "ID User", "NPM", "Waktu", "Status"};
        for (String title : titles) {
            TextView tv = new TextView(this);
            tv.setText(title);
            tv.setTextColor(0xFFFFFFFF);
            tv.setPadding(16, 8, 16, 8);
            header.addView(tv);
        }
        tableLayout.addView(header);
    }

    private void addRow(JSONObject data) throws JSONException {
        TableRow row = new TableRow(this);
        row.setBackgroundColor(0xFFFFF3E0);

        String statusText = data.getInt("status") == 1 ? "Masuk" : "Keluar";

        String[] values = {
                String.valueOf(data.getInt("id")),
                String.valueOf(data.getInt("id_user")),
                String.valueOf(data.getInt("npm")),
                data.getString("waktu"),
                statusText
        };

        for (String value : values) {
            TextView tv = new TextView(this);
            tv.setText(value);
            tv.setPadding(16, 8, 16, 8);
            row.addView(tv);
        }

        tableLayout.addView(row);
    }
}
