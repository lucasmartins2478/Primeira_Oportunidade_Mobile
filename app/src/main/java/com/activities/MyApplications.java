package com.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adapters.VacancyAdapter;
import com.fragments.SearchFormFragment;
import com.models.Application;
import com.models.Vacancy;
import com.services.ApplicationService;
import com.services.VacancyService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MyApplications extends AppCompatActivity {

    private RecyclerView recyclerView;
    private VacancyService vacancyService;

private ArrayList<Integer> vacancyIdsCandidatadas;
    private int candidateId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_applications);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });



        // Pegando o ID do candidato salvo no SharedPreferences
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        candidateId = prefs.getInt("candidateId", -1);

        if (candidateId == -1) {
            Toast.makeText(this, "Erro: ID do candidato não encontrado", Toast.LENGTH_SHORT).show();
            return;
        }

        vacancyService = new VacancyService();
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        buscarCandidaturasDoUsuario();

    }

    private void buscarCandidaturasDoUsuario() {

        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        String token = prefs.getString("token", "Nenhum token encontrado" );
        ApplicationService.getAllApplications(this,token, new ApplicationService.ApplicationsListCallback() {
            @Override
            public void onSuccess(List<Application> applications) {
                // Filtra as candidaturas que são do candidato atual
                List<Application> minhasCandidaturas = applications.stream()
                        .filter(app -> app.getUserId() == candidateId)
                        .collect(Collectors.toList());

                // Extrai todos os vacancyIds únicos dessas candidaturas
                ArrayList<Integer> vacancyIdsCandidatadas = minhasCandidaturas.stream()
                        .map(Application::getVacancyId)
                        .distinct()
                        .collect(Collectors.toCollection(ArrayList::new));

                // Agora sim, cria o fragmento e passa a lista
                runOnUiThread(() -> {
                    SearchFormFragment fragment = new SearchFormFragment();
                    Bundle args = new Bundle();
                    args.putBoolean("isMyApplicationsScreen", true);
                    args.putIntegerArrayList("vacancyIdsCandidatadas", vacancyIdsCandidatadas);
                    fragment.setArguments(args);

                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.searchMenuFragmentContainer, fragment)
                            .commit();
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                runOnUiThread(() -> Toast.makeText(MyApplications.this, "Erro ao buscar candidaturas: " + errorMessage, Toast.LENGTH_SHORT).show());
            }
        });
    }


}
