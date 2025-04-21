package com.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.activities.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.models.Application;
import com.models.Candidate;
import com.models.Question;
import com.models.Vacancy;
import com.services.ApplicationService;
import com.services.CandidateService;
import com.services.QuestionService;

import java.util.List;


public class VacancyDetailsFragment extends BottomSheetDialogFragment {

    CandidateService candidateService;
    private Vacancy vacancy;

    // Cria uma nova instância do fragmento com a vaga que será exibida
    public static VacancyDetailsFragment newInstance(Vacancy vacancy) {
        VacancyDetailsFragment fragment = new VacancyDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable("vacancy", vacancy);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
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
        View view = inflater.inflate(R.layout.bottom_sheet_vacancy_details, container, false);

        candidateService = new CandidateService();
        // Recupera a vaga passada como argumento
        if (getArguments() != null) {
            vacancy = (Vacancy) getArguments().getSerializable("vacancy");
        }

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserPrefs", requireActivity().MODE_PRIVATE);
        int candidateId = sharedPreferences.getInt("candidateId", -1);
        String userType = sharedPreferences.getString("type", "Usuário não encontrado");

        // Preenche os dados no layout do BottomSheet
        TextView tvTitle = view.findViewById(R.id.tvTitle);
        TextView tvCompany = view.findViewById(R.id.tvCompany);
        TextView tvLocation = view.findViewById(R.id.tvLocation);
        TextView tvDescription = view.findViewById(R.id.tvDescription);
        TextView tvBenefits = view.findViewById(R.id.tvBenefits);
        TextView tvRequirements = view.findViewById(R.id.tvRequirements);
        TextView tvSalary = view.findViewById(R.id.tvSalary);

        // Preenchendo os TextViews com os dados da vaga
        tvTitle.setText(vacancy.getTitle());
        tvCompany.setText(vacancy.getCompanyName());
        tvLocation.setText(vacancy.getLocality());
        tvDescription.setText(vacancy.getDescription());
        tvBenefits.setText(vacancy.getBenefits()); // Adicionando benefícios
        tvRequirements.setText(vacancy.getRequirements()); // Adicionando requisitos
        tvSalary.setText(vacancy.getSalary()); // Adicionando salário


        AppCompatButton btnApply = view.findViewById(R.id.apply);
        btnApply.setOnClickListener(v -> {
            QuestionService.getQuestionsByVacancyId(getContext(), vacancy.getId(), new QuestionService.QuestionListCallback() {
                @Override
                public void onSuccess(List<Question> questionList) {
                    requireActivity().runOnUiThread(() -> {
                        if (questionList.isEmpty()) {
                            // Não tem perguntas → aplicar direto
                            applyForVacancy(vacancy.getId());
                            dismiss();
                        } else {
                            // Tem perguntas → abre o bottom sheet de respostas
                            AnswerBottomSheetFragment bottomSheet = AnswerBottomSheetFragment.newInstance(vacancy.getId());
                            bottomSheet.setOnAnswersSubmittedListener(() -> {
                                applyForVacancy(vacancy.getId());
                            });
                            bottomSheet.show(getParentFragmentManager(), bottomSheet.getTag());
                            dismiss();
                        }
                    });
                }

                @Override
                public void onFailure(String errorMessage) {
                    requireActivity().runOnUiThread(() -> {
                        if (isExpectedNoQuestionsError(errorMessage)) {
                            applyForVacancy(vacancy.getId());
                            dismiss();
                        } else {
                            Toast.makeText(getContext(), "Erro ao verificar perguntas: " + errorMessage, Toast.LENGTH_SHORT).show();
                        }

                    });
                }


            });
        });


        AppCompatButton btnViewApplications = view.findViewById(R.id.viewApplications);
        btnViewApplications.setOnClickListener(v -> {
            ApplicationsBottomSheetFragment applicationsSheet = ApplicationsBottomSheetFragment.newInstance(vacancy.getId());
            applicationsSheet.show(getParentFragmentManager(), applicationsSheet.getTag());
            dismiss();
        });

        AppCompatButton btnCancelApplication = view.findViewById(R.id.cancelApplication);
        btnCancelApplication.setOnClickListener(v -> {
            cancelApplication(vacancy.getId());
        });


        if ("candidate".equals(userType)) {

            ApplicationService.getApplicationsByVacancyId(getContext(), vacancy.getId(), new ApplicationService.ApplicationsListCallback() {
                @Override
                public void onSuccess(java.util.List<Application> applications) {
                    boolean alreadyApplied = false;
                    for (Application app : applications) {
                        if (app.getUserId() == candidateId) {
                            alreadyApplied = true;
                            break;
                        }
                    }

                    if (alreadyApplied) {
                        btnCancelApplication.setVisibility(View.VISIBLE);
                    } else {
                        btnApply.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onFailure(String errorMessage) {
                    Toast.makeText(getContext(), "Erro ao verificar candidatura: " + errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        }
        if ("company".equals(userType)) {
            btnViewApplications.setVisibility(View.VISIBLE);
        }

        return view;
    }

    public void applyForVacancy(int vacancyId) {
        Log.d("ApplyDebug", "Chamou applyForVacancy para vagaId: " + vacancyId);

        SharedPreferences prefs = getContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        int userId = prefs.getInt("candidateId", -1);

        candidateService.fetchCandidateFromApiByCandidateId(userId, new CandidateService.CandidateCallback() {
            @Override
            public void onSuccess(Candidate candidate) {
                Log.d("ApplyDebug", "Candidato retornado: " + candidate.getId() + " | CurriculumId: " + candidate.getCurriculumId());

                if(candidate.getCurriculumId() == userId){

                    Application application = new Application(vacancyId,userId);
                    Log.d("ApplyDebug", "Passou do if, vai tentar aplicar!");


                    ApplicationService.registerApplication(getContext(), application, new ApplicationService.ApplicationCallback() {
                        @Override
                        public void onSuccess() {
                            if (isAdded()) {
                                Toast.makeText(getContext(), "Aplicação realizada com sucesso!", Toast.LENGTH_SHORT).show();
                                // Espera o toast aparecer, depois fecha
                                new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> dismiss(), 1500);
                            }
                        }

                        @Override
                        public void onFailure(String errorMessage) {
                            if (isAdded()) {
                                Toast.makeText(getContext(), "Erro ao se candidatar: " + errorMessage, Toast.LENGTH_SHORT).show();
                                dismiss();
                            }
                        }
                    });



                }
                else{
                    Toast.makeText(getContext(), "Você precisa de um currículo para se candidatar", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(String error) {

            }
        });
    }
    public void cancelApplication(int vacancyId){
        SharedPreferences prefs = getContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        int userId = prefs.getInt("candidateId", -1);
        ApplicationService.cancelApplication(vacancyId, userId, new ApplicationService.ApplicationCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(getContext(), "Candidatura cancelada com sucesso!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(getContext(), "Erro: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

    }
    private boolean isExpectedNoQuestionsError(String errorMessage) {
        return errorMessage == null
                || errorMessage.trim().isEmpty()
                || errorMessage.toLowerCase().contains("nenhuma pergunta")
                || errorMessage.toLowerCase().contains("no questions");
    }

}