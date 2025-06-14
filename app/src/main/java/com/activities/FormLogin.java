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

import com.fragments.LoadingDialogFragment;
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

    private LoadingDialogFragment loadingDialog;


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
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        loadingDialog = new LoadingDialogFragment();


        if (isLoggedIn) {
            String userType = sharedPreferences.getString("type", null);
            Intent intent;
            if (userType != null) {
                if (userType.equals("candidate")) {
                    intent = new Intent(FormLogin.this, Vacancies.class);
                } else if (userType.equals("company")) {
                    intent = new Intent(FormLogin.this, MyVacancies.class);
                } else {
                    intent = null;
                }

                if (intent != null) {
                    startActivity(intent);
                    finish(); // finaliza tela de login
                    return;
                }
            }
        }

        Log.d("LOGIN_STATUS", "isLoggedIn: " + isLoggedIn);

        loginService = new LoginService();
        candidateService = new CandidateService();
        companyService = new CompanyService();
    }
    public void forgotPassword(View view){
        Intent intent = new Intent(FormLogin.this, ResetPassword.class);
        startActivity(intent);
    }
    public void login(View view) {
        loadingDialog.show(getSupportFragmentManager(), "loading");

        EditText emailInput = findViewById(R.id.email_input);
        String email = emailInput.getText().toString();
        EditText passwordInput = findViewById(R.id.password_input);
        String password = passwordInput.getText().toString();

        if (email.isEmpty()) {
            emailInput.setError("Preencha o email");
            emailInput.requestFocus();
            loadingDialog.dismiss();

            return;
        } else if (password.isEmpty()) {
            passwordInput.setError("Preencha a senha");
            passwordInput.requestFocus();
            loadingDialog.dismiss();

            return;
        } else {
            loginService.login(email, password, new LoginService.LoginCallback() {
                @Override
                public void onSuccess(User user) {
                    SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    editor.putString("token", user.getToken());

                    if (user.getType() == UserType.CANDIDATE) {
                        candidateService.fetchCandidateFromApi(user.getId(), user.getToken(), new CandidateService.CandidateCallback() {
                            @Override
                            public void onSuccess(Candidate candidate) {
                                runOnUiThread(() -> {
                                    editor.putInt("userId", user.getId());
                                    editor.putInt("candidateId", candidate.getId());
                                    editor.putString("email", user.getEmail());
                                    editor.putString("type", user.getType().getValue());
                                    editor.putString("name", candidate.getName());
                                    editor.putString("cpf", candidate.getCpf());
                                    editor.putString("phone", candidate.getPhoneNumber());
                                    if(candidate.getCurriculumId() != null){
                                        editor.putInt("curriculumId", candidate.getCurriculumId());

                                    }
                                    editor.putBoolean("isLoggedIn", true);
                                    editor.commit();  // <- garante que foi salvo antes de redirecionar

                                    Log.d("LOGIN_SAVE", "Login salvo: isLoggedIn = true");
                                    loadingDialog.dismiss();



                                    Toast.makeText(FormLogin.this, "Login realizado com sucesso!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(FormLogin.this, Vacancies.class);
                                    startActivity(intent);
                                    finish(); // opcional: encerra a tela de login
                                });
                            }

                            @Override
                            public void onFailure(String error) {
                                runOnUiThread(() -> {
                                            Toast.makeText(FormLogin.this, "Erro ao buscar candidato: " + error, Toast.LENGTH_SHORT).show();
                                            loadingDialog.dismiss();

                                        }
                                );
                            }
                        });
                    } else {
                        companyService.fetchCompanyFromApi(user.getId(), user.getToken(), new CompanyService.CompanyCallback() {
                            @Override
                            public void onSuccess(Company company) {
                                runOnUiThread(() -> {
                                    editor.putInt("userId", user.getId());
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
                                    editor.putBoolean("isLoggedIn", true);
                                    editor.commit();  // <- garante que foi salvo antes de redirecionar

                                    Log.d("LOGIN_SAVE", "Login salvo: isLoggedIn = true");


                                    loadingDialog.dismiss();

                                    Toast.makeText(FormLogin.this, "Login realizado com sucesso!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(FormLogin.this, MyVacancies.class);
                                    startActivity(intent);
                                    finish(); // opcional: encerra a tela de login
                                });
                            }

                            @Override
                            public void onFailure(String error) {
                                runOnUiThread(() -> {
                                    Toast.makeText(FormLogin.this, "Erro ao buscar empresa: " + error, Toast.LENGTH_SHORT).show();
                                    loadingDialog.dismiss();

                                });
                            }
                        });
                    }
                }

                @Override
                public void onFailure(String errorMessage) {
                    runOnUiThread(() -> {
                        Toast.makeText(FormLogin.this, errorMessage, Toast.LENGTH_SHORT).show();
                        loadingDialog.dismiss();

                    });
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