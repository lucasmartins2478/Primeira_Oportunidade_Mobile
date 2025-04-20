package com.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.activities.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.models.Answer;
import com.models.Question;
import com.services.AnswerService;
import com.services.QuestionService;

import java.util.ArrayList;
import java.util.List;

public class AnswerBottomSheetFragment extends BottomSheetDialogFragment {

    private int vacancyId;
    private LinearLayout answersContainer;
    private final List<Question> questions = new ArrayList<>();
    private final List<EditText> answerInputs = new ArrayList<>();
    private OnAnswersSubmittedListener listener;

    public void setOnAnswersSubmittedListener(OnAnswersSubmittedListener listener) {
        this.listener = listener;
    }


    public static AnswerBottomSheetFragment newInstance(int vacancyId) {
        AnswerBottomSheetFragment fragment = new AnswerBottomSheetFragment();
        Bundle args = new Bundle();
        args.putInt("vacancyId", vacancyId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_answer_questions, container, false);
        answersContainer = view.findViewById(R.id.answers_container);

        if (getArguments() != null) {
            vacancyId = getArguments().getInt("vacancyId", -1);
        }

        loadQuestions();

        view.findViewById(R.id.btnSubmitAnswers).setOnClickListener(v -> submitAnswers());

        return view;
    }

    private void loadQuestions() {
        QuestionService.getQuestionsByVacancyId(getContext(), vacancyId, new QuestionService.QuestionListCallback() {
            @Override
            public void onSuccess(List<Question> questionList) {
                questions.addAll(questionList);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        for (Question q : questionList) {
                            addQuestionView(q.getQuestion());
                        }
                    });
                }

            }

            @Override
            public void onFailure(String errorMessage) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(), "Erro ao carregar perguntas: " + errorMessage, Toast.LENGTH_SHORT).show()
                    );
                }
                dismiss();
            }
        });
    }

    private void addQuestionView(String questionText) {
        EditText answerField = new EditText(getContext());
        answerField.setHint(questionText);
        answerField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        answerField.setMinLines(2);
        answerField.setMaxLines(4);
        answerField.setPadding(20, 20, 20, 20);

        answersContainer.addView(answerField);
        answerInputs.add(answerField);
    }

    private void submitAnswers() {
        SharedPreferences prefs = getContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        int userId = prefs.getInt("candidateId", -1);

        boolean allAnswered = true;
        List<Answer> answers = new ArrayList<>();

        for (int i = 0; i < questions.size(); i++) {
            String response = answerInputs.get(i).getText().toString().trim();
            if (response.isEmpty()) {
                allAnswered = false;
                break;
            }
            answers.add(new Answer(response, questions.get(i).getId(), userId));
        }

        if (!allAnswered) {
            Toast.makeText(getContext(), "Responda todas as perguntas antes de se candidatar.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Log para depurar
        Log.d("AnswerBottomSheet", "Respostas enviadas, iniciando envio para o backend.");

        // Envia as respostas individualmente (ou pode fazer em lote se seu backend permitir)
        for (Answer answer : answers) {
            AnswerService.submitAnswer(answer, new AnswerService.AnswerCallback() {
                @Override
                public void onSuccess() {
                    // Log de sucesso
                    Log.d("AnswerBottomSheet", "Resposta enviada com sucesso.");
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), "Candidatura finalizada com sucesso", Toast.LENGTH_SHORT).show();
                        });
                    }
                    // Fechar o fragmento somente após o sucesso
                    dismiss();
                }

                @Override
                public void onFailure(String errorMessage) {
                    // Log de erro
                    Log.d("AnswerBottomSheet", "Erro ao enviar resposta: " + errorMessage);
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), "Erro ao enviar resposta: " + errorMessage, Toast.LENGTH_SHORT).show();
                        });
                    }
                    // Não fechar o fragmento aqui se houve erro
                }
            });
        }

        // Verifique se a lógica de candidatura está correta
        if (listener != null) {
            listener.onAnswersSubmitted();
        }
    }

    public interface OnAnswersSubmittedListener {
        void onAnswersSubmitted();
    }

}
