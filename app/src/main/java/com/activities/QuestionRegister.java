package com.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
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
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        vacancyId = getIntent().getIntExtra("vacancyId", -1); // sem o 'int' na frente

        if (vacancyId == -1) {
            Toast.makeText(this, "ID da vaga não encontrado", Toast.LENGTH_SHORT).show();
        }

        questionsContainer = findViewById(R.id.questions_layout); // ID que vamos adicionar no XML

        addQuestionInput();

        AppCompatButton addQuestion = findViewById(R.id.add_question);
        addQuestion.setOnClickListener(v -> {
            addQuestionInput();
        });
    }

    public void addQuestionInput() {
        EditText newInput = new EditText( new ContextThemeWrapper(this, R.style.EditTextRegister),
                null,
                android.R.attr.editTextStyle);
        newInput.setHint("Digite a pergunta");
        newInput.setMinLines(2);
        newInput.setMaxLines(4);
        newInput.setPadding(20, 20, 20, 20);
        newInput.setTextColor(ContextCompat.getColor(QuestionRegister.this, R.color.black));
        questionsContainer.addView(newInput);
        questionInputs.add(newInput);
    }

    public void registerVacancy(View view) {
        QuestionService service = new QuestionService();
        int total = questionInputs.size();
        AtomicInteger completed = new AtomicInteger(0);

        if (total == 0) {
            Toast.makeText(this, "Adicione pelo menos uma pergunta.", Toast.LENGTH_SHORT).show();
            return;
        }

        for (EditText input : questionInputs) {
            String questionText = input.getText().toString().trim();
            if (!questionText.isEmpty()) {
                Question question = new Question(questionText, vacancyId);

                service.registerQuestion(this, question, new QuestionService.QuestionCallback() {
                    @Override
                    public void onSuccess() {
                        if (completed.incrementAndGet() == total) {
                            runOnUiThread(() -> {
                                Toast.makeText(QuestionRegister.this, "Perguntas salvas com sucesso!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(QuestionRegister.this, MyVacancies.class);
                                startActivity(intent);
                            });
                        }
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        runOnUiThread(() -> {
                            Toast.makeText(QuestionRegister.this, "Erro ao salvar pergunta: " + errorMessage, Toast.LENGTH_SHORT).show();
                        });
                    }
                });
            } else {
                completed.incrementAndGet(); // ignora vazias
            }
        }
    }



    public void onBackPressed(View view){
        Intent intent = new Intent(QuestionRegister.this, MyVacancies.class);
        startActivity(intent);
    }
}