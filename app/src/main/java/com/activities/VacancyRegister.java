package com.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.fragments.LoadingDialogFragment;
import com.models.MaskEditText;
import com.models.MoneyTextWatcher;
import com.models.Vacancy;
import com.services.VacancyService;

public class VacancyRegister extends AppCompatActivity {

    private VacancyService vacancyService;
    private Vacancy vacancyToEdit;

    LoadingDialogFragment loadingDialog;
    private boolean isEditMode = false;


    private EditText   salaryInput, cityInput,  contactInput, aboutCompanyInput, descriptionInput,requirementsInput, benefitsInput, localityInput;
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



        loadingDialog = new LoadingDialogFragment();



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

        Spinner levelSpinner = findViewById(R.id.level_spinner);
        ArrayAdapter<CharSequence> levelAdapter = ArrayAdapter.createFromResource(this,
                R.array.vacancy_level_options, R.layout.spinner_item);
        levelAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        levelSpinner.setAdapter(levelAdapter);

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


        if (vacancyToEdit != null) {
            isEditMode = true;
            preencherCamposComVacancy(vacancyToEdit);
            AppCompatButton registerBtn = findViewById(R.id.register_button);
            registerBtn.setText("Salvar Alterações"); // muda o texto do botão
        }

        vacancyToEdit = (Vacancy) getIntent().getSerializableExtra("vacancyToEdit");

        if (vacancyToEdit != null) {
            isEditMode = true;
            preencherCamposComVacancy(vacancyToEdit);

            AppCompatButton registerBtn = findViewById(R.id.register_button);
            registerBtn.setText("Salvar alterações");
        }


