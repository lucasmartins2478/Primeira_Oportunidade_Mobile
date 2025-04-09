package com.services;

import com.models.Candidate;
import com.models.Vacancy;

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

//    public VacancyService(){
//        client = new OkHttpClient();
//
//    }
//
//
//
//    public void fetchVacanciesFromApi(VacancyCallback callback){
//
//        new Thread(() -> {
//            try {
//                String url = apiUrl + "ies";
//
//
//                Request request = new Request.Builder()
//                        .url(url)
//                        .build();
//
//                try (Response response = client.newCall(request).execute()) {
//                    if (response.isSuccessful() && response.body() != null) {
//                        String responseData = response.body().string();
//                        JSONObject vacancyJson = new JSONObject(responseData);
//
//
//                        Vacancy vacancy = new Vacancy(
//                                vacancyJson.getInt("id"),
//                                vacancyJson.getString("title"),
//                                vacancyJson.getString("description"),
//                                vacancyJson.getString("aboutCompany"),
//                                vacancyJson.getString("benefits"),
//                                vacancyJson.getString("requirements"),
//                                vacancyJson.getString("modality"),
//                                vacancyJson.getString("locality"),
//                                vacancyJson.getString("uf"),
//                                vacancyJson.getString("contact"),
//                                vacancyJson.getString("salary"),
//                                vacancyJson.getString("level"),
//                                vacancyJson.getInt("companyId"),
//                                vacancyJson.getBoolean("isActive"),
//                                vacancyJson.getBoolean("isFilled")
//
//                        );
//
//
//                        callback.onSuccess();
//                    } else {
//                        callback.onFailure("Erro ao buscar vagas: " + response.code());
//                    }
//                }
//            } catch (Exception e) {
//                callback.onFailure("Erro ao conectar: " + e.getMessage());
//            }
//        }).start();
//
//    }
//
//
//    public interface VacancyCallback{
//        public void onSuccess(ArrayList<Vacancy>);
//
//        public void onFailure(String error);
//    }
}
