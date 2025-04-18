package com.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.activities.R;
import com.adapters.VacancyAdapter;
import com.models.Vacancy;
import com.services.VacancyService;

import java.util.ArrayList;

public class SearchFormFragment extends Fragment {

    private EditText searchInput;
    private RecyclerView recyclerView;
    private VacancyAdapter adapter;
    private ArrayList<Vacancy> allVacancies = new ArrayList<>();

    private VacancyService vacancyService;

    public SearchFormFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_form, container, false);

        searchInput = view.findViewById(R.id.searchInput);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        vacancyService = new VacancyService();

        // 1. Carrega todas as vagas ao abrir
        vacancyService.fetchVacanciesFromApi(new VacancyService.VacancyCallback() {
            @Override
            public void onSuccess(ArrayList<Vacancy> vacancies) {
                // Recupera o companyId (se estiver logado como empresa)
                SharedPreferences prefs = getActivity().getSharedPreferences("UserPrefs", getContext().MODE_PRIVATE);
                int companyId = prefs.getInt("companyId", -1);

                if (companyId != -1) {
                    ArrayList<Vacancy> vagasDaEmpresa = new ArrayList<>();
                    for (Vacancy v : vacancies) {
                        if (v.getCompanyId() == companyId) {
                            vagasDaEmpresa.add(v);
                        }
                    }
                    allVacancies = vagasDaEmpresa;
                } else {
                    allVacancies = vacancies; // Candidato: mostra todas
                }

                getActivity().runOnUiThread(() -> atualizarLista(""));
            }


            @Override
            public void onFailure(String error) {
                getActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), "Erro ao buscar vagas: " + error, Toast.LENGTH_SHORT).show()
                );
            }
        });

        // 2. Escuta digitação no campo
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                atualizarLista(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        return view;
    }

    private void atualizarLista(String query) {
        String termo = query.toLowerCase().trim();
        ArrayList<Vacancy> filtradas = new ArrayList<>();

        for (Vacancy v : allVacancies) {
            if (v.getTitle().toLowerCase().contains(termo)) {
                filtradas.add(v);
            }
        }

        adapter = new VacancyAdapter(filtradas, getContext(), vaga -> {
            // Aqui vai a ação do botão "Ver Mais" (se usar)
        });

        recyclerView.setAdapter(adapter);
    }
}
