package com.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.activities.Community;
import com.activities.FormLogin;
import com.activities.MyVacancies;
import com.activities.Profile;
import com.activities.R;
import com.activities.Vacancies;
import com.activities.VacancyRegister;
import com.google.android.material.bottomsheet.BottomSheetDialog;


public class BottomMenuFragment extends Fragment {

    public BottomMenuFragment() {
        // Construtor vazio necessário para fragmentos
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bottom_menu, container, false);

        Button btnMenu = view.findViewById(R.id.btnMenu);
        btnMenu.setOnClickListener(v -> showBottomMenu());


        return view;

    }

    private void showBottomMenu() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        View view = getLayoutInflater().inflate(R.layout.bottom_menu, null);
        bottomSheetDialog.setContentView(view);

        Button btnHome = view.findViewById(R.id.btnHome);
        Button btnProfile = view.findViewById(R.id.btnProfile);
        Button btnClose = view.findViewById(R.id.btnClose);
        Button btnVacancyRegister = view.findViewById(R.id.btnVacancyRegister);
        Button btnCompanies = view.findViewById(R.id.btnCompanies);
        Button btnCommunity = view.findViewById(R.id.btnComunity);
        Button btnMyApplications = view.findViewById(R.id.btnMyApplications);


        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserPrefs", requireActivity().MODE_PRIVATE);

        String userType = sharedPreferences.getString("userType", "Usuário não encontrado");


        if ("user".equals(userType)) {
            btnCompanies.setVisibility(View.VISIBLE);
            btnCommunity.setVisibility(View.VISIBLE);
            btnMyApplications.setVisibility(View.VISIBLE);
        }
        if ("company".equals(userType)) {
            btnVacancyRegister.setVisibility(View.VISIBLE);
        }

        btnHome.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();

            if(userType == "user"){
                Intent intent = new Intent(getContext(), Vacancies.class);
                startActivity(intent);
            }else{
                Intent intent = new Intent(getContext(), MyVacancies.class);
                startActivity(intent);
            }

        });

        btnProfile.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();

            Intent intent = new Intent(getContext(), Profile.class);
            startActivity(intent);
        });

        btnCompanies.setOnClickListener(v ->{
            bottomSheetDialog.dismiss();
        });
        btnCommunity.setOnClickListener(v ->{
            bottomSheetDialog.dismiss();
            Intent intent = new Intent(getContext(), Community.class);
            startActivity(intent);
        });
        btnVacancyRegister.setOnClickListener(v ->{
            bottomSheetDialog.dismiss();

            Intent intent = new Intent(getContext(), VacancyRegister.class);
            startActivity(intent);
        });
        btnMyApplications.setOnClickListener(v ->{
            bottomSheetDialog.dismiss();
        });

        btnClose.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            logout();
        });

        bottomSheetDialog.show();
    }

    private void logout() {
        // 🔹 Apagar os dados do usuário do SharedPreferences
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", requireActivity().MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        // 🔹 Redirecionar para a tela de login
        Intent intent = new Intent(requireActivity(), FormLogin.class);
        startActivity(intent);
        requireActivity().finish(); // Fecha a Activity principal para evitar que o usuário volte ao pressionar "Voltar"
    }



}