package com.services;

import android.util.Log;

import com.models.User;
import com.models.UserType;

import org.json.JSONArray;
import org.json.JSONObject;

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
            Request request = new Request.Builder()
                    .url(apiUrl + "s") // /users
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    String responseData = response.body().string();
                    JSONArray usersArray = new JSONArray(responseData);

                    for (int i = 0; i < usersArray.length(); i++) {
                        JSONObject userJson = usersArray.getJSONObject(i);
                        String userEmail = userJson.getString("email");
                        String userPassword = userJson.getString("password");

                        if (email.equals(userEmail) && password.equals(userPassword)) {
                            int id = userJson.getInt("id");
                            String type = userJson.getString("type");

                            User user = new User(id, userEmail, userPassword, UserType.valueOf(type.toUpperCase()));
                            callback.onSuccess(user);
                            return;
                        }
                    }
                    callback.onFailure("Email ou senha inválidos");
                } else {
                    callback.onFailure("Erro ao buscar usuários: " + response.message());
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

}


