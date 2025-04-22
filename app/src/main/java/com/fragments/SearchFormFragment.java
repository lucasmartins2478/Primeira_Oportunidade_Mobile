package com.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
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
    private ArrayList<Integer> candidaturasIds = new ArrayList<>();


    private boolean isMyApplicationsScreen = false;


    public SearchFormFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_form, container, false);

        Bundle args = getArguments();
        if (args != null) {
            isMyApplicationsScreen = args.getBoolean("isMyApplicationsScreen", false);
            candidaturasIds = args.getIntegerArrayList("vacancyIdsCandidatadas");
        }


        searchInput = view.findViewById(R.id.searchInput);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        Spinner spinnerUf = view.findViewById(R.id.uf_spinner);
        Spinner spinnerModality = view.findViewById(R.id.modality_spinner);
        Spinner spinnerLevel = view.findViewById(R.id.level_spinner);

        if (isMyApplicationsScreen) {
            ArrayAdapter<CharSequence> activeAdapter = ArrayAdapter.createFromResource(getContext(), R.array.active_filter_options, R.layout.spinner_item);
            activeAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
            spinnerUf.setAdapter(activeAdapter);

            ArrayAdapter<CharSequence> filledAdapter = ArrayAdapter.createFromResource(getContext(), R.array.filled_filter_options, R.layout.spinner_item);
            filledAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
            spinnerModality.setAdapter(filledAdapter);

            ArrayAdapter<CharSequence> dummyAdapter = ArrayAdapter.createFromResource(getContext(), R.array.dummy_filter_options, R.layout.spinner_item);
            dummyAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
            spinnerLevel.setAdapter(dummyAdapter);
        } else {
            ArrayAdapter<CharSequence> ufAdapter = ArrayAdapter.createFromResource(getContext(), R.array.uf_options, R.layout.spinner_item);
            ufAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
            spinnerUf.setAdapter(ufAdapter);

            ArrayAdapter<CharSequence> modalityAdapter = ArrayAdapter.createFromResource(getContext(), R.array.modality_options, R.layout.spinner_item);
            modalityAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
            spinnerModality.setAdapter(modalityAdapter);

            ArrayAdapter<CharSequence> levelAdapter = ArrayAdapter.createFromResource(getContext(), R.array.vacancy_level_options, R.layout.spinner_item);
            levelAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
            spinnerLevel.setAdapter(levelAdapter);
        }




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
                    if (isMyApplicationsScreen) {
                        ArrayList<Vacancy> vagasCandidatadas = new ArrayList<>();
                        for (Vacancy v : vacancies) {
                            if (candidaturasIds.contains(v.getId())) {
                                vagasCandidatadas.add(v);
                            }
                        }
                        allVacancies = vagasCandidatadas;
                    } else {
                        allVacancies = vacancies;
                    }
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

        AdapterView.OnItemSelectedListener filterListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                atualizarLista(searchInput.getText().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        };

        spinnerUf.setOnItemSelectedListener(filterListener);
        spinnerModality.setOnItemSelectedListener(filterListener);
        spinnerLevel.setOnItemSelectedListener(filterListener);


        return view;
    }

    private void atualizarLista(String query) {
        String termo = query.toLowerCase().trim();

        Spinner spinnerUf = getView().findViewById(R.id.uf_spinner);
        Spinner spinnerModality = getView().findViewById(R.id.modality_spinner);
        Spinner spinnerLevel = getView().findViewById(R.id.level_spinner);

        String uf = spinnerUf.getSelectedItem().toString();
        String modality = spinnerModality.getSelectedItem().toString();
        String level = spinnerLevel.getSelectedItem().toString();

        ArrayList<Vacancy> filtradas = new ArrayList<>();

        for (Vacancy v : allVacancies) {
            boolean match = v.getTitle().toLowerCase().contains(termo);

            if (isMyApplicationsScreen) {
                // Filtro pelo isActive (Agora é booleano)
                if (!uf.equals("Selecione")) {
                    if (uf.equals("Ativas")) match &= !v.isActive();  // isActive é true para vagas ativas
                    else if (uf.equals("Inativas")) match &= v.isActive();  // isActive é false para vagas inativas
                }

                // Filtro pelo isFilled (Agora é booleano)
                if (!modality.equals("Selecione")) {
                    if (modality.equals("Preenchidas")) match &= v.isFilled();  // isFilled é true para vagas preenchidas
                    else if (modality.equals("Não preenchidas")) match &= !v.isFilled();  // isFilled é false para vagas não preenchidas
                }

                // O terceiro spinner pode ser ignorado por enquanto, serve só pra manter o layout
            } else {
                // Filtro padrão das outras telas
                if (!uf.equals("Selecione")) {
                    match &= v.getUf().toUpperCase().contains(uf);
                }

                if (!modality.equals("Selecione")) {
                    match &= v.getModality().equalsIgnoreCase(modality);
                }

                if (!level.equals("Selecione")) {
                    match &= v.getLevel().equalsIgnoreCase(level);
                }
            }

            if (match) {
                filtradas.add(v);
            }
        }

        adapter = new VacancyAdapter(filtradas, getContext(), vaga -> {
            // ação ao clicar na vaga
        });

        recyclerView.setAdapter(adapter);
    }




}
