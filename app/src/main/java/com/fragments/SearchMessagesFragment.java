package com.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.activities.R;
import com.adapters.MessageAdapter;
import com.models.Message;

import java.util.ArrayList;

public class SearchMessagesFragment extends Fragment {

    private RecyclerView recyclerView;
    private MessageAdapter adapter;
    private ArrayList<Message> allMessages = new ArrayList<>();

    public SearchMessagesFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_messages, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewMessages);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Configura o adapter com a lista inicial de mensagens (inicialmente vazia)
        int meuId = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                .getInt("candidateId", -1);



        adapter = new MessageAdapter(allMessages, getContext(), meuId);
        recyclerView.setAdapter(adapter);
        recyclerView.setAdapter(adapter);

        return view;
    }

    // Método para atualizar a lista de mensagens
    // Método para atualizar a lista de mensagens
    public void atualizarMensagens(ArrayList<Message> messages) {
        Log.d("SearchMessagesFragment", "Atualizando mensagens...");
        Log.d("SearchMessagesFragment", "Tamanho das mensagens: " + messages.size());
        getActivity().runOnUiThread(() -> {
            allMessages.clear(); // Limpar mensagens antigas
            allMessages.addAll(messages); // Adicionar as novas mensagens
            adapter.notifyDataSetChanged(); // Notificar o adapter de que a lista foi atualizada
        });
    }





    // Método para adicionar uma nova mensagem
    public void adicionarMensagem(Message message) {
        allMessages.add(0, message); // Adiciona a nova mensagem no início da lista
        adapter.notifyItemInserted(0); // Notifica que um item foi inserido na posição 0 (início da lista)
    }
}
