package com.services;

import com.models.Address;
import com.models.User;
import com.models.UserType;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AddressService {
    private OkHttpClient client;

    public AddressService(){
        client = new OkHttpClient();

    }

    public interface FetchCallback{
        void onSuccess(Address address);

        void onFailure(String error);
    }

    public void fetchAddressFromViacep(String cep, FetchCallback callback) {
        new Thread(() -> {
            try {
                Request request = new Request.Builder()
                        .url("https://viacep.com.br/ws/" + cep + "/json/")
                        .build();

                Response response = client.newCall(request).execute();

                if (response.isSuccessful()) {
                    String responseData = response.body().string();

                    // Verifica se retornou erro (API retorna {"erro": true} para CEP inválido)
                    JSONObject json = new JSONObject(responseData);
                    if (json.has("erro") && json.getBoolean("erro")) {
                        callback.onFailure("CEP não encontrado.");
                        return;
                    }

                    // Pega os campos necessários
                    String logradouro = json.optString("logradouro", "");
                    String bairro = json.optString("bairro", "");
                    String localidade = json.optString("localidade", "");
                    String uf = json.optString("uf", "");
                    String cepResult = json.optString("cep", "");

                    Address address = new Address(
                            cepResult,
                            logradouro,
                            bairro,
                            localidade,
                            uf
                    );

                    callback.onSuccess(address);

                } else {
                    callback.onFailure("Erro na requisição: " + response.message());
                }
            } catch (Exception e) {
                callback.onFailure("Erro ao buscar CEP: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }



}
