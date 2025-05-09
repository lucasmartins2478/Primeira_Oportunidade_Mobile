package com.activities;

import static com.models.UserType.CANDIDATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.fragments.LoadingDialogFragment;
import com.models.Candidate;
import com.models.MaskEditText;
import com.models.User;
import com.models.UserType;
import com.services.CandidateService;
import com.services.LoginService;


public class UserRegister extends AppCompatActivity {

    private CandidateService candidateService;
    private LoginService loginService;
    private TextView haveAccount;
    private int candidateId;

    LoadingDialogFragment loadingDialog;
    private AppCompatButton changePassword, registerBtn;
    private EditText nameInput, phoneInput, cpfInput, emailInput, passwordInput, confirmPasswordInput;
    private LinearLayout confirmPasswordContainer, passwordContainer;



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

        loadingDialog = new LoadingDialogFragment();

        nameInput = findViewById(R.id.name_input);
        phoneInput = findViewById(R.id.phone_number_input);
        cpfInput = findViewById(R.id.cpf_input);
        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);
        confirmPasswordInput = findViewById(R.id.confirm_password_input);
        confirmPasswordContainer = findViewById(R.id.confirm_password_container);
        passwordContainer = findViewById(R.id.password_container);
        haveAccount = findViewById(R.id.have_account);
        changePassword = findViewById(R.id.change_password);
        registerBtn = findViewById(R.id.register_button);
        cpfInput.addTextChangedListener(MaskEditText.mask(cpfInput, "###.###.###-##"));
        phoneInput.addTextChangedListener(MaskEditText.mask(phoneInput, "(##) #####-####"));

        candidateService = new CandidateService();
        loginService = new LoginService();

        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        candidateId = prefs.getInt("candidateId", -1);

        if (candidateId != -1) {
            confirmPasswordContainer.setVisibility(View.GONE);
            passwordContainer.setVisibility(View.GONE);
            haveAccount.setVisibility(View.GONE);
            changePassword.setVisibility(View.VISIBLE);
            registerBtn.setText("Salvar alterações");
            loadCandidateData();
        }
    }

    public void changePassword(View view){

    }

    public void registerUser(View view){

        loadingDialog.show(getSupportFragmentManager(), "loading");

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
        if(candidateId == -1){
            if (password.isEmpty()) {
                passwordInput.setError("Preencha a senha");
                passwordInput.requestFocus();
                return;
            }
            if (confirmPassword.isEmpty()){
                confirmPasswordInput.setError("Confirme a senha");
                confirmPasswordInput.requestFocus();
                return;
            }
        }




        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = prefs.getInt("userId", -1);

        // Criar o objeto User e Candidate
        User user = new User(email, password, CANDIDATE);


        if (candidateId != -1) {
            // Atualizar candidato

            Log.d("Update", "Se mostrou isso entrou no update");
            Candidate updated = new Candidate(
                    name,
                    cpf,
                    phone,
                    userId
            );

            Log.d("CandidateUpdate", "Updated Candidate - Name: " + updated.getName() +
                    ", CPF: " + updated.getCpf() +
                    ", Phone: " + updated.getPhoneNumber() +
                    ", UserID: " + updated.getUserId());


            candidateService.updateCandidate(updated, new CandidateService.registerCallback() {
                @Override
                public void onSuccess(Candidate candidate) {
                    runOnUiThread(() -> {
                        Toast.makeText(UserRegister.this, "Dados atualizados com sucesso!", Toast.LENGTH_SHORT).show();
                        loadingDialog.dismiss();
                        finish(); // ou voltar pra tela anterior
                    });
                }

                @Override
                public void onFailure(String error) {
                    runOnUiThread(() ->
                            Toast.makeText(UserRegister.this, "Erro ao atualizar: " + error, Toast.LENGTH_SHORT).show());
                            loadingDialog.dismiss();
                }
            });
        } else if (candidateId == -1){
            loginService.registerUser(user, new LoginService.UserCallback() {
                @Override
                public void onSuccess(int userId) {
                    Log.d("UserRegister", "Novo userId: " + userId);

                    Candidate candidate = new Candidate(name, cpf, phone, userId);


                    candidateService.registerCandidate(candidate, new CandidateService.registerCallback() {
                        @Override
                        public void onSuccess(Candidate candidate) {
                            runOnUiThread(() -> {
                                // Tudo o que você tem aqui
                                SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putInt("userId", userId);
                                editor.putInt("candidateId", candidate.getId());
                                editor.putString("email", user.getEmail());
                                editor.putString("type", user.getType().getValue());
                                editor.putString("name", candidate.getName());
                                editor.putString("cpf", candidate.getCpf());
                                editor.putString("phone", candidate.getPhoneNumber());
                                editor.putBoolean("isLoggedIn", true);

                                editor.apply();

                                // Agora chama o login
                                loginService.login(email, password, new LoginService.LoginCallback() {
                                    @Override
                                    public void onSuccess(User user) {
                                        runOnUiThread(() -> {
                                            loadingDialog.dismiss();
                                            Toast.makeText(UserRegister.this, "Usuário cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(UserRegister.this, Vacancies.class);
                                            startActivity(intent);

                                            finish();
                                        });
                                    }

                                    @Override
                                    public void onFailure(String errorMessage) {
                                        runOnUiThread(() -> {
                                            loadingDialog.dismiss();
                                                    Toast.makeText(UserRegister.this, "Erro ao fazer login após o cadastro: " + errorMessage, Toast.LENGTH_LONG).show();
                                                }
                                        );
                                    }
                                });
                            });

                        }


                        @Override
                        public void onFailure(String error) {
                            runOnUiThread(() -> {
                                Toast.makeText(UserRegister.this, "Erro ao cadastrar candidato: " + error, Toast.LENGTH_SHORT).show();
                                loadingDialog.dismiss();
                            });
                        }
                    });

                }

                @Override
                public void onFailure(String error) {

                    runOnUiThread(() -> {
                        Toast.makeText(UserRegister.this, "Erro ao cadastrar candidato: " + error, Toast.LENGTH_SHORT).show();
                        loadingDialog.dismiss();
                    });
                }
            });
        }


    }

    private void loadCandidateData() {

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String email = sharedPreferences.getString("email", "Email não encontrado");
        String name  = sharedPreferences.getString("name", "Nome não encontrado");
        String phone = sharedPreferences.getString("phone", "Telefone não encontrado");
        String cpf = sharedPreferences.getString("cpf", "CPF não encontrado");


        emailInput.setText(email);
        nameInput.setText(name);
        phoneInput.setText(phone);
        cpfInput.setText(cpf);





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