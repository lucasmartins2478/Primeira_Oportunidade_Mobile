package com.services;

import android.util.Log;

import com.models.Answer;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AnswerService {

    public interface AnswerCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }

    public static void submitAnswer(Answer answer, AnswerCallback callback) {
        new Thread(() -> {
            try {
                OkHttpClient client = new OkHttpClient();
                JSONObject json = new JSONObject();
                json.put("answer", answer.getAnswer());
                json.put("questionId", answer.getQuestionId());
                json.put("userId", answer.getUserId());

                Log.d("AnswerService", "Enviando JSON: " + json.toString());


                RequestBody body = RequestBody.create(
                        json.toString(),
                        MediaType.parse("application/json; charset=utf-8")
                );

                Request request = new Request.Builder()
                        .url("https://backend-po.onrender.com/answer")
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Accept", "application/json")
                        .post(body)
                        .build();


                Response response = client.newCall(request).execute();
                String responseBody = response.body() != null ? response.body().string() : "sem corpo";

                if (response.isSuccessful()) {
                    callback.onSuccess();
                } else {
                    callback.onFailure("Erro HTTP " + response.code() + ": " + responseBody);
                }


            } catch (Exception e) {
                callback.onFailure("Erro: " + e.getMessage());
            }
        }).start();
    }

    public static void getAnswerByCandidateAndQuestionId(int questionId, int candidateId, FetchAnswerByQuestionCallback callback) {
        new Thread(() -> {
            try {
                OkHttpClient client = new OkHttpClient();
                String url = "https://backend-po.onrender.com/answers/question/" + questionId;

                Request request = new Request.Builder()
                        .url(url)
                        .build();

                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    JSONArray jsonArray = new JSONArray(responseBody);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        if (obj.getInt("userId") == candidateId) {
                            Answer answer = new Answer();
                            answer.setAnswer(obj.getString("answer"));
                            answer.setQuestionId(obj.getInt("questionId"));
                            answer.setUserId(obj.getInt("userId"));
                            callback.onSuccess(answer);
                            return;
                        }
                    }

                    // Se não encontrou resposta para o candidato
                    callback.onSuccess(null);

                } else {
                    callback.onFailure("Erro: " + response.message());
                }
            } catch (Exception e) {
                callback.onFailure("Erro: " + e.getMessage());
            }
        }).start();
    }

    public interface FetchAnswerByQuestionCallback {
        void onSuccess(Answer answer);
        void onFailure(String errorMessage);
    }

    public interface FetchAnswerCallback {
        void onSuccess(Answer answer);
        void onFailure(String errorMessage);
    }

}

