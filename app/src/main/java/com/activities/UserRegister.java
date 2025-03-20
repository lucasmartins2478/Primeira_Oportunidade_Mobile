package com.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.models.Candidate;
import com.models.User;
import com.services.CandidateService;


public class UserRegister extends AppCompatActivity {

    private CandidateService candidateService;

//    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

//        progressBar = findViewById(R.id.progressBar);
        candidateService = new CandidateService();
    }
    public void registerUser(View view) {
        EditText nameInput = findViewById(R.id.name_input);
        String name = nameInput.getText().toString();

        EditText phoneInput = findViewById(R.id.phone_number_input);
        String phone = phoneInput.getText().toString();

        EditText cpfInput = findViewById(R.id.cpf_input);
        String cpf = cpfInput.getText().toString();

        EditText emailInput = findViewById(R.id.email_input);
        String email = emailInput.getText().toString();

        EditText passwordInput = findViewById(R.id.password_input);
        String password = passwordInput.getText().toString();

        EditText confirmPasswordInput = findViewById(R.id.confirm_password_input);
        String confirmPassword = confirmPasswordInput.getText().toString();

        if (name.isEmpty()) {
            nameInput.setError("Preencha o seu nome");
            nameInput.requestFocus();
            return;
        }
        if(phone.isEmpty()){
            phoneInput.setError("Preencha o telefone");
            phoneInput.requestFocus();
            return;
        }
        if(cpf.isEmpty()){
            cpfInput.setError("Preencha o CPF");
            cpfInput.requestFocus();
            return;
        }
        if (email.isEmpty()) {
            emailInput.setError("Preencha o email");
            emailInput.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            passwordInput.setError("Preencha a senha");
            passwordInput.requestFocus();
            return;
        }
        if (confirmPassword.isEmpty()) {
            confirmPasswordInput.setError("Preencha a confirmação de senha");
            confirmPasswordInput.requestFocus();
            return;
        }
        if (!password.equals(confirmPassword)) {
            Toast.makeText(UserRegister.this, "As senhas não coincidem!", Toast.LENGTH_SHORT).show();
            return;
        }

//        progressBar.setVisibility(View.VISIBLE);
//        setFormEnabled();

        Candidate candidate = new Candidate(name, phone, cpf);
        User user = new User(email, password, "user");
        candidateService.registerCandidate(candidate, new CandidateService.CandidateCallback() {
            @Override
            public void onSuccess() {
                runOnUiThread(() -> {
//                    progressBar.setVisibility(View.GONE);
//                    setFormEnabled();
                    Toast.makeText(UserRegister.this, "Usuário cadastrado!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(UserRegister.this, Vacancies.class);
                    startActivity(intent);
                });
            }

            @Override
            public void onFailure(String error) {
                runOnUiThread(() -> {
//                    progressBar.setVisibility(View.GONE);
//                    setFormEnabled();
                    Toast.makeText(UserRegister.this, "Erro ao cadastrar: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    public void haveAccount(View view){
        Intent intent = new Intent(UserRegister.this, FormLogin.class);
        startActivity(intent);
    }
    public void onBackPressed(View view){
        finish();
    }

//    private void setFormEnabled() {
//        EditText nameInput = findViewById(R.id.name_input);
//        EditText phoneInput = findViewById(R.id.phone_number_input);
//        EditText cpfInput = findViewById(R.id.cpf_input);
//        EditText emailInput = findViewById(R.id.email_input);
//        EditText passwordInput = findViewById(R.id.password_input);
//        EditText confirmPasswordInput = findViewById(R.id.confirm_password_input);
//        View submitButton = findViewById(R.id.register_button); // Adapte conforme seu ID de botão
//
//        nameInput.setVisibility(View.GONE);
//        phoneInput.setVisibility(View.GONE);
//        cpfInput.setVisibility(View.GONE);
//        emailInput.setVisibility(View.GONE);
//        passwordInput.setVisibility(View.GONE);
//        confirmPasswordInput.setVisibility(View.GONE);
//        submitButton.setVisibility(View.GONE);
//    }

}