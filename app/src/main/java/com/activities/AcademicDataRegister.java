package com.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

import com.models.DateValidator;
import com.models.MaskEditText;

public class AcademicDataRegister extends AppCompatActivity {

    private EditText instituitionNameInput, levelInput, periodInput, startDateInput, endDateInput, courseNameInput, cityInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_academic_data_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        instituitionNameInput = findViewById(R.id.institution_name_input);

        startDateInput = findViewById(R.id.start_date_input);
        endDateInput = findViewById(R.id.end_date_input);
        courseNameInput = findViewById(R.id.course_name_input);
        cityInput = findViewById(R.id.city_input);

        startDateInput.addTextChangedListener(MaskEditText.mask(startDateInput, "##/####"));
        endDateInput.addTextChangedListener(MaskEditText.mask(endDateInput, "##/####"));



        Spinner periodSpinner = findViewById(R.id.period_spinner);
        ArrayAdapter<CharSequence> periodAdapter = ArrayAdapter.createFromResource(this,
                R.array.period_options, R.layout.spinner_item);
        periodAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        periodSpinner.setAdapter(periodAdapter);


        Spinner levelSpinner = findViewById(R.id.level_spinner);
        ArrayAdapter<CharSequence> levelAdapter = ArrayAdapter.createFromResource(this,
                R.array.uf_options, R.layout.spinner_item);
        levelAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        levelSpinner.setAdapter(levelAdapter);



        startDateInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String data = s.toString();

                if (data.length() == 7) { // Apenas calcular se a data estiver completa
                    if (!DateValidator.isValidDate(data)) {
                        startDateInput.setError("Data inválida! Usen MM/yyyy");
                        Toast.makeText(AcademicDataRegister.this, "Data inválida!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        endDateInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String data = s.toString();

                if (data.length() == 7) { // Apenas calcular se a data estiver completa
                    if (!DateValidator.isValidDate(data)) {
                        endDateInput.setError("Data inválida! Usen MM/yyyy");
                        Toast.makeText(AcademicDataRegister.this, "Data inválida!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    public void onBackPressed(View view){
        finish();
    }
    public void courseData(View view){
        String institutionName = instituitionNameInput.getText().toString();
        String level = ((Spinner) findViewById(R.id.level_spinner)).getSelectedItem().toString();
        String period = ((Spinner) findViewById(R.id.period_spinner)).getSelectedItem().toString();
        String startDate = startDateInput.getText().toString();
        String endDate = endDateInput.getText().toString();
        String courseName = courseNameInput.getText().toString();
        String city = cityInput.getText().toString();

        if(institutionName.isEmpty()){
            instituitionNameInput.setError("Preencha o nome da intituição de ensino");
            instituitionNameInput.requestFocus();
            return;
        }
        if(level.isEmpty()){
            levelInput.setError("Preencha o nível do curso");
            levelInput.requestFocus();
            return;
        }
        if(period.isEmpty()){
            periodInput.setError("Preencha o ano/semestre");
            periodInput.requestFocus();
            return;
        }
        if(startDate.isEmpty()){
            startDateInput.setError("Preencha a data de início");
            startDateInput.requestFocus();
            return;
        }
        if(endDate.isEmpty()){
            endDateInput.setError("Preencha a data de conclusão");
            endDateInput.requestFocus();
            return;
        }
        if(courseName.isEmpty()){
            courseNameInput.setError("Preencha o nome do curso");
            courseNameInput.requestFocus();
            return;
        }
        if(city.isEmpty()){
            cityInput.setError("Preencha a cidade");
            cityInput.requestFocus();
            return;
        }
        Intent intent = new Intent(AcademicDataRegister.this, CoursesRegister.class);
        startActivity(intent);
    }
}