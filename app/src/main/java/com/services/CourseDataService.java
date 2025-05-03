package com.services;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.models.AcademicData;
import com.models.CourseData;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
    public static void updateCourseData(Context context, CourseData courseData, CourseDataService.CourseDataCallback callback) {
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
                String isoEndDate = convertMonthYearToIso(courseData.getEndDate());
                System.out.println("Data convertida para ISO: " + isoEndDate);
                json.put("endDate", isoEndDate);
                json.put("isCurrentlyStudying", courseData.isInProgress());
                json.put("institutionName", courseData.getGrantingIntitution());
                json.put("curriculumId", courseData.getCurriculumId());

                System.out.println("JSON enviado para o backend: " + json.toString());

                // Fazer a requisição HTTP
                URL url = new URL(BASE_URL+"/"+courseData.getId());
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


    public static String convertMonthYearToIso(String input) throws ParseException {
        if (input == null || input.isEmpty()) return null;

        SimpleDateFormat inputFormat = new SimpleDateFormat("MM/yyyy", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        Date date = inputFormat.parse(input);
        return outputFormat.format(date);
    }


    public interface FetchCourseDataCallback {
        void onSuccess(List<CourseData> courseDataList);
        void onFailure(String errorMessage);
    }

    public static void getCourseDataByCurriculumId(int curriculumId, FetchCourseDataCallback callback) {
        new Thread(() -> {
            try {
                // Fazendo a requisição para a URL com curriculumId
                URL url = new URL("https://backend-po.onrender.com/coursesData" + "/" + curriculumId);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.connect();

                int responseCode = conn.getResponseCode();

                // Verificando a resposta do servidor
                if (responseCode == 200) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    reader.close();

                    // Convertendo a resposta em um JSONArray (não precisa de JSONObject para um array simples)
                    JSONArray coursesArray = new JSONArray(response.toString());
                    List<CourseData> courseDataList = new ArrayList<>();

                    // Iterando sobre o array de cursos e adicionando na lista
                    if (coursesArray.length() > 0) {
                        for (int i = 0; i < coursesArray.length(); i++) {
                            JSONObject courseJson = coursesArray.getJSONObject(i);

                            CourseData courseData = new CourseData(
                                    courseJson.optInt("id"),
                                    courseJson.optString("name"),
                                    courseJson.optString("modality"),
                                    courseJson.optString("duration"),
                                    courseJson.optString("endDate"),
                                    courseJson.optBoolean("isCurrentlyStudying"),
                                    courseJson.optString("institutionName"),
                                    courseJson.optInt("curriculumId")
                            );

                            courseDataList.add(courseData);
                        }

                        // Enviar a lista para a callback de sucesso
                        callback.onSuccess(courseDataList);
                    } else {
                        callback.onFailure("Nenhum curso encontrado.");
                    }
                } else {
                    callback.onFailure("Erro ao buscar dados: " + responseCode);
                }

            } catch (Exception e) {
                e.printStackTrace();
                callback.onFailure("Erro ao buscar dados: " + e.getMessage());
            }
        }).start();
    }

    public static void deleteCourseData(Context context, int courseId, CourseDataCallback callback) {
        new Thread(() -> {
            try {
                URL url = new URL(BASE_URL + "/" + courseId);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("DELETE");

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




}
