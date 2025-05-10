package com.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.activities.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.models.Answer;
import com.models.Application;
import com.models.Candidate;
import com.models.Question;
import com.services.AnswerService;
import com.services.ApplicationService;
import com.services.CandidateService;
import com.services.QuestionService;

import java.util.ArrayList;
import java.util.List;

public class AnswerBottomSheetFragment extends BottomSheetDialogFragment {

    private int vacancyId;
    private CandidateService candidateService;
    private int sentAnswersCount;
    private LinearLayout answersContainer;

    LoadingDialogFragment loadingDialog;
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

    public void onStart() {
        super.onStart();

        View view = getView();
        if (view != null) {
            View parent = (View) view.getParent();
            BottomSheetBehavior behavior = BottomSheetBehavior.from(parent);
            int desiredHeight = (int)(getResources().getDisplayMetrics().heightPixels * 0.65); // 65% da tela
            parent.getLayoutParams().height = desiredHeight;
            parent.requestLayout();

            parent.requestLayout();
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_answer_questions, container, false);
        answersContainer = view.findViewById(R.id.answers_container);

        loadingDialog = new LoadingDialogFragment();

        loadingDialog.show(getParentFragmentManager(), "loading");

        candidateService = new CandidateService();

        if (getArguments() != null) {
            vacancyId = getArguments().getInt("vacancyId", -1);
        }

        loadQuestions();

        view.findViewById(R.id.btnSubmitAnswers).setOnClickListener(v -> submitAnswers());

        loadingDialog.dismiss();
        return view;
    }

    private void loadQuestions() {

        loadingDialog.show(getParentFragmentManager(), "loading");
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
                loadingDialog.dismiss();

            }

            @Override
            public void onFailure(String errorMessage) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(), "Erro ao carregar perguntas: " + errorMessage, Toast.LENGTH_SHORT).show()

                    );
                }
                loadingDialog.dismiss();

                dismiss();
            }
        });
    }

    private void addQuestionView(String questionText) {
        Context context = getContext();
        LinearLayout container = new LinearLayout(context);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        container.setPadding(0, 0, 0, 24); // espaçamento entre perguntas

        // Título da pergunta
        TextView title = new TextView(context);
        title.setText(questionText);
        title.setTextColor(ContextCompat.getColor(context, R.color.black));
        title.setTextSize(15f);
        title.setTypeface(null, android.graphics.Typeface.BOLD);
        title.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        // Campo de resposta
        EditText answerField = new EditText(
                new ContextThemeWrapper(context, R.style.EditTextRegister),
                null,
                R.style.EditTextRegister
        );
        answerField.setHint("Digite sua resposta");
        answerField.setTextColor(ContextCompat.getColor(context, R.color.black));
        answerField.setHintTextColor(ContextCompat.getColor(context, R.color.black));
        answerField.setEnabled(true);
        answerField.setFocusable(true);
        answerField.setFocusableInTouchMode(true);

        container.addView(title);
        container.addView(answerField);

        answersContainer.addView(container);
        answerInputs.add(answerField);
    }



    private void submitAnswers() {

        loadingDialog.show(getParentFragmentManager(), "loading");
        SharedPreferences prefs = getContext().getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = prefs.getInt("candidateId", -1);
        Log.d("submitAnswers", "User ID: " + userId);

        boolean allAnswered = true;
        List<Answer> answers = new ArrayList<>();

        for (int i = 0; i < questions.size(); i++) {
            String response = answerInputs.get(i).getText().toString().trim();
            int qId = questions.get(i).getId();

            Log.d("submitAnswers", "Pergunta ID: " + qId + ", Resposta: " + response);

            if (response.isEmpty()) {
                allAnswered = false;
                break;
            }
            answers.add(new Answer(response, qId, userId));
        }

        if (!allAnswered) {
            Toast.makeText(getContext(), "Responda todas as perguntas antes de se candidatar.", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("AnswerBottomSheet", "Respostas enviadas, iniciando envio para o backend.");

        // Zera o contador antes de enviar
        sentAnswersCount = 0;

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserPrefs", MODE_PRIVATE);

        String token = sharedPreferences.getString("token", "Nanhum token encontrado");

        for (Answer answer : answers) {
            AnswerService.submitAnswer(answer, token, new AnswerService.AnswerCallback() {
                @Override
                public void onSuccess() {
                    Log.d("AnswerBottomSheet", "Resposta enviada com sucesso.");

                    sentAnswersCount++; // Incrementa quando a resposta é enviada com sucesso

                    // Quando todas as respostas forem enviadas, podemos aplicar para a vaga
                    if (sentAnswersCount == answers.size()) {
                        applyForVacancy(vacancyId);
                    }

                    // Exibe mensagem de sucesso
                    if (isAdded() && getContext() != null) {
                        getActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), "Candidatura finalizada com sucesso", Toast.LENGTH_SHORT).show();
                        });
                    }
                    loadingDialog.dismiss();

                    dismiss(); // Fechar o fragmento após a candidatura ser registrada
                }

                @Override
                public void onFailure(String errorMessage) {
                    Log.d("AnswerBottomSheet", "Erro ao enviar resposta: " + errorMessage);
                    if (isAdded() && getContext() != null) {
                        getActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), "Erro ao enviar resposta: " + errorMessage, Toast.LENGTH_SHORT).show();
                            loadingDialog.dismiss();
                        });
                    }
                }
            });
        }

        // Se você tem um listener, chama a função de candidatura
        if (listener != null) {
            loadingDialog.dismiss();
            applyForVacancy(vacancyId);
        }
    }


    public void applyForVacancy(int vacancyId) {

        loadingDialog.show(getParentFragmentManager(), "loading");
        SharedPreferences prefs = getContext().getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = prefs.getInt("candidateId", -1);
        String token = prefs.getString("token", "Nenhum token encontrado");

        candidateService.fetchCandidateFromApiByCandidateId(userId, token,  new CandidateService.CandidateCallback() {
            @Override
            public void onSuccess(Candidate candidate) {
                if(candidate.getCurriculumId() == userId){
                    Application application = new Application(vacancyId,userId);

                    String token = prefs.getString("token", "Nenhum token encontrado");

                    ApplicationService.registerApplication(getContext(), application, token, new ApplicationService.ApplicationCallback() {
                        @Override
                        public void onSuccess() {
                            if (isAdded() && getContext() != null) {
                                getActivity().runOnUiThread(() -> {
                                    Toast.makeText(getContext(), "Candidatura finalizada com sucesso", Toast.LENGTH_SHORT).show();
                                    loadingDialog.dismiss();
                                });
                            }
                        }

                        @Override
                        public void onFailure(String errorMessage) {
                            Toast.makeText(getContext(), "Erro ao se candidatar: "+errorMessage, Toast.LENGTH_SHORT).show();
                            loadingDialog.dismiss();
                            dismiss();
                        }
                    });
                }
                else{
                    Toast.makeText(getContext(), "Você precisa de um currículo para se candidatar", Toast.LENGTH_SHORT).show();
                    loadingDialog.dismiss();
                }
            }

            @Override
            public void onFailure(String error) {

            }
        });
    }

    public interface OnAnswersSubmittedListener {
        void onAnswersSubmitted();
    }

}
