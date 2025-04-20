package com.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.activities.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.models.Application;
import com.models.Candidate;
import com.models.Vacancy;
import com.services.ApplicationService;
import com.services.CandidateService;


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
            AnswerBottomSheetFragment bottomSheet = AnswerBottomSheetFragment.newInstance(vacancy.getId());

            bottomSheet.setOnAnswersSubmittedListener(() -> {
                applyForVacancy(vacancy.getId());
            });

            bottomSheet.show(getParentFragmentManager(), bottomSheet.getTag());
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
        SharedPreferences prefs = getContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        int userId = prefs.getInt("candidateId", -1);

        candidateService.fetchCandidateFromApiByCandidateId(userId, new CandidateService.CandidateCallback() {
            @Override
            public void onSuccess(Candidate candidate) {
                if(candidate.getCurriculumId() == userId){
                    Application application = new Application(vacancyId,userId);


                    ApplicationService.registerApplication(getContext(), application, new ApplicationService.ApplicationCallback() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(getContext(), "Candidatura realizada com sucesso", Toast.LENGTH_SHORT).show();
                            dismiss();
                        }

                        @Override
                        public void onFailure(String errorMessage) {
                            Toast.makeText(getContext(), "Erro ao se candidatar: "+errorMessage, Toast.LENGTH_SHORT).show();
                            dismiss();
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
}