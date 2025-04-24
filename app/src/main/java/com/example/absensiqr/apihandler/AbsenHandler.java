package com.example.absensiqr.apihandler;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class AbsenHandler {

    private static final String BASE_URL = "http://192.168.100.126/absensiAPI/";
    private static final String APP_PREF = "account_logged_in";
    private static final String SESSION_KEY = "SessionId";

    public interface CallBack {
        void onSuccess(JSONObject data);
        void onError(String message);
    }

    private static void sendRequest(Context context, String endpoint, String method, JSONObject jsonBody, CallBack callback) {
        new Thread(() -> {
            try {
                URL url = new URL(BASE_URL + endpoint);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod(method);
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");
                conn.setUseCaches(false);

                SharedPreferences prefs = context.getSharedPreferences(APP_PREF, Context.MODE_PRIVATE);
                String sessionId = prefs.getString(SESSION_KEY, null);
                if (sessionId != null) {
                    conn.setRequestProperty("Cookie", sessionId);
                }

                if (jsonBody != null) {
                    byte[] postData = jsonBody.toString().getBytes(StandardCharsets.UTF_8);
                    conn.setRequestProperty("Content-Length", String.valueOf(postData.length));
                    conn.setDoOutput(true);
                    try (OutputStream os = conn.getOutputStream()) {
                        os.write(postData);
                    }
                }

                Map<String, List<String>> headerFields = conn.getHeaderFields();
                List<String> cookiesHeader = headerFields.get("Set-Cookie");
                if (cookiesHeader != null) {
                    for (String cookie : cookiesHeader) {
                        if (cookie.startsWith("PHPSESSID")) {
                            String newSession = cookie.split(";")[0];
                            prefs.edit().putString(SESSION_KEY, newSession).apply();
                        }
                    }
                }

                StringBuilder response = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                }

                JSONObject resJson = new JSONObject(response.toString());
                if (resJson.optString("status").equals("Success")) {
                    callback.onSuccess(resJson.optJSONObject("data"));
                } else {
                    callback.onError(resJson.optString("message", "Unknown error occurred"));
                }
            } catch (Exception e) {
                callback.onError("Error: " + e.getMessage());
            }
        }).start();
    }

    public static void Absen(Context context, int id_user, int npm, CallBack callback) {
        JSONObject json = new JSONObject();
        try {
            json.put("id_user", id_user);
            json.put("npm", npm);
        } catch (JSONException e) {
            callback.onError("JSON Error: " + e.getMessage());
            return;
        }
        sendRequest(context, "absen.php", "POST", json, callback);
    }
}
