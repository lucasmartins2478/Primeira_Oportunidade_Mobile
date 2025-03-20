package com.services;

import com.models.User;

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
    private String apiUrl = "https://664f28a4fafad45dfae29755.mockapi.io/api/v1/users";
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
                        .url(apiUrl)
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
                                userJson.getString("userType")
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
        void onSuccess();

        void onFailure(String error);
    }

    public void registerUser(User user, UserCallback callback){
        new Thread(() ->{
            try{
                JSONObject json = new JSONObject();
                json.put("email", user.getEmail());
                json.put("password", user.getPassword());
                json.put("userType", user.getUserType());


                RequestBody body = RequestBody.create(
                        json.toString(),
                        MediaType.get("application/json; charset=utf-8")
                );



                Request request = new Request.Builder().url(apiUrl).post(body).build();

                Response response = client.newCall(request).execute();

                if(response.isSuccessful()){
                    callback.onSuccess();
                }else{
                    callback.onFailure("Erro"+response.message());
                }
            }catch (Exception e){
                callback.onFailure("Falha ao conectar: "+ e.getMessage());
            }

        }).start();
    }

    public User login(String email, String password) {
        for (User user : users) {
            if (user.getEmail().equals(email) && user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }
}
