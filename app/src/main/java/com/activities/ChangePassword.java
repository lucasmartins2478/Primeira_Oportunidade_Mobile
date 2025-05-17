package com.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.fragments.LoadingDialogFragment;
import com.services.LoginService;

public class ChangePassword extends AppCompatActivity {

    EditText currentPasswordInput, newPasswordInput, confirmPasswordInput;
    LoadingDialogFragment loadingDialog;

    int userId;
    String token;
    LoginService loginService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_change_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        userId = prefs.getInt("userId", -1);
        token = prefs.getString("token", "Nenhum token encontrado");

        currentPasswordInput = findViewById(R.id.currentPassword);
        newPasswordInput = findViewById(R.id.newPassword);
        confirmPasswordInput = findViewById(R.id.confirmPassword);

        loadingDialog = new LoadingDialogFragment();
        loginService = new LoginService();

    }


    public void changePassword(View view){
        loadingDialog.show(getSupportFragmentManager(), "loading");

        String currentPassword = currentPasswordInput.getText().toString();
        String newPassword = newPasswordInput.getText().toString();
        String confirmPassword = confirmPasswordInput.getText().toString();
        if (newPassword.isEmpty()) {
            newPasswordInput.setError("Preencha a senha");
            newPasswordInput.requestFocus();
            loadingDialog.dismiss();

            return;
        }
        if (confirmPassword.isEmpty()){
            confirmPasswordInput.setError("Confirme a senha");
            confirmPasswordInput.requestFocus();
            loadingDialog.dismiss();

            return;
        }
        if (currentPassword.isEmpty()){
            currentPasswordInput.setError("Preencha a senha atual");
            currentPasswordInput.requestFocus();
            loadingDialog.dismiss();
            return;
        }
        if(!newPassword.equals(confirmPassword)){
            Toast.makeText(this, "As senhas não coincidem!", Toast.LENGTH_SHORT).show();

            loadingDialog.dismiss();

            return;
        }


        loginService.updatePassword(newPassword, currentPassword, userId, token, new LoginService.DeleteCallback() {
            @Override
            public void onSuccess() {
                runOnUiThread(()->{
                    loadingDialog.dismiss();
                    Toast.makeText(ChangePassword.this, "Senha alterada com sucesso", Toast.LENGTH_SHORT).show();

                });
            }

            @Override
            public void onFailure(String error) {
                runOnUiThread(()->{
                    loadingDialog.dismiss();
                    Toast.makeText(ChangePassword.this, "Erro ao atualizar senha", Toast.LENGTH_SHORT).show();

                });
            }
        });

    }

    public void onBackPressed(View view){
        finish();
    }

}