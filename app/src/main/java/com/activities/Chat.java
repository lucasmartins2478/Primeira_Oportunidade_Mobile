package com.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.fragments.LoadingDialogFragment;
import com.fragments.SearchMessagesFragment;
import com.models.Message;
import com.services.ChatService;

import java.util.ArrayList;

public class Chat extends AppCompatActivity {

    private EditText inputMessage;
    private ImageView sendMessageButton;
    private ChatService chatService;
    private SearchMessagesFragment searchMessagesFragment;

    LoadingDialogFragment loadingDialog;

    private static final String TAG = "ChatActivity"; // Tag para logs

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);

        loadingDialog = new LoadingDialogFragment();

        // Inicializando as views
        inputMessage = findViewById(R.id.inputMessage);
        sendMessageButton = findViewById(R.id.sendMessageButton);
        chatService = new ChatService(); // Instanciando o ChatService

        // Carregar o fragmento de pesquisa
        if (savedInstanceState == null) {
            searchMessagesFragment = new SearchMessagesFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.containerSearchMessages, searchMessagesFragment);
            transaction.commit();
        }

        // Buscar mensagens quando a Activity for carregada
        Log.d(TAG, "Iniciando o carregamento de mensagens...");
        carregarMensagens();

        // Lidar com o clique no botão de envio
        sendMessageButton.setOnClickListener(view -> {
            String messageText = inputMessage.getText().toString().trim();
            Log.d(TAG, "Botão de envio pressionado com mensagem: " + messageText);
            if (!messageText.isEmpty()) {
                enviarMensagem(messageText);
                inputMessage.setText(""); // Limpar campo de digitação
            } else {
                Toast.makeText(Chat.this, "Digite uma mensagem", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Nenhuma mensagem para enviar (campo vazio).");
            }
        });
    }


    private void carregarMensagens() {
        loadingDialog.show(getSupportFragmentManager(), "loading");
        Log.d("ChatActivity", "Iniciando o carregamento de mensagens...");

        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        String token = prefs.getString("token", "Nanhum token encontrado");
        chatService.getMessages(token, new ChatService.MessageCallback() {
            @Override
            public void onSuccess(ArrayList<Message> messages) {
                // Atualizar o fragmento com as mensagens obtidas
                loadingDialog.dismiss();
                if (searchMessagesFragment != null) {
                    Log.d("ChatActivity", "Mensagens carregadas com sucesso. Total: " + messages.size());
                    // Garantir que a atualização da UI seja feita na thread principal
                    runOnUiThread(() -> {
                        searchMessagesFragment.atualizarMensagens(messages); // Atualizar as mensagens no fragmento
                    });
                }
            }

            @Override
            public void onFailure(String error) {
                // Exibir erro na UI thread
                runOnUiThread(() -> {
                    loadingDialog.dismiss();
                    Log.e("ChatActivity", "Erro ao carregar mensagens: " + error);
                    Toast.makeText(Chat.this, "Erro ao carregar mensagens: " + error, Toast.LENGTH_LONG).show();
                });
            }
        });
    }




    // Método para enviar mensagem
    private void enviarMensagem(String messageText) {
        loadingDialog.show(getSupportFragmentManager(), "loading");

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        String name = sharedPreferences.getString("name", "Usuário não encontrado");
        int curriculumId = sharedPreferences.getInt("candidateId", -1);

        String token = sharedPreferences.getString("token", "Nenhum token encontrado");
        Log.d(TAG, "Enviando mensagem: " + messageText + " (ID: " + curriculumId + ", Nome: " + name + ")");

        chatService.sendMessage(curriculumId, name, messageText,token, new ChatService.SendMessageCallback() {
            @Override
            public void onSuccess(Message message) {
                Log.d(TAG, "Mensagem enviada com sucesso: " + message.getContent());
                // Mensagem enviada com sucesso
                runOnUiThread(() -> {
                    loadingDialog.dismiss();
                    Toast.makeText(Chat.this, "Mensagem enviada!", Toast.LENGTH_SHORT).show();
                    // Adicionar a nova mensagem à lista no fragmento
                    if (searchMessagesFragment != null) {
                        searchMessagesFragment.adicionarMensagem(message);
                    }
                });
            }

            @Override
            public void onFailure(String error) {
                Log.e(TAG, "Erro ao enviar mensagem: " + error);
                // Exibir erro na thread principal utilizando runOnUiThread
                runOnUiThread(() -> {
                    loadingDialog.dismiss();
                    Toast.makeText(Chat.this, "Erro ao enviar mensagem: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
}
