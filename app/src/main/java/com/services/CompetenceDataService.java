package com.services;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.models.CompetenceData;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class CompetenceDataService {

    private static final String BASE_URL = "https://backend-po.onrender.com/competences";


    public interface CompetenceDataCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }



    public static void registerCompetenceData(Context context, CompetenceData competenceData, CompetenceDataService.CompetenceDataCallback callback) {
        new Thread(() -> {
            try {
                // Recuperar o userId do SharedPreferences
                if (competenceData.getCurriculumId() == -1) {
                    throw new Exception("ID do usuário não encontrado");
                }


                JSONObject json = new JSONObject();

                json.put("name", competenceData.getCompetence());
                json.put("curriculumId", competenceData.getCurriculumId());


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

                InputStream is = conn.getResponseCode() < HttpURLConnection.HTTP_BAD_REQUEST
                        ? conn.getInputStream()
                        : conn.getErrorStream();

                if (is != null) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    is.close();
                    System.out.println("Resposta: " + response.toString());
                }

                int responseCode = conn.getResponseCode();

                if (responseCode == 200 || responseCode == 201) {
                    new Handler(Looper.getMainLooper()).post(callback::onSuccess);
                } else {
                    new Handler(Looper.getMainLooper()).post(() -> callback.onFailure("Erro ao registrar currículo. Código: " + responseCode));
                }

            } catch (Exception e) {
                e.printStackTrace();
                new Handler(Looper.getMainLooper()).post(() -> callback.onFailure("Erro: " + e.getMessage()));
            }
        }).start();
    }
}
