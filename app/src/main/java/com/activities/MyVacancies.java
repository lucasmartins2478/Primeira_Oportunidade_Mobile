package com.activities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
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

public class MyVacancies extends AppCompatActivity {

    private RecyclerView recyclerView;
    private VacancyService vacancyService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_vacancies);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        vacancyService = new VacancyService();



        vacancyService = new VacancyService();
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int companyId = sharedPreferences.getInt("companyId", -1);
        Log.d("LoginDebug", "Company ID recuperado: " + companyId);

        String token = sharedPreferences.getString("token", "Nenhum token encontrado");


        if(companyId != -1){
            vacancyService.fetchVacanciesByCompanyId(companyId,token, new VacancyService.VacancyCallback() {
                @Override
                public void onSuccess(ArrayList<Vacancy> vacancies) {
                    runOnUiThread(() -> {
                        // Agora, passamos a função para o adapter
                        VacancyAdapter adapter = new VacancyAdapter(vacancies, MyVacancies.this, new VacancyAdapter.OnVerMaisClickListener() {
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
                        Toast.makeText(MyVacancies.this, error, Toast.LENGTH_LONG).show();
                    });
                }
            });
        }

        criarCanalDeNotificacaoEmpresa();


    }

    private void criarCanalDeNotificacaoEmpresa() {
        // Verifica se a versão do Android é 8.0 (API 26) ou superior
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Canal de Vagas da Empresa";
            String description = "Notificações sobre novos candidatos";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            // Cria o canal de notificação
            NotificationChannel channel = new NotificationChannel("CANAL_VAGAS_EMPRESA", name, importance);
            channel.setDescription(description);

            // Registra o canal no sistema
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}