package com.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.models.Curriculum;
import com.models.DateValidator;
import com.models.MaskEditText;
import com.services.CurriculumService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CurriculumRegister extends AppCompatActivity {

    private CurriculumService curriculumService;
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



        curriculumService = new CurriculumService();

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        String name = sharedPreferences.getString("name", "Usuário não encontrado");
        String email = sharedPreferences.getString("email", "Email não encontrado");
        String phone = sharedPreferences.getString("phone", "Telefone não encontrado");



        fullNameInput = findViewById(R.id.full_name_input);
        fullNameInput.setText(name);
        birthDateInput = findViewById(R.id.birth_date_input);
        ageInput = findViewById(R.id.age_input);
        cpfInput = findViewById(R.id.cpf_input);
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


        CurriculumService.registerCurriculum(CurriculumRegister.this, curriculum, new CurriculumService.CurriculumCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(CurriculumRegister.this, "Currículo cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
                // Vai pra próxima tela
                Intent intent = new Intent(CurriculumRegister.this, AcademicDataRegister.class);
                startActivity(intent);
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(CurriculumRegister.this, "Erro: " + errorMessage, Toast.LENGTH_LONG).show();
            }
        });

    }




}