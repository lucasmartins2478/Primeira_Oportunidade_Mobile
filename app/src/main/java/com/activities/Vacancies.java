package com.activities;

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
                    vagaAdapter = new VacancyAdapter(vacancies);
                    recyclerView.setAdapter(vagaAdapter);
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