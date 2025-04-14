package com.services;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.models.Curriculum;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class CurriculumService {

    private static final String BASE_URL = "https://backend-po.onrender.com/curriculum";

    public interface CurriculumCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }

    public static void registerCurriculum(Context context, Curriculum curriculum, CurriculumCallback callback) {
        new Thread(() -> {
            try {
                // Recuperar o userId do SharedPreferences
                SharedPreferences prefs = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
                int userId = prefs.getInt("candidateId", -1);

                if (userId == -1) {
                    throw new Exception("ID do usuário não encontrado");
                }



                JSONObject json = new JSONObject();

                json.put("id", userId);
                String isoDate = convertToIsoDate(curriculum.getBirthDate());
                json.put("dateOfBirth", isoDate);
                json.put("age", Integer.parseInt(curriculum.getAge()));
                json.put("gender", curriculum.getGender());
                json.put("race", curriculum.getRace());
                json.put("city", curriculum.getCity());
                json.put("attached", "");
                json.put("description", "");
                json.put("address", curriculum.getAddress());
                json.put("addressNumber", curriculum.getAddressNumber());
                json.put("cep", curriculum.getCep());
                json.put("uf", curriculum.getUf());



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

    public static void addSchoolData(Context context, Curriculum curriculum, CurriculumCallback callback){
        new Thread(() -> {
            try {
                // Recuperar o userId do SharedPreferences
                SharedPreferences prefs = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
                int userId = prefs.getInt("candidateId", -1);

                if (userId == -1) {
                    throw new Exception("ID do usuário não encontrado");
                }



                JSONObject json = new JSONObject();

                json.put("schoolName", curriculum.getSchoolName());
                json.put("schoolYear", curriculum.getSchoolYear());
                json.put("schoolCity", curriculum.getSchoolCity());
                json.put("schoolStartDate", convertMonthYearToIso(curriculum.getSchoolStartDate()));
                json.put("schoolEndDate", convertMonthYearToIso(curriculum.getSchoolEndDate()));
                json.put("isCurrentlyStudying", curriculum.isCurrentlyStudying());



                System.out.println("JSON enviado para o backend: " + json.toString());


                // Fazer a requisição HTTP
                URL url = new URL(BASE_URL+"/"+userId+"/addSchoolData");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("PUT");
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                os.write(json.toString().getBytes("UTF-8"));
                os.close();

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
    private static String convertMonthYearToIso(String input) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("MM/yyyy", Locale.getDefault());
            Date date = inputFormat.parse(input);

            // Use sempre o dia 01 como default
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }


    private static String convertToIsoDate(String inputDate) {
        try {
            SimpleDateFormat brFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date date = brFormat.parse(inputDate);

            SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            return isoFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }




}
