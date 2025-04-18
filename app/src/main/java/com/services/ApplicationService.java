package com.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;

import com.models.Application;
import com.models.Curriculum;
import com.models.Vacancy;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

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



    public static void getApplicationsByVacancyId(Context context, int vacancyId, ApplicationsListCallback callback) {
        new Thread(() -> {
            try {
                String urlString = BASE_URL + "s/vacancy/" + vacancyId;
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/json");

                int responseCode = conn.getResponseCode();

                if (responseCode == 200) {
                    InputStream inputStream = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    reader.close();
                    inputStream.close();

                    JSONArray jsonArray = new JSONArray(result.toString());
                    List<Application> applications = new ArrayList<>();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);

                        int vId = obj.getInt("vacancyId");
                        int cId = obj.getInt("userId");

                        applications.add(new Application(vId, cId));
                    }

                    // Retorna no main thread
                    new Handler(Looper.getMainLooper()).post(() -> callback.onSuccess(applications));

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
                            callback.onFailure("Erro ao buscar candidaturas. Código: " + responseCode)
                    );
                }

            } catch (Exception e) {
                e.printStackTrace();
                new Handler(Looper.getMainLooper()).post(() ->
                        callback.onFailure("Erro: " + e.getMessage()));
            }
        }).start();
    }
    public static void getAllApplications(Context context, ApplicationsListCallback callback) {
        new Thread(() -> {
            try {
                String urlString = "https://backend-po.onrender.com/applications";
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/json");

                int responseCode = conn.getResponseCode();

                if (responseCode == 200) {
                    InputStream inputStream = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    reader.close();
                    inputStream.close();

                    JSONArray jsonArray = new JSONArray(result.toString());
                    List<Application> applications = new ArrayList<>();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);

                        int vId = obj.getInt("vacancyId");
                        int cId = obj.getInt("userId"); // <- Nome correto aqui!

                        applications.add(new Application(vId, cId));
                    }

                    new Handler(Looper.getMainLooper()).post(() -> callback.onSuccess(applications));

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
                            callback.onFailure("Erro ao buscar candidaturas. Código: " + responseCode)
                    );
                }

            } catch (Exception e) {
                e.printStackTrace();
                new Handler(Looper.getMainLooper()).post(() ->
                        callback.onFailure("Erro: " + e.getMessage()));
            }
        }).start();
    }





    public interface ApplicationsListCallback {
        void onSuccess(List<Application> applications);
        void onFailure(String errorMessage);
    }


}
