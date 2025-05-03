package com.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.models.AcademicData;
import com.models.CompetenceData;
import com.models.CourseData;
import com.models.DateValidator;
import com.models.MaskEditText;
import com.services.AcademicDataService;
import com.services.CompetenceDataService;
import com.services.CourseDataService;

import java.util.ArrayList;
import java.util.List;

public class CoursesRegister extends AppCompatActivity {

    LinearLayout containerLayout;

    LayoutInflater inflater;
    List<View> formViews = new ArrayList<>();
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

        containerLayout = findViewById(R.id.forms_container); // <- você vai criar esse id
        inflater = LayoutInflater.from(this);




        EditText competenceInput = findViewById(R.id.competenceInput);
        LinearLayout competencesContainer = findViewById(R.id.competences_list_container);

        List<CompetenceData> competenceList = new ArrayList<>();

        competenceInput.setOnEditorActionListener((v, actionId, event) -> {
            String input = competenceInput.getText().toString().trim();

            if (!input.isEmpty()) {
                addCompetenceItem(input, competencesContainer, competenceList);
                competenceInput.setText(""); // limpa o campo
            }
            return true;
        });

        // Adiciona o formulário inicial e salva referência
        addCourseDataForm(null);
        View formInicial = formViews.get(0); // <- importante: pega o form REAL que foi adicionado


