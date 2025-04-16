package com.activities;

import static java.security.AccessController.getContext;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adapters.VacancyAdapter;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.models.Vacancy;
import com.services.VacancyService;

import java.util.ArrayList;
import java.util.List;

public class Vacancies extends AppCompatActivity {

    private RecyclerView recyclerView;
    private VacancyAdapter vagaAdapter;
    private VacancyService vacancyService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_vacancies);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        vacancyService = new VacancyService();
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        vacancyService.fetchVacanciesFromApi(new VacancyService.VacancyCallback() {
            @Override
            public void onSuccess(ArrayList<Vacancy> vacancies) {
                runOnUiThread(() -> {
                    // Agora, passamos a função para o adapter
                    VacancyAdapter adapter = new VacancyAdapter(vacancies, Vacancies.this, new VacancyAdapter.OnVerMaisClickListener() {
                        @Override
                        public void onVerMaisClick(Vacancy vaga) {
                            // Aqui você pode tratar o clique no botão "Ver Mais" caso precise de algo mais
                            // Por exemplo, você pode exibir detalhes em outro lugar ou fazer outra ação.
                        }
                    });

                    recyclerView.setAdapter(adapter);

                });
            }

            @Override
            public void onFailure(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(Vacancies.this, error, Toast.LENGTH_LONG).show();
                });
            }
        });
    }
}
