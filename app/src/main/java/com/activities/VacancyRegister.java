package com.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.models.Vacancy;
import com.services.VacancyService;

public class VacancyRegister extends AppCompatActivity {

    private VacancyService vacancyService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_vacancy_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        vacancyService = new VacancyService();
    }

    public void addQuestions (View view){
        Intent intent = new Intent(VacancyRegister.this, QuestionRegister.class);
        startActivity(intent);
    }
    public void registerVacancy(View view){
        EditText vacancyNameInput = findViewById(R.id.vacancy_name_input);
        String vacancyName = vacancyNameInput.getText().toString().trim();
        EditText vacancyLevelInput = findViewById(R.id.level_input);
        String level = vacancyLevelInput.getText().toString().trim();
        EditText salaryInput = findViewById(R.id.salary_input);
        String salary = salaryInput.getText().toString().trim();
        EditText cityInput = findViewById(R.id.city_input);
        String city = cityInput.getText().toString().trim();
        EditText ufInput = findViewById(R.id.uf_input);
        String uf = ufInput.getText().toString().trim();
        EditText contactInput = findViewById(R.id.contact_input);
        String contact = contactInput.getText().toString().trim();
        EditText modalityInput = findViewById(R.id.modality_input);
        String modality = modalityInput.getText().toString().trim();
        EditText aboutCompanyInput = findViewById(R.id.about_company_input);
        String aboutCompany = aboutCompanyInput.getText().toString().trim();
        EditText descriptionInput = findViewById(R.id.description_input);
        String description = descriptionInput.getText().toString().trim();
        EditText requirementsInput = findViewById(R.id.requirements_input);
        String requirements = requirementsInput.getText().toString().trim();
        EditText benefitsInput = findViewById(R.id.benefits_input);
        String benefits = benefitsInput.getText().toString().trim();
        String locality = "Rua das flores";

        // Verificação dos campos vazios
        if (vacancyName.isEmpty()) {
            vacancyNameInput.setError("Preencha o nome da vaga");
            vacancyNameInput.requestFocus();
            return;
        }
        if (level.isEmpty()) {
            vacancyLevelInput.setError("Preencha o nível da vaga");
            vacancyLevelInput.requestFocus();
            return;
        }
        if (salary.isEmpty()) {
            salaryInput.setError("Preencha o salário");
            salaryInput.requestFocus();
            return;
        }
        if (city.isEmpty()) {
            cityInput.setError("Preencha a cidade");
            cityInput.requestFocus();
            return;
        }
        if (uf.isEmpty()) {
            ufInput.setError("Preencha o estado (UF)");
            ufInput.requestFocus();
            return;
        }
        if (contact.isEmpty()) {
            contactInput.setError("Preencha o contato");
            contactInput.requestFocus();
            return;
        }
        if (modality.isEmpty()) {
            modalityInput.setError("Preencha a modalidade");
            modalityInput.requestFocus();
            return;
        }
        if (aboutCompany.isEmpty()) {
            aboutCompanyInput.setError("Preencha informações sobre a empresa");
            aboutCompanyInput.requestFocus();
            return;
        }
        if (description.isEmpty()) {
            descriptionInput.setError("Preencha a descrição da vaga");
            descriptionInput.requestFocus();
            return;
        }
        if (requirements.isEmpty()) {
            requirementsInput.setError("Preencha os requisitos");
            requirementsInput.requestFocus();
            return;
        }
        if (benefits.isEmpty()) {
            benefitsInput.setError("Preencha os benefícios");
            benefitsInput.requestFocus();
            return;
        }


        Vacancy vacancy = new Vacancy(vacancyName, description, aboutCompany, benefits, requirements, modality, locality, uf, contact, salary, level, 1);

        if (vacancyService.registerVacancy(vacancy)) {
            Toast.makeText(this, "Vaga cadastrada com sucesso!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(VacancyRegister.this, MyVacancies.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Ocorreu um erro!", Toast.LENGTH_SHORT).show();
        }
    }
    public void onBackPressed(View view){
        finish();
    }

}