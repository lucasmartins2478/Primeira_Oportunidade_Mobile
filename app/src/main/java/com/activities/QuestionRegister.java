package com.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.view.ViewCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.WindowInsetsCompat;

import com.fragments.LoadingDialogFragment;
import com.models.Question;
import com.services.QuestionService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class QuestionRegister extends AppCompatActivity {

    private LinearLayout questionsContainer;
    private List<EditText> questionInputs = new ArrayList<>();

    LoadingDialogFragment loadingDialog;
    private int vacancyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_question_register);

        // Ajusta a área de conteúdo para não ficar sobrepondo a barra de status
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        loadingDialog = new LoadingDialogFragment();

        vacancyId = getIntent().getIntExtra("vacancyId", -1);

        if (vacancyId == -1) {
            Toast.makeText(this, "ID da vaga não encontrado", Toast.LENGTH_SHORT).show();
        }

        questionsContainer = findViewById(R.id.questions_layout);

        // Carrega perguntas existentes da vaga
        loadQuestions();

        AppCompatButton addQuestion = findViewById(R.id.add_question);
        addQuestion.setOnClickListener(v -> addQuestionInput());

        AppCompatButton registerButton = findViewById(R.id.register_button); // Botão de finalizar
        registerButton.setOnClickListener(v -> saveQuestions());
    }


    private void saveQuestions() {
        loadingDialog.show(getSupportFragmentManager(), "loading");
        QuestionService service = new QuestionService();

        AtomicInteger pendingRequests = new AtomicInteger(questionInputs.size());

        for (EditText input : questionInputs) {
            String questionText = input.getText().toString().trim();
            if (questionText.isEmpty()) {
                pendingRequests.decrementAndGet(); // Pula campo vazio
                continue;
            }

            Question existingQuestion = (Question) input.getTag();
            Question q = new Question();
            q.setQuestion(questionText);
            q.setVacancyId(vacancyId);

            Log.d("QuestionRegister", "Question : question:"+q.getQuestion()+", vacancyId:"+q.getVacancyId());
            SharedPreferences prefs  = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            String token = prefs.getString("token", "Nenhum token encontrado");

            if (existingQuestion != null && existingQuestion.getId() > 0) {
                q.setId(existingQuestion.getId());
                service.updateQuestion(this, q,token, new QuestionService.QuestionCallback() {
                    @Override
                    public void onSuccess() {
                        checkCompletion(pendingRequests);
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        runOnUiThread(() -> Toast.makeText(QuestionRegister.this, "Erro ao editar pergunta: " + errorMessage, Toast.LENGTH_SHORT).show());
                        checkCompletion(pendingRequests);
                    }
                });
            } else {
                service.registerQuestion(this, q, token,new QuestionService.QuestionCallback() {
                    @Override
                    public void onSuccess() {
                        checkCompletion(pendingRequests);
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        runOnUiThread(() -> Toast.makeText(QuestionRegister.this, "Erro ao cadastrar pergunta: " + errorMessage, Toast.LENGTH_SHORT).show());
                        checkCompletion(pendingRequests);
                    }
                });
            }
        }
    }

    private void checkCompletion(AtomicInteger counter) {
        if (counter.decrementAndGet() == 0) {
            runOnUiThread(() -> {
                loadingDialog.dismiss();
                Toast.makeText(this, "Perguntas salvas com sucesso!", Toast.LENGTH_SHORT).show();
                finish(); // Volta para a tela anterior
            });
        }
    }
    private void loadQuestions() {
        SharedPreferences prefs  = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String token = prefs.getString("token", "Nenhum token encontrado");

        loadingDialog.show(getSupportFragmentManager(), "loading");
        QuestionService service = new QuestionService();
        service.getQuestionsByVacancyId(this, vacancyId,token, new QuestionService.QuestionListCallback() {
            @Override
            public void onSuccess(List<Question> questions) {
                runOnUiThread(() -> {
                    if (questions.isEmpty()) {
                        addQuestionInput(); // Se não houver perguntas, permite adicionar uma nova
                    } else {
                        for (Question q : questions) {
                            addQuestionInput(q); // Preenche as perguntas existentes no layout
                        }
                    }
                    loadingDialog.dismiss();
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                runOnUiThread(() -> {
                    Toast.makeText(QuestionRegister.this, "Erro ao carregar perguntas: " + errorMessage, Toast.LENGTH_SHORT).show();
                    loadingDialog.dismiss();
                    addQuestionInput(); // Fallback: adiciona uma pergunta em branco
                });
            }
        });
    }

    public void addQuestionInput() {
        addQuestionInput(null); // Passando null para adicionar uma nova pergunta sem texto
    }

    public void addQuestionInput(Question question) {
        EditText input = (EditText) getLayoutInflater().inflate(R.layout.edit_text_question_input, questionsContainer, false);
        input.setHint("Digite a pergunta");

        // Define altura personalizada (em dp)
        int heightInDp = 90;
        int heightInPx = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, heightInDp, getResources().getDisplayMetrics()
        );

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                heightInPx
        );
        params.setMargins(0, 16, 0, 16); // margem entre os campos
        input.setLayoutParams(params);

        // Se a pergunta já existir, preenche o texto
        if (question != null) {
            input.setText(question.getQuestion());
            input.setTag(question);
        }

        questionsContainer.addView(input);
        questionInputs.add(input);
    }



    // Volta para a tela anterior
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(QuestionRegister.this, MyVacancies.class);
        startActivity(intent);
    }
}
