package com.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.models.AcademicData;
import com.models.Company;
import com.models.CourseData;
import com.models.Curriculum;
import com.models.DateUtils;
import com.services.AcademicDataService;
import com.services.CompanyService;
import com.services.CompetenceDataService;
import com.services.CourseDataService;
import com.services.CurriculumService;

import java.util.List;

public class Profile extends AppCompatActivity {

    private TextView txtName, txtAge, academicContainerTitle, courseContainerTitle, competenceContainerTitle, companyDataTitle, txtCompanyName, txtCnpj, txtSegment, txtEmail, txtPhone, txtResponsible, txtUrl, txtCompanyAddressTitle, txtCompanyCity, txtCep, txtUf, txtAddress, txtAddressNumber;
    TextView   textCity;
    CompanyService companyService;
    String email;
    AppCompatButton btnAddCurriculum;
    LinearLayout academicContainer;
    LinearLayout coursesContainer, competencesContainer;



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

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        int candidateId = sharedPreferences.getInt("candidateId", -1);
        int companyId = sharedPreferences.getInt("userId", -1);
        email = sharedPreferences.getString("email", "Email não encontrado");






        txtName = findViewById(R.id.txt_name);
        txtAge = findViewById(R.id.txt_age);
        textCity = findViewById(R.id.txt_city);

        companyService = new CompanyService();


        academicContainer = findViewById(R.id.academicContainer);
        academicContainerTitle = findViewById(R.id.textAcademicTitle);
        competencesContainer = findViewById(R.id.competencesContainer);
        courseContainerTitle = findViewById(R.id.textCoursesLabel);
        competenceContainerTitle = findViewById(R.id.textCompetencesLabel);


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

        loadUserData();

        if( candidateId != -1){

            txtAge.setVisibility(View.VISIBLE);
            academicContainerTitle.setVisibility(View.VISIBLE);
            academicContainer.setVisibility(View.VISIBLE);
            competencesContainer.setVisibility(View.VISIBLE);
            competenceContainerTitle.setVisibility(View.VISIBLE);
            courseContainerTitle.setVisibility(View.VISIBLE);
            coursesContainer.setVisibility(View.VISIBLE);
            btnAddCurriculum.setVisibility(View.VISIBLE);


            fetchCurriculumData(candidateId);



        }else {

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
            fetchCompanyData(companyId);
        }


    }
    public void curriculumData(View view){
        Intent intent = new Intent(Profile.this, CurriculumRegister.class);
        startActivity(intent);
    }


    private void fetchCurriculumData(int candidateId) {
        CurriculumService.getCurriculumByCandidateId(candidateId, new CurriculumService.FetchCurriculumCallback() {
            @Override
            public void onSuccess(Curriculum curriculum) {
                runOnUiThread(() -> {
                    txtAge.setText(curriculum.getAge()+" anos"+", "+DateUtils.formatDate(curriculum.getBirthDate()));
                    textCity.setText( curriculum.getCity()+" - "+ curriculum.getUf());

                    fetchAcademicData(candidateId);
                    fetchCourseData(candidateId);
                    fetchCompetences(candidateId);

                });
            }

            @Override
            public void onFailure(String errorMessage) {
                runOnUiThread(() ->
                        Toast.makeText(Profile.this, "Erro ao buscar currículo: " + errorMessage, Toast.LENGTH_SHORT).show()
                );
            }
        });
    }

    private void fetchAcademicData(int curriculumId) {
        AcademicDataService.getAcademicDataByCurriculumId(curriculumId, new AcademicDataService.FetchAcademicDataCallback() {
            @Override
            public void onSuccess(List<AcademicData> dataList) {
                runOnUiThread(() -> {
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
                runOnUiThread(() -> Toast.makeText(Profile.this, "Erro ao buscar dados acadêmicos: " + errorMessage, Toast.LENGTH_SHORT).show());
            }
        });
    }


    private void fetchCourseData(int curriculumId) {
        CourseDataService.getCourseDataByCurriculumId(curriculumId, new CourseDataService.FetchCourseDataCallback() {
            @Override
            public void onSuccess(List<CourseData> courseDataList) {
                runOnUiThread(() -> {
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
                runOnUiThread(() ->
                        Toast.makeText(Profile.this, "Erro ao buscar curso complementar: " + errorMessage, Toast.LENGTH_SHORT).show()
                );
            }
        });
    }

    private void fetchCompetences(int curriculumId) {
        CompetenceDataService.getCompetencesByCurriculumId(curriculumId, new CompetenceDataService.FetchCompetencesCallback() {
            @Override
            public void onSuccess(List<String> competences) {
                runOnUiThread(() -> {
                    // Se houver competências
                    if (competences.isEmpty()) {
                        TextView noCompetencesMessage = new TextView(Profile.this);
                        noCompetencesMessage.setText("Nenhuma competência informada.");
                        competencesContainer.addView(noCompetencesMessage);
                    } else {
                        // Adiciona cada competência no layout
                        for (String competence : competences) {
                            TextView competenceTextView = new TextView(Profile.this);
                            competenceTextView.setText("• " + competence);
                            competenceTextView.setTextColor(ContextCompat.getColor(Profile.this, R.color.black));
                            competencesContainer.addView(competenceTextView);
                        }
                    }
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                runOnUiThread(() -> Toast.makeText(Profile.this, "Erro ao buscar competências: " + errorMessage, Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void fetchCompanyData(int companyId){
        companyService.fetchCompanyFromApi(companyId, new CompanyService.CompanyCallback() {
            @Override
            public void onSuccess(Company company) {
                runOnUiThread(() -> {
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
                });
            }


            @Override
            public void onFailure(String error) {
                runOnUiThread(()->{
                    Toast.makeText(Profile.this, "Erro ao buscar dados da empresa: "+error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void loadUserData() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        String name = sharedPreferences.getString("name", "Usuário não encontrado");

        txtName.setText(name);

    }
}