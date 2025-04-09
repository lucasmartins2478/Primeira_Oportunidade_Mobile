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

public class CoursesRegister extends AppCompatActivity {

    private EditText courseNameInput, modalityInput, durationInput, endDateInput, grantingInstitutionInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_courses_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        courseNameInput = findViewById(R.id.course_name_input);
        durationInput = findViewById(R.id.duration_input);
        endDateInput = findViewById(R.id.end_date_input);
        grantingInstitutionInput = findViewById(R.id.granting_intitution_input);

        Spinner modalitySpinner = findViewById(R.id.modality_spinner);
        ArrayAdapter<CharSequence> modalityAdapter = ArrayAdapter.createFromResource(this,
                R.array.course_modality_options, R.layout.spinner_item);
        modalityAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        modalitySpinner.setAdapter(modalityAdapter);


        endDateInput.addTextChangedListener(MaskEditText.mask(endDateInput, "##/####"));

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
                        Toast.makeText(CoursesRegister.this, "Data inválida!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    public void onBackPressed(View view){
        finish();
    }
    public void additionalData(View view){

        String courseName = courseNameInput.getText().toString();
        String modality = ((Spinner) findViewById(R.id.modality_spinner)).getSelectedItem().toString();
        String duration = durationInput.getText().toString();
        String endDate = endDateInput.getText().toString();
        String grantingIntitution = grantingInstitutionInput.getText().toString();


        if(courseName.isEmpty()){
            courseNameInput.setError("Preencha o nome do curso");
            courseNameInput.requestFocus();
            return;
        }
        if(modality.isEmpty()){
            modalityInput.setError("Preencha a modalidade");
            modalityInput.requestFocus();
            return;
        }
        if(duration.isEmpty()){
            durationInput.setError("Preencha a duração do curso");
            durationInput.requestFocus();
            return;
        }
        if(endDate.isEmpty()){
            endDateInput.setError("Preencha a data de conclusão");
            endDateInput.requestFocus();
            return;
        }
        if(grantingIntitution.isEmpty()){
            grantingInstitutionInput.setError("Preencha o nome da instituição concedente");
            grantingInstitutionInput.requestFocus();
            return;
        }


        Intent intent = new Intent(CoursesRegister.this, AdditionalDataRegister.class);
        startActivity(intent);
    }
}