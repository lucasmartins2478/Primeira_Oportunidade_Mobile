package com.services;

import android.util.Log;

import com.models.Company;

import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CompanyService {

    private String apiUrl = "https://backend-po.onrender.com/companies";
    private OkHttpClient client;


    public CompanyService(){
        client = new OkHttpClient();
    }
    public interface RegisterCallback{
        void onSuccess();
        void onFailure(String error);
    }

    public interface CompanyCallback{
        void onSuccess(Company company);

        void onFailure(String error);
    }

    public void fetchCompanyFromApi(int userId, CompanyCallback callback){
        new Thread(()->{
            String url = apiUrl + "/" +userId;

            Request request = new Request.Builder().url(url).build();

            try (Response response = client.newCall(request).execute()){
                if(response.isSuccessful() && response.body() != null){
                    String responseData = response.body().string();
                    JSONObject companyJson = new JSONObject(responseData);

                    String urlSite = companyJson.isNull("url") ? null : companyJson.getString("url");

                    Company company = new Company(
                            companyJson.getInt("id"),
                            companyJson.getString("name"),
                            companyJson.getString("cnpj"),
                            companyJson.getString("segment"),
                            companyJson.getString("responsible"),
                            companyJson.getString("phoneNumber"),
                            companyJson.getString("city"),
                            companyJson.getString("cep"),
                            companyJson.getString("address"),
                            companyJson.getInt("addressNumber"),
                            companyJson.getString("uf"),
                            companyJson.getString("url"),
                            companyJson.getString("logo"),
                            companyJson.getInt("userId")
                    );

                    callback.onSuccess(company);


                }else{
                    callback.onFailure("Erro ao buscar dados da empresa:"+response.code());
                }
            }
            catch (Exception e){
                callback.onFailure("Erro ao conectar: " + e.getMessage());
            }
        }).start();
    }



    public void registerCompany(Company company, RegisterCallback callback){

        new Thread(()->{
            try{
                JSONObject json = new JSONObject();
                json.put("name", company.getCompanyName());
                json.put("cnpj", company.getCnpj());
                json.put("segment", company.getSegment());
                json.put("responsible", company.getResponsible());
                json.put("phoneNumber", company.getPhoneNumber());
                json.put("city", company.getCity());
                json.put("cep", company.getCep());
                json.put("address", company.getAddress());
                json.put("addressNumber", company.getAddressNumber());
                json.put("uf", company.getUf());
                json.put("url", company.getWebsite());
                json.put("logo", company.getLogo());
                json.put("userId", company.getUserId());


                RequestBody body =  RequestBody.create(
                        json.toString(),
                        MediaType.get("application/json; charset=utf-8")
                );

                Log.d("CompanyRegisterJSON", json.toString());


                Request request = new Request.Builder().url(apiUrl).post(body).build();

                Response response = client.newCall(request).execute();

                if(response.isSuccessful()){
                    callback.onSuccess();
                }
                else{
                    String errorBody = response.body() != null ? response.body().string() : "sem corpo";
                    Log.e("CompanyRegisterError", "Erro: " + response.code() + " - " + errorBody);
                    callback.onFailure("Erro: " + response.message());                }


            }catch (Exception e){
                callback.onFailure("Faha ao conectar: "+e.getMessage());
            }
        }).start();

    }
    public void updateCompany(Company company, RegisterCallback callback){

        new Thread(()->{
            try{
                JSONObject json = new JSONObject();
                json.put("name", company.getCompanyName());
                json.put("cnpj", company.getCnpj());
                json.put("segment", company.getSegment());
                json.put("responsible", company.getResponsible());
                json.put("phoneNumber", company.getPhoneNumber());
                json.put("city", company.getCity());
                json.put("cep", company.getCep());
                json.put("address", company.getAddress());
                json.put("addressNumber", company.getAddressNumber());
                json.put("uf", company.getUf());
                json.put("url", company.getWebsite());
                json.put("logo", company.getLogo());
                json.put("userId", company.getUserId());


                RequestBody body =  RequestBody.create(
                        json.toString(),
                        MediaType.get("application/json; charset=utf-8")
                );

                Log.d("CompanyRegisterJSON", json.toString());


                Request request = new Request.Builder().url(apiUrl+"/"+company.getId()).put(body).build();

                Response response = client.newCall(request).execute();

                if(response.isSuccessful()){
                    callback.onSuccess();
                }
                else{
                    String errorBody = response.body() != null ? response.body().string() : "sem corpo";
                    Log.e("CompanyRegisterError", "Erro: " + response.code() + " - " + errorBody);
                    callback.onFailure("Erro: " + response.message());                }


            }catch (Exception e){
                callback.onFailure("Faha ao conectar: "+e.getMessage());
            }
        }).start();

    }
}
