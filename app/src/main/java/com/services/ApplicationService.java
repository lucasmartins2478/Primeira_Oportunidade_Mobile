package com.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;

import com.models.Application;
import com.models.Curriculum;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ApplicationService {


    private static final String BASE_URL = "https://backend-po.onrender.com/application";

    public interface ApplicationCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }

    public static void registerApplication(Context context, Application application, ApplicationService.ApplicationCallback callback) {
        new Thread(() -> {
            try {



                JSONObject json = new JSONObject();

                json.put("vacancyId", application.getVacancyId());
                json.put("candidateId", application.getUserId());



                System.out.println("JSON enviado para o backend: " + json.toString());


                // Fazer a requisição HTTP
                URL url = new URL(BASE_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                os.write(json.toString().getBytes("UTF-8"));
                os.close();

                int responseCode = conn.getResponseCode();

                if (responseCode == 200 || responseCode == 201) {
                    if (conn.getInputStream() != null) {
                        conn.getInputStream().close();
                    }
                    new Handler(Looper.getMainLooper()).post(callback::onSuccess);
                } else {
                    InputStream errorStream = conn.getErrorStream();
                    if (errorStream != null) {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(errorStream));
                        StringBuilder errorResponse = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            errorResponse.append(line);
                        }
                        reader.close();
                        System.out.println("Erro do backend: " + errorResponse.toString());
                    }
                    new Handler(Looper.getMainLooper()).post(() ->
                            callback.onFailure("Erro ao registrar candidatura. Código: " + responseCode)
                    );
                }


            } catch (Exception e) {
                e.printStackTrace();
                new Handler(Looper.getMainLooper()).post(() -> callback.onFailure("Erro: " + e.getMessage()));
            }
        }).start();
    }
}
