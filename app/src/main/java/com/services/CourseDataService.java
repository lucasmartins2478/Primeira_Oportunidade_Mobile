package com.services;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.models.AcademicData;
import com.models.CourseData;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CourseDataService {

    private static final String BASE_URL = "https://backend-po.onrender.com/courseData";


    public interface CourseDataCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }


    public static void registerCourseData(Context context, CourseData courseData, CourseDataService.CourseDataCallback callback) {
        new Thread(() -> {
            try {
                // Recuperar o userId do SharedPreferences
                if (courseData.getCurriculumId() == -1) {
                    throw new Exception("ID do usuário não encontrado");
                }


                JSONObject json = new JSONObject();

                json.put("name", courseData.getCourseName());
                json.put("modality", courseData.getModality());
                json.put("duration", courseData.getDuration());
                json.put("endDate", convertMonthYearToIso(courseData.getEndDate()));
                json.put("isCurrentlyStudying", courseData.isInProgress());
                json.put("institutionName", courseData.getGrantingIntitution());
                json.put("curriculumId", courseData.getCurriculumId());

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


    private static String convertMonthYearToIso(String input) {
        try {
            // Defina o padrão de data que corresponde ao formato de entrada
            SimpleDateFormat inputFormat = new SimpleDateFormat("MM-yyyy", Locale.getDefault());

            // A string de entrada pode ter o formato "MM-yyyy"
            Date date = inputFormat.parse(input); // Isso agora deve funcionar com "MM-yyyy"

            // Agora, converta para o formato "yyyy-MM-dd", garantindo que sempre seja o dia 01
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            return outputFormat.format(date); // Isso irá retornar a data no formato correto
        } catch (ParseException e) {
            e.printStackTrace();
            return null; // Em caso de erro, retorne null
        }
    }

}
