package com.fragments;

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
import com.models.Vacancy;


public class VacancyDetailsFragment extends BottomSheetDialogFragment {

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

        // Recupera a vaga passada como argumento
        if (getArguments() != null) {
            vacancy = (Vacancy) getArguments().getSerializable("vacancy");
        }

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
        tvCompany.setText(vacancy.getAboutCompany());
        tvLocation.setText(vacancy.getLocality());
        tvDescription.setText(vacancy.getDescription());
        tvBenefits.setText(vacancy.getBenefits()); // Adicionando benefícios
        tvRequirements.setText(vacancy.getRequirements()); // Adicionando requisitos
        tvSalary.setText(vacancy.getSalary()); // Adicionando salário


        AppCompatButton btnApply = view.findViewById(R.id.apply);
        btnApply.setOnClickListener(v -> {
            applyForVacancy();
        });

        return view;
    }
    private void applyForVacancy() {
        // Aqui você coloca a lógica que quiser
        // Exemplo simples:
        Toast.makeText(getContext(), "Você se candidatou à vaga: " + vacancy.getTitle(), Toast.LENGTH_SHORT).show();

        // Se quiser fechar o BottomSheet depois:
        dismiss();
    }
}