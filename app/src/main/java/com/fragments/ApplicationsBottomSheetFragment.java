package com.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.activities.R;
import com.adapters.ApplicationsAdapter;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.models.Application;
import com.services.ApplicationService;

import java.util.List;


public class ApplicationsBottomSheetFragment extends BottomSheetDialogFragment {
    private static final String ARG_VACANCY_ID = "vacancyId";

    LoadingDialogFragment loadingDialog;

    public static ApplicationsBottomSheetFragment newInstance(int vacancyId) {
        ApplicationsBottomSheetFragment fragment = new ApplicationsBottomSheetFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_VACANCY_ID, vacancyId);
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

    private int vacancyId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_applications, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadingDialog = new LoadingDialogFragment();
        loadingDialog.show(getParentFragmentManager(), "loading");

        RecyclerView recyclerView = view.findViewById(R.id.recyclerApplications);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        TextView noApplications = view.findViewById(R.id.noApplications);

        if (getArguments() != null) {
            vacancyId = getArguments().getInt(ARG_VACANCY_ID);
        }

        if (getActivity() != null) {
            SharedPreferences prefs = getActivity().getSharedPreferences("UserPrefs", MODE_PRIVATE);
            String token = prefs.getString("token", "Nenhum token encontrado");

            ApplicationService.getApplicationsByVacancyId(getContext(), vacancyId, token, new ApplicationService.ApplicationsListCallback() {
                @Override
                public void onSuccess(List<Application> applications) {
                    if (applications.isEmpty()) {
                        noApplications.setVisibility(View.VISIBLE);
                    } else {
                        recyclerView.setAdapter(new ApplicationsAdapter(requireContext(), applications));
                    }
                    loadingDialog.dismiss();
                }

                @Override
                public void onFailure(String error) {
                    Toast.makeText(getContext(), "Erro: " + error, Toast.LENGTH_SHORT).show();
                    loadingDialog.dismiss();
                }
            });
        }
    }

}
