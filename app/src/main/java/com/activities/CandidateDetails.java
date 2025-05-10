package com.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.fragments.LoadingDialogFragment;
import com.models.AcademicData;
import com.models.Answer;
import com.models.Candidate;
import com.models.CompetenceData;
import com.models.CourseData;
import com.models.Curriculum;
import com.models.DateUtils;
import com.models.Question;
import com.services.AcademicDataService;
import com.services.AnswerService;
import com.services.CandidateService;
import com.services.CompetenceDataService;
import com.services.CourseDataService;
import com.services.CurriculumService;
import com.services.QuestionService;

import java.util.List;

public class CandidateDetails extends AppCompatActivity {

    TextView textCandidateName, textCandidateCpf, textCandidatePhone;
    TextView textBirthDate, textGender, textCity, textAbout, textInterest;

    LinearLayout academicContainer;
    LoadingDialogFragment loadingDialog;
    String vacancyId;
    LinearLayout coursesContainer, competencesContainer;





    String candidateId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_candidate_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        loadingDialog = new LoadingDialogFragment();



        textCandidateName = findViewById(R.id.textCandidateName);
        textCandidateCpf = findViewById(R.id.textCandidateCpf);
        textCandidatePhone = findViewById(R.id.textCandidatePhone);

        // Pega o ID enviado pelo Adapter
        candidateId = getIntent().getStringExtra("candidate_id");
        vacancyId = getIntent().getStringExtra("vacancy_id");  // Recupera o vacancyId


        fetchCandidateData();

        textBirthDate = findViewById(R.id.textBirthDate);
        textGender = findViewById(R.id.textGender);
        textCity = findViewById(R.id.textCity);
        textAbout = findViewById(R.id.textAbout);
        textInterest = findViewById(R.id.textInterest);




        academicContainer = findViewById(R.id.academicContainer);

        competencesContainer = findViewById(R.id.competencesContainer);


        coursesContainer = findViewById(R.id.coursesContainer);  // A linha que está causando o problema


