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

import com.models.Company;
import com.models.MaskEditText;
import com.models.User;
import com.services.CompanyService;
import com.services.LoginService;

public class CompanyRegister extends AppCompatActivity {

    private CompanyService companyService;
    private LoginService loginService;

    private EditText companyNameInput, cnpjInput, segmentInput, emailInput, phoneInput, responsibleInput, websiteInput, cityInput, cepInput,ufInput, addressInput, addressNumberInput, passwordInput, confirmPasswordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_company_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        companyNameInput = findViewById(R.id.compay_name_input);
        cnpjInput = findViewById(R.id.cnpj_input);
        segmentInput = findViewById(R.id.segment_input);
        emailInput = findViewById(R.id.email_input);
        phoneInput = findViewById(R.id.phone_number_input);
        responsibleInput = findViewById(R.id.responsible_input);
        websiteInput = findViewById(R.id.website_input);
        cityInput = findViewById(R.id.city_input);
        cepInput = findViewById(R.id.cep_input);
        ufInput = findViewById(R.id.uf_input);
        addressInput = findViewById(R.id.address_input);
        addressNumberInput = findViewById(R.id.address_number_input);
        passwordInput = findViewById(R.id.password_input);
        confirmPasswordInput = findViewById(R.id.confirm_password_input);

        phoneInput.addTextChangedListener(MaskEditText.mask(phoneInput, "(##) #####-####"));
        cnpjInput.addTextChangedListener(MaskEditText.mask(cnpjInput, "##.###.###/####-##"));
        cepInput.addTextChangedListener(MaskEditText.mask(cepInput, "#####-###"));




        companyService = new CompanyService();
        loginService = new LoginService();
    }



    public void registerCompany(View view) {

        String companyName = companyNameInput.getText().toString().trim();
        String cnpj = cnpjInput.getText().toString().trim();
        String segment = segmentInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String phoneNumber = phoneInput.getText().toString().trim();
        String responsible = responsibleInput.getText().toString().trim();
        String website = websiteInput.getText().toString().trim();
        String city = cityInput.getText().toString().trim();
        String cep = cepInput.getText().toString().trim();
        String uf = ufInput.getText().toString().trim();
        String address = addressInput.getText().toString().trim();
        String addressNumber = addressNumberInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String confirmPassword = confirmPasswordInput.getText().toString().trim();

        EditText logoInput = findViewById(R.id.logo_input);
        String logo = logoInput.getText().toString().trim();

        // Validação de campos obrigatórios
        if (companyName.isEmpty()) {
            companyNameInput.setError("Preencha o nome da empresa");
            companyNameInput.requestFocus();
            return;
        }
        if (cnpj.isEmpty()) {
            cnpjInput.setError("Preencha o CNPJ");
            cnpjInput.requestFocus();
            return;
        }
        if (segment.isEmpty()) {
            segmentInput.setError("Preencha o segmento");
            segmentInput.requestFocus();
            return;
        }
        if (email.isEmpty()) {
            emailInput.setError("Preencha o email corporativo");
            emailInput.requestFocus();
            return;
        }
        if (phoneNumber.isEmpty()) {
            phoneInput.setError("Preencha o telefone");
            phoneInput.requestFocus();
            return;
        }
        if (responsible.isEmpty()) {
            responsibleInput.setError("Preencha o nome do responsável");
            responsibleInput.requestFocus();
            return;
        }
        if (city.isEmpty()) {
            cityInput.setError("Preencha o nome da cidade");
            cityInput.requestFocus();
            return;
        }
        if (cep.isEmpty()) {
            cepInput.setError("Preencha o CEP");
            cepInput.requestFocus();
            return;
        }
        if (uf.isEmpty()) {
            ufInput.setError("Preencha o nome do estado");
            ufInput.requestFocus();
            return;
        }
        if (address.isEmpty()) {
            addressInput.setError("Preencha o endereço");
            addressInput.requestFocus();
            return;
        }
        if (addressNumber.isEmpty()) {
            addressNumberInput.setError("Preencha o número do endereço");
            addressNumberInput.requestFocus();
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
                Toast.makeText(this, "As senhas não coincidem!", Toast.LENGTH_SHORT).show();
                return;
            }


        User user = new User(email, password, "company");

        loginService.registerUser(user, new LoginService.UserCallback()
         {
            @Override
            public void onSuccess() {

                Company company = new Company(companyName, cnpj, segment,  phoneNumber, responsible,
                        website, city, cep, uf, address, addressNumber, logo, user.getId());
                companyService.registerCompany(company, new CompanyService.CompanyCallback() {
                    @Override
                    public void onSuccess() {
                        runOnUiThread(() -> {
                            Toast.makeText(CompanyRegister.this, "Empresa cadastrada com sucesso!", Toast.LENGTH_SHORT).show();
                            SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("email", user.getEmail());
                            editor.putString("password", user.getPassword());
                            editor.putString("userType", user.getUserType());
                            editor.apply();
                            Intent intent = new Intent(CompanyRegister.this, MyVacancies.class);
                            startActivity(intent);
                        });
                    }

                    @Override
                    public void onFailure(String error) {
                        runOnUiThread(() -> Toast.makeText(CompanyRegister.this, "Erro ao cadastrar usuário: " + error, Toast.LENGTH_SHORT).show());
                    }
                });
            }

            @Override
            public void onFailure(String error) {
                runOnUiThread(() -> Toast.makeText(CompanyRegister.this, "Erro ao cadastrar empresa: " + error, Toast.LENGTH_SHORT).show());


            }
        });


        }


    public void haveAccount(View view){
        Intent intent = new Intent(CompanyRegister.this, FormLogin.class);
        startActivity(intent);
    }
    public void onBackPressed(View view){
        finish();
    }
    }



