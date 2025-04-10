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

import com.models.Candidate;
import com.models.Company;
import com.models.User;
import com.models.UserType;
import com.services.CandidateService;
import com.services.CompanyService;
import com.services.LoginService;

import java.util.List;

public class FormLogin extends AppCompatActivity {

    private LoginService loginService;

    private CandidateService candidateService;

    private CompanyService companyService;

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
        candidateService = new CandidateService();
        companyService = new CompanyService();
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
            loginService.login(email, password, new LoginService.LoginCallback() {
                @Override
                public void onSuccess(User user) {
                    if (user.getType() == UserType.CANDIDATE){
                        candidateService.fetchCandidateFromApi(user.getId(), new CandidateService.CandidateCallback() {
                            @Override
                            public void onSuccess(Candidate candidate) {
                                runOnUiThread(() -> {
                                    SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putInt("candidateId", candidate.getId());
                                    editor.putString("email", user.getEmail());
                                    editor.putString("type", user.getType().getValue());
                                    editor.putString("name", candidate.getName());
                                    editor.putString("cpf", candidate.getCpf());
                                    editor.putString("phone", candidate.getPhoneNumber());
                                    editor.apply();

                                    Toast.makeText(FormLogin.this, "Login realizado com sucesso!", Toast.LENGTH_SHORT).show(); // ✅ agora pode usar Toast aqui

                                    Intent intent = new Intent(FormLogin.this, Vacancies.class);
                                    startActivity(intent);
                                    finish(); // opcional: encerra a tela de login
                                });
                            }

                            @Override
                            public void onFailure(String error) {
                                runOnUiThread(() -> Toast.makeText(FormLogin.this, "Erro ao buscar candidato: " + error, Toast.LENGTH_SHORT).show());
                            }
                        });
                    } else {
                        companyService.fetchCompanyFromApi(user.getId(), new CompanyService.CompanyCallback() {
                            @Override
                            public void onSuccess(Company company) {
                                runOnUiThread(()->{
                                    SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putInt("companyId", company.getId());
                                    editor.putString("email", user.getEmail());
                                    editor.putString("type", user.getType().getValue());
                                    editor.putString("name", company.getCompanyName());
                                    editor.putString("cnpj", company.getCnpj());
                                    editor.putString("segment", company.getSegment());
                                    editor.putString("responsible", company.getResponsible());
                                    editor.putString("phone", company.getPhoneNumber());
                                    editor.putString("city", company.getCity());
                                    editor.putString("cep", company.getCep());
                                    editor.putString("address", company.getAddress());
                                    editor.putInt("addressNumber", company.getAddressNumber());
                                    editor.putString("uf", company.getUf());
                                    editor.putString("url", company.getWebsite());
                                    editor.putString("logo", company.getLogo());
                                    editor.apply();

                                    Toast.makeText(FormLogin.this, "Login realizado com sucesso!", Toast.LENGTH_SHORT).show(); // ✅ agora pode usar Toast aqui

                                    Intent intent = new Intent(FormLogin.this, MyVacancies.class);
                                    startActivity(intent);
                                    finish(); // opcional: encerra a tela de login
                                });


                            }

                            @Override
                            public void onFailure(String error) {
                                runOnUiThread(() -> Toast.makeText(FormLogin.this, "Erro ao buscar empresa: " + error, Toast.LENGTH_SHORT).show());
                            }
                        });
                    }
                }

                @Override
                public void onFailure(String errorMessage) {
                    runOnUiThread(() -> Toast.makeText(FormLogin.this, errorMessage, Toast.LENGTH_SHORT).show());
                }
            });

        }
    }

    public void selectUser(View view){
        Intent intent = new Intent(FormLogin.this, SelectUser.class);
        startActivity(intent);
    }
    public void closeApp( View view){

    }
}