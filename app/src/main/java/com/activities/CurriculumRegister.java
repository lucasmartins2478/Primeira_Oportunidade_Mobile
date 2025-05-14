package com.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.fragments.LoadingDialogFragment;
import com.models.Candidate;
import com.models.Curriculum;
import com.models.DateValidator;
import com.models.MaskEditText;
import com.services.CandidateService;
import com.services.CurriculumService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CurriculumRegister extends AppCompatActivity {

    private CurriculumService curriculumService;
    private CandidateService candidateService;
    int curriculumId, candidateId, userId;
    AppCompatButton registerBtn;
    LoadingDialogFragment loadingDialog;
    private EditText fullNameInput, birthDateInput, ageInput, cpfInput, genderInput, raceInput, phoneNumberInput, emailInput, cityInput, cepInput, ufInput, addressInput, addressNumberInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_curriculum_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        loadingDialog = new LoadingDialogFragment();



        curriculumService = new CurriculumService();
        candidateService = new CandidateService();

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        curriculumId = sharedPreferences.getInt("curriculumId", -1);
        candidateId = sharedPreferences.getInt("candidateId", -1);
        userId = sharedPreferences.getInt("userId", -1);


        Log.d("Curriculo", "CurriculumId: "+curriculumId);

        String name = sharedPreferences.getString("name", "Usuário não encontrado");
        String email = sharedPreferences.getString("email", "Email não encontrado");
        String phone = sharedPreferences.getString("phone", "Telefone não encontrado");
        String cpf = sharedPreferences.getString("cpf", "CPF não encontrado");



        registerBtn = findViewById(R.id.register_button);
        fullNameInput = findViewById(R.id.full_name_input);
        fullNameInput.setText(name);
        birthDateInput = findViewById(R.id.birth_date_input);
        ageInput = findViewById(R.id.age_input);
        cpfInput = findViewById(R.id.cpf_input);
        cpfInput.setText(cpf);
        phoneNumberInput = findViewById(R.id.phone_number_input);
        phoneNumberInput.setText(phone);
        emailInput = findViewById(R.id.email_input);
        emailInput.setText(email);
        cityInput = findViewById(R.id.city_input);
        cepInput = findViewById(R.id.cep_input);
        addressInput = findViewById(R.id.address_input);
        addressNumberInput = findViewById(R.id.address_number_input);


        birthDateInput.addTextChangedListener(MaskEditText.mask(birthDateInput, "##/##/####"));
        cpfInput.addTextChangedListener(MaskEditText.mask(cpfInput, "###.###.###-##"));
        phoneNumberInput.addTextChangedListener(MaskEditText.mask(phoneNumberInput, "(##) #####-####"));
        cepInput.addTextChangedListener(MaskEditText.mask(cepInput, "#####-###"));


        Spinner ufSpinner = findViewById(R.id.uf_spinner);
        ArrayAdapter<CharSequence> ufAdapter = ArrayAdapter.createFromResource(this,
                R.array.uf_options, R.layout.spinner_item);
        ufAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        ufSpinner.setAdapter(ufAdapter);



        Spinner genderSpinner = findViewById(R.id.gender_spinner);

        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(this,
                R.array.gender_options, R.layout.spinner_item);
        genderAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        genderSpinner.setAdapter(genderAdapter);

        Spinner raceSpinner = findViewById(R.id.race_spinner);

        ArrayAdapter<CharSequence> raceAdapter = ArrayAdapter.createFromResource(this,
                R.array.race_options, R.layout.spinner_item);
        raceAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        raceSpinner.setAdapter(raceAdapter);



        if(curriculumId != -1){
            registerBtn.setText("Salvar alterações");
            loadCurriculumData(curriculumId);
        }



        birthDateInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String data = s.toString();

                if (data.length() == 10) { // Apenas calcular se a data estiver completa
                    if (!DateValidator.isValidDate(data)) {
                        birthDateInput.setError("Data inválida! Use dd/MM/yyyy");
                        Toast.makeText(CurriculumRegister.this, "Data inválida!", Toast.LENGTH_SHORT).show();
                    } else {
                        int idade = calculateAge(data);
                        if (idade >= 0) {
                            ageInput.setText(String.valueOf(idade)); // Define a idade no campo
                        }
                    }
                }
            }
        });
    }
    private int calculateAge(String birthDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try {
            Calendar birth = Calendar.getInstance();
            birth.setTime(sdf.parse(birthDate));

            Calendar today = Calendar.getInstance();

            int age = today.get(Calendar.YEAR) - birth.get(Calendar.YEAR);

            // Verifica se ainda não fez aniversário este ano
            if (today.get(Calendar.DAY_OF_YEAR) < birth.get(Calendar.DAY_OF_YEAR)) {
                age--;
            }

            return age;
        } catch (ParseException e) {
            e.printStackTrace();
            return -1; // Retorna -1 para indicar erro
        }
    }

    public void onBackPressed(View view){
        finish();
    }
    public void registerCurriculum(View view){

        loadingDialog.show(getSupportFragmentManager(), "loading");

        String fullName = fullNameInput.getText().toString();
        String birthDate = birthDateInput.getText().toString();
        String age = ageInput.getText().toString();
        String cpf = cpfInput.getText().toString();
        String gender = ((Spinner) findViewById(R.id.gender_spinner)).getSelectedItem().toString();
        String race = ((Spinner) findViewById(R.id.race_spinner)).getSelectedItem().toString();
        String phoneNumber = phoneNumberInput.getText().toString();
        String email = emailInput.getText().toString();
        String city = cityInput.getText().toString();
        String cep = cepInput.getText().toString();
        String uf = ((Spinner) findViewById(R.id.uf_spinner)).getSelectedItem().toString();
        String address = addressInput.getText().toString();
        String addressNumber = addressNumberInput.getText().toString();


        if(fullName.isEmpty()){
            fullNameInput.setError("Preencha o nome completo");
            fullNameInput.requestFocus();
            return;
        }
        if(birthDate.isEmpty()){
            birthDateInput.setError("Preencha a data de nascimento");
            birthDateInput.requestFocus();
            return;
        }
        if(age.isEmpty()){
            ageInput.setError("Preencha a idade");
            ageInput.requestFocus();
            return;
        }
        if(cpf.isEmpty()){
            cpfInput.setError("Preencha o CPF");
            cpfInput.requestFocus();
            return;
        }
        if(gender.isEmpty()){
            genderInput.setError("Preencha o gênero");
            genderInput.requestFocus();
            return;
        }
        if(race.isEmpty()){
            raceInput.setError("Preencha a raça");
            raceInput.requestFocus();
            return;
        }
        if(phoneNumber.isEmpty()){
            phoneNumberInput.setError("Preencha o número de telefone");
            phoneNumberInput.requestFocus();
            return;
        }
        if(email.isEmpty()){
            emailInput.setError("Preencha o email");
            emailInput.requestFocus();
            return;
        }
        if(city.isEmpty()){
            cityInput.setError("Preencha a cidade");
            cityInput.requestFocus();
            return;
        }
        if(cep.isEmpty()){
            cepInput.setError("Preencha o CEP");
            cepInput.requestFocus();
            return;
        }
        if(uf.isEmpty()){
            ufInput.setError("Preencha o estado");
            ufInput.requestFocus();
            return;
        }
        if(address.isEmpty()){
            addressInput.setError("Preencha o endereço");
            addressInput.requestFocus();
            return;
        }
        if(addressNumber.isEmpty()){
            addressNumberInput.setError("Preencha o número de endereço");
            addressNumberInput.requestFocus();
            return;
        }
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        String token = sharedPreferences.getString("token", "Nenhum token encontrado");

        Candidate candidate = new Candidate(fullName, phoneNumber, cpf,userId );

        candidateService.updateCandidate(candidate, token, new CandidateService.registerCallback() {
            @Override
            public void onSuccess(Candidate candidate) {

            }

            @Override
            public void onFailure(String error) {

            }
        });

        Curriculum curriculum = new Curriculum(
                birthDate,
                age,
                gender,
                race,
                city,
                cep,
                uf,
                address,
                addressNumber
        );

        if(curriculumId != -1){

            curriculumService.updateCurriculum(CurriculumRegister.this, curriculum,token, new CurriculumService.CurriculumCallback() {
                @Override
                public void onSuccess() {
                    Toast.makeText(CurriculumRegister.this, "Currículo atualizado com sucesso!", Toast.LENGTH_SHORT).show();

                    loadingDialog.dismiss();
                    Intent intent = new Intent(CurriculumRegister.this, Profile.class);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onFailure(String errorMessage) {
                    Toast.makeText(CurriculumRegister.this, "Erro: " + errorMessage, Toast.LENGTH_LONG).show();
                    loadingDialog.dismiss();

                }
            });
        }else if(curriculumId == -1){
            CurriculumService.registerCurriculum(CurriculumRegister.this, curriculum,token, new CurriculumService.CurriculumCallback() {
                @Override
                public void onSuccess() {
                    Toast.makeText(CurriculumRegister.this, "Currículo cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
                    loadingDialog.dismiss();
                    Intent intent = new Intent(CurriculumRegister.this, AcademicDataRegister.class);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onFailure(String errorMessage) {
                    Toast.makeText(CurriculumRegister.this, "Erro: " + errorMessage, Toast.LENGTH_LONG).show();
                    loadingDialog.dismiss();
                }
            });
        }




    }



    private void loadCurriculumData(int candidateId) {

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        String token = sharedPreferences.getString("token", "Nenhum token encontrado");
        loadingDialog.show(getSupportFragmentManager(), "loading");
        CurriculumService.getCurriculumByCandidateId(candidateId, token, new CurriculumService.FetchCurriculumCallback() {
            @Override
            public void onSuccess(Curriculum curriculum) {
                runOnUiThread(() -> {

                    birthDateInput.setText(convertIsoToBrazilianDate(curriculum.getBirthDate()));
                    ageInput.setText(curriculum.getAge());
                    cityInput.setText(curriculum.getCity());
                    cepInput.setText(curriculum.getCep());
                    addressInput.setText(curriculum.getAddress());
                    addressNumberInput.setText(curriculum.getAddressNumber());

                    // Preenche spinners de forma segura
                    Spinner ufSpinner = findViewById(R.id.uf_spinner);
                    Spinner genderSpinner = findViewById(R.id.gender_spinner);
                    Spinner raceSpinner = findViewById(R.id.race_spinner);

                    String uf = curriculum.getUf();
                    String gender = curriculum.getGender();
                    String race = curriculum.getRace();

                    if (uf != null) {
                        ufSpinner.post(() -> {
                            ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) ufSpinner.getAdapter();
                            for (int i = 0; i < adapter.getCount(); i++) {
                                if (uf.equalsIgnoreCase(adapter.getItem(i).toString())) {
                                    ufSpinner.setSelection(i);
                                    break;
                                }
                            }
                        });
                    }

                    if (gender != null) {
                        genderSpinner.post(() -> {
                            ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) genderSpinner.getAdapter();
                            for (int i = 0; i < adapter.getCount(); i++) {
                                if (gender.equalsIgnoreCase(adapter.getItem(i).toString())) {
                                    genderSpinner.setSelection(i);
                                    break;
                                }
                            }
                        });
                    }

                    if (race != null) {
                        raceSpinner.post(() -> {
                            ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) raceSpinner.getAdapter();
                            for (int i = 0; i < adapter.getCount(); i++) {
                                if (race.equalsIgnoreCase(adapter.getItem(i).toString())) {
                                    raceSpinner.setSelection(i);
                                    break;
                                }
                            }
                        });
                    }

                    loadingDialog.dismiss();
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                runOnUiThread(() -> {
                    Toast.makeText(CurriculumRegister.this, "Erro ao carregar currículo: " + errorMessage, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private String convertIsoToBrazilianDate(String isoDate) {
        try {
            SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            SimpleDateFormat brFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

            return brFormat.format(isoFormat.parse(isoDate));
        } catch (ParseException | NullPointerException e) {
            e.printStackTrace();
            return ""; // Retorna vazio caso falhe
        }
    }




}