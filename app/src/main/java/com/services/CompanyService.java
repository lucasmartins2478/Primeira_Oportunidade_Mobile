package com.services;

import com.models.Company;

import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CompanyService {

    private String apiUrl = "https://679c282633d3168463260adf.mockapi.io/company";
    private OkHttpClient client;


    public CompanyService(){
        client = new OkHttpClient();
    }
    public interface CompanyCallback{
        void onSuccess();
        void onFailure(String error);
    }



    public void registerCompany(Company company, CompanyCallback callback){

        new Thread(()->{
            try{
                JSONObject json = new JSONObject();
                json.put("companyName", company.getCompanyName());
                json.put("cnpj", company.getCnpj());
                json.put("segment", company.getSegment());
                json.put("phoneNumber", company.getPhoneNumber());
                json.put("resonsible", company.getResponsible());
                json.put("website", company.getWebsite());
                json.put("city", company.getCity());
                json.put("cep", company.getCep());
                json.put("uf", company.getUf());
                json.put("address", company.getAddress());
                json.put("addressNumber", company.getAddressNumber());
                json.put("logo", company.getLogo());

                RequestBody body =  RequestBody.create(
                        json.toString(),
                        MediaType.get("application/json; charset=utf-8")
                );

                Request request = new Request.Builder().url(apiUrl).post(body).build();

                Response response = client.newCall(request).execute();

                if(response.isSuccessful()){
                    callback.onSuccess();
                }
                else{
                    callback.onFailure("Erro: "+response.message());
                }


            }catch (Exception e){
                callback.onFailure("Faha ao conectar: "+e.getMessage());
            }
        }).start();

    }
}
