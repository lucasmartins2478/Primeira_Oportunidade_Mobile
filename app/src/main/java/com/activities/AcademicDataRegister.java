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
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.fragments.LoadingDialogFragment;
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

    Spinner levelSpinner;
    LoadingDialogFragment loadingDialog;

    private EditText instituitionNameInput, startDateInput, endDateInput, cityInput;

    private String token;

    CheckBox isCurrentlyStudying;

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


        loadingDialog = new LoadingDialogFragment();


        containerLayout = findViewById(R.id.forms_container); // <- você vai criar esse id
        inflater = LayoutInflater.from(this);




        instituitionNameInput = findViewById(R.id.institution_name_input);

        startDateInput = findViewById(R.id.start_date_input);
        endDateInput = findViewById(R.id.end_date_input);
        cityInput = findViewById(R.id.city_input);
        isCurrentlyStudying = findViewById(R.id.isCurrentlyStudying);


        startDateInput.addTextChangedListener(MaskEditText.mask(startDateInput, "##/####"));
        endDateInput.addTextChangedListener(MaskEditText.mask(endDateInput, "##/####"));





        levelSpinner = findViewById(R.id.level_spinner);
        ArrayAdapter<CharSequence> levelAdapter = ArrayAdapter.createFromResource(this,
                R.array.level_options, R.layout.spinner_item);
        levelAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        levelSpinner.setAdapter(levelAdapter);



        int curriculumId = getSharedPreferences("UserPrefs", MODE_PRIVATE).getInt("curriculumId", -1);
        if(curriculumId != -1){
            loadingDialog.show(getSupportFragmentManager(), "loading");


            loadCurriculumData(curriculumId);


            token = getSharedPreferences("UserPrefs", MODE_PRIVATE).getString("token", "Nanhum token encontrado");

            AcademicDataService.getAcademicDataByCurriculumId(curriculumId, token, new AcademicDataService.FetchAcademicDataCallback() {
                @Override
                public void onSuccess(List<AcademicData> dataList) {

                    runOnUiThread(() -> {
                        loadingDialog.dismiss();
                        populateAcademicDataForms(dataList);
                    });
                }

                @Override
                public void onFailure(String errorMessage) {
                    runOnUiThread(() -> {
                        if (loadingDialog != null && loadingDialog.isAdded()) {
                            loadingDialog.dismissAllowingStateLoss();
                        }
                    });
                }
            });
        }




    }



    public void onBackPressed(View view){
        finish();
    }
    public void registerAcademicData(View view){

        loadingDialog.show(getSupportFragmentManager(), "loading");

        List<AcademicData> academicDataList = new ArrayList<>();

        String institutionName = instituitionNameInput.getText().toString();
        String level = ((Spinner) findViewById(R.id.level_spinner)).getSelectedItem().toString();
        String startDate = startDateInput.getText().toString();
        String endDate = endDateInput.getText().toString();
        String city = cityInput.getText().toString();
        Boolean isChecked = isCurrentlyStudying.isChecked();

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
                institutionName, level, city, startDate, endDate, isChecked
        );
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        token = sharedPreferences.getString("token", "nanhum token encontrado");
        int curriculumId = sharedPreferences.getInt("curriculumId", -1);


        CurriculumService.addSchoolData(AcademicDataRegister.this, schoolData,token,  new CurriculumService.CurriculumCallback() {
            @Override
            public void onSuccess() {



                if (formViews.isEmpty()) {
                    // Se não houver formulários dinâmicos, redireciona para a próxima tela
                    Intent intent = new Intent(AcademicDataRegister.this, CoursesRegister.class);
                    startActivity(intent);
                    return; // Retorna para evitar o envio de dados de academicData
                }

                // Agora percorre os formulários dinâmicos e envia
                for (View form : formViews) {
                    EditText institutionInput = form.findViewById(R.id.institution_name_input);
                    Spinner levelSpinner = form.findViewById(R.id.level_spinner);
                    Spinner periodSpinner = form.findViewById(R.id.period_spinner);
                    EditText startDateInput = form.findViewById(R.id.start_date_input);
                    EditText endDateInput = form.findViewById(R.id.end_date_input);
                    EditText courseInput = form.findViewById(R.id.course_name_input);
                    EditText cityInput = form.findViewById(R.id.city_input);
                    CheckBox isStudyingYet = form.findViewById(R.id.isCurrentlyStudying);

                    String inst = institutionInput.getText().toString().trim();
                    String lvl = levelSpinner.getSelectedItem().toString();
                    String per = periodSpinner.getSelectedItem().toString();
                    String start = startDateInput.getText().toString();
                    String end = endDateInput.getText().toString();
                    String course = courseInput.getText().toString().trim();
                    String cityDyn = cityInput.getText().toString().trim();
                    Boolean isStudying = isStudyingYet.isChecked();

                    if (inst.isEmpty() || lvl.isEmpty() || per.isEmpty() ||
                            start.isEmpty() || course.isEmpty() || cityDyn.isEmpty()) {
                        Toast.makeText(AcademicDataRegister.this, "Preencha todos os campos dos formulários adicionados.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String formattedStartDate = startDateInput.getText().toString().replace("/", "-"); // Exemplo: "03/2023" para "2023-03-01"
                    String formattedEndDate = endDateInput.getText().toString().replace("/", "-");


                    AcademicData academicData = (AcademicData) form.getTag();
                    if (academicData == null) {
                        academicData = new AcademicData(); // novo
                        Log.d("AcademicDataDebug", "Novo academicData criado. ID: " + academicData.getId());
                    } else {
                        Log.d("AcademicDataDebug", "Editando academicData existente. ID: " + academicData.getId());
                    }

                    academicData.setCourseName(course);
                    academicData.setPeriod(per);
                    academicData.setStartDate(formattedStartDate);
                    academicData.setEndDate(formattedEndDate);
                    academicData.setIsCurrentlyStudying(isStudying);
                    academicData.setInstitutionName(inst);
                    academicData.setLevel(lvl);
                    academicData.setCity(cityDyn);
                    academicData.setCurriculumId(curriculumId);


                    Log.d("AcademicDataDebug", "Dados finais de academicData: " +
                            "ID=" + academicData.getId() + ", Curso=" + course + ", Instituição=" + inst);

                    token = sharedPreferences.getString("token", "Nenhum token encontrado");


                    if (academicData.getId() != 0) { // Editando
                        AcademicDataService.updateAcademicData(AcademicDataRegister.this, academicData,token, new AcademicDataService.AcademicDataCallback() {
                            @Override
                            public void onSuccess() {
                                if (loadingDialog != null && loadingDialog.isAdded()) {
                                    loadingDialog.dismissAllowingStateLoss();
                                }
                                Toast.makeText(AcademicDataRegister.this, "Dados da instituição " + inst + " atualizados com sucesso", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(AcademicDataRegister.this, Profile.class);
                                startActivity(intent);
                                finish();
                            }

                            @Override
                            public void onFailure(String errorMessage) {
                                if (loadingDialog != null && loadingDialog.isAdded()) {
                                    loadingDialog.dismissAllowingStateLoss();
                                }
                                Toast.makeText(AcademicDataRegister.this, "Erro ao atualizar dados: " + errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else { // Cadastrando novo
                        AcademicDataService.registerAcademicData(AcademicDataRegister.this, academicData,token, new AcademicDataService.AcademicDataCallback() {
                            @Override
                            public void onSuccess() {
                                if (loadingDialog != null && loadingDialog.isAdded()) {
                                    loadingDialog.dismissAllowingStateLoss();
                                }
                                Toast.makeText(AcademicDataRegister.this, "Dados da instituição " + inst + " enviados", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(AcademicDataRegister.this, CoursesRegister.class);
                                startActivity(intent);
                                finish();
                            }

                            @Override
                            public void onFailure(String errorMessage) {
                                if (loadingDialog != null && loadingDialog.isAdded()) {
                                    loadingDialog.dismissAllowingStateLoss();
                                }
                                Toast.makeText(AcademicDataRegister.this, "Erro ao registrar dados: " + errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }



                }

                // Redireciona depois de todos os envios (pode usar delay ou lógica extra se quiser garantir que todos completaram)

            }

            @Override
            public void onFailure(String errorMessage) {
                if (loadingDialog != null && loadingDialog.isAdded()) {
                    loadingDialog.dismissAllowingStateLoss();
                }
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

    private void populateAcademicDataForms(List<AcademicData> academicDataList) {
        for (AcademicData data : academicDataList) {
            View form = inflater.inflate(R.layout.item_academic_data_form, containerLayout, false);


            SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

            EditText courseInput = form.findViewById(R.id.course_name_input);
            EditText startDateInput = form.findViewById(R.id.start_date_input);
            EditText endDateInput = form.findViewById(R.id.end_date_input);
            EditText cityInput = form.findViewById(R.id.city_input);
            EditText institutionInput = form.findViewById(R.id.institution_name_input);
            CheckBox isStudyingYet = form.findViewById(R.id.isCurrentlyStudying);

            Spinner levelSpinner = form.findViewById(R.id.level_spinner);
            Spinner periodSpinner = form.findViewById(R.id.period_spinner);

            // Adapters dos spinners
            ArrayAdapter<CharSequence> levelAdapter = ArrayAdapter.createFromResource(this,
                    R.array.level_options, R.layout.spinner_item);
            levelAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
            levelSpinner.setAdapter(levelAdapter);

            ArrayAdapter<CharSequence> periodAdapter = ArrayAdapter.createFromResource(this,
                    R.array.period_options, R.layout.spinner_item);
            periodAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
            periodSpinner.setAdapter(periodAdapter);

            // Preenche os campos
            courseInput.setText(data.getCourseName());
            cityInput.setText(data.getCity());
            institutionInput.setText(data.getInstitutionName());
            startDateInput.setText(data.getStartDate().replace("-", "/"));
            endDateInput.setText(data.getEndDate().replace("-", "/"));
            isStudyingYet.setChecked(data.getIsCurrentlyStudying());

            // Seleciona valores nos spinners
            if (data.getLevel() != null) {
                int levelPosition = levelAdapter.getPosition(data.getLevel());
                levelSpinner.setSelection(levelPosition);
            }
            if (data.getPeriod() != null) {
                int periodPosition = periodAdapter.getPosition(data.getPeriod());
                periodSpinner.setSelection(periodPosition);
            }

            // Máscara para datas
            startDateInput.addTextChangedListener(MaskEditText.mask(startDateInput, "##/####"));
            endDateInput.addTextChangedListener(MaskEditText.mask(endDateInput, "##/####"));



            token = sharedPreferences.getString("token", "Nanhum token encontrado");

            // Botão de remover
            ImageButton removeButton = form.findViewById(R.id.remove_form_button);
            removeButton.setOnClickListener(v -> {
                Log.d("Delete", "Clique no botão de remover registrado");

                if (data != null && data.getId() > 0) {
                    // Exclusão no backend
                    AcademicDataService.deleteAcademicData(AcademicDataRegister.this, data.getId(),token, new AcademicDataService.AcademicDataCallback() {
                        @Override
                        public void onSuccess() {
                            runOnUiThread(() -> {
                                Toast.makeText(AcademicDataRegister.this, "Dados removidos com sucesso", Toast.LENGTH_SHORT).show();
                                containerLayout.removeView(form);
                                formViews.remove(form);
                            });
                        }

                        @Override
                        public void onFailure(String errorMessage) {
                            runOnUiThread(() -> {
                                Toast.makeText(AcademicDataRegister.this, "Erro ao excluir: " + errorMessage, Toast.LENGTH_SHORT).show();
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

    private void loadCurriculumData(int candidateId) {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        token = sharedPreferences.getString("token", "nanhum token encontrado");
        CurriculumService.getCurriculumByCandidateId(candidateId,token, new CurriculumService.FetchCurriculumCallback() {
            @Override
            public void onSuccess(Curriculum curriculum) {
                runOnUiThread(() -> {

                    instituitionNameInput.setText(safeText(curriculum.getSchoolName()));
                    startDateInput.setText(safeText(curriculum.getSchoolStartDate()).replace("-", "/"));
                    endDateInput.setText(safeText(curriculum.getSchoolEndDate()).replace("-", "/"));
                    cityInput.setText(safeText(curriculum.getSchoolCity()));


                    isCurrentlyStudying.setChecked(Boolean.TRUE.equals(curriculum.isCurrentlyStudying()));

                    if (curriculum.getSchoolYear() != null) {
                        levelSpinner.post(() -> {
                            ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) levelSpinner.getAdapter();
                            for (int i = 0; i < adapter.getCount(); i++) {
                                if (curriculum.getSchoolYear().equalsIgnoreCase(adapter.getItem(i).toString())) {
                                    levelSpinner.setSelection(i);
                                    break;
                                }
                            }
                        });
                    }



                });
            }

            @Override
            public void onFailure(String errorMessage) {
                runOnUiThread(() -> {
                    Toast.makeText(AcademicDataRegister.this, "Erro ao carregar currículo: " + errorMessage, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private String safeText(String value) {
        return (value == null || value.equalsIgnoreCase("null")) ? "" : value;
    }






}