        fetchCurriculumData(Integer.parseInt(candidateId));



    }

    private void fetchCandidateData() {

        loadingDialog.show(getSupportFragmentManager(), "loading");

        CandidateService candidateService = new CandidateService();

        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        String token = prefs.getString("token", "Nenhum token encontrado");

        candidateService.fetchCandidateFromApiByCandidateId(
                Integer.parseInt(candidateId), token,
                new CandidateService.CandidateCallback() {
                    @Override
                    public void onSuccess(Candidate candidate) {
                        runOnUiThread(() -> {
                            loadingDialog.dismiss();
                            textCandidateName.setText("Nome: " + candidate.getName());
                            textCandidateCpf.setText("CPF: " + candidate.getCpf());
                            textCandidatePhone.setText("Telefone: " + candidate.getPhoneNumber());
                        });
                    }

                    @Override
                    public void onFailure(String error) {
                        runOnUiThread(() -> {
                            loadingDialog.dismiss();
                            textCandidateName.setText("Erro ao buscar candidato");
                        });
                    }
                }
        );

    }
    private void fetchCurriculumData(int candidateId) {

        loadingDialog.show(getSupportFragmentManager(), "loading");

        CurriculumService.getCurriculumByCandidateId(candidateId, new CurriculumService.FetchCurriculumCallback() {
            @Override
            public void onSuccess(Curriculum curriculum) {
                runOnUiThread(() -> {
                    loadingDialog.dismiss();
                    textBirthDate.setText("Nascimento: " + DateUtils.formatDate(curriculum.getBirthDate()));
                    textGender.setText("Gênero: " + curriculum.getGender());
                    textCity.setText("Cidade: " + curriculum.getCity());
                    textAbout.setText("Sobre: " + curriculum.getAboutYou());
                    textInterest.setText("Área de interesse: " + curriculum.getInterestArea());
                    fetchAcademicData(candidateId);
                    fetchCourseData(candidateId);
                    fetchCompetences(candidateId);
                    fetchQuestions(Integer.parseInt(vacancyId)); // Usando o vacancyId

                });
            }

            @Override
            public void onFailure(String errorMessage) {
                runOnUiThread(() -> {
                            loadingDialog.dismiss();
                            Toast.makeText(CandidateDetails.this, "Erro ao buscar currículo: " + errorMessage, Toast.LENGTH_SHORT).show();
                        }
                );
            }
        });
    }

    private void fetchAcademicData(int curriculumId) {
        loadingDialog.show(getSupportFragmentManager(), "loading");

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        String token = sharedPreferences.getString("token", "Nenhum token encontrado");

        AcademicDataService.getAcademicDataByCurriculumId(curriculumId, token, new AcademicDataService.FetchAcademicDataCallback() {
            @Override
            public void onSuccess(List<AcademicData> dataList) {
                runOnUiThread(() -> {
                    academicContainer.removeAllViews(); // limpa antes de adicionar

                    loadingDialog.dismiss();

                    for (AcademicData data : dataList) {
                        TextView info = new TextView(CandidateDetails.this);
                        info.setText(
                                "Instituição: " + data.getInstitutionName() + "\n" +
                                        "Curso: " + data.getCourseName() + "\n" +
                                        "Período: " + data.getPeriod() + "\n" +
                                        "Duração: " + DateUtils.formatMonthYear(data.getStartDate())
                                        + " até " +
                                        (data.getIsCurrentlyStudying() ? "Atual" : DateUtils.formatMonthYear(data.getEndDate())
                                        )
                        );
                        info.setPadding(0, 8, 0, 8);
                        info.setTextColor(ContextCompat.getColor(CandidateDetails.this, R.color.black));

                        academicContainer.addView(info);
                    }
                });

            }

            @Override
            public void onFailure(String errorMessage) {
                runOnUiThread(() -> {
                    loadingDialog.dismiss();
                    Toast.makeText(CandidateDetails.this, "Erro ao buscar dados acadêmicos: " + errorMessage, Toast.LENGTH_SHORT).show();

                });
            }
        });
    }


    private void fetchCourseData(int curriculumId) {
        loadingDialog.show(getSupportFragmentManager(), "loading");

        CourseDataService.getCourseDataByCurriculumId(curriculumId, new CourseDataService.FetchCourseDataCallback() {
            @Override
            public void onSuccess(List<CourseData> courseDataList) {
                runOnUiThread(() -> {
                    loadingDialog.dismiss();
                    // Exibindo os cursos
                    if (courseDataList.isEmpty()) {
                        TextView noCoursesMessage = new TextView(CandidateDetails.this);
                        noCoursesMessage.setText("Nenhum curso encontrado.");
                        coursesContainer.addView(noCoursesMessage);
                    } else {
                        for (CourseData courseData : courseDataList) {
                            TextView courseTextView = new TextView(CandidateDetails.this);
                            courseTextView.setText("Curso: " + courseData.getCourseName() +
                                    "\nModalidade: " + courseData.getModality() +
                                    "\nDuração: " + courseData.getDuration()+" horas");
                            courseTextView.setTextColor(ContextCompat.getColor(CandidateDetails.this, R.color.black));

                            coursesContainer.addView(courseTextView);
                        }
                    }
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                runOnUiThread(() -> {
                    loadingDialog.dismiss();
                    Toast.makeText(CandidateDetails.this, "Erro ao buscar curso complementar: " + errorMessage, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void fetchCompetences(int curriculumId) {
        loadingDialog.show(getSupportFragmentManager(), "loading");

        CompetenceDataService.getCompetencesByCurriculumId(curriculumId, new CompetenceDataService.FetchCompetencesCallback() {
            @Override
            public void onSuccess(List<CompetenceData> competences) {
                runOnUiThread(() -> {
                    // Se houver

                    loadingDialog.dismiss();
                    if (competences.isEmpty()) {
                        TextView noCompetencesMessage = new TextView(CandidateDetails.this);
                        noCompetencesMessage.setText("Nenhuma competência informada.");
                        competencesContainer.addView(noCompetencesMessage);
                    } else {
                        // Adiciona cada competência no layout
                        for (CompetenceData competence : competences) {
                            TextView competenceTextView = new TextView(CandidateDetails.this);
                            competenceTextView.setText("• " + competence.getCompetence());
                            competenceTextView.setTextColor(ContextCompat.getColor(CandidateDetails.this, R.color.black));
                            competencesContainer.addView(competenceTextView);
                        }
                    }
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                runOnUiThread(() -> {
                    loadingDialog.dismiss();
                    Toast.makeText(CandidateDetails.this, "Erro ao buscar competências: " + errorMessage, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void fetchQuestions(int vacancyId) {
        loadingDialog.show(getSupportFragmentManager(), "loading");

        QuestionService.getQuestionsByVacancyId(CandidateDetails.this, vacancyId, new QuestionService.QuestionListCallback() {
            @Override
            public void onSuccess(List<Question> questions) {
                runOnUiThread(() -> {

                    loadingDialog.dismiss();
                    if (!questions.isEmpty()) {
                        // Armazenar as perguntas, para depois buscar as respostas
                        fetchAnswersForQuestions(questions);
                    } else {
                        Toast.makeText(CandidateDetails.this, "Nenhuma pergunta encontrada.", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                runOnUiThread(() -> {
                    loadingDialog.dismiss();
                    Toast.makeText(CandidateDetails.this, "Erro ao buscar perguntas: " + errorMessage, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }




    private void fetchAnswersForQuestions(List<Question> questions) {

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        String token = sharedPreferences.getString("token", "Nenhum token encontrado");
        loadingDialog.show(getSupportFragmentManager(), "loading");

        // Vamos fazer as requisições para pegar as respostas para cada pergunta
        for (Question question : questions) {
            AnswerService.getAnswerByCandidateAndQuestionId(question.getId(), Integer.parseInt(candidateId), token,new AnswerService.FetchAnswerByQuestionCallback() {
                @Override
                public void onSuccess(Answer answer) {
                    runOnUiThread(() -> {
                        loadingDialog.dismiss();
                        if (answer != null) {
                            displayAnswer(question, answer);
                        }
                    });
                }

                @Override
                public void onFailure(String errorMessage) {
                    runOnUiThread(() -> {
                        loadingDialog.dismiss();
                        Log.d("Erro", "Erro ao buscar resposta: " + errorMessage);
                        Toast.makeText(CandidateDetails.this, "Erro ao buscar resposta: " + errorMessage, Toast.LENGTH_SHORT).show();
                    });
                }
            });

        }
    }



    private void displayAnswer(Question question, Answer answer) {
        LinearLayout answersContainer = findViewById(R.id.answersContainer);

        // Pergunta
        TextView questionView = new TextView(CandidateDetails.this);
        questionView.setText("Pergunta: " + question.getQuestion());
        questionView.setTypeface(null, android.graphics.Typeface.BOLD);
        questionView.setTextColor(ContextCompat.getColor(CandidateDetails.this, R.color.black));

        // Resposta
        TextView answerView = new TextView(CandidateDetails.this);
        answerView.setText("Resposta: " + (answer != null ? answer.getAnswer() : "Sem resposta"));
        answerView.setTextColor(ContextCompat.getColor(CandidateDetails.this, R.color.black));
        answerView.setPadding(0, 0, 0, 16);

        // Adiciona as views ao container
        answersContainer.addView(questionView);
        answersContainer.addView(answerView);
    }






    public void onBackPressed(View view){
        finish();
    }





}