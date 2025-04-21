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

import com.models.Curriculum;
import com.services.CandidateService;
import com.services.CompanyService;
import com.services.CurriculumService;

public class AdditionalDataRegister extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_additional_data_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Spinner interestAreaSpinner = findViewById(R.id.interest_area_spinner);
        ArrayAdapter<CharSequence> interestAreaAdapter = ArrayAdapter.createFromResource(this,
                R.array.interest_area_options, R.layout.spinner_item);
        interestAreaAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        interestAreaSpinner.setAdapter(interestAreaAdapter);






    }



    public void finishCurriculumRegister(View view){
        EditText attachedInput = findViewById(R.id.curriculum_input);
        EditText descriptionInput = findViewById(R.id.about_you_input);



        String description = descriptionInput.getText().toString();
        String interestArea = ((Spinner) findViewById(R.id.interest_area_spinner)).getSelectedItem().toString();
        String attached  = attachedInput.getText().toString();


        Curriculum additionalData = new Curriculum("curriculo", description, interestArea );


        CurriculumService.additionalData(this, additionalData, new CurriculumService.CurriculumCallback() {
            @Override
            public void onSuccess() {
                CandidateService.addCurriculumToCandidate(AdditionalDataRegister.this, new CompanyService.RegisterCallback() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(AdditionalDataRegister.this, "Dados adicionais enviados. Cadastro finalizado", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AdditionalDataRegister.this, Vacancies.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onFailure(String error) {

                    }
                });

            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(AdditionalDataRegister.this, "Erro ao finalizar cadastro: "+errorMessage, Toast.LENGTH_SHORT).show();
            }
        });


    }


    public void onBackPressed(View view){
        finish();
    }

}