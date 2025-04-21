package com.activities;

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

import com.models.AcademicData;
import com.models.Answer;
import com.models.Candidate;
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

    TextView textAcademicInstitution, textAcademicCourse, textAcademicPeriod, textAcademicDates;
    TextView textCourseName, textCourseInstitution, textCourseModality, textCourseDuration;
    TextView textCompetences;
    LinearLayout academicContainer;
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

        CandidateService candidateService = new CandidateService();

        // Usa o método que recebe o ID do candidato
        candidateService.fetchCandidateFromApiByCandidateId(
                Integer.parseInt(candidateId),
                new CandidateService.CandidateCallback() {
                    @Override
                    public void onSuccess(Candidate candidate) {
                        runOnUiThread(() -> {
                            textCandidateName.setText("Nome: " + candidate.getName());
                            textCandidateCpf.setText("CPF: " + candidate.getCpf());
                            textCandidatePhone.setText("Telefone: " + candidate.getPhoneNumber());
                        });
                    }

                    @Override
                    public void onFailure(String error) {
                        runOnUiThread(() -> {
                            textCandidateName.setText("Erro ao buscar candidato");
                        });
                    }
                }
        );

    }
    private void fetchCurriculumData(int candidateId) {
        CurriculumService.getCurriculumByCandidateId(candidateId, new CurriculumService.FetchCurriculumCallback() {
            @Override
            public void onSuccess(Curriculum curriculum) {
                runOnUiThread(() -> {
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
                runOnUiThread(() ->
                        Toast.makeText(CandidateDetails.this, "Erro ao buscar currículo: " + errorMessage, Toast.LENGTH_SHORT).show()
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
                runOnUiThread(() -> Toast.makeText(CandidateDetails.this, "Erro ao buscar dados acadêmicos: " + errorMessage, Toast.LENGTH_SHORT).show());
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
                runOnUiThread(() ->
                        Toast.makeText(CandidateDetails.this, "Erro ao buscar curso complementar: " + errorMessage, Toast.LENGTH_SHORT).show()
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
                        TextView noCompetencesMessage = new TextView(CandidateDetails.this);
                        noCompetencesMessage.setText("Nenhuma competência informada.");
                        competencesContainer.addView(noCompetencesMessage);
                    } else {
                        // Adiciona cada competência no layout
                        for (String competence : competences) {
                            TextView competenceTextView = new TextView(CandidateDetails.this);
                            competenceTextView.setText("• " + competence);
                            competenceTextView.setTextColor(ContextCompat.getColor(CandidateDetails.this, R.color.black));
                            competencesContainer.addView(competenceTextView);
                        }
                    }
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                runOnUiThread(() -> Toast.makeText(CandidateDetails.this, "Erro ao buscar competências: " + errorMessage, Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void fetchQuestions(int vacancyId) {
        QuestionService.getQuestionsByVacancyId(CandidateDetails.this, vacancyId, new QuestionService.QuestionListCallback() {
            @Override
            public void onSuccess(List<Question> questions) {
                runOnUiThread(() -> {
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
                    Toast.makeText(CandidateDetails.this, "Erro ao buscar perguntas: " + errorMessage, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }




    private void fetchAnswersForQuestions(List<Question> questions) {
        // Vamos fazer as requisições para pegar as respostas para cada pergunta
        for (Question question : questions) {
            AnswerService.getAnswerByCandidateAndQuestionId(question.getId(), Integer.parseInt(candidateId), new AnswerService.FetchAnswerByQuestionCallback() {
                @Override
                public void onSuccess(Answer answer) {
                    runOnUiThread(() -> {
                        if (answer != null) {
                            displayAnswer(question, answer);
                        }
                    });
                }

                @Override
                public void onFailure(String errorMessage) {
                    runOnUiThread(() -> {
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