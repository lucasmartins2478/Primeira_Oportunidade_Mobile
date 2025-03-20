package com.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.models.User;
import com.services.LoginService;

import java.util.List;

public class FormLogin extends AppCompatActivity {

    private LoginService loginService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_form_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String savedEmail = sharedPreferences.getString("email", null);
        String savedPassword = sharedPreferences.getString("password", null);

        if (savedEmail != null && savedPassword != null) {
            // 🔹 Redireciona para a tela principal sem precisar fazer login
            Intent intent = new Intent(FormLogin.this, Vacancies.class);
            startActivity(intent);
            finish();
        }

        loginService = new LoginService();

    }

    public void forgotPassword(View view){
        Intent intent = new Intent(FormLogin.this, ResetPassword.class);
        startActivity(intent);
    }
    public void login(View view){
        EditText emailInput = findViewById(R.id.email_input);
        String email = emailInput.getText().toString();
        EditText passwordInput = findViewById(R.id.password_input);
        String password = passwordInput.getText().toString();

        if (email.isEmpty()){
            emailInput.setError("Preencha o email");
            emailInput.requestFocus();
            return;
        }else if(password.isEmpty()){
            passwordInput.setError("Preencha a senha");
            passwordInput.requestFocus();
            return;
        }
        else{
            User user = loginService.login(email, password);

            if(user != null){
                Toast.makeText(this, "Bem vindo !", Toast.LENGTH_SHORT).show();


                SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("email", user.getEmail());
                editor.putString("password", user.getPassword());
                editor.putString("userType", user.getUserType());
                editor.apply();

                Intent intent = new Intent(FormLogin.this, Vacancies.class);
                startActivity(intent);
            }else {
                Toast.makeText(this, "Usuário não encontrado!", Toast.LENGTH_SHORT).show();
            }
        }

    }
    public void selectUser(View view){
        Intent intent = new Intent(FormLogin.this, SelectUser.class);
        startActivity(intent);
    }
    public void closeApp( View view){

    }
}