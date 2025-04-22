package com.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.view.ViewCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.WindowInsetsCompat;

import com.models.Question;
import com.services.QuestionService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class QuestionRegister extends AppCompatActivity {

    private LinearLayout questionsContainer;
    private List<EditText> questionInputs = new ArrayList<>();
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

        vacancyId = getIntent().getIntExtra("vacancyId", -1);

        if (vacancyId == -1) {
            Toast.makeText(this, "ID da vaga não encontrado", Toast.LENGTH_SHORT).show();
        }

        questionsContainer = findViewById(R.id.questions_layout);

        // Carrega perguntas existentes da vaga
        loadQuestions();

        AppCompatButton addQuestion = findViewById(R.id.add_question);
        addQuestion.setOnClickListener(v -> addQuestionInput());
    }

    // Método para carregar as perguntas já registradas
    private void loadQuestions() {
        QuestionService service = new QuestionService();
        service.getQuestionsByVacancyId(this, vacancyId, new QuestionService.QuestionListCallback() {
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
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                runOnUiThread(() -> {
                    Toast.makeText(QuestionRegister.this, "Erro ao carregar perguntas: " + errorMessage, Toast.LENGTH_SHORT).show();
                    addQuestionInput(); // Fallback: adiciona uma pergunta em branco
                });
            }
        });
    }

    // Método para adicionar uma nova entrada de pergunta
    public void addQuestionInput() {
        addQuestionInput(null); // Passando null para adicionar uma nova pergunta sem texto
    }

    // Método para adicionar uma entrada de pergunta (usado tanto para nova pergunta quanto para editar)
    public void addQuestionInput(Question question) {
        EditText input = new EditText(this);
        input.setPadding(20, 20, 20, 20);
        input.setHint("Digite a pergunta");

        if (question != null) {
            input.setText(question.getQuestion()); // Preenche com a pergunta existente
            input.setTag(question); // Marca a pergunta com o objeto Question
        }

        questionsContainer.addView(input);
        questionInputs.add(input);
    }

    // Método para registrar as perguntas (salvar novas ou atualizar as existentes)


    // Volta para a tela anterior
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(QuestionRegister.this, MyVacancies.class);
        startActivity(intent);
    }
}
