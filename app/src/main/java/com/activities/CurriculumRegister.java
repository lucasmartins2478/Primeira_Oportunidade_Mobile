package com.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.models.DateValidator;
import com.models.MaskEditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CurriculumRegister extends AppCompatActivity {

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


        fullNameInput = findViewById(R.id.full_name_input);
        birthDateInput = findViewById(R.id.birth_date_input);
        ageInput = findViewById(R.id.age_input);
        cpfInput = findViewById(R.id.cpf_input);
        genderInput = findViewById(R.id.gender_input);
        raceInput = findViewById(R.id.race_input);
        phoneNumberInput = findViewById(R.id.phone_number_input);
        emailInput = findViewById(R.id.email_input);
        cityInput = findViewById(R.id.city_input);
        cepInput = findViewById(R.id.cep_input);
        ufInput = findViewById(R.id.uf_input);
        addressInput = findViewById(R.id.address_input);
        addressNumberInput = findViewById(R.id.address_number_input);


        birthDateInput.addTextChangedListener(MaskEditText.mask(birthDateInput, "##/##/####"));
        cpfInput.addTextChangedListener(MaskEditText.mask(cpfInput, "###.###.###-##"));
        phoneNumberInput.addTextChangedListener(MaskEditText.mask(phoneNumberInput, "(##) #####-####"));
        cepInput.addTextChangedListener(MaskEditText.mask(cepInput, "#####-###"));


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
    public void academicData(View view){

        String fullName = fullNameInput.getText().toString();
        String birthDate = birthDateInput.getText().toString();
        String age = ageInput.getText().toString();
        String cpf = cpfInput.getText().toString();
        String gender = genderInput.getText().toString();
        String race = raceInput.getText().toString();
        String phoneNumber = phoneNumberInput.getText().toString();
        String email = emailInput.getText().toString();
        String city = cityInput.getText().toString();
        String cep = cepInput.getText().toString();
        String uf = ufInput.getText().toString();
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


        Intent intent = new Intent(CurriculumRegister.this, AcademicDataRegister.class);
        startActivity(intent);
    }
}