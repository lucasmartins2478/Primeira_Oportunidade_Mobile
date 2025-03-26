package com.models;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.text.NumberFormat;
import java.util.Locale;

public class MoneyTextWatcher implements TextWatcher {
    private final EditText editText;
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    public MoneyTextWatcher(EditText editText) {
        this.editText = editText;
        currencyFormat.setMaximumFractionDigits(2);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {}

    @Override
    public void afterTextChanged(Editable s) {
        editText.removeTextChangedListener(this);

        try {
            // Remove tudo que não for número
            String cleanString = s.toString().replaceAll("[R$,.\\s]", "");

            // Converte para um valor decimal
            double parsed = Double.parseDouble(cleanString) / 100;

            // Formata no estilo de moeda (ex: R$ 1.234,56)
            String formatted = currencyFormat.format(parsed);

            editText.setText(formatted);
            editText.setSelection(formatted.length()); // Move o cursor para o final
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        editText.addTextChangedListener(this);
    }
}
