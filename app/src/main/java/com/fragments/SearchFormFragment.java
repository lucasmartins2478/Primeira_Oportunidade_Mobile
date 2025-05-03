package com.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.activities.MainActivity;
import com.activities.Profile;
import com.activities.R;
import com.activities.Vacancies;
import com.adapters.VacancyAdapter;
import com.models.AcademicData;
import com.models.CompetenceData;
import com.models.CourseData;
import com.models.Curriculum;
import com.models.DateUtils;
import com.models.Vacancy;
import com.services.AcademicDataService;
import com.services.CompetenceDataService;
import com.services.CourseDataService;
import com.services.CurriculumService;
import com.services.VacancyRecomender;
import com.services.VacancyService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SearchFormFragment extends Fragment {

    private EditText searchInput;
    private RecyclerView recyclerView;
    private VacancyAdapter adapter;
    private ArrayList<Vacancy> allVacancies = new ArrayList<>();

    private VacancyService vacancyService;
    private ArrayList<Integer> candidaturasIds = new ArrayList<>();

    private Curriculum meuCurriculo;
    private List<AcademicData> dadosAcademicos = new ArrayList<>();
    private List<CourseData> dadosCursos = new ArrayList<>();
    private List<String> competencias = new ArrayList<>();
    private boolean isFilterOpen;



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
        SharedPreferences prefs = getActivity().getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int candidateId = prefs.getInt("candidateId", -1);

        if (candidateId != -1) {
            fetchCurriculumData(candidateId);
        } else {

        }

        isFilterOpen = false;

        ImageView filterIcon = view.findViewById(R.id.filterIcon);
        LinearLayout filtersContainer = view.findViewById(R.id.filtersContainer);

        filterIcon.setOnClickListener(v -> {
            isFilterOpen = !isFilterOpen;
            filtersContainer.setVisibility(isFilterOpen ? View.VISIBLE : View.GONE);
        });



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
            ArrayAdapter<CharSequence> ufAdapter = ArrayAdapter.createFromResource(getContext(), R.array.uf_options, R.layout.spinner_item_white);
            ufAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
            spinnerUf.setAdapter(ufAdapter);

            ArrayAdapter<CharSequence> modalityAdapter = ArrayAdapter.createFromResource(getContext(), R.array.modality_options, R.layout.spinner_item_white);
            modalityAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
            spinnerModality.setAdapter(modalityAdapter);

            ArrayAdapter<CharSequence> levelAdapter = ArrayAdapter.createFromResource(getContext(), R.array.vacancy_level_options, R.layout.spinner_item_white);
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


        // Passo 2: Filtragem das vagas baseadas no termo de busca e nos filtros selecionados
        for (Vacancy v : allVacancies) {
            boolean match = v.getTitle().toLowerCase().contains(termo);



            // Se for uma empresa (meuCurriculo é null), filtre apenas pelo companyId
            if (meuCurriculo == null) {
                SharedPreferences prefs = getActivity().getSharedPreferences("UserPrefs", getContext().MODE_PRIVATE);
                int companyId = prefs.getInt("companyId", -1);

                // Verifica se o companyId é válido e se a vaga pertence à empresa
                if (companyId != -1) {
                    match &= v.getCompanyId() == companyId;
                }
            } else {
                // Filtro para candidatos
                if (isMyApplicationsScreen) {
                    if (!uf.equals("Selecione")) {
                        if (uf.equals("Ativas")) match &= !v.isActive();
                        else if (uf.equals("Inativas")) match &= v.isActive();
                    }

                    if (!modality.equals("Selecione")) {
                        if (modality.equals("Preenchidas")) match &= v.isFilled();
                        else if (modality.equals("Não preenchidas")) match &= !v.isFilled();
                    }
                } else {
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
            }

            if (match) {
                filtradas.add(v);
            } else {

            }
        }

        if (filtradas.isEmpty()) {
        }

        // Se meuCurriculo não for null, faça o cálculo do score
        if (meuCurriculo != null) {
            // Calcular o score e ordenar as vagas
            ArrayList<Vacancy> vagasComScore = new ArrayList<>();
            for (Vacancy vaga : filtradas) {
                int score = calcularScore(vaga);  // Calcula o score da vaga
                vaga.setScore(score);  // Adiciona o score à vaga

                // Se o score for alto o suficiente, envie a notificação
                calcularEEnviarNotificacao(vaga);  // Chama a função para enviar a notificação

                vagasComScore.add(vaga);
            }

            // Ordenar por score
            Collections.sort(vagasComScore, (vaga1, vaga2) -> Integer.compare(vaga2.getScore(), vaga1.getScore()));

            getActivity().runOnUiThread(() -> {
                adapter = new VacancyAdapter(vagasComScore, getContext(), vaga -> {
                    // Ação ao clicar na vaga
                });

                recyclerView.setAdapter(adapter);
            });
        } else {
            // Se meuCurriculo for null, apenas exibe as vagas filtradas
            getActivity().runOnUiThread(() -> {
                adapter = new VacancyAdapter(filtradas, getContext(), vaga -> {
                    // Ação ao clicar na vaga
                });

                recyclerView.setAdapter(adapter);
            });
        }
    }






    public void enviarNotificacao(Context context, Vacancy vaga) {
        // Crie um Intent que será disparado quando o usuário clicar na notificação
        Intent intent = new Intent(context, Vacancies.class);  // Ou a Activity que contém o Fragment
        intent.putExtra("vacancy", vaga);  // Passe os dados da vaga para o Fragment

        // Crie um PendingIntent para abrir a Activity/Fragment
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Crie a notificação
        Notification notification = new NotificationCompat.Builder(context, "CANAL_VAGAS")
                .setContentTitle("Vaga Compatível Encontrada!")
                .setContentText("Uma vaga pode ser interessante para você! Clique para ver os detalhes.")
                .setSmallIcon(R.drawable.my_applications_icon)  // Defina um ícone para a notificação
                .setContentIntent(pendingIntent)  // Associe o PendingIntent à notificação
                .setAutoCancel(true)  // A notificação desaparece quando clicada
                .build();

        // Envie a notificação
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);  // ID única para a notificação
    }




    private void calcularEEnviarNotificacao(Vacancy vaga) {
        // Verificar se o usuário já se candidatou à vaga
        if (candidaturasIds.contains(vaga.getId())) {
            // Se o usuário já se candidatou, não envie a notificação
            return;
        }

        // Lógica do cálculo de score
        int score = calcularScore(vaga);

        // Se o score for maior que 7, enviar a notificação
        if (score >= 7) {
            enviarNotificacao(getContext(), vaga);  // Envia a notificação
        }
    }


    private int calcularScore(Vacancy vaga) {
        // Verifique se a função do VacancyRecomender está retornando um valor adequado
        int score = VacancyRecomender.calculateJobScore(vaga, meuCurriculo, dadosAcademicos, dadosCursos, competencias);
        Log.d("Score", "Vaga: " + vaga.getTitle() + ", Score: " + score);  // Adicione um log para depurar o valor do score
        return score;
    }







    private void fetchCurriculumData(int candidateId) {
        CurriculumService.getCurriculumByCandidateId(candidateId, new CurriculumService.FetchCurriculumCallback() {
            @Override
            public void onSuccess(Curriculum curriculum) {
                meuCurriculo = curriculum;
                fetchAcademicData(candidateId);
                fetchCourseData(candidateId);
                fetchCompetences(candidateId);
            }

            @Override
            public void onFailure(String errorMessage) {

            }
        });
    }

    private void fetchAcademicData(int curriculumId) {
        AcademicDataService.getAcademicDataByCurriculumId(curriculumId, new AcademicDataService.FetchAcademicDataCallback() {
            @Override
            public void onSuccess(List<AcademicData> dataList) {
                dadosAcademicos = dataList;
                checkIfDataLoaded();
            }

            @Override
            public void onFailure(String errorMessage) {
                           }
        });
    }

    private void fetchCourseData(int curriculumId) {
        CourseDataService.getCourseDataByCurriculumId(curriculumId, new CourseDataService.FetchCourseDataCallback() {
            @Override
            public void onSuccess(List<CourseData> courseDataList) {
                dadosCursos = courseDataList;
                checkIfDataLoaded();
            }

            @Override
            public void onFailure(String errorMessage) {
                         }
        });
    }

    private void fetchCompetences(int curriculumId) {
        CompetenceDataService.getCompetencesByCurriculumId(curriculumId, new CompetenceDataService.FetchCompetencesCallback() {
            @Override
            public void onSuccess(List<CompetenceData> competencesList) {
                // Cria uma lista de strings contendo apenas os textos das competências
                competencias.clear();  // Limpa a lista antes de adicionar novos itens
                for (CompetenceData competence : competencesList) {
                    competencias.add(competence.getCompetence());  // Adiciona apenas o texto da competência
                }
                checkIfDataLoaded();
            }

            @Override
            public void onFailure(String errorMessage) {
                        }
        });
    }


    private void checkIfDataLoaded() {
        if (meuCurriculo != null && !dadosAcademicos.isEmpty() && !dadosCursos.isEmpty() && !competencias.isEmpty()) {
            // Todos os dados estão carregados, então podemos atualizar a lista
            atualizarLista(searchInput.getText().toString());
        }
    }








}
