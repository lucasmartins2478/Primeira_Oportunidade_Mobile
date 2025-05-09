package com.fragments;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.activities.R;
import com.activities.Vacancies;
import com.activities.VacancyRegister;
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
import com.services.VacancyService;

import java.util.List;


public class VacancyDetailsFragment extends BottomSheetDialogFragment {

    CandidateService candidateService;
    VacancyService vacancyService;
    private Vacancy vacancy;

    LoadingDialogFragment loadingDialog;

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

        loadingDialog = new LoadingDialogFragment();

        loadingDialog.show(getParentFragmentManager(), "loading");

        candidateService = new CandidateService();
        vacancyService = new VacancyService();
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

        ImageView ivEditIcon = view.findViewById(R.id.ivEditIcon);


        TextView btnCancelVacancy = view.findViewById(R.id.remove_vacancy_button);
        AppCompatButton btnFillVacancy = view.findViewById(R.id.fill_vacancy);


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

        ivEditIcon.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), VacancyRegister.class);
            intent.putExtra("vacancyToEdit", vacancy); // a classe Vacancy precisa implementar Serializable ou Parcelable

            startActivity(intent);
        });


        btnCancelVacancy.setOnClickListener(v -> {
            vacancyService.updateIsActiveToFalse(vacancy.getId(), new VacancyService.RegisterIdCallback() {
                @Override
                public void onSuccess(int vacancyId) {
                    requireActivity().runOnUiThread(()->{
                        Toast.makeText(getContext(), "Vaga removida!", Toast.LENGTH_SHORT).show();
                        dismiss();
                    });
                }

                @Override
                public void onFailure(String error) {
                    requireActivity().runOnUiThread(()->{
                        Toast.makeText(getContext(), "Erro ao remover vaga!", Toast.LENGTH_SHORT).show();
                        dismiss();
                    });
                }
            });

        });

        btnFillVacancy.setOnClickListener(v -> {
            vacancyService.updateIsFilledToTrue(vacancy.getId(), new VacancyService.RegisterIdCallback() {
                @Override
                public void onSuccess(int vacancyId) {
                    requireActivity().runOnUiThread(()->{
                        Toast.makeText(getContext(), "Vaga preenchida com sucesso!", Toast.LENGTH_SHORT).show();
                        dismiss();
                    });
                }

                @Override
                public void onFailure(String error) {
                    requireActivity().runOnUiThread(()->{
                        Toast.makeText(getContext(), "Erro ao preencher vaga!", Toast.LENGTH_SHORT).show();
                        dismiss();
                    });
                }
            });

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
                    loadingDialog.dismiss();
                }
            });
        }
        if ("company".equals(userType)) {
            btnViewApplications.setVisibility(View.VISIBLE);
            btnCancelVacancy.setVisibility(View.VISIBLE);
            btnFillVacancy.setVisibility(View.VISIBLE);
            ivEditIcon.setVisibility(View.VISIBLE);

        }
        loadingDialog.dismiss();

        return view;
    }

    public void applyForVacancy(int vacancyId) {

        loadingDialog.show(getParentFragmentManager(), "loading");
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

                                loadingDialog.dismiss();
                                int companyId = vacancy.getCompanyId();

                                // Envia notificação para a empresa
                                enviarNotificacaoParaEmpresa(getContext(), vacancy, companyId);
                            }
                        }

                        @Override
                        public void onFailure(String errorMessage) {
                            if (isAdded()) {
                                Toast.makeText(getContext(), "Erro ao se candidatar: " + errorMessage, Toast.LENGTH_SHORT).show();
                                loadingDialog.dismiss();
                                dismiss();
                            }
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
                loadingDialog.dismiss();

            }
        });
    }

    // Função para enviar notificação para a empresa quando um candidato se inscrever
    public void enviarNotificacaoParaEmpresa(Context context, Vacancy vaga, int companyId) {
        // Crie um Intent que será disparado quando a empresa clicar na notificação
        Intent intent = new Intent(context, Vacancies.class);  // Ou a Activity que contém o Fragment
        intent.putExtra("vacancy", vaga);  // Passe os dados da vaga para o Fragment

        // Crie um PendingIntent para abrir a Activity/Fragment
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Crie a notificação
        Notification notification = new NotificationCompat.Builder(context, "CANAL_VAGAS_EMPRESA")
                .setContentTitle("Novo Candidato para sua Vaga!")
                .setContentText("Um candidato se inscreveu na vaga: " + vaga.getTitle() + ". Clique para ver os detalhes.")
                .setSmallIcon(R.drawable.my_applications_icon)  // Defina um ícone para a notificação
                .setContentIntent(pendingIntent)  // Associe o PendingIntent à notificação
                .setAutoCancel(true)  // A notificação desaparece quando clicada
                .build();

        // Envie a notificação para a empresa
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(companyId, notification);  // ID única para a empresa
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