package com.activities;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.fragments.ConfirmationDialogFragment;
import com.fragments.LoadingDialogFragment;
import com.models.AcademicData;
import com.models.Company;
import com.models.CompetenceData;
import com.models.CourseData;
import com.models.Curriculum;
import com.models.DateUtils;
import com.services.AcademicDataService;
import com.services.CandidateService;
import com.services.CompanyService;
import com.services.CompetenceDataService;
import com.services.CourseDataService;
import com.services.CurriculumService;
import com.services.LoginService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class Profile extends AppCompatActivity {

    private TextView txtName, txtAge, academicContainerTitle, courseContainerTitle, competenceContainerTitle, companyDataTitle, txtCompanyName, txtCnpj, txtSegment, txtEmail, txtPhone, txtResponsible, txtUrl, txtCompanyAddressTitle, txtCompanyCity, txtCep, txtUf, txtAddress, txtAddressNumber;
    TextView   textCity;
    CompanyService companyService;
    String email;
    FrameLayout imageContainer;
    int  candidateId, companyId, curriculumId, userId;
    AppCompatButton btnAddCurriculum, btnAnalysis;
    LinearLayout academicContainer;
    LinearLayout coursesContainer, competencesContainer;

    LoginService loginService;

    LoadingDialogFragment loadingDialog;
    ImageView profileImageView;

    CandidateService candidateService;

    private static final int PICK_FILE_REQUEST_CODE = 101;
    private Uri selectedFileUri = null;
    private String selectedFileName = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        loadingDialog = new LoadingDialogFragment();

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        candidateId = sharedPreferences.getInt("candidateId", -1);
        companyId = sharedPreferences.getInt("companyId", -1);
        userId = sharedPreferences.getInt("userId", -1);
        curriculumId = sharedPreferences.getInt("curriculumId", -1);
        email = sharedPreferences.getString("email", "Email não encontrado");


        ImageView changeImage = findViewById(R.id.change_image);
        changeImage.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*"); // só imagens
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(Intent.createChooser(intent, "Selecione uma imagem"), PICK_FILE_REQUEST_CODE);
        });

        profileImageView = findViewById(R.id.profile_img);

        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String logoPath = prefs.getString("companyLogoPath", null);

        if (logoPath != null) {
            File logoFile = new File(logoPath);
            if (logoFile.exists()) {
                Glide.with(this)
                        .load(logoFile)
                        .apply(RequestOptions.circleCropTransform())
                        .into(profileImageView);
            } else {
                Log.d("Profile", "Arquivo de imagem não encontrado: " + logoPath);
            }
        } else {
            Glide.with(this)
                    .load(R.drawable.user_img) // imagem padrão
                    .apply(RequestOptions.circleCropTransform())
                    .into(profileImageView);
        }





        txtName = findViewById(R.id.txt_name);
        txtAge = findViewById(R.id.txt_age);
        textCity = findViewById(R.id.txt_city);

        companyService = new CompanyService();
        loginService = new LoginService();
        candidateService = new CandidateService();

        btnAnalysis = findViewById(R.id.analize_curriculum);
        academicContainer = findViewById(R.id.academicContainer);
        academicContainerTitle = findViewById(R.id.textAcademicTitle);
        competencesContainer = findViewById(R.id.competencesContainer);
        courseContainerTitle = findViewById(R.id.textCoursesLabel);
        competenceContainerTitle = findViewById(R.id.textCompetencesLabel);


        imageContainer = findViewById(R.id.image_container);
        companyDataTitle = findViewById(R.id.textCompanyDataTitle);
        txtCompanyName = findViewById(R.id.txt_company_name);
        txtCnpj = findViewById(R.id.txt_cnpj);
        txtSegment = findViewById(R.id.txt_segment);
        txtEmail = findViewById(R.id.txt_email);
        txtPhone = findViewById(R.id.txt_phone);
        txtResponsible = findViewById(R.id.txt_responsible);
        txtUrl = findViewById(R.id.txt_url);
        txtCompanyAddressTitle = findViewById(R.id.textCompanyAddressTitle);
        txtCompanyCity = findViewById(R.id.txt_company_city);
        txtCep = findViewById(R.id.txt_cep);
        txtUf = findViewById(R.id.txt_uf);
        txtAddress = findViewById(R.id.txt_address);
        txtAddressNumber = findViewById(R.id.txt_address_number);


        btnAddCurriculum = findViewById(R.id.add_curriculum);

        coursesContainer = findViewById(R.id.coursesContainer);


        if( candidateId != -1){


            if(curriculumId != -1){
                btnAddCurriculum.setText("Editar currículo");
            }

            fetchCurriculumData(candidateId);





        }else {


            fetchCompanyData(userId);
        }





    }
    public void curriculumData(View view){
        Intent intent = new Intent(Profile.this, CurriculumRegister.class);
        startActivity(intent);
    }


    private void fetchCurriculumData(int candidateId) {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String token = prefs.getString("token", "Nenhum token encontrado");
        loadingDialog.show(getSupportFragmentManager(), "loading");
        CurriculumService.getCurriculumByCandidateId(candidateId, token,new CurriculumService.FetchCurriculumCallback() {
            @Override
            public void onSuccess(Curriculum curriculum) {
                runOnUiThread(() -> {
                    txtName.setVisibility(View.VISIBLE);
                    txtAge.setVisibility(View.VISIBLE);
                    textCity.setVisibility(View.VISIBLE);
                    txtAge.setText(curriculum.getAge()+" anos"+", "+DateUtils.formatDate(curriculum.getBirthDate()));
                    textCity.setText( curriculum.getCity()+" - "+ curriculum.getUf());
                    String name = prefs.getString("name", "Usuário não encontrado");
                    txtName.setText(name);
                    imageContainer.setVisibility(View.VISIBLE);
                    competenceContainerTitle.setVisibility(View.VISIBLE);
                    academicContainerTitle.setVisibility(View.VISIBLE);
                    courseContainerTitle.setVisibility(View.VISIBLE);
                    btnAddCurriculum.setVisibility(View.VISIBLE);
                    btnAnalysis.setVisibility(View.VISIBLE);
                    fetchAcademicData(candidateId, token);
                    fetchCourseData(candidateId, token);
                    fetchCompetences(candidateId, token);

                    loadingDialog.dismiss();

                });
            }

            @Override
            public void onFailure(String errorMessage) {
                runOnUiThread(() -> {
                    // Mesmo sem currículo, mostra nome e imagem padrão
                    String name = prefs.getString("name", "Usuário não encontrado");
                    txtName.setText(name);
                    txtName.setVisibility(View.VISIBLE);
                    textCity.setVisibility(View.GONE); // Não tem cidade ainda
                    txtAge.setVisibility(View.GONE);   // Nem idade

                    imageContainer.setVisibility(View.VISIBLE);

                    Glide.with(Profile.this)
                            .load(R.drawable.user_img)
                            .apply(RequestOptions.circleCropTransform())
                            .into(profileImageView);

                    btnAddCurriculum.setVisibility(View.VISIBLE); // ainda pode cadastrar

                    loadingDialog.dismiss();
                });
            }

        });
    }

    private void fetchAcademicData(int curriculumId, String token) {


        AcademicDataService.getAcademicDataByCurriculumId(curriculumId, token, new AcademicDataService.FetchAcademicDataCallback() {
            @Override
            public void onSuccess(List<AcademicData> dataList) {
                runOnUiThread(() -> {
                    academicContainerTitle.setVisibility(View.VISIBLE);
                    academicContainer.setVisibility(View.VISIBLE);
                    academicContainer.removeAllViews(); // limpa antes de adicionar

                    for (AcademicData data : dataList) {
                        TextView info = new TextView(Profile.this);
                        info.setText(
                                data.getCourseName() + "\n" +
                                        data.getInstitutionName() +" - " +data.getCity()+ "\n" +
                                        DateUtils.formatMonthYear(data.getStartDate())
                                        + " até " +
                                        (data.getIsCurrentlyStudying() ? "Atual" : DateUtils.formatMonthYear(data.getEndDate())
                                        )
                        );
                        info.setPadding(0, 8, 0, 8);
                        info.setTextColor(ContextCompat.getColor(Profile.this, R.color.black));

                        academicContainer.addView(info);
                    }
                });

            }

            @Override
            public void onFailure(String errorMessage) {
            }
        });
    }


    private void fetchCourseData(int curriculumId, String token) {
        CourseDataService.getCourseDataByCurriculumId(curriculumId,token, new CourseDataService.FetchCourseDataCallback() {
            @Override
            public void onSuccess(List<CourseData> courseDataList) {
                runOnUiThread(() -> {
                    courseContainerTitle.setVisibility(View.VISIBLE);
                    coursesContainer.setVisibility(View.VISIBLE);
                    // Exibindo os cursos
                    if (courseDataList.isEmpty()) {
                        TextView noCoursesMessage = new TextView(Profile.this);
                        noCoursesMessage.setText("Nenhum curso encontrado.");
                        coursesContainer.addView(noCoursesMessage);
                    } else {
                        for (CourseData courseData : courseDataList) {
                            TextView courseTextView = new TextView(Profile.this);
                            courseTextView.setText(courseData.getCourseName() +
                                    "\n"+ courseData.getGrantingIntitution() +
                                    "\n" + courseData.getDuration()+" horas");
                            courseTextView.setTextColor(ContextCompat.getColor(Profile.this, R.color.black));

                            coursesContainer.addView(courseTextView);
                        }
                    }
                });
            }

            @Override
            public void onFailure(String errorMessage) {

            }
        });
    }

    private void fetchCompetences(int curriculumId, String token) {


        CompetenceDataService.getCompetencesByCurriculumId(curriculumId,token, new CompetenceDataService.FetchCompetencesCallback() {
            @Override
            public void onSuccess(List<CompetenceData> competences) {
                runOnUiThread(() -> {
                    competencesContainer.setVisibility(View.VISIBLE);
                    competenceContainerTitle.setVisibility(View.VISIBLE);
                    // Se houver competências
                    if (competences.isEmpty()) {
                        TextView noCompetencesMessage = new TextView(Profile.this);
                        noCompetencesMessage.setText("Nenhuma competência informada.");
                        competencesContainer.addView(noCompetencesMessage);
                    } else {
                        // Adiciona cada competência no layout
                        for (CompetenceData competence : competences) {
                            TextView competenceTextView = new TextView(Profile.this);
                            competenceTextView.setText("• " + competence.getCompetence());
                            competenceTextView.setTextColor(ContextCompat.getColor(Profile.this, R.color.black));
                            competencesContainer.addView(competenceTextView);
                        }
                    }
                });
            }

            @Override
            public void onFailure(String errorMessage) {
            }
        });
    }

    private void fetchCompanyData(int companyId){

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        String token = sharedPreferences.getString("token", "Nenhum token encontrado");
        loadingDialog.show(getSupportFragmentManager(), "loading");
        companyService.fetchCompanyFromApi(companyId, token, new CompanyService.CompanyCallback() {
            @Override
            public void onSuccess(Company company) {
                runOnUiThread(() -> {
                    imageContainer.setVisibility(View.VISIBLE);
                    txtName.setVisibility(View.VISIBLE);
                    companyDataTitle.setVisibility(View.VISIBLE);
                    txtCompanyName.setVisibility(View.VISIBLE);
                    txtCnpj.setVisibility(View.VISIBLE);
                    txtSegment.setVisibility(View.VISIBLE);
                    txtEmail.setVisibility(View.VISIBLE);
                    txtPhone.setVisibility(View.VISIBLE);
                    txtResponsible.setVisibility(View.VISIBLE);
                    txtUrl.setVisibility(View.VISIBLE);
                    txtCompanyAddressTitle.setVisibility(View.VISIBLE);
                    txtCompanyCity.setVisibility(View.VISIBLE);
                    txtCep.setVisibility(View.VISIBLE);
                    txtUf.setVisibility(View.VISIBLE);
                    txtAddress.setVisibility(View.VISIBLE);
                    txtAddressNumber.setVisibility(View.VISIBLE);
                    textCity.setText(company.getCity()+" - "+company.getUf());
                    txtCompanyName.setText("Nome: " + company.getCompanyName());
                    txtCnpj.setText("CNPJ: " + company.getCnpj());
                    txtSegment.setText("Segmento: " + company.getSegment());
                    txtEmail.setText("Email: " + email);
                    txtPhone.setText("Telefone: " + company.getPhoneNumber());
                    txtResponsible.setText("Responsável: " + company.getResponsible());
                    txtUrl.setText("Site: " + company.getWebsite());
                    txtCompanyCity.setText("Cidade: " + company.getCity());
                    txtCep.setText("CEP: " + company.getCep());
                    txtUf.setText("Estado: " + company.getUf());
                    txtAddress.setText("Endereço: " + company.getAddress());
                    txtAddressNumber.setText("Número: " + company.getAddressNumber());
                    loadingDialog.dismiss();
                });
            }


            @Override
            public void onFailure(String error) {
                runOnUiThread(()->{
                    Toast.makeText(Profile.this, "Erro ao buscar dados da empresa: "+error, Toast.LENGTH_SHORT).show();
                    loadingDialog.dismiss();
                });
            }
        });
    }

    private void loadUserData() {

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        String name = sharedPreferences.getString("name", "Usuário não encontrado");

        txtName.setText(name);

    }

    public void updateUser(View view){
        if(candidateId != -1){
            Intent intent = new Intent(Profile.this, UserRegister.class);
            startActivity(intent);
        }
        else{
            Intent intent = new Intent(Profile.this, CompanyRegister.class);
            startActivity(intent);
        }

    }
    public void updateAcademicData(View view){
        Intent intent = new Intent(Profile.this, AcademicDataRegister.class);
        startActivity(intent);
    }
    public void updateCoursesData(View view){
        Intent intent = new Intent(Profile.this, CoursesRegister.class);
        startActivity(intent);
    }
    public void updateCompetencesData(View view){
        Intent intent = new Intent(Profile.this, CoursesRegister.class);
        startActivity(intent);
    }

    public void analize(View view){
        Intent intent = new Intent(Profile.this, CurriculumAnalysis.class);
        startActivity(intent);
    }

    public void deleteAllData(View view) {
        ConfirmationDialogFragment.newInstance(
                "Tem certeza que deseja apagar todos os seus dados? Essa ação não poderá ser desfeita.",
                new ConfirmationDialogFragment.ConfirmationListener() {
                    @Override
                    public void onConfirmed() {
                        performDeleteAllData();
                    }

                    @Override
                    public void onCancelled() {
                        // nada
                    }
                }
        ).show(getSupportFragmentManager(), "ConfirmationDialog");
    }

    private void performDeleteAllData() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", "Nenhum token encontrado");

        loadingDialog.show(getSupportFragmentManager(), "loading");

        if (candidateId != -1) {
            candidateService.deleteAllCandidateData(Profile.this, userId, curriculumId, token, new CandidateService.DeleteCallback() {
                @Override
                public void onSuccess() {
                    candidateService.deleteCandidate(Profile.this, userId, token, new CandidateService.DeleteCallback() {
                        @Override
                        public void onSuccess() {
                            loginService.deleteUserData(Profile.this, userId, token, new LoginService.DeleteCallback() {
                                @Override
                                public void onSuccess() {
                                    Toast.makeText(Profile.this, "Dados apagados com sucesso", Toast.LENGTH_SHORT).show();
                                    loadingDialog.dismiss();
                                    logout();
                                }

                                @Override
                                public void onFailure(String error) {
                                    Toast.makeText(Profile.this, "Erro ao apagar dados", Toast.LENGTH_SHORT).show();
                                    loadingDialog.dismiss();
                                }
                            });
                        }

                        @Override
                        public void onFailure(String error) {
                            Toast.makeText(Profile.this, "Erro ao apagar dados", Toast.LENGTH_SHORT).show();
                            loadingDialog.dismiss();
                        }
                    });
                }

                @Override
                public void onFailure(String error) {
                    Toast.makeText(Profile.this, "Erro ao apagar dados", Toast.LENGTH_SHORT).show();
                    loadingDialog.dismiss();
                }
            });
        } else {
            companyService.deleteAllCompanyData(Profile.this, userId, token, new CompanyService.RegisterCallback() {
                @Override
                public void onSuccess() {
                    Toast.makeText(Profile.this, "Dados apagados com sucesso", Toast.LENGTH_SHORT).show();
                    loadingDialog.dismiss();
                    logout();
                }

                @Override
                public void onFailure(String error) {
                    Toast.makeText(Profile.this, "Erro ao apagar dados", Toast.LENGTH_SHORT).show();
                    loadingDialog.dismiss();
                }
            });
        }
    }

    @SuppressLint("Range")
    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                if (cursor != null) cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private String saveImageLocally(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            File file = new File(getFilesDir(), "company_logo.jpg"); // Nome fixo
            OutputStream outputStream = new FileOutputStream(file);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.close();
            inputStream.close();

            return file.getAbsolutePath(); // Esse path será salvo no SharedPreferences
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_FILE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            selectedFileUri = data.getData();

            if (selectedFileUri != null) {
                selectedFileName = getFileName(selectedFileUri);
                String savedPath = saveImageLocally(selectedFileUri);
                if (savedPath != null) {
                    SharedPreferences.Editor editor = getSharedPreferences("UserPrefs", MODE_PRIVATE).edit();
                    editor.putString("userLogoPath", savedPath);
                    editor.apply();
                    Log.d("CompanyRegister", "Logo salva em: " + savedPath);
                    ImageView profileImageView = findViewById(R.id.profile_img);
                    Glide.with(this)
                            .load(selectedFileUri)
                            .apply(RequestOptions.circleCropTransform())
                            .into(profileImageView);

                } else {
                    Log.d("CompanyRegister", "Falha ao salvar imagem localmente.");
                }

                Toast.makeText(this, "Arquivo selecionado: " + selectedFileName, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void logout() {
        // 🔹 Apagar os dados do usuário do SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        // 🔹 Redirecionar para a tela de login
        Intent intent = new Intent(Profile.this, FormLogin.class);
        startActivity(intent);
        finish(); // Fecha a Activity principal para evitar que o usuário volte ao pressionar "Voltar"
    }
}