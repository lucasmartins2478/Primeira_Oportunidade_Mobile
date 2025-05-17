package com.activities;

import static java.security.AccessController.getContext;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
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
import com.fragments.VacancyDetailsFragment;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.models.Vacancy;
import com.services.VacancyService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Vacancies extends AppCompatActivity {

    private RecyclerView recyclerView;

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


        if (getIntent().hasExtra("vacancy")) {
            Vacancy vacancy = (Vacancy) getIntent().getSerializableExtra("vacancy");

            if (vacancy != null) {
                // Exibir o Fragment com os detalhes da vaga
                VacancyDetailsFragment fragment = VacancyDetailsFragment.newInstance(vacancy);
                fragment.show(getSupportFragmentManager(), fragment.getTag());
            }
        }

        criarCanalDeNotificacao();


        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


    }


    private void criarCanalDeNotificacao() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager.getNotificationChannel("CANAL_VAGAS") == null) {
                CharSequence name = "Canal de Vagas";
                String description = "Notificações sobre vagas compatíveis";
                int importance = NotificationManager.IMPORTANCE_DEFAULT;

                NotificationChannel channel = new NotificationChannel("CANAL_VAGAS", name, importance);
                channel.setDescription(description);

                notificationManager.createNotificationChannel(channel);
            }
        }
    }


}
