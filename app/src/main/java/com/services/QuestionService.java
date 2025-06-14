package com.services;

import android.content.Context;
import android.util.Log;

import com.models.Question;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class QuestionService {

    private static final String REGISTER_URL = "https://backend-po.onrender.com/vacancy/questions";
    private final OkHttpClient client = new OkHttpClient();

    public interface QuestionCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }

    public void registerQuestion(Context context, Question question,String token, QuestionCallback callback) {
        new Thread(() -> {
            Response response = null;
            try {
                JSONObject json = new JSONObject();
                json.put("question", question.getQuestion());
                json.put("vacancyId", question.getVacancyId());




                RequestBody body = RequestBody.create(
                        json.toString(),
                        MediaType.parse("application/json; charset=utf-8")
                );

                Request request = new Request.Builder()
                        .url(REGISTER_URL)
                        .post(body)
                        .addHeader("Authorization", "Bearer " + token)
                        .build();

                response = client.newCall(request).execute();

                if (response.isSuccessful()) {
                    callback.onSuccess();
                } else {
                    callback.onFailure("Erro: " + response.message());
                }

            } catch (Exception e) {
                try {
                    callback.onFailure("Erro: " + response.code() + " - " + response.body().string());
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }).start();
    }
    public void updateQuestion(Context context, Question question, String token, QuestionCallback callback) {
        new Thread(() -> {
            Response response = null;
            try {
                JSONObject json = new JSONObject();
                json.put("question", question.getQuestion());
                json.put("vacancyId", question.getVacancyId());




                RequestBody body = RequestBody.create(
                        json.toString(),
                        MediaType.parse("application/json; charset=utf-8")
                );

                Request request = new Request.Builder()
                        .url("https://backend-po.onrender.com/vacancy/"+question.getVacancyId()+"/questions/"+question.getId())
                        .put(body)
                        .addHeader("Authorization", "Bearer " + token)
                        .build();

                response = client.newCall(request).execute();

                if (response.isSuccessful()) {
                    callback.onSuccess();
                } else {
                    callback.onFailure("Erro: " + response.message());
                }

            } catch (Exception e) {
                try {
                    callback.onFailure("Erro: " + response.code() + " - " + response.body().string());
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }).start();
    }


    public static void getQuestionsByVacancyId(Context context, int vacancyId, String token,QuestionListCallback callback) {
        new Thread(() -> {
            try {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url("https://backend-po.onrender.com/questions/vacancy/" + vacancyId)
                        .addHeader("Authorization", "Bearer " + token)
                        .build();

                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    JSONArray jsonArray = new JSONArray(response.body().string());
                    List<Question> questions = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        Question q = new Question();
                        q.setId(obj.getInt("id")); // ✅ ID da pergunta
                        q.setQuestion(obj.getString("question"));
                        q.setVacancyId(obj.getInt("vacancyId")); // ✅ ID da vaga

                        questions.add(q);
                    }
                    callback.onSuccess(questions);
                } else {
                    if (response.code() == 404) {
                        // Assumimos que não existem perguntas para essa vaga
                        callback.onSuccess(new ArrayList<>()); // Retorna lista vazia normalmente
                    } else {
                        callback.onFailure("Erro ao buscar perguntas: " + response.message());
                    }
                }
            } catch (Exception e) {
                callback.onFailure("Erro: " + e.getMessage());
            }
        }).start();
    }

    public interface QuestionListCallback {
        void onSuccess(List<Question> questions);
        void onFailure(String errorMessage);
    }

}
