package com.activities;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.models.QuestionOption;
import com.models.QuestionTest;
import com.services.QuestionOptionService;
import com.services.QuestionTestService;
import com.services.UserResultService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Test extends AppCompatActivity {
    private List<QuestionTest> questions;
    private List<QuestionOption> options;
    private int currentQuestionIndex = 0;
    private int score = 0;
    private Map<Integer, Integer> userAnswers = new HashMap<>();

    private TextView questionText;
    private long startTime;
    private RadioGroup optionsGroup;
    private AppCompatButton btnNext, btnPrevious;

    private int testId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_test);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        startTime = System.currentTimeMillis();
        testId = getIntent().getIntExtra("testId", -1);
        btnPrevious = findViewById(R.id.btnPrevious);
        questionText = findViewById(R.id.questionText);
        optionsGroup = findViewById(R.id.optionsGroup);
        btnNext = findViewById(R.id.btnNext);

        loadQuestionsAndOptions();
        btnPrevious.setOnClickListener(v -> {
            if (currentQuestionIndex > 0) {
                currentQuestionIndex--;
                showCurrentQuestion();
            } else {
                Toast.makeText(this, "Esta é a primeira pergunta.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void loadQuestionsAndOptions() {
        new Thread(() -> {
            List<QuestionTest> allQuestions = QuestionTestService.fetchQuestions();
            List<QuestionOption> allOptions = QuestionOptionService.fetchQuestionOptions();

            questions = new ArrayList<>();
            options = new ArrayList<>();

            for (QuestionTest q : allQuestions) {
                if (q.getTest_id() == testId) {
                    Log.d("QUESTIONS", "Pergunta ID: " + q.getId() + " | test_id: " + q.getTest_id());

                    questions.add(q);
                }
            }

            for (QuestionOption o : allOptions) {
                Log.d("OPTION_VERIFICA", "Opção " + o.getId() + " pertence à pergunta " + o.getQuestion_test_id());

                for (QuestionTest q : questions) {
                    if (o.getQuestion_test_id() == q.getId()) {
                        Log.d("OPTIONS", "Opção adicionada: " + o.getOption_text() + " para pergunta ID: " + o.getQuestion_test_id());

                        options.add(o);
                    }
                }
            }

            runOnUiThread(this::showCurrentQuestion);
        }).start();
    }

    private void showCurrentQuestion() {
        if (currentQuestionIndex >= questions.size()) {
            showResult();
            return;
        }

        QuestionTest current = questions.get(currentQuestionIndex);
        questionText.setText(current.getQuestion_text());


        optionsGroup.removeAllViews();
        optionsGroup.clearCheck(); // Limpar seleção anterior

        List<QuestionOption> currentOptions = new ArrayList<>();
        for (QuestionOption opt : options) {
            if (opt.getQuestion_test_id() == current.getId()) {
                currentOptions.add(opt);
            }
        }
        Log.d("DEBUG", "Total de opções encontradas para pergunta ID " + current.getId() + ": " + currentOptions.size());

        if (currentOptions.isEmpty()) {
            Toast.makeText(this, "Nenhuma opção disponível para esta pergunta.", Toast.LENGTH_SHORT).show();
            return;
        }

        for (QuestionOption opt : currentOptions) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setText(opt.getOption_text());
            radioButton.setTag(opt);
            radioButton.setTextColor(Color.BLACK);
            radioButton.setId(opt.getId()); // Definir um ID único para cada RadioButton
            optionsGroup.addView(radioButton);
        }

        btnNext.setOnClickListener(v -> {
            int selectedId = optionsGroup.getCheckedRadioButtonId();
            if (selectedId == -1) {
                Toast.makeText(this, "Selecione uma opção!", Toast.LENGTH_SHORT).show();
                return;
            }

            RadioButton selected = findViewById(selectedId);
            QuestionOption selectedOption = (QuestionOption) selected.getTag();

            userAnswers.put(current.getId(), selectedOption.getId());

            if (selectedOption.isIs_correct()) {
                score++;
            }

            currentQuestionIndex++;
            showCurrentQuestion();
        });
    }


    private void showResult() {
        int totalQuestions = questions.size();
        int timeTaken = calculateTimeTaken(); // você pode implementar isso como quiser

        int userId = getSharedPreferences("UserPrefs", MODE_PRIVATE).getInt("candidateId", -1);

        // Enviar resultado para o backend
        UserResultService.sendUserTestResult(userId, testId, score, totalQuestions, timeTaken);

        new AlertDialog.Builder(this)
                .setTitle("Resultado")
                .setMessage("Você acertou " + score + " de " + totalQuestions + " perguntas.")
                .setPositiveButton("OK", (dialog, which) -> finish())
                .setCancelable(false)
                .show();
    }

    private int calculateTimeTaken() {
        long endTime = System.currentTimeMillis();
        return (int) ((endTime - startTime) / 1000); // tempo em segundos
    }
}