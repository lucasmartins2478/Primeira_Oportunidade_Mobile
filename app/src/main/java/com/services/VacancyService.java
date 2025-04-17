package com.services;

import com.models.Vacancy;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class VacancyService {

    private List<Vacancy> vacancies;

    private String registerUrl = "https://backend-po.onrender.com/vacancy";
    private String fetchUrl = "https://backend-po.onrender.com/vacancies";

    private OkHttpClient client;

    public boolean registerVacancy(Vacancy vacancy){
        return true;
    }


    public VacancyService(){
        client = new OkHttpClient();
    }

    public List<Vacancy> getVacancies(){
        return vacancies;
    }



  public void fetchVacanciesFromApi(VacancyCallback callback) {
    new Thread(() -> {
        try {
            client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(fetchUrl)
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
                            obj.optBoolean("isFilled", false),
                                obj.getString("companyName")
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
                client = new OkHttpClient();

                Request request = new Request.Builder()
                        .url(fetchUrl)
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
                                        obj.optBoolean("isFilled", false),
                                        obj.getString("companyName"));
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

    public void registerVacancy(Vacancy vacancy, RegisterCallback callback){
        new Thread(()->{
            try{
                JSONObject json = new JSONObject();
                json.put("title", vacancy.getTitle());
                json.put("description", vacancy.getDescription());
                json.put("aboutCompany", vacancy.getAboutCompany());
                json.put("benefits", vacancy.getBenefits());
                json.put("requirements", vacancy.getRequirements());
                json.put("modality", vacancy.getModality());
                json.put("locality", vacancy.getLocality());
                json.put("uf", vacancy.getUf());
                json.put("contact", vacancy.getContact());
                json.put("salary", vacancy.getSalary());
                json.put("level", vacancy.getLevel());
                json.put("companyId", vacancy.getCompanyId());
                json.put("companyName", vacancy.getCompanyName());

                RequestBody body = RequestBody.create(
                        json.toString(),
                        MediaType.get("application/json; charset=utf-8")
                );

                Request request = new Request.Builder().url(registerUrl).post(body).build();

                Response response = client.newCall(request).execute();
                if(response.isSuccessful()){
                    callback.onSuccess();
                }else{
                    callback.onFailure("Erro :"+response.message());
                }



            } catch (Exception e) {
                callback.onFailure("Falha ao conectar: "+e.getMessage());
            }

        }).start();

    }




    public interface RegisterCallback {
        void onSuccess();
        void onFailure(String error);
    }
    public interface VacancyCallback {
        void onSuccess(ArrayList<Vacancy> vacancies);
        void onFailure(String error);
    }
}
