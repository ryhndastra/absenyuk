package com.example.absensiqr.apihandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class LoginHandler {
    private static final String BASE_URL = "http://192.168.100.126/absensiAPI/";

    public interface CallBack{
        void onSuccess(JSONObject data);
        void onError(String message);
    }

    private static void sendRequest(String endPoint, String method, JSONObject jsonBody,CallBack callBack){
        new Thread(() -> {
            try {
                URL url = new URL(BASE_URL + endPoint);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod(method);
                conn.setRequestProperty("Content-Type" , "application/json");
                conn.setRequestProperty("Accept" , "application/json");

                if (jsonBody != null){
                    conn.setDoOutput(true);
                    byte[] postData = jsonBody.toString().getBytes(StandardCharsets.UTF_8);
                    try(OutputStream os = conn.getOutputStream()){
                        os.write(postData);
                    }
                }


                StringBuilder response = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))){
                    String line;
                    while ((line = reader.readLine()) != null){
                        response.append(line);
                    }
                }

                JSONObject resJSon = new JSONObject(response.toString());
                if (resJSon.optString("status").equals("success")){
                    callBack.onSuccess(resJSon.optJSONObject("data"));
                }else {
                    callBack.onError(resJSon.optString("Message", "Unknown error occured"));
                }
            } catch (IOException | JSONException e) {
                callBack.onError("Error : " + e.getMessage());
                throw new RuntimeException(e);
            }
        });
    }

    public static void Login(String email, String password, CallBack callBack){
        JSONObject json = new JSONObject();
        try {
            json.put("email", email);
            json.put("password",password);
        } catch (JSONException e) {
            callBack.onError("Error : " + e.getMessage());
            throw new RuntimeException(e);
        }
        sendRequest("login.php", "POST", json, callBack);
    }

    public static void CheckLogin(CallBack callBack){
        sendRequest("login.php", "GET", null, callBack);
    }

    public static void Logout(CallBack callBack){
        sendRequest("logout.php", "GET", null, callBack);
    }
}
