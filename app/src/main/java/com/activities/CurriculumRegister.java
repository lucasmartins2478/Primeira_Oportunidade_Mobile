package com.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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

    }

    public void onBackPressed(View view){
        finish();
    }
    public void academicData(View view){
        Intent intent = new Intent(CurriculumRegister.this, AcademicDataRegister.class);
        startActivity(intent);
    }
}