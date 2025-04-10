package com.services;

import com.models.Candidate;
import com.models.Vacancy;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class VacancyService {

    private List<Vacancy> vacancies;

    private String apiUrl = "https://backend-po.onrender.com/vacanc";

    private OkHttpClient client;

    public boolean registerVacancy(Vacancy vacancy){
        return true;
    }

    public VacancyService (){

        vacancies = new ArrayList<Vacancy>();

        vacancies.add(new Vacancy( "Front-end", "Desenvolvedor", "Facebook", "Vale alimentação", "Formado", "Remoto", "São Paulo", "SP", "facebook@gmail.com", "R$12.000,00", "Pleno", 1));
        vacancies.add(new Vacancy( "Back-end", "Desenvolvedor", "Facebook", "Vale alimentação", "Formado", "Remoto", "São Paulo", "SP", "facebook@gmail.com", "R$12.000,00", "Pleno", 1));
        vacancies.add(new Vacancy( "Fullstack", "Desenvolvedor", "Facebook", "Vale alimentação", "Formado", "Remoto", "São Paulo", "SP", "facebook@gmail.com", "R$12.000,00", "Pleno", 1));

    }

    public List<Vacancy> getVacancies(){
        return vacancies;
    }

  public void fetchVacanciesFromApi(VacancyCallback callback) {
    new Thread(() -> {
        try {
            String url = apiUrl + "ies"; // URL completa: https://backend-po.onrender.com/vacancies
            client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(url)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    String responseData = response.body().string();
                    JSONArray vacanciesJson = new JSONArray(responseData);

                    ArrayList<Vacancy> vacancies = new ArrayList<>();

                    for (int i = 0; i < vacanciesJson.length(); i++) {
                        JSONObject obj = vacanciesJson.getJSONObject(i);
                        Vacancy vacancy = new Vacancy(
                            obj.getInt("id"),
                            obj.getString("title"),
                            obj.getString("description"),
                            obj.getString("aboutCompany"),
                            obj.getString("benefits"),
                            obj.getString("requirements"),
                            obj.getString("modality"),
                            obj.getString("locality"),
                            obj.getString("uf"),
                            obj.getString("contact"),
                            obj.getString("salary"),
                            obj.getString("level"),
                            obj.getInt("companyId"),
                            obj.optBoolean("isActive", false),
                            obj.optBoolean("isFilled", false)
                        );
                        vacancies.add(vacancy);
                    }

                    callback.onSuccess(vacancies);
                } else {
                    callback.onFailure("Erro ao buscar vagas: " + response.code());
                }
            }
        } catch (Exception e) {
            callback.onFailure("Erro ao conectar: " + e.getMessage());
        }
    }).start();
}

    public void fetchVacanciesByCompanyId(int companyId, VacancyCallback callback) {
        new Thread(() -> {
            try {
                String url = "https://backend-po.onrender.com/vacancies"; // ou apiUrl se preferir
                client = new OkHttpClient();

                Request request = new Request.Builder()
                        .url(url)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (response.isSuccessful() && response.body() != null) {
                        String responseData = response.body().string();
                        JSONArray vacanciesJson = new JSONArray(responseData);

                        ArrayList<Vacancy> filteredVacancies = new ArrayList<>();

                        for (int i = 0; i < vacanciesJson.length(); i++) {
                            JSONObject obj = vacanciesJson.getJSONObject(i);

                            int currentCompanyId = obj.getInt("companyId");

                            if (currentCompanyId == companyId) {
                                Vacancy vacancy = new Vacancy(
                                        obj.getInt("id"),
                                        obj.getString("title"),
                                        obj.getString("description"),
                                        obj.getString("aboutCompany"),
                                        obj.getString("benefits"),
                                        obj.getString("requirements"),
                                        obj.getString("modality"),
                                        obj.getString("locality"),
                                        obj.getString("uf"),
                                        obj.getString("contact"),
                                        obj.getString("salary"),
                                        obj.getString("level"),
                                        currentCompanyId,
                                        obj.optBoolean("isActive", false),
                                        obj.optBoolean("isFilled", false)
                                );
                                filteredVacancies.add(vacancy);
                            }
                        }

                        callback.onSuccess(filteredVacancies);
                    } else {
                        callback.onFailure("Erro ao buscar vagas: " + response.code());
                    }
                }
            } catch (Exception e) {
                callback.onFailure("Erro ao conectar: " + e.getMessage());
            }
        }).start();
    }


public interface VacancyCallback {
    void onSuccess(ArrayList<Vacancy> vacancies);
    void onFailure(String error);
}
}
