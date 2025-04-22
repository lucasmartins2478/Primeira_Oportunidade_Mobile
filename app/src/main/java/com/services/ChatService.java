package com.services;

import android.util.Log;

import com.models.Message;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatService {

    private final String registerUrl = "https://backend-po.onrender.com/message";
    private final String fetchUrl = "https://backend-po.onrender.com/messages";
    private final OkHttpClient client = new OkHttpClient();
    private static final String TAG = "ChatService"; // Tag para logs

    public interface MessageCallback {
        void onSuccess(ArrayList<Message> messages);
        void onFailure(String error);
    }

    public interface SendMessageCallback {
        void onSuccess(Message message);
        void onFailure(String error);
    }

    // ✅ Buscar mensagens (GET)
    public void getMessages(MessageCallback callback) {
        Log.d(TAG, "Iniciando requisição para buscar mensagens...");

        Request request = new Request.Builder().url(fetchUrl).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Erro ao buscar mensagens: " + e.getMessage());
                callback.onFailure("Erro ao buscar mensagens: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String jsonData = response.body().string();
                        Log.d(TAG, "Resposta recebida ao buscar mensagens: " + jsonData);

                        JSONArray jsonArray = new JSONArray(jsonData);
                        ArrayList<Message> messages = new ArrayList<>();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            int senderId = obj.getInt("sender_id");
                            String senderName = obj.getString("sender_name");
                            String content = obj.getString("content");

                            messages.add(new Message(senderId, content, senderName));
                        }

                        Log.d(TAG, "Mensagens processadas: " + messages.size());
                        callback.onSuccess(messages);
                    } catch (Exception e) {
                        Log.e(TAG, "Erro ao processar a resposta das mensagens: " + e.getMessage());
                        callback.onFailure("Erro ao processar resposta: " + e.getMessage());
                    }
                } else {
                    String errorMsg = "Erro na resposta da API: " + response.message();
                    Log.e(TAG, errorMsg);
                    callback.onFailure(errorMsg);
                }
            }
        });
    }

    // ✅ Enviar mensagem (POST)
    public void sendMessage(int senderId, String senderName, String content, SendMessageCallback callback) {
        try {
            JSONObject json = new JSONObject();
            json.put("sender_id", senderId);
            json.put("content", content);
            json.put("sender_name", senderName);

            Log.d(TAG, "Enviando mensagem: " + json.toString());

            RequestBody body = RequestBody.create(
                    json.toString(),
                    MediaType.get("application/json; charset=utf-8")
            );

            Request request = new Request.Builder()
                    .url(registerUrl)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, "Erro ao enviar mensagem: " + e.getMessage());
                    callback.onFailure("Erro ao enviar mensagem: " + e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        if (response.isSuccessful()) {
                            String jsonData = response.body().string();
                            Log.d(TAG, "Resposta recebida ao enviar mensagem: " + jsonData);

                            JSONObject obj = new JSONObject(jsonData);
                            // Aqui verificamos os dados que a API retorna
                            int senderId = obj.getInt("sender_id");
                            String senderName = obj.getString("sender_name");
                            String content = obj.getString("content");
                            int id = obj.getInt("id");  // ID retornado pela API

                            // Criar objeto de mensagem com os dados retornados
                            Message message = new Message(senderId, content, senderName);
                            message.setId(id);  // Definir o ID gerado pela API

                            // Chamando o callback com sucesso
                            callback.onSuccess(message);
                        } else {
                            // Logando código de status e corpo da resposta em caso de falha
                            String errorMsg = "Erro ao enviar mensagem. Código: " + response.code() +
                                    ", Mensagem: " + response.message();
                            String responseBody = response.body() != null ? response.body().string() : "null";
                            Log.e(TAG, errorMsg + ", Corpo da resposta: " + responseBody);

                            callback.onFailure("Erro ao enviar mensagem: " + errorMsg);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Erro ao processar a resposta ao enviar mensagem: " + e.getMessage());
                        callback.onFailure("Erro ao processar resposta: " + e.getMessage());
                    }
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Erro ao montar JSON para envio de mensagem: " + e.getMessage());
            callback.onFailure("Erro ao montar JSON: " + e.getMessage());
        }
    }
}
