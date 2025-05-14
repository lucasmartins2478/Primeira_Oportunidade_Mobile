package com.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.fragments.LoadingDialogFragment;
import com.models.Candidate;
import com.models.Company;
import com.models.MaskEditText;
import com.models.User;
import com.models.UserType;
import com.services.CandidateService;
import com.services.CompanyService;
import com.services.LoginService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class CompanyRegister extends AppCompatActivity {

    private CompanyService companyService;
    private LoginService loginService;

    private int companyId;
    private TextView haveAccount;

    LoadingDialogFragment loadingDialog;

    private LinearLayout confirmPasswordContainer, passwordContainer;

    AppCompatButton registerBtn, changePassword;

    private static final int PICK_FILE_REQUEST_CODE = 101;
    private Uri selectedFileUri = null;
    private String selectedFileName = null;



    private EditText companyNameInput, cnpjInput, emailInput, phoneInput, responsibleInput, websiteInput, cityInput, cepInput, addressInput, addressNumberInput, passwordInput, confirmPasswordInput;

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

        loadingDialog = new LoadingDialogFragment();

        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        companyId = prefs.getInt("companyId", -1);
        Log.d("CompanyID", "companyId: "+companyId);

        registerBtn = findViewById(R.id.register_button);


        companyNameInput = findViewById(R.id.compay_name_input);
        cnpjInput = findViewById(R.id.cnpj_input);
        emailInput = findViewById(R.id.email_input);
        phoneInput = findViewById(R.id.phone_number_input);
        responsibleInput = findViewById(R.id.responsible_input);
        websiteInput = findViewById(R.id.website_input);
        cityInput = findViewById(R.id.city_input);
        cepInput = findViewById(R.id.cep_input);
        changePassword = findViewById(R.id.change_password);
        confirmPasswordContainer = findViewById(R.id.confirm_password_container);
        passwordContainer = findViewById(R.id.password_container);

        addressInput = findViewById(R.id.address_input);
        addressNumberInput = findViewById(R.id.address_number_input);
        passwordInput = findViewById(R.id.password_input);
        confirmPasswordInput = findViewById(R.id.confirm_password_input);
        haveAccount = findViewById(R.id.have_account);

        phoneInput.addTextChangedListener(MaskEditText.mask(phoneInput, "(##) #####-####"));
        cnpjInput.addTextChangedListener(MaskEditText.mask(cnpjInput, "##.###.###/####-##"));
        cepInput.addTextChangedListener(MaskEditText.mask(cepInput, "#####-###"));


        AppCompatButton selectLogoButton = findViewById(R.id.select_logo_button);

        selectLogoButton.setOnClickListener(view1 -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(Intent.createChooser(intent, "Selecione o arquivo"), PICK_FILE_REQUEST_CODE);
        });

        Spinner segmentSpinner = findViewById(R.id.segment_spinner);

        ArrayAdapter<CharSequence> segmentAdapter = ArrayAdapter.createFromResource(this,
                R.array.segment_options, R.layout.spinner_item);
        segmentAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        segmentSpinner.setAdapter(segmentAdapter);


        Spinner ufSpinner = findViewById(R.id.uf_spinner);
        ArrayAdapter<CharSequence> ufAdapter = ArrayAdapter.createFromResource(this,
                R.array.uf_options, R.layout.spinner_item);
        ufAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        ufSpinner.setAdapter(ufAdapter);



        if(companyId != -1){
            changePassword.setVisibility(View.VISIBLE);
            confirmPasswordContainer.setVisibility(View.GONE);
            passwordContainer.setVisibility(View.GONE);
            registerBtn.setText("Salvar alterações");
            haveAccount.setVisibility(View.GONE);
            loadCompanyData();

        }


        companyService = new CompanyService();
        loginService = new LoginService();
    }

    public void changePassword(View view){

    }

    public void registerCompany(View view) {

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        String token = sharedPreferences.getString("token", "Nenhum token encontrado");

        loadingDialog.show(getSupportFragmentManager(), "loading");

        String companyName = companyNameInput.getText().toString().trim();
        String cnpj = cnpjInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String phoneNumber = phoneInput.getText().toString().trim();
        String responsible = responsibleInput.getText().toString().trim();
        String website = websiteInput.getText().toString().trim();
        String city = cityInput.getText().toString().trim();
        String cep = cepInput.getText().toString().trim();
        String segment = ((Spinner) findViewById(R.id.segment_spinner)).getSelectedItem().toString();
        String uf = ((Spinner) findViewById(R.id.uf_spinner)).getSelectedItem().toString();
        String address = addressInput.getText().toString().trim();
        String addressNumber = addressNumberInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String confirmPassword = confirmPasswordInput.getText().toString().trim();



        String logo = selectedFileName != null ? selectedFileName : "";


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
        int addressNumberInt = Integer.parseInt(addressNumber);


        if(companyId == -1){
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
        }


        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = prefs.getInt("userId", -1);

        User user = new User(email, password, UserType.COMPANY);


        if(companyId != -1){
//            Company company = new Company(companyName, cnpj, segment, responsible, phoneNumber,
//                    city, cep, address, addressNumberInt, uf, website, logo, userId);

            Company company = new Company();

            company.setCompanyName(companyName);
            company.setCnpj(cnpj);
            company.setSegment(segment);
            company.setResponsible(responsible);
            company.setPhoneNumber(phoneNumber);
            company.setCity(city);
            company.setCep(cep);
            company.setAddress(address);
            company.setAddressNumber(addressNumberInt);
            company.setUf(uf);
            company.setWebsite(website);
            company.setLogo(logo);
            company.setUserId(userId);

            loginService.updateUser(user, token, new LoginService.UserCallback() {
                @Override
                public void onSuccess(int userId) {

                }

                @Override
                public void onFailure(String error) {

                }
            });

            companyService.updateCompany(company,token, new CompanyService.RegisterCallback() {
                @Override
                public void onSuccess() {
                    runOnUiThread(() -> {
                        Toast.makeText(CompanyRegister.this, "Dados atualizados com sucesso!", Toast.LENGTH_SHORT).show();
                        loadingDialog.dismiss();
                        finish(); // ou voltar pra tela anterior
                    });
                }

                @Override
                public void onFailure(String error) {
                    runOnUiThread(() ->
                            Toast.makeText(CompanyRegister.this, "Erro ao atualizar: " + error, Toast.LENGTH_SHORT).show());
                            loadingDialog.dismiss();

                }
            });
        }else if(companyId == -1){
            loginService.registerUser(user, new LoginService.UserCallback() {
                @Override
                public void onSuccess(int userId) {


//                    Company company = new Company(companyName, cnpj, segment, responsible, phoneNumber,
//                            city, cep, address, addressNumberInt, uf, website, logo, userId);

                    Company company = new Company();

                    company.setCompanyName(companyName);
                    company.setCnpj(cnpj);
                    company.setSegment(segment);
                    company.setResponsible(responsible);
                    company.setPhoneNumber(phoneNumber);
                    company.setCity(city);
                    company.setCep(cep);
                    company.setAddress(address);
                    company.setAddressNumber(addressNumberInt);
                    company.setUf(uf);
                    company.setWebsite(website);
                    company.setLogo(logo);
                    company.setUserId(userId);


                    loginService.login(email, password, new LoginService.LoginCallback() {
                        @Override
                        public void onSuccess(User loggedUser) {
                            String token = loggedUser.getToken();



                            companyService.registerCompany(company, token, new CompanyService.CompanyCallback() {
                                @Override
                                public void onSuccess(Company fetchedCompany) {
                                    runOnUiThread(() -> {
                                        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putString("token", token); // <- salva token aqui também
                                        editor.putInt("companyId", fetchedCompany.getId()); // SALVANDO companyId agora
                                        editor.putInt("userId", userId);
                                        editor.putInt("companyId", fetchedCompany.getId()); // <-- Aqui está o ID que você precisa
                                        editor.putString("email", user.getEmail());
                                        editor.putString("type", user.getType().getValue());
                                        editor.putString("name", fetchedCompany.getCompanyName());
                                        editor.putString("cnpj", fetchedCompany.getCnpj());
                                        editor.putString("segment", fetchedCompany.getSegment());
                                        editor.putString("responsible", fetchedCompany.getResponsible());
                                        editor.putString("phone", fetchedCompany.getPhoneNumber());
                                        editor.putString("city", fetchedCompany.getCity());
                                        editor.putString("cep", fetchedCompany.getCep());
                                        editor.putString("address", fetchedCompany.getAddress());
                                        editor.putInt("addressNumber", fetchedCompany.getAddressNumber());
                                        editor.putString("uf", fetchedCompany.getUf());
                                        editor.putString("url", fetchedCompany.getWebsite());
                                        editor.putString("logo", fetchedCompany.getLogo());
                                        editor.putBoolean("isLoggedIn", true);

                                        editor.apply();

                                        loadingDialog.dismiss();
                                        Toast.makeText(CompanyRegister.this, "Usuário cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(CompanyRegister.this, Vacancies.class);
                                        startActivity(intent);

                                        finish();
                                    });
                                }

                                @Override
                                public void onFailure(String error) {
                                    runOnUiThread(() -> {
                                        loadingDialog.dismiss();
                                        Toast.makeText(CompanyRegister.this, "Erro ao cadastrar candidato: " + error, Toast.LENGTH_SHORT).show();
                                    });
                                }
                            });
                        }

                        @Override
                        public void onFailure(String errorMessage) {
                            runOnUiThread(() -> {
                                loadingDialog.dismiss();
                                Toast.makeText(CompanyRegister.this, "Erro ao fazer login após o cadastro: " + errorMessage, Toast.LENGTH_LONG).show();
                            });
                        }
                    });




                }

                @Override
                public void onFailure(String error) {
                    runOnUiThread(() -> Toast.makeText(CompanyRegister.this, "Erro ao cadastrar empresa: " + error, Toast.LENGTH_SHORT).show());
                    loadingDialog.dismiss();


                }
            });

        }

    }

    @SuppressLint("Range")
    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                if (cursor != null) cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private String saveImageLocally(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            File file = new File(getFilesDir(), "company_logo.jpg"); // Nome fixo
            OutputStream outputStream = new FileOutputStream(file);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.close();
            inputStream.close();

            return file.getAbsolutePath(); // Esse path será salvo no SharedPreferences
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }




    private void loadCompanyData() {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        String name = prefs.getString("name", "");
        String cnpj = prefs.getString("cnpj", "");
        String email = prefs.getString("email", "");
        String phone = prefs.getString("phone", "");
        String responsible = prefs.getString("responsible", "");
        String website = prefs.getString("url", "");
        String city = prefs.getString("city", "");
        String cep = prefs.getString("cep", "");
        String address = prefs.getString("address", "");
        int addressNumber = prefs.getInt("addressNumber", -1);
        String uf = prefs.getString("uf", "");
        String segment = prefs.getString("segment", "");
        String logo = prefs.getString("logo", "");

        companyNameInput.setText(name);
        cnpjInput.setText(cnpj);
        emailInput.setText(email);
        phoneInput.setText(phone);
        responsibleInput.setText(responsible);
        websiteInput.setText(website);
        cityInput.setText(city);
        cepInput.setText(cep);
        addressInput.setText(address);
        addressNumberInput.setText(addressNumber != -1 ? String.valueOf(addressNumber) : "");


        Spinner segmentSpinner = findViewById(R.id.segment_spinner);
        Spinner ufSpinner = findViewById(R.id.uf_spinner);

        // Setando valor do Spinner de Segmento
        segmentSpinner.post(() -> {
            ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) segmentSpinner.getAdapter();
            for (int i = 0; i < adapter.getCount(); i++) {
                if (segment.equalsIgnoreCase(adapter.getItem(i).toString())) {
                    segmentSpinner.setSelection(i);
                    break;
                }
            }
        });

        ufSpinner.post(() -> {
            ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) ufSpinner.getAdapter();
            for (int i = 0; i < adapter.getCount(); i++) {
                if (uf.equalsIgnoreCase(adapter.getItem(i).toString())) {
                    ufSpinner.setSelection(i);
                    break;
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_FILE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            selectedFileUri = data.getData();

            if (selectedFileUri != null) {
                selectedFileName = getFileName(selectedFileUri);
                String savedPath = saveImageLocally(selectedFileUri);
                if (savedPath != null) {
                    SharedPreferences.Editor editor = getSharedPreferences("UserPrefs", MODE_PRIVATE).edit();
                    editor.putString("companyLogoPath", savedPath);
                    editor.apply();
                    Log.d("CompanyRegister", "Logo salva em: " + savedPath);
                } else {
                    Log.d("CompanyRegister", "Falha ao salvar imagem localmente.");
                }

                Toast.makeText(this, "Arquivo selecionado: " + selectedFileName, Toast.LENGTH_SHORT).show();
            }
        }
    }




    public void haveAccount(View view) {
        Intent intent = new Intent(CompanyRegister.this, FormLogin.class);
        startActivity(intent);
    }

    public void onBackPressed(View view) {
        finish();
    }
}