        int curriculumId = getSharedPreferences("UserPrefs", MODE_PRIVATE).getInt("curriculumId", -1);
        if(curriculumId != -1){

            CompetenceDataService.getCompetencesByCurriculumId(curriculumId, new CompetenceDataService.FetchCompetencesCallback() {

                @Override
                public void onSuccess(List<CompetenceData> competences) {
                    runOnUiThread(() -> populateCompetenceDataForms(competences));

                }

                @Override
                public void onFailure(String errorMessage) {
                    runOnUiThread(() -> Toast.makeText(CoursesRegister.this, "Erro ao carregar competências: " + errorMessage, Toast.LENGTH_SHORT).show());
                }
            });



            CourseDataService.getCourseDataByCurriculumId(curriculumId, new CourseDataService.FetchCourseDataCallback() {
                public void onSuccess(List<CourseData> dataList) {
                    runOnUiThread(() -> {
                        removeCourse(formInicial); // <- Agora sim: remove o formulário que está na tela
                        verificarECriarFormularioCurso(dataList);
                    });
                }





                @Override
                public void onFailure(String errorMessage) {
                    runOnUiThread(() -> Toast.makeText(CoursesRegister.this, "Erro: " + errorMessage, Toast.LENGTH_SHORT).show());
                }
            });
        }



    }

    public void onBackPressed(View view){
        finish();
    }
    public void additionalData(View view){



        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        int curriculumId = sharedPreferences.getInt("curriculumId", -1);
        if (formViews.isEmpty()) {
            // Se não houver formulários dinâmicos, redireciona para a próxima tela
            Intent intent = new Intent(CoursesRegister.this, AdditionalDataRegister.class);
            startActivity(intent);
            return; // Retorna para evitar o envio de dados de academicData
        }

        for (View form : formViews) {
            EditText courseName = form.findViewById(R.id.course_name_input);
            Spinner modalitySpinner = form.findViewById(R.id.course_modality_spinner);
            EditText durationInput = form.findViewById(R.id.duration_input);
            EditText endDateInput = form.findViewById(R.id.end_date_input);
            EditText institutionName = form.findViewById(R.id.granting_institution_input);
            CheckBox isStudyingYet = form.findViewById(R.id.isCurrentlyStudying);


            String inst = institutionName.getText().toString().trim();
            String modality = modalitySpinner.getSelectedItem().toString();
            String duration = durationInput.getText().toString();
            String end = endDateInput.getText().toString();
            String course = courseName.getText().toString().trim();
            Boolean inProgress = isStudyingYet.isChecked();


            if (inst.isEmpty() || modality.isEmpty() || duration.isEmpty() || end.isEmpty() || course.isEmpty() ) {
                Toast.makeText(CoursesRegister.this, "Preencha todos os campos dos formulários adicionados.", Toast.LENGTH_SHORT).show();
                return;
            }

            String formattedEndDate = endDateInput.getText().toString().replace("/", "-");




            CourseData courseData = (CourseData) form.getTag();

            if(courseData == null){
                courseData = new CourseData();
            }

            courseData.setCourseName(course);
            courseData.setGrantingIntitution(inst);
            courseData.setModality(modality);
            courseData.setDuration(duration);
            courseData.setEndDate(end);
            courseData.setInProgress(inProgress);
            courseData.setCurriculumId(curriculumId); // <-- ESSENCIAL


            if(courseData.getId() != 0){
                CourseDataService.updateCourseData(CoursesRegister.this, courseData, new CourseDataService.CourseDataCallback() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(CoursesRegister.this, "Dados da instituição " + inst + " atualizados com sucesso", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(CoursesRegister.this, Profile.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Toast.makeText(CoursesRegister.this, "Erro ao atualizar dados", Toast.LENGTH_SHORT).show();
                    }
                });

            }else{
                CourseDataService.registerCourseData(CoursesRegister.this, courseData, new CourseDataService.CourseDataCallback() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(CoursesRegister.this, "Dados da instituição "+ inst + " Enviados", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(CoursesRegister.this, AdditionalDataRegister.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onFailure(String errorMessage) {

                    }
                });
            }

        }

    }





    public void addCourseDataForm(View view) {
        View form = inflater.inflate(R.layout.item_course_data_form, containerLayout, false);

        EditText endDateInput = form.findViewById(R.id.end_date_input);

        Spinner modalitySpinner = form.findViewById(R.id.course_modality_spinner);
        ArrayAdapter<CharSequence> modalityAdapter = ArrayAdapter.createFromResource(this,
                R.array.modality_options, R.layout.spinner_item);
        modalityAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        modalitySpinner.setAdapter(modalityAdapter);

        endDateInput.addTextChangedListener(MaskEditText.mask(endDateInput, "##/####"));

        ImageButton removeButton = form.findViewById(R.id.remove_form_button);
        removeButton.setOnClickListener(v -> {
            containerLayout.removeView(form);
            formViews.remove(form);
        });

        formViews.add(form);
        containerLayout.addView(form);
    }

    private void addCompetenceItem(String competenceText, LinearLayout container, List<CompetenceData> list) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View itemView = inflater.inflate(R.layout.competence_item, container, false);

        TextView textView = itemView.findViewById(R.id.competence_text);
        ImageButton deleteButton = itemView.findViewById(R.id.delete_competence_button);

        textView.setText(competenceText);

        // Adiciona à lista de dados
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int curriculumId = sharedPreferences.getInt("candidateId", -1);
        CompetenceData competenceData = new CompetenceData(competenceText, curriculumId);
        list.add(competenceData);

        // Listener para deletar

        deleteButton.setOnClickListener(v -> {
            Log.d("Delete", "Clique no botão de remover registrado");

            if (competenceData != null && competenceData.getId() > 0) {
                // Exclusão no backend
                CourseDataService.deleteCourseData(CoursesRegister.this, competenceData.getId(), new CourseDataService.CourseDataCallback() {
                    @Override
                    public void onSuccess() {
                        runOnUiThread(() -> {
                            Toast.makeText(CoursesRegister.this, "Dados removidos com sucesso", Toast.LENGTH_SHORT).show();
                            container.removeView(itemView); // <- correto!
                        });
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        runOnUiThread(() -> {
                            Toast.makeText(CoursesRegister.this, "Erro ao excluir: " + errorMessage, Toast.LENGTH_SHORT).show();
                        });
                    }
                });
            } else {
                Log.d("Delete", "ID não válido ou null, removendo apenas da interface.");
                container.removeView(itemView); // <- correto!
            }
        });

        container.addView(itemView);


        CompetenceDataService.registerCompetenceData(this, competenceData, new CompetenceDataService.CompetenceDataCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(getApplicationContext(), "Competência enviada!", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(getApplicationContext(), "Erro: " + errorMessage, Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void populateCompetenceDataForms(List<CompetenceData> competenceDataList) {
        LinearLayout competencesContainer = findViewById(R.id.competences_list_container);

        for (CompetenceData data : competenceDataList) {
            View itemView = inflater.inflate(R.layout.competence_item, competencesContainer, false);

            TextView textView = itemView.findViewById(R.id.competence_text);
            ImageButton deleteButton = itemView.findViewById(R.id.delete_competence_button);

            textView.setText(data.getCompetence());

            // Listener para deletar
            deleteButton.setOnClickListener(v -> {
                CompetenceDataService.deleteCompetenceData(this, data.getId(), new CompetenceDataService.CompetenceDataCallback() {
                    @Override
                    public void onSuccess() {
                        runOnUiThread(() -> {
                            competencesContainer.removeView(itemView);
                            Toast.makeText(getApplicationContext(), "Competência removida!", Toast.LENGTH_SHORT).show();
                        });
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        runOnUiThread(() -> {
                            Toast.makeText(getApplicationContext(), "Erro ao remover: " + errorMessage, Toast.LENGTH_SHORT).show();
                        });
                    }
                });
            });

            competencesContainer.addView(itemView);
        }
    }



    private void populateCourseDataForms(List<CourseData> courseDataList){
        for (CourseData data : courseDataList){
            View form = inflater.inflate(R.layout.item_course_data_form, containerLayout, false);

            EditText courseName = form.findViewById(R.id.course_name_input);
            EditText duration = form.findViewById(R.id.duration_input);
            EditText endDate = form.findViewById(R.id.end_date_input);
            EditText institutionName = form.findViewById(R.id.granting_institution_input);
            CheckBox isStudyingYet = form.findViewById(R.id.isCurrentlyStudying);

            Spinner modalitySpinner = form.findViewById(R.id.course_modality_spinner);

            ArrayAdapter<CharSequence> modalityAdapter = ArrayAdapter.createFromResource(this,
                    R.array.modality_options, R.layout.spinner_item);
            modalityAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
            modalitySpinner.setAdapter(modalityAdapter);


            courseName.setText(data.getCourseName());
            duration.setText(data.getDuration());
            institutionName.setText(data.getGrantingIntitution());
            endDate.setText(data.getEndDate().replace("-", "/"));
            isStudyingYet.setChecked(data.isInProgress());
            if (data.getModality() != null) {
                int levelPosition = modalityAdapter.getPosition(data.getModality());
                modalitySpinner.setSelection(levelPosition);
            }


            ImageButton removeButton = form.findViewById(R.id.remove_form_button);
            removeButton.setOnClickListener(v -> {
                Log.d("Delete", "Clique no botão de remover registrado");

                if (data != null && data.getId() > 0) {
                    // Exclusão no backend
                    CourseDataService.deleteCourseData(CoursesRegister.this, data.getId(), new CourseDataService.CourseDataCallback() {
                        @Override
                        public void onSuccess() {
                            runOnUiThread(() -> {
                                Toast.makeText(CoursesRegister.this, "Dados removidos com sucesso", Toast.LENGTH_SHORT).show();
                                containerLayout.removeView(form);
                                formViews.remove(form);
                            });
                        }

                        @Override
                        public void onFailure(String errorMessage) {
                            runOnUiThread(() -> {
                                Toast.makeText(CoursesRegister.this, "Erro ao excluir: " + errorMessage, Toast.LENGTH_SHORT).show();
                            });
                        }
                    });
                } else {
                    Log.d("Delete", "ID não válido ou null, removendo apenas da interface.");
                    containerLayout.removeView(form);
                    formViews.remove(form);
                }
            });

            form.setTag(data); // Salva o AcademicData na View

            formViews.add(form);
            containerLayout.addView(form);

        }
    }

    private void verificarECriarFormularioCurso(List<CourseData> dataList) {
        if (dataList == null || dataList.isEmpty()) {
            addCourseDataForm(null); // cadastro
        } else {
            populateCourseDataForms(dataList); // edição
        }
    }

    private void removeCourse(View form){
        containerLayout.removeView(form);
        formViews.remove(form);
    }








}