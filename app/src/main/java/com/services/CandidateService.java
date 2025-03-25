package com.services;

import com.models.Candidate;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CandidateService {

    private List<Candidate> candidates;

    private String apiUrl = "https://664f28a4fafad45dfae29755.mockapi.io/api/v1/candidates";
    private OkHttpClient client;


    public CandidateService() {
        client = new OkHttpClient();
    }

    public interface registerCallback {
        void onSuccess();
        void onFailure(String error);
    }

    public void getCandidateByUserId(int userId, CandidateCallback callback) {
        new Thread(() -> {
            try {
                // Criando requisição GET
                Request request = new Request.Builder()
                        .url(apiUrl + "/" + userId) // Buscar candidato por ID
                        .get()
                        .build();

                // Executando requisição
                try (Response response = client.newCall(request).execute()) {
                    if (response.isSuccessful() && response.body() != null) {
                        String responseData = response.body().string();
                        JSONObject json = new JSONObject(responseData);

                        // Criando objeto Candidate com os dados retornados
                        Candidate candidate = new Candidate(
                                json.getString("name"),
                                json.getString("phoneNumber"),
                                json.getString("cpf"),
                                json.getInt("id")
                        );

                        callback.onSuccess(candidate);
                    } else {
                        callback.onFailure("Erro ao buscar candidato: " + response.message());
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            } catch (IOException e) {
                callback.onFailure("Falha na conexão: " + e.getMessage());
            }
        }).start();
    }

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
                json.put("phoneNumber", candidate.getPhoneNumber());
                json.put("cpf", candidate.getCpf());
                json.put("email", candidate.getEmail());
                json.put("id", candidate.getId());

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
