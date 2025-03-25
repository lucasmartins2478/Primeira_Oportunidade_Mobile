package com.activities;

import android.content.Intent;
import android.content.SharedPreferences;
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
import com.services.LoginService;


public class UserRegister extends AppCompatActivity {

    private CandidateService candidateService;

    private LoginService loginService;

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
        loginService = new LoginService();
    }
    public void registerUser(View view){
        // Captura os dados dos campos de entrada
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

        // Validação dos dados
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

        // Criar o objeto User e Candidate
        User user = new User(email, password, "user");  // "user" pode ser alterado conforme seu caso


        // Cadastro do candidato e do usuário

        loginService.registerUser(user, new LoginService.UserCallback() {
            @Override
            public void onSuccess() {

                Candidate candidate = new Candidate(name, phone, cpf, user.getId());

                candidateService.registerCandidate(candidate, new CandidateService.registerCallback() {
                    @Override
                    public void onSuccess() {
                        runOnUiThread(() -> {
//                            loginService.login(email, password);
                            Toast.makeText(UserRegister.this, "Usuário cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
                            SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("email", user.getEmail());
                            editor.putString("password", user.getPassword());
                            editor.putString("userType", user.getUserType());
                            editor.putString("name", candidate.getName());
                            editor.putString("cpf", candidate.getCpf());
                            editor.putString("phoneNumber", candidate.getPhoneNumber());
                            editor.apply();
                            Intent intent = new Intent(UserRegister.this, Vacancies.class);
                            startActivity(intent);
                        });
                    }


                    public void onSuccess(Candidate candidate) {

                    }


                    public void onFailure(String error) {
                        runOnUiThread(() -> Toast.makeText(UserRegister.this, "Erro ao cadastrar usuário: " + error, Toast.LENGTH_SHORT).show());
                    }
                });
            }

            @Override
            public void onFailure(String error) {

                runOnUiThread(() -> Toast.makeText(UserRegister.this, "Erro ao cadastrar candidato: " + error, Toast.LENGTH_SHORT).show());
            }
        });
    }

    // Função para quando o usuário já tiver uma conta
    public void haveAccount(View view){
        Intent intent = new Intent(UserRegister.this, FormLogin.class);
        startActivity(intent);
    }

    // Função de back
    public void onBackPressed(View view){
        finish();
    }

}