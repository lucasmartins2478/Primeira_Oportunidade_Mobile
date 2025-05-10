package com.services;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.models.CompetenceData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CompetenceDataService {

    private static final String BASE_URL = "https://backend-po.onrender.com/competences";


    public interface CompetenceDataCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }



    public static void registerCompetenceData(Context context, CompetenceData competenceData, String token, CompetenceDataService.CompetenceDataCallback callback) {
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
                conn.setRequestProperty("Authorization", "Bearer " + token);
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
    public static void updateCompetenceData(Context context, CompetenceData competenceData, String token, CompetenceDataService.CompetenceDataCallback callback) {
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
                URL url = new URL(BASE_URL+"/"+competenceData.getId());
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("PUT");
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setRequestProperty("Authorization", "Bearer " + token);
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
    public static void deleteCompetenceData(Context context, int competenceId, String token, CompetenceDataCallback callback) {
        new Thread(() -> {
            try {
                URL url = new URL(BASE_URL + "/" + competenceId);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("DELETE");
                conn.setRequestProperty("Authorization", "Bearer " + token);

                int responseCode = conn.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_NO_CONTENT || responseCode == HttpURLConnection.HTTP_OK) {
                    new Handler(Looper.getMainLooper()).post(callback::onSuccess);
                } else {
                    new Handler(Looper.getMainLooper()).post(() ->
                            callback.onFailure("Erro ao deletar curso. Código: " + responseCode));
                }

                conn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
                new Handler(Looper.getMainLooper()).post(() -> callback.onFailure("Erro: " + e.getMessage()));
            }
        }).start();
    }

    public interface FetchCompetencesCallback {
        void onSuccess(List<CompetenceData> competences);
        void onFailure(String errorMessage);
    }

    public static void getCompetencesByCurriculumId(int curriculumId, String token, FetchCompetencesCallback callback) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://backend-po.onrender.com/competences/" + curriculumId)
                .addHeader("Authorization", "Bearer " + token)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure("Erro na requisição: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    callback.onFailure("Erro na resposta: " + response.code());
                    return;
                }

                String responseData = response.body().string();
                List<CompetenceData> competences = new ArrayList<>();

                try {
                    JSONArray jsonArray = new JSONArray(responseData);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject comp = jsonArray.getJSONObject(i);

                        // Extrai os dados do JSON
                        int id = comp.getInt("id");
                        String name = comp.getString("name");
                        int curriculumId = comp.getInt("curriculumId");

                        // Cria o objeto CompetenceData
                        CompetenceData data = new CompetenceData();
                        data.setId(id);
                        data.setCompetence(name);
                        data.setCurriculumId(curriculumId);

                        competences.add(data);
                    }

                    callback.onSuccess(competences);

                } catch (JSONException e) {
                    callback.onFailure("Erro ao processar JSON: " + e.getMessage());
                }
            }
        });
    }


}
