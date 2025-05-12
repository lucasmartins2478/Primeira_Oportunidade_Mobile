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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.fragments.LoadingDialogFragment;
import com.models.Curriculum;
import com.services.CandidateService;
import com.services.CompanyService;
import com.services.CurriculumService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class AdditionalDataRegister extends AppCompatActivity {

    LoadingDialogFragment loadingDialog;
    private static final int PICK_FILE_REQUEST_CODE = 101;
    private Uri selectedFileUri = null;
    private String selectedFileName = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_additional_data_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        loadingDialog = new LoadingDialogFragment();
        Spinner interestAreaSpinner = findViewById(R.id.interest_area_spinner);
        ArrayAdapter<CharSequence> interestAreaAdapter = ArrayAdapter.createFromResource(this,
                R.array.interest_area_options, R.layout.spinner_item);
        interestAreaAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        interestAreaSpinner.setAdapter(interestAreaAdapter);

        AppCompatButton selectLogoButton = findViewById(R.id.curriculum_input);

        selectLogoButton.setOnClickListener(view1 -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(Intent.createChooser(intent, "Selecione o arquivo"), PICK_FILE_REQUEST_CODE);
        });





    }



    public void finishCurriculumRegister(View view){

        loadingDialog.show(getSupportFragmentManager(), "loading");
        EditText descriptionInput = findViewById(R.id.about_you_input);

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        String token = sharedPreferences.getString("token", "nanhum token encontrado");

        String description = descriptionInput.getText().toString();
        String interestArea = ((Spinner) findViewById(R.id.interest_area_spinner)).getSelectedItem().toString();
        String attached = selectedFileName != null ? selectedFileName : "";


        Curriculum additionalData = new Curriculum(attached, description, interestArea );


        CurriculumService.additionalData(this, additionalData, token, new CurriculumService.CurriculumCallback() {
            @Override
            public void onSuccess() {
                CandidateService.addCurriculumToCandidate(AdditionalDataRegister.this,token,  new CompanyService.RegisterCallback() {
                    @Override
                    public void onSuccess() {

                        loadingDialog.dismiss();


                        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                        int candidateId = sharedPreferences.getInt("candidateId", -1);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt("curriculumId", candidateId);
                        editor.apply(); // <- ESSENCIAL


                        Toast.makeText(AdditionalDataRegister.this, "Dados adicionais enviados. Cadastro finalizado", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AdditionalDataRegister.this, Vacancies.class);
                        startActivity(intent);
                        finish(); // <- para evitar que o usuário volte para essa tela com o botão de voltar

                    }

                    @Override
                    public void onFailure(String error) {

                        Log.d("ERRO", "Erro ao cadastrar dados adicionais "+error);
                        Toast.makeText(AdditionalDataRegister.this, "Erro ao cadastrar dados adicionais :" +error, Toast.LENGTH_SHORT).show();
                        loadingDialog.dismiss();
                    }
                });

            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(AdditionalDataRegister.this, "Erro ao finalizar cadastro: "+errorMessage, Toast.LENGTH_SHORT).show();
            }
        });


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
            File file = new File(getFilesDir(), "candidate_curriculum.jpg"); // Nome fixo
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

                AppCompatButton selectLogoButton = findViewById(R.id.curriculum_input);
                selectLogoButton.setText(selectedFileName); // Define o nome do arquivo como texto no botão
                selectLogoButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null); // Remove o ícone
            }
        }
    }


    public void onBackPressed(View view){
        finish();
    }

}