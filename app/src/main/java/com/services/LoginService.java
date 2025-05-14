package com.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.models.Candidate;
import com.models.User;
import com.models.UserType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginService {

    private List<User> users;
    private String apiUrl = "https://backend-po.onrender.com/user";
    private OkHttpClient client;

    public LoginService() {
        client = new OkHttpClient();
        users = new ArrayList<>();
        // Fazer a requisição para buscar todos os usuários ao criar o serviço
        fetchUsersFromApi();
    }

    private void fetchUsersFromApi() {
        new Thread(() -> {
            try {
                // Criando a requisição GET
                Request request = new Request.Builder()
                        .url(apiUrl+"s")
                        .build();

                // Enviando requisição
                Response response = client.newCall(request).execute();

                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    // Convertendo o JSON retornado para uma lista de usuários
                    JSONArray jsonArray = new JSONArray(responseData);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject userJson = jsonArray.getJSONObject(i);
                        User user = new User(
                                userJson.getString("email"),
                                userJson.getString("password"),
                                UserType.valueOf(userJson.getString("type").toUpperCase())
                        );
                        users.add(user); // Adicionando o usuário na lista
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void fetchUsersFromApiByUserId(int userId, FetchCallback callback) {
        new Thread(() -> {
            try {
                // Criando a requisição GET
                Request request = new Request.Builder()
                        .url(apiUrl+"/"+userId)
                        .build();

                // Enviando requisição
                Response response = client.newCall(request).execute();

                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    JSONObject userJson = new JSONObject(responseData);


                    User user = new User(
                            userJson.getInt("id"),
                            userJson.getString("email"),
                            userJson.getString("password"),
                            UserType.valueOf(userJson.getString("type").toUpperCase())
                    );


                    callback.onSuccess(user);


                }
            } catch (Exception e) {
                callback.onFailure("Erro ao buscar usuário");
                e.printStackTrace();
            }
        }).start();
    }

    public  interface FetchCallback{
        void onSuccess(User user);

        void onFailure(String error);
    }

    public interface DeleteCallback{
        void onSuccess();

        void onFailure(String error);
    }

    public interface UserCallback{
        void onSuccess(int userId);

        void onFailure(String error);
    }

    public void registerUser(User user, UserCallback callback) {
        new Thread(() -> {
            try {
                // Criando JSON do usuário
                JSONObject json = new JSONObject();
                json.put("email", user.getEmail());
                json.put("password", user.getPassword());
                json.put("type", user.getType().getValue());

                Log.d("UserRegisterJSON", json.toString());


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
                    try {
                        String responseData = response.body().string();
                        Log.d("UserRegisterResponse", "Dados recebidos: " + responseData);

                        JSONObject jsonResponse = new JSONObject(responseData);
                        JSONObject userJson = jsonResponse.getJSONObject("user");
                        int userId = userJson.getInt("id");

                        callback.onSuccess(userId);
                    } catch (Exception e) {
                        Log.e("UserRegisterParseError", "Erro ao ler resposta: ", e);
                        callback.onFailure("Erro ao processar resposta do servidor.");
                    }
                } else {
                    callback.onFailure("Erro ao cadastrar o usuário: " + response.message());
                }


            } catch (Exception e) {
                callback.onFailure("Falha ao conectar: " + e.getMessage());
            }
        }).start();
    }

    public void updateUser(User user, String token, UserCallback callback) {
        new Thread(() -> {
            try {
                // Criando JSON do usuário
                JSONObject json = new JSONObject();
                json.put("email", user.getEmail());
                json.put("password", user.getPassword());
                json.put("type", user.getType().getValue());

                Log.d("UserRegisterJSON", json.toString());


                // Criando corpo da requisição
                RequestBody body = RequestBody.create(
                        json.toString(),
                        MediaType.get("application/json; charset=utf-8")
                );

                // Criando requisição POST
                Request request = new Request.Builder()
                        .url(apiUrl+"/"+user.getId())
                        .put(body)
                        .addHeader("Authorization", "Bearer " + token)
                        .build();

                // Enviando requisição
                Response response = client.newCall(request).execute();

                if (response.isSuccessful()) {
                    try {
                        String responseData = response.body().string();
                        Log.d("UserRegisterResponse", "Dados recebidos: " + responseData);

                        JSONObject jsonResponse = new JSONObject(responseData);
                        int userId = jsonResponse.getInt("id");

                        callback.onSuccess(userId);
                    } catch (Exception e) {
                        Log.e("UserRegisterParseError", "Erro ao ler resposta: ", e);
                        callback.onFailure("Erro ao processar resposta do servidor.");
                    }
                } else {
                    callback.onFailure("Erro ao cadastrar o usuário: " + response.message());
                }


            } catch (Exception e) {
                callback.onFailure("Falha ao conectar: " + e.getMessage());
            }
        }).start();
    }


    public void login(String email, String password, LoginCallback callback) {
        new Thread(() -> {
            JSONObject json = new JSONObject();
            try {
                json.put("email", email);
                json.put("password", password);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            Log.d("UserLoginJSON", json.toString());

            RequestBody body = RequestBody.create(
                    json.toString(),
                    MediaType.get("application/json; charset=utf-8")
            );

            Request request = new Request.Builder()
                    .url("https://backend-po.onrender.com/login")
                    .post(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    String responseData = response.body().string();
                    JSONObject responseJson = new JSONObject(responseData);

                    // Captura o token da resposta
                    String token = responseJson.getString("token");

                    // Captura os dados do usuário
                    JSONObject userJson = responseJson.getJSONObject("user");
                    int id = userJson.getInt("id");
                    String userEmail = userJson.getString("email");
                    String type = userJson.getString("type");

                    // Cria objeto User sem senha (ou salva a que foi enviada)
                    User user = new User(id, userEmail, password, UserType.valueOf(type.toUpperCase()));
                    user.setToken(token); // seu User precisa ter esse campo

                    callback.onSuccess(user);
                } else {
                    callback.onFailure("Email ou senha inválidos");
                }
            } catch (Exception e) {
                e.printStackTrace();
                callback.onFailure("Erro de conexão: " + e.getMessage());
            }
        }).start();
    }


    public interface LoginCallback {
        void onSuccess(User user);
        void onFailure(String errorMessage);
    }



    public  void deleteUserData(Context context, int userId, String token,  LoginService.DeleteCallback callback) {
        new Thread(() -> {
            try {
                URL url = new URL("https://backend-po.onrender.com/user/" + userId);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("DELETE");
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setRequestProperty("Authorization", "Bearer " + token);


                int responseCode = conn.getResponseCode();



                if (responseCode == 200 || responseCode == 204) {
                    new Handler(Looper.getMainLooper()).post(callback::onSuccess);
                } else {
                    new Handler(Looper.getMainLooper()).post(() -> callback.onFailure("Erro ao excluir usuário. Código: " + responseCode));
                }

            } catch (Exception e) {
                e.printStackTrace();
                new Handler(Looper.getMainLooper()).post(() -> callback.onFailure("Erro: " + e.getMessage()));
            }
        }).start();
    }

}


