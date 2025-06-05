package com.activities;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.fragments.ConfirmationDialogFragment;

public class Premium extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_premium);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void cancel(View v){
        ConfirmationDialogFragment.newInstance(
                "Tem certeza que não quer os benefícios? Seria uma honra te ter como premium!.",
                new ConfirmationDialogFragment.ConfirmationListener() {
                    @Override
                    public void onConfirmed() {
                        finish();
                    }

                    @Override
                    public void onCancelled() {
                        // nada
                    }
                }
        ).show(getSupportFragmentManager(), "ConfirmationDialog");
    }
}