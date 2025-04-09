package com.activities;

import android.content.Intent;
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
import com.models.Candidate;
import com.models.MaskEditText;
import com.models.User;
import com.models.UserType;
import com.services.CandidateService;
import com.services.LoginService;


public class UserRegister extends AppCompatActivity {

    private CandidateService candidateService;

    private LoginService loginService;

    private EditText nameInput, phoneInput, cpfInput, emailInput, passwordInput, confirmPasswordInput;



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
        nameInput = findViewById(R.id.name_input);
        phoneInput = findViewById(R.id.phone_number_input);
        cpfInput = findViewById(R.id.cpf_input);
        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);
        confirmPasswordInput = findViewById(R.id.confirm_password_input);
        cpfInput.addTextChangedListener(MaskEditText.mask(cpfInput, "###.###.###-##"));
        phoneInput.addTextChangedListener(MaskEditText.mask(phoneInput, "(##) #####-####"));

        candidateService = new CandidateService();
        loginService = new LoginService();
    }
    public void registerUser(View view){

        String name = nameInput.getText().toString();
        String phone = phoneInput.getText().toString();
        String cpf = cpfInput.getText().toString();
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();
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
        User user = new User(email, password, UserType.CANDIDATE);


        // Cadastro do candidato e do usuário

        loginService.registerUser(user, new LoginService.UserCallback() {
            @Override
            public void onSuccess(int userId) {
                System.out.println(userId);

                Candidate candidate = new Candidate(name, cpf, phone, userId);


                candidateService.registerCandidate(candidate, new CandidateService.registerCallback() {
                    @Override
                    public void onSuccess() {
                        runOnUiThread(() -> {

                            Toast.makeText(UserRegister.this, "Usuário cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
                            SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("email", user.getEmail());
                            editor.putString("type", user.getType().getValue());
                            editor.putString("name", candidate.getName());
                            editor.putString("cpf", candidate.getCpf());
                            editor.putString("phone", candidate.getPhoneNumber());
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