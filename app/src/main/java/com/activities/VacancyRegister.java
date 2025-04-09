package com.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.models.MaskEditText;
import com.models.MoneyTextWatcher;
import com.models.Vacancy;
import com.services.VacancyService;

public class VacancyRegister extends AppCompatActivity {

    private VacancyService vacancyService;

    private EditText vacancyNameInput, vacancyLevelInput, salaryInput, cityInput, ufInput, contactInput, modalityInput, aboutCompanyInput, descriptionInput,requirementsInput, benefitsInput, localityInput;
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

        vacancyLevelInput = findViewById(R.id.level_input);
        salaryInput = findViewById(R.id.salary_input);
        cityInput = findViewById(R.id.city_input);
        contactInput = findViewById(R.id.contact_input);
        aboutCompanyInput = findViewById(R.id.about_company_input);
        descriptionInput = findViewById(R.id.description_input);
        requirementsInput = findViewById(R.id.requirements_input);
        benefitsInput = findViewById(R.id.benefits_input);
        salaryInput.addTextChangedListener(new MoneyTextWatcher(salaryInput));

        Spinner modalitySpinner = findViewById(R.id.modality_spinner);
        ArrayAdapter<CharSequence> modalityAdapter = ArrayAdapter.createFromResource(this,
                R.array.modality_options, R.layout.spinner_item);
        modalityAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        modalitySpinner.setAdapter(modalityAdapter);

        Spinner ufSpinner = findViewById(R.id.uf_spinner);
        ArrayAdapter<CharSequence> ufAdapter = ArrayAdapter.createFromResource(this,
                R.array.uf_options, R.layout.spinner_item);
        ufAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        ufSpinner.setAdapter(ufAdapter);

        Spinner nameSpinner = findViewById(R.id.vacancy_name_spinner);
        ArrayAdapter<CharSequence> nameAdapter = ArrayAdapter.createFromResource(this,
                R.array.vacancy_titles_options, R.layout.spinner_item);
        nameAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        nameSpinner.setAdapter(nameAdapter);





        vacancyService = new VacancyService();
    }

    public void addQuestions (View view){
        Intent intent = new Intent(VacancyRegister.this, QuestionRegister.class);
        startActivity(intent);
    }
    public void registerVacancy(View view){

        String vacancyName = ((Spinner) findViewById(R.id.vacancy_name_spinner)).getSelectedItem().toString();
        String level = vacancyLevelInput.getText().toString().trim();
        String salary = salaryInput.getText().toString().trim();
        String city = cityInput.getText().toString().trim();
        String uf = ((Spinner) findViewById(R.id.uf_spinner)).getSelectedItem().toString();
        String contact = contactInput.getText().toString().trim();
        String modality = ((Spinner) findViewById(R.id.modality_spinner)).getSelectedItem().toString();
        String aboutCompany = aboutCompanyInput.getText().toString().trim();
        String description = descriptionInput.getText().toString().trim();
        String requirements = requirementsInput.getText().toString().trim();
        String benefits = benefitsInput.getText().toString().trim();
        String locality = localityInput.getText().toString().trim();

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
        if(locality.isEmpty()){
            localityInput.setError("Preencha a localidade");
            localityInput.requestFocus();
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