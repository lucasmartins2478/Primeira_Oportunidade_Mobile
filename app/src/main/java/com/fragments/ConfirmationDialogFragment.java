package com.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.activities.R;

public class ConfirmationDialogFragment extends DialogFragment {

    private String message;
    private ConfirmationListener listener;

    public interface ConfirmationListener {
        void onConfirmed();
        void onCancelled();
    }

    public static ConfirmationDialogFragment newInstance(String message, ConfirmationListener listener) {
        ConfirmationDialogFragment dialog = new ConfirmationDialogFragment();
        dialog.message = message;
        dialog.listener = listener;
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.confirmation_dialog, container, false);

        TextView confirmationText = view.findViewById(R.id.confirmation_text);
        Button btnYes = view.findViewById(R.id.btn_yes);
        Button btnNo = view.findViewById(R.id.btn_no);

        confirmationText.setText(message);

        btnYes.setOnClickListener(v -> {
            listener.onConfirmed();
            dismiss();
        });

        btnNo.setOnClickListener(v -> {
            listener.onCancelled();
            dismiss();
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }
}
