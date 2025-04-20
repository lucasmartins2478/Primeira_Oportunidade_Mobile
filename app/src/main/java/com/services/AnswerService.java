package com.services;

import com.models.Answer;

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

                RequestBody body = RequestBody.create(
                        json.toString(),
                        MediaType.parse("application/json; charset=utf-8")
                );

                Request request = new Request.Builder()
                        .url("https://backend-po.onrender.com/answer")
                        .post(body)
                        .build();

                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    callback.onSuccess();
                } else {
                    callback.onFailure("Erro: " + response.message());
                }

            } catch (Exception e) {
                callback.onFailure("Erro: " + e.getMessage());
            }
        }).start();
    }
}

