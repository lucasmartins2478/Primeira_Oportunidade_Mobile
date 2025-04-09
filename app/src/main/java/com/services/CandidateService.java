package com.services;


import com.models.Candidate;



import org.json.JSONObject;

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
        void onSuccess();
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
                // Criando corpo da requisição
                RequestBody body = RequestBody.create(
                        json.toString(),
                        MediaType.get("application/json; charset=utf-8")
                );

                // Criando requisição POST
                Request request = new Request.Builder()
                        .url(apiUrl)
                        .post(body)
                        .build();

                // Enviando requisição
                Response response = client.newCall(request).execute();

                if (response.isSuccessful()) {
                    callback.onSuccess(); // Chamando callback de sucesso
                } else {
                    callback.onFailure("Erro: " + response.message());
                }

            } catch (Exception e) {
                callback.onFailure("Falha ao conectar: " + e.getMessage());
            }
        }).start();
    }


}