        vacancyService = new VacancyService();
    }


    public void registerVacancy(View view){

        loadingDialog.show(getSupportFragmentManager(), "loading");

        String vacancyName = ((Spinner) findViewById(R.id.vacancy_name_spinner)).getSelectedItem().toString();
        String salary = salaryInput.getText().toString().trim();
        String city = cityInput.getText().toString().trim();
        String uf = ((Spinner) findViewById(R.id.uf_spinner)).getSelectedItem().toString();
        String contact = contactInput.getText().toString().trim();
        String modality = ((Spinner) findViewById(R.id.modality_spinner)).getSelectedItem().toString();
        String level  = ((Spinner) findViewById(R.id.level_spinner)).getSelectedItem().toString();
        String aboutCompany = aboutCompanyInput.getText().toString().trim();
        String description = descriptionInput.getText().toString().trim();
        String requirements = requirementsInput.getText().toString().trim();
        String benefits = benefitsInput.getText().toString().trim();




        if (salary.isEmpty()) {
            salaryInput.setError("Preencha o salário");
            salaryInput.requestFocus();
            loadingDialog.dismiss();

            return;
        }
        if (city.isEmpty()) {
            cityInput.setError("Preencha a cidade");
            cityInput.requestFocus();
            loadingDialog.dismiss();

            return;
        }

        if (aboutCompany.isEmpty()) {
            aboutCompanyInput.setError("Preencha informações sobre a empresa");
            aboutCompanyInput.requestFocus();
            loadingDialog.dismiss();

            return;
        }
        if (description.isEmpty()) {
            descriptionInput.setError("Preencha a descrição da vaga");
            descriptionInput.requestFocus();
            loadingDialog.dismiss();

            return;
        }
        if (requirements.isEmpty()) {
            requirementsInput.setError("Preencha os requisitos");
            requirementsInput.requestFocus();
            loadingDialog.dismiss();

            return;
        }
        if (benefits.isEmpty()) {
            benefitsInput.setError("Preencha os benefícios");
            benefitsInput.requestFocus();
            loadingDialog.dismiss();

            return;
        }


        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        int companyId = sharedPreferences.getInt("companyId", 0);
        String locality = sharedPreferences.getString("address", "Nenhum endereço encontrado");
        String companyName = sharedPreferences.getString("name", "Nenhum nome encontrado");

        String token = sharedPreferences.getString("token", "Nenhum token encontrado");


        if (isEditMode) {
            vacancyToEdit.setTitle(vacancyName);
            vacancyToEdit.setSalary(salary);
            vacancyToEdit.setLocality(city);
            vacancyToEdit.setUf(uf);
            vacancyToEdit.setContact(contact);
            vacancyToEdit.setModality(modality);
            vacancyToEdit.setLevel(level);
            vacancyToEdit.setAboutCompany(aboutCompany);
            vacancyToEdit.setDescription(description);
            vacancyToEdit.setRequirements(requirements);
            vacancyToEdit.setBenefits(benefits);

            vacancyService.updateVacancy(vacancyToEdit, token, new VacancyService.RegisterIdCallback() {
                @Override
                public void onSuccess(int vacancyId) {
                    runOnUiThread(() -> {
                        Toast.makeText(VacancyRegister.this, "Vaga atualizada com sucesso", Toast.LENGTH_SHORT).show();
                        loadingDialog.dismiss();
                        finish(); // ou redirecionar de volta pra lista
                    });
                }

                @Override
                public void onFailure(String error) {
                    runOnUiThread(() -> {
                        Log.e("VacancyRegister", "Erro ao cadastrar/atualizar vaga: " + error);
                        loadingDialog.dismiss();
                        Toast.makeText(VacancyRegister.this, "Erro ao atualizar vaga: " + error, Toast.LENGTH_SHORT).show();
                    });
                }
            });
        } else {
            // Modo cadastro (já está pronto)
            Vacancy vacancy = new Vacancy(vacancyName, description, aboutCompany, benefits, requirements, modality, locality, uf, contact, salary, level, companyId , false, true, companyName);

            vacancyService.registerVacancyWithId(vacancy,token, new VacancyService.RegisterIdCallback() {
                @Override
                public void onSuccess(int vacancyId) {
                    runOnUiThread(() -> {
                        Toast.makeText(VacancyRegister.this, "Vaga cadastrada com sucesso", Toast.LENGTH_SHORT).show();
                        loadingDialog.dismiss();
                        startActivity(new Intent(VacancyRegister.this, MyVacancies.class));
                    });
                }

                @Override
                public void onFailure(String error) {
                    runOnUiThread(() -> {
                        Toast.makeText(VacancyRegister.this, "Erro ao cadastrar vaga", Toast.LENGTH_SHORT).show();
                        loadingDialog.dismiss();
                    });
                }
            });
        }
    }

    public void addQuestion(View view){

        loadingDialog.show(getSupportFragmentManager(), "loading");

        String vacancyName = ((Spinner) findViewById(R.id.vacancy_name_spinner)).getSelectedItem().toString();
        String salary = salaryInput.getText().toString().trim();
        String city = cityInput.getText().toString().trim();
        String level = ((Spinner) findViewById(R.id.level_spinner)).getSelectedItem().toString();
        String uf = ((Spinner) findViewById(R.id.uf_spinner)).getSelectedItem().toString();
        String contact = contactInput.getText().toString().trim();
        String modality = ((Spinner) findViewById(R.id.modality_spinner)).getSelectedItem().toString();
        String aboutCompany = aboutCompanyInput.getText().toString().trim();
        String description = descriptionInput.getText().toString().trim();
        String requirements = requirementsInput.getText().toString().trim();
        String benefits = benefitsInput.getText().toString().trim();




        if (salary.isEmpty()) {
            salaryInput.setError("Preencha o salário");
            salaryInput.requestFocus();
            loadingDialog.dismiss();

            return;
        }
        if (city.isEmpty()) {
            cityInput.setError("Preencha a cidade");
            cityInput.requestFocus();
            loadingDialog.dismiss();

            return;
        }

        if (aboutCompany.isEmpty()) {
            aboutCompanyInput.setError("Preencha informações sobre a empresa");
            aboutCompanyInput.requestFocus();
            loadingDialog.dismiss();

            return;
        }
        if (description.isEmpty()) {
            descriptionInput.setError("Preencha a descrição da vaga");
            descriptionInput.requestFocus();
            loadingDialog.dismiss();

            return;
        }
        if (requirements.isEmpty()) {
            requirementsInput.setError("Preencha os requisitos");
            requirementsInput.requestFocus();
            loadingDialog.dismiss();

            return;
        }
        if (benefits.isEmpty()) {
            benefitsInput.setError("Preencha os benefícios");
            benefitsInput.requestFocus();
            loadingDialog.dismiss();

            return;
        }


        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int companyId = sharedPreferences.getInt("companyId", 0);
        String locality = sharedPreferences.getString("address", "Nenhum endereço encontrado");
        String companyName = sharedPreferences.getString("name", "Nenhum nome encontrado");
        String token = sharedPreferences.getString("token", "Nenhum token encontrado");


        if (isEditMode) {
            vacancyToEdit.setTitle(vacancyName);
            vacancyToEdit.setSalary(salary);
            vacancyToEdit.setLocality(city);
            vacancyToEdit.setUf(uf);
            vacancyToEdit.setContact(contact);
            vacancyToEdit.setModality(modality);
            vacancyToEdit.setLevel(level);
            vacancyToEdit.setAboutCompany(aboutCompany);
            vacancyToEdit.setDescription(description);
            vacancyToEdit.setRequirements(requirements);
            vacancyToEdit.setBenefits(benefits);

            vacancyService.updateVacancy(vacancyToEdit, token, new VacancyService.RegisterIdCallback() {
                @Override
                public void onSuccess(int vacancyId) {
                    runOnUiThread(() -> {
                        Toast.makeText(VacancyRegister.this, "Vaga atualizada com sucesso", Toast.LENGTH_SHORT).show();
                        loadingDialog.dismiss();

                        Intent intent = new Intent(VacancyRegister.this, QuestionRegister.class);
                        intent.putExtra("vacancyId", vacancyId);  // ✅ envia o ID
                        startActivity(intent);
                        finish();
                    });
                }

                @Override
                public void onFailure(String error) {
                    runOnUiThread(() -> {
                        Log.e("VacancyRegister", "Erro ao cadastrar/atualizar vaga: " + error);
                        loadingDialog.dismiss();
                        Toast.makeText(VacancyRegister.this, "Erro ao atualizar vaga: " + error, Toast.LENGTH_SHORT).show();
                    });
                }
            });
        } else {
            // Modo cadastro (já está pronto)
            Vacancy vacancy = new Vacancy(vacancyName, description, aboutCompany, benefits, requirements, modality, locality, uf, contact, salary, level, companyId , false, true, companyName);

            vacancyService.registerVacancyWithId(vacancy, token, new VacancyService.RegisterIdCallback() {
                @Override
                public void onSuccess(int vacancyId) {
                    runOnUiThread(() -> {
                        Toast.makeText(VacancyRegister.this, "Vaga cadastrada com sucesso", Toast.LENGTH_SHORT).show();
                        loadingDialog.dismiss();

                        Intent intent = new Intent(VacancyRegister.this, QuestionRegister.class);
                        intent.putExtra("vacancyId", vacancyId);  // ✅ envia o ID
                        startActivity(intent);
                        finish();
                    });
                }

                @Override
                public void onFailure(String error) {
                    runOnUiThread(() -> {
                        Toast.makeText(VacancyRegister.this, "Erro ao cadastrar vaga", Toast.LENGTH_SHORT).show();
                        loadingDialog.dismiss();
                    });
                }
            });
        }
    }
    private void preencherCamposComVacancy(Vacancy vacancy) {
        loadingDialog.show(getSupportFragmentManager(), "loading");
        salaryInput.setText(vacancy.getSalary());
        cityInput.setText(vacancy.getLocality());
        contactInput.setText(vacancy.getContact());
        aboutCompanyInput.setText(vacancy.getAboutCompany());
        descriptionInput.setText(vacancy.getDescription());
        requirementsInput.setText(vacancy.getRequirements());
        benefitsInput.setText(vacancy.getBenefits());

        Spinner modalitySpinner = findViewById(R.id.modality_spinner);
        Spinner levelSpinner = findViewById(R.id.level_spinner);
        Spinner ufSpinner = findViewById(R.id.uf_spinner);
        Spinner nameSpinner = findViewById(R.id.vacancy_name_spinner);

        ArrayAdapter modalityAdapter = (ArrayAdapter) modalitySpinner.getAdapter();
        int modalityPosition = modalityAdapter.getPosition(vacancy.getModality());
        modalitySpinner.setSelection(modalityPosition);

        ArrayAdapter levelAdapter = (ArrayAdapter) levelSpinner.getAdapter();
        int levelPosition = levelAdapter.getPosition(vacancy.getLevel());
        levelSpinner.setSelection(levelPosition);

        ArrayAdapter ufAdapter = (ArrayAdapter) ufSpinner.getAdapter();
        int ufPosition = ufAdapter.getPosition(vacancy.getUf());
        ufSpinner.setSelection(ufPosition);

        ArrayAdapter nameAdapter = (ArrayAdapter) nameSpinner.getAdapter();
        int namePosition = nameAdapter.getPosition(vacancy.getTitle());
        nameSpinner.setSelection(namePosition);
        loadingDialog.dismiss();
    }

    private void setSpinnerValue(int spinnerId, String value) {
        Spinner spinner = findViewById(spinnerId);
        ArrayAdapter adapter = (ArrayAdapter) spinner.getAdapter();
        int position = adapter.getPosition(value);
        if (position >= 0) {
            spinner.setSelection(position);
        }
    }

    public void onBackPressed(View view){
        finish();
    }

}