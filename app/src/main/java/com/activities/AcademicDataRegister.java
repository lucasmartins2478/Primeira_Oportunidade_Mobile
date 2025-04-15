package com.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.models.AcademicData;
import com.models.Curriculum;
import com.models.DateValidator;
import com.models.MaskEditText;
import com.services.AcademicDataService;
import com.services.CurriculumService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AcademicDataRegister extends AppCompatActivity {


    LinearLayout containerLayout;

    LayoutInflater inflater;
    List<View> formViews = new ArrayList<>();

    private EditText instituitionNameInput, startDateInput, endDateInput, cityInput;

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





        containerLayout = findViewById(R.id.forms_container); // <- você vai criar esse id
        inflater = LayoutInflater.from(this);




        instituitionNameInput = findViewById(R.id.institution_name_input);

        startDateInput = findViewById(R.id.start_date_input);
        endDateInput = findViewById(R.id.end_date_input);
        cityInput = findViewById(R.id.city_input);

        startDateInput.addTextChangedListener(MaskEditText.mask(startDateInput, "##/####"));
        endDateInput.addTextChangedListener(MaskEditText.mask(endDateInput, "##/####"));





        Spinner levelSpinner = findViewById(R.id.level_spinner);
        ArrayAdapter<CharSequence> levelAdapter = ArrayAdapter.createFromResource(this,
                R.array.level_options, R.layout.spinner_item);
        levelAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        levelSpinner.setAdapter(levelAdapter);



    }



    public void onBackPressed(View view){
        finish();
    }
    public void registerAcademicData(View view){
        List<AcademicData> academicDataList = new ArrayList<>();

        String institutionName = instituitionNameInput.getText().toString();
        String level = ((Spinner) findViewById(R.id.level_spinner)).getSelectedItem().toString();
        String startDate = startDateInput.getText().toString();
        String endDate = endDateInput.getText().toString();
        String city = cityInput.getText().toString();

        if(institutionName.isEmpty()){
            instituitionNameInput.setError("Preencha o nome da intituição de ensino");
            instituitionNameInput.requestFocus();
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

        if(city.isEmpty()){
            cityInput.setError("Preencha a cidade");
            cityInput.requestFocus();
            return;
        }

        Curriculum schoolData = new Curriculum(
                institutionName, level, city, startDate, endDate, false
        );
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        int curriculumId = sharedPreferences.getInt("candidateId", -1);


        CurriculumService.addSchoolData(AcademicDataRegister.this, schoolData, new CurriculumService.CurriculumCallback() {
            @Override
            public void onSuccess() {
                // Mostra mensagem
                Toast.makeText(AcademicDataRegister.this, "Dados principais enviados com sucesso!", Toast.LENGTH_SHORT).show();

                // Agora percorre os formulários dinâmicos e envia
                for (View form : formViews) {
                    EditText institutionInput = form.findViewById(R.id.institution_name_input);
                    Spinner levelSpinner = form.findViewById(R.id.level_spinner);
                    Spinner periodSpinner = form.findViewById(R.id.period_spinner);
                    EditText startDateInput = form.findViewById(R.id.start_date_input);
                    EditText endDateInput = form.findViewById(R.id.end_date_input);
                    EditText courseInput = form.findViewById(R.id.course_name_input);
                    EditText cityInput = form.findViewById(R.id.city_input);

                    String inst = institutionInput.getText().toString().trim();
                    String lvl = levelSpinner.getSelectedItem().toString();
                    String per = periodSpinner.getSelectedItem().toString();
                    String start = startDateInput.getText().toString();
                    String end = endDateInput.getText().toString();
                    String course = courseInput.getText().toString().trim();
                    String cityDyn = cityInput.getText().toString().trim();

                    if (inst.isEmpty() || lvl.isEmpty() || per.isEmpty() ||
                            start.isEmpty() || end.isEmpty() || course.isEmpty() || cityDyn.isEmpty()) {
                        Toast.makeText(AcademicDataRegister.this, "Preencha todos os campos dos formulários adicionados.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String formattedStartDate = startDateInput.getText().toString().replace("/", "-"); // Exemplo: "03/2023" para "2023-03-01"
                    String formattedEndDate = endDateInput.getText().toString().replace("/", "-");


                    AcademicData academicData = new AcademicData(
                            course,
                            per,
                            formattedStartDate,
                            formattedEndDate,
                            false,
                            inst,
                            lvl,
                            cityDyn,
                            curriculumId

                    );


                    AcademicDataService.registerAcademicData(AcademicDataRegister.this, academicData, new AcademicDataService.AcademicDataCallback() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(AcademicDataRegister.this, "Dados da instituição "+ inst + " Enviados", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(AcademicDataRegister.this, CoursesRegister.class);
                            startActivity(intent);
                        }

                        @Override
                        public void onFailure(String errorMessage) {

                        }
                    });



                }

                // Redireciona depois de todos os envios (pode usar delay ou lógica extra se quiser garantir que todos completaram)

            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(AcademicDataRegister.this, "Erro ao enviar dados principais: " + errorMessage, Toast.LENGTH_LONG).show();
            }
        });

    }






    public void addAcademicDataForm(View view){
        View form = inflater.inflate(R.layout.item_academic_data_form, containerLayout, false);
        Spinner periodSpinner = form.findViewById(R.id.period_spinner);
        ArrayAdapter<CharSequence> periodAdapter = ArrayAdapter.createFromResource(this,
                R.array.period_options, R.layout.spinner_item);
        periodAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        periodSpinner.setAdapter(periodAdapter);

        Spinner levelSpinner = form.findViewById(R.id.level_spinner);
        ArrayAdapter<CharSequence> levelAdapter = ArrayAdapter.createFromResource(this,
                R.array.level_options, R.layout.spinner_item);
        levelAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        levelSpinner.setAdapter(levelAdapter);

        EditText startDateInput = form.findViewById(R.id.start_date_input);
        EditText endDateInput = form.findViewById(R.id.end_date_input);

        startDateInput.addTextChangedListener(MaskEditText.mask(startDateInput, "##/####"));
        endDateInput.addTextChangedListener(MaskEditText.mask(endDateInput, "##/####"));


        // Referência ao botão de remover
        ImageButton removeButton = form.findViewById(R.id.remove_form_button);

        removeButton.setOnClickListener(v -> {
            containerLayout.removeView(form);
            formViews.remove(form);
        });

        formViews.add(form);
        containerLayout.addView(form);

    }


    public void atalho (View view){
        Intent intent = new Intent(AcademicDataRegister.this, CoursesRegister.class);
        startActivity(intent);
    }

}