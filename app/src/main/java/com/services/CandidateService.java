package com.services;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;

import com.models.Candidate;



import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CandidateService {

    private List<Candidate> candidates;

    private String apiUrl = "https://backend-po.onrender.com/candidate";
    private OkHttpClient client;


    public CandidateService() {
        client = new OkHttpClient();
    }

    public interface registerCallback {
        void onSuccess(Candidate candidate); // já tem
        void onFailure(String error);
    }



    public void fetchCandidateFromApi(int userId, CandidateCallback callback) {
        new Thread(() -> {
            try {
                String url = apiUrl + "/" + userId;


                Request request = new Request.Builder()
                        .url(url)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (response.isSuccessful() && response.body() != null) {
                        String responseData = response.body().string();
                        JSONObject candidateJson = new JSONObject(responseData);

                        // Trata curriculumId que pode vir como null
                        Integer curriculumId = candidateJson.isNull("curriculumId") ? null : candidateJson.getInt("curriculumId");

                        Candidate candidate = new Candidate(
                                candidateJson.getInt("id"),
                                candidateJson.getString("name"),
                                candidateJson.getString("cpf"),
                                candidateJson.getString("phoneNumber"),
                                curriculumId,
                                candidateJson.getInt("userId")
                        );


                        callback.onSuccess(candidate);
                    } else {
                        callback.onFailure("Erro ao buscar candidato: " + response.code());
                    }
                }
            } catch (Exception e) {
                callback.onFailure("Erro ao conectar: " + e.getMessage());
            }
        }).start();
    }
    public void fetchCandidateFromApiByCandidateId(int userId, CandidateCallback callback) {
        new Thread(() -> {
            try {
                String url = apiUrl + "Id/" + userId;


                Request request = new Request.Builder()
                        .url(url)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (response.isSuccessful() && response.body() != null) {
                        String responseData = response.body().string();
                        JSONObject candidateJson = new JSONObject(responseData);

                        // Trata curriculumId que pode vir como null
                        Integer curriculumId = candidateJson.isNull("curriculumId") ? null : candidateJson.getInt("curriculumId");

                        Candidate candidate = new Candidate(
                                candidateJson.getInt("id"),
                                candidateJson.getString("name"),
                                candidateJson.getString("cpf"),
                                candidateJson.getString("phoneNumber"),
                                curriculumId,
                                candidateJson.getInt("userId")
                        );


                        callback.onSuccess(candidate);
                    } else {
                        callback.onFailure("Erro ao buscar candidato: " + response.code());
                    }
                }
            } catch (Exception e) {
                callback.onFailure("Erro ao conectar: " + e.getMessage());
            }
        }).start();
    }



    // Interface para callback
    public interface CandidateCallback {
        void onSuccess(Candidate candidate);
        void onFailure(String error);
    }

    public void registerCandidate(Candidate candidate, registerCallback callback) {
        new Thread(() -> {
            try {
                // Criando JSON do candidato
                JSONObject json = new JSONObject();
                json.put("name", candidate.getName());
                json.put("cpf", candidate.getCpf());
                json.put("phoneNumber", candidate.getPhoneNumber());
                json.put("userId", candidate.getUserId());

                RequestBody body = RequestBody.create(
                        json.toString(),
                        MediaType.get("application/json; charset=utf-8")
                );

                Request request = new Request.Builder()
                        .url(apiUrl)
                        .post(body)
                        .build();

                Response response = client.newCall(request).execute();

                if (response.isSuccessful() && response.body() != null) {
                    String responseData = response.body().string();
                    JSONObject responseJson = new JSONObject(responseData);

                    // Extrai os dados retornados do backend
                    int id = responseJson.getInt("id");
                    String name = responseJson.getString("name");
                    String cpf = responseJson.getString("cpf");
                    String phone = responseJson.getString("phoneNumber");
                    Integer curriculumId = responseJson.isNull("curriculumId") ? null : responseJson.getInt("curriculumId");
                    int userId = responseJson.getInt("userId");

                    // Cria novo Candidate com dados completos
                    Candidate createdCandidate = new Candidate(id, name, cpf, phone, curriculumId, userId);

                    callback.onSuccess(createdCandidate);
                } else {
                    callback.onFailure("Erro ao cadastrar candidato: " + response.message());
                }

            } catch (Exception e) {
                callback.onFailure("Falha ao conectar: " + e.getMessage());
            }
        }).start();
    }


    public static void addCurriculumToCandidate(Context context, CompanyService.RegisterCallback callback){
        new Thread(() -> {
            try {
                // Recuperar o userId do SharedPreferences

                SharedPreferences prefs = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
                int userId = prefs.getInt("candidateId", -1);
                int curriculumId = prefs.getInt("curriculumId", -1);


                if (userId == -1) {
                    throw new Exception("ID do usuário não encontrado");
                }

                JSONObject json = new JSONObject();

                json.put("curriculumId", curriculumId);






                System.out.println("JSON enviado para o backend: " + json.toString());

                // Fazer a requisição HTTP
                URL url = new URL("https://backend-po.onrender.com/candidate/"+userId+"/curriculum");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("PUT");
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
