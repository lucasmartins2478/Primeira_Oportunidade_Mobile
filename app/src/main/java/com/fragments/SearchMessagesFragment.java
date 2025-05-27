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
import android.widget.ScrollView;

import com.activities.R;
import com.adapters.MessageAdapter;
import com.models.Message;

import java.util.ArrayList;

public class SearchMessagesFragment extends Fragment {

    private RecyclerView recyclerView;
    private MessageAdapter adapter;
    LoadingDialogFragment loadingDialog;
    private ArrayList<Message> allMessages = new ArrayList<>();

    public SearchMessagesFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_messages, container, false);

        loadingDialog = new LoadingDialogFragment();

        loadingDialog.show(getParentFragmentManager(), "loading");

        recyclerView = view.findViewById(R.id.recyclerViewMessages);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Configura o adapter com a lista inicial de mensagens (inicialmente vazia)
        int meuId = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                .getInt("candidateId", -1);



        adapter = new MessageAdapter(allMessages, getContext(), meuId);
        recyclerView.setAdapter(adapter);
        recyclerView.setAdapter(adapter);

        loadingDialog.dismiss();
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
            recyclerView.scrollToPosition(allMessages.size() - 1);


        });
    }

    // Método para adicionar uma nova mensagem
    public void adicionarMensagem(Message message) {
        allMessages.add(message); // Adiciona a nova mensagem no final da lista
        adapter.notifyItemInserted(allMessages.size() - 1); // Notifica que o item foi inserido na última posição
        recyclerView.scrollToPosition(allMessages.size() - 1);

    }

}
