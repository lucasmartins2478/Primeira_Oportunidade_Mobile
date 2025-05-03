package com.services;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;



import com.models.AcademicData;

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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AcademicDataService {


    private static final String BASE_URL = "https://backend-po.onrender.com/academicData";


    public interface AcademicDataCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }

    public static void registerAcademicData(Context context, AcademicData academicData, AcademicDataCallback callback) {
        new Thread(() -> {
            try {
                // Recuperar o userId do SharedPreferences
                if (academicData.getCurriculumId() == -1) {
                    throw new Exception("ID do usuário não encontrado");
                }

                // Log para depuração
                System.out.println("startDate: " + academicData.getStartDate());
                System.out.println("startDateFormatted: " + convertMonthYearToIso(academicData.getStartDate()));
                System.out.println("endDate: " + academicData.getEndDate());
                System.out.println("endDateFormatted: " + convertMonthYearToIso(academicData.getEndDate()));

                JSONObject json = new JSONObject();

                json.put("name", academicData.getCourseName());
                json.put("semester", Integer.parseInt(academicData.getPeriod()));
                json.put("startDate", convertMonthYearToIso(academicData.getStartDate()));
                json.put("endDate", convertMonthYearToIso(academicData.getEndDate()));
                json.put("isCurrentlyStudying", academicData.getIsCurrentlyStudying());
                json.put("institutionName", academicData.getInstitutionName());
                json.put("degree", academicData.getLevel());
                json.put("city", academicData.getCity());
                json.put("curriculumId", academicData.getCurriculumId());

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
    public static void updateAcademicData(Context context, AcademicData academicData, AcademicDataCallback callback) {
        new Thread(() -> {
            try {
                // Recuperar o userId do SharedPreferences
                if (academicData.getCurriculumId() == -1) {
                    throw new Exception("ID do usuário não encontrado");
                }

                // Log para depuração
                System.out.println("startDate: " + academicData.getStartDate());
                System.out.println("startDateFormatted: " + convertMonthYearToIso(academicData.getStartDate()));
                System.out.println("endDate: " + academicData.getEndDate());
                System.out.println("endDateFormatted: " + convertMonthYearToIso(academicData.getEndDate()));

                JSONObject json = new JSONObject();

                json.put("name", academicData.getCourseName());
                json.put("semester", Integer.parseInt(academicData.getPeriod()));
                json.put("startDate", convertMonthYearToIso(academicData.getStartDate()));
                json.put("endDate", convertMonthYearToIso(academicData.getEndDate()));
                json.put("isCurrentlyStudying", academicData.getIsCurrentlyStudying());
                json.put("institutionName", academicData.getInstitutionName());
                json.put("degree", academicData.getLevel());
                json.put("city", academicData.getCity());
                json.put("curriculumId", academicData.getCurriculumId());

                System.out.println("JSON enviado para o backend: " + json.toString());

                // Fazer a requisição HTTP
                URL url = new URL(BASE_URL+"/"+academicData.getId());
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



    public static void getAcademicDataByCurriculumId(int curriculumId, FetchAcademicDataCallback callback) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(BASE_URL + "/" + curriculumId)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure("Erro de rede: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String json = response.body().string();

                    try {
                        JSONArray array = new JSONArray(json);

                        if (array.length() == 0) {
                            callback.onFailure("Nenhum dado encontrado.");
                            return;
                        }

                        List<AcademicData> dataList = new ArrayList<>();

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);

                            AcademicData data = new AcademicData();
                            data.setId(obj.getInt("id"));
                            data.setInstitutionName(obj.getString("institutionName"));
                            data.setCourseName(obj.getString("name"));
                            data.setPeriod(obj.getString("semester"));
                            data.setStartDate(obj.getString("startDate"));
                            data.setEndDate(obj.getString("endDate"));
                            data.setCity(obj.getString("city"));
                            data.setIsCurrentlyStudying(obj.getInt("isCurrentlyStudying") == 1);

                            dataList.add(data);
                        }

                        callback.onSuccess(dataList);

                    } catch (JSONException e) {
                        callback.onFailure("Erro ao processar JSON: " + e.getMessage());
                    }

                } else {
                    callback.onFailure("Erro: " + response.code());
                }
            }
        });
    }

    public static void deleteAcademicData(Context context, int academicDataId, AcademicDataCallback callback) {
        new Thread(() -> {
            try {
                URL url = new URL(BASE_URL + "/" + academicDataId);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("DELETE");
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

                int responseCode = conn.getResponseCode();

                InputStream is = responseCode < HttpURLConnection.HTTP_BAD_REQUEST
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
                    System.out.println("Resposta (delete): " + response.toString());
                }

                if (responseCode == 200 || responseCode == 204) {
                    new Handler(Looper.getMainLooper()).post(callback::onSuccess);
                } else {
                    new Handler(Looper.getMainLooper()).post(() -> callback.onFailure("Erro ao excluir dado acadêmico. Código: " + responseCode));
                }

            } catch (Exception e) {
                e.printStackTrace();
                new Handler(Looper.getMainLooper()).post(() -> callback.onFailure("Erro: " + e.getMessage()));
            }
        }).start();
    }




    public interface FetchAcademicDataCallback {
        void onSuccess(List<AcademicData> dataList);
        void onFailure(String errorMessage);
    }







}
