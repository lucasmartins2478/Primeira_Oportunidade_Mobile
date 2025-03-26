package com.models;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class MaskEditText {

    public static TextWatcher mask(final EditText editText, final String mask) {
        return new TextWatcher() {
            boolean isUpdating;
            String oldText = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String newText = s.toString().replaceAll("[^0-9]", "");
                String formattedText = "";
                int i = 0;

                if (isUpdating) {
                    oldText = newText;
                    isUpdating = false;
                    return;
                }

                for (char m : mask.toCharArray()) {
                    if (m != '#' && newText.length() > oldText.length()) {
                        formattedText += m;
                        continue;
                    }
                    try {
                        formattedText += newText.charAt(i);
                    } catch (Exception e) {
                        break;
                    }
                    i++;
                }

                isUpdating = true;
                editText.setText(formattedText);
                editText.setSelection(formattedText.length());
            }

            @Override
            public void afterTextChanged(Editable s) { }
        };
    }

    public static String unmask(String s) {
        return s.replaceAll("[^0-9]*", "");
    }
}
