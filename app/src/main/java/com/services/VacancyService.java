package com.services;

import com.models.Vacancy;
import com.models.VacancyScore;

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
                            obj.optBoolean("isActive"),
                            obj.optBoolean("isFilled"),
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



    public interface RegisterIdCallback {
        void onSuccess(int vacancyId);
        void onFailure(String error);
    }

    public void registerVacancyWithId(Vacancy vacancy, RegisterIdCallback callback) {
        new Thread(() -> {
            try {
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

                if(response.isSuccessful() && response.body() != null){
                    String responseData = response.body().string();
                    JSONObject obj = new JSONObject(responseData);
                    int vacancyId = obj.getInt("id");
                    callback.onSuccess(vacancyId);
                }
                else {
                    callback.onFailure("Erro ao cadastrar: " + response.message());
                }

            } catch (Exception e) {
                callback.onFailure("Erro: " + e.getMessage());
            }
        }).start();
    }
    public void updateVacancy(Vacancy vacancy, RegisterIdCallback callback) {
        new Thread(() -> {
            try {
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

                RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString());

                Request request = new Request.Builder().url(registerUrl+"/"+vacancy.getId()).put(body).build();

                Response response = client.newCall(request).execute();

                if(response.isSuccessful() && response.body() != null){
                    String responseData = response.body().string();
                    JSONObject obj = new JSONObject(responseData);
                    int vacancyId = obj.getInt("id");
                    callback.onSuccess(vacancyId);
                }
                else {
                    callback.onFailure("Erro ao cadastrar: " + response.message());
                }

            } catch (Exception e) {
                callback.onFailure("Erro: " + e.getMessage());
            }
        }).start();
    }

    public void updateIsActiveToFalse(int vacancyId, RegisterIdCallback callback) {
        new Thread(() -> {
            try {
                JSONObject json = new JSONObject();
                json.put("isActive", false);


                RequestBody body = RequestBody.create(
                        json.toString(),
                        MediaType.get("application/json; charset=utf-8")
                );

                Request request = new Request.Builder().url(registerUrl+"IsActive/"+vacancyId).put(body).build();

                Response response = client.newCall(request).execute();

                if(response.isSuccessful() && response.body() != null){
                    String responseData = response.body().string();
                    JSONObject obj = new JSONObject(responseData);
                    callback.onSuccess(vacancyId);
                }
                else {
                    callback.onFailure("Erro ao cadastrar: " + response.message());
                }

            } catch (Exception e) {
                callback.onFailure("Erro: " + e.getMessage());
            }
        }).start();
    }
    public void updateIsFilledToTrue(int vacancyId, RegisterIdCallback callback) {
        new Thread(() -> {
            try {
                JSONObject json = new JSONObject();
                json.put("isFilled", true);


                RequestBody body = RequestBody.create(
                        json.toString(),
                        MediaType.get("application/json; charset=utf-8")
                );

                Request request = new Request.Builder().url(registerUrl+"IsFilled/"+vacancyId).put(body).build();

                Response response = client.newCall(request).execute();

                if(response.isSuccessful() && response.body() != null){
                    String responseData = response.body().string();
                    JSONObject obj = new JSONObject(responseData);
                    callback.onSuccess(vacancyId);
                }
                else {
                    callback.onFailure("Erro ao cadastrar: " + response.message());
                }

            } catch (Exception e) {
                callback.onFailure("Erro: " + e.getMessage());
            }
        }).start();
    }

    public void recomendVacancies(
            JSONObject candidato,
            JSONArray cursos,
            JSONArray competencias,
            VacancyCallback callback
    ) {
        fetchVacanciesFromApi(new VacancyCallback() {
            @Override
            public void onSuccess(ArrayList<Vacancy> allVagas) {
                ArrayList<VacancyScore> scoredVagas = new ArrayList<>();

                for (Vacancy vaga : allVagas) {
                    if (!vaga.isActive() || vaga.isFilled()) continue;

                    int score = 0;

                    // Localidade
                    if (vaga.getUf().equalsIgnoreCase(candidato.optString("uf"))) {
                        score += 2;
                    }

                    // Interesse na área
                    String interesse = candidato.optString("interestArea").toLowerCase();
                    if (vaga.getTitle().toLowerCase().contains(interesse) || vaga.getDescription().toLowerCase().contains(interesse)) {
                        score += 3;
                    }

                    // Competências
                    for (int i = 0; i < competencias.length(); i++) {
                        String comp = competencias.optJSONObject(i).optString("name", "").toLowerCase();
                        if (vaga.getRequirements().toLowerCase().contains(comp) || vaga.getDescription().toLowerCase().contains(comp)) {
                            score += 3;
                        }
                    }

                    // Cursos
                    for (int i = 0; i < cursos.length(); i++) {
                        String curso = cursos.optJSONObject(i).optString("name", "").toLowerCase();
                        if (vaga.getRequirements().toLowerCase().contains(curso) || vaga.getDescription().toLowerCase().contains(curso)) {
                            score += 2;
                        }
                    }

                    scoredVagas.add(new VacancyScore(vaga, score));
                }

                // Ordenar pela pontuação
                scoredVagas.sort((a, b) -> Integer.compare(b.score, a.score));

                // Extrair só as vagas
                ArrayList<Vacancy> recomendadas = new ArrayList<>();
                for (VacancyScore vs : scoredVagas) {
                    recomendadas.add(vs.vaga);
                }

                callback.onSuccess(recomendadas);
            }

            @Override
            public void onFailure(String error) {
                callback.onFailure(error);
            }
        });
    }


    public interface VacancyCallback {
        void onSuccess(ArrayList<Vacancy> vacancies);
        void onFailure(String error);
    }
}
