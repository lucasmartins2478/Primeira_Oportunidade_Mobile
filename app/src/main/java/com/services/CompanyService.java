package com.services;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.models.Company;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CompanyService {

    private static String apiUrl = "https://backend-po.onrender.com/companies";
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


                RequestBody body =  RequestBody.create(
                        json.toString(),
                        MediaType.get("application/json; charset=utf-8")
                );

                Log.d("CompanyRegisterJSON", json.toString());


                Request request = new Request.Builder().url(apiUrl+"/"+company.getUserId()).put(body).build();

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
    public  void deleteAllCompanyData(Context context, int companyId, CompanyService.RegisterCallback callback) {
        new Thread(() -> {
            try {
                URL url = new URL("https://backend-po.onrender.com/company/" + companyId);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("DELETE");
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

                int responseCode = conn.getResponseCode();

                InputStream is = responseCode < HttpURLConnection.HTTP_BAD_REQUEST
                        ? conn.getInputStream()
                        : conn.getErrorStream();

                if (is != null) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    is.close();
                    System.out.println("Resposta (delete): " + response.toString());
                }

                if (responseCode == 200 || responseCode == 204) {
                    new Handler(Looper.getMainLooper()).post(callback::onSuccess);
                } else {
                    new Handler(Looper.getMainLooper()).post(() -> callback.onFailure("Erro ao excluir empresa. Código: " + responseCode));
                }

            } catch (Exception e) {
                e.printStackTrace();
                new Handler(Looper.getMainLooper()).post(() -> callback.onFailure("Erro: " + e.getMessage()));
            }
        }).start();
    }

    public  void deleteCompanyData(Context context, int companyId, CompanyService.RegisterCallback callback) {
        new Thread(() -> {
            try {
                URL url = new URL("https://backend-po.onrender.com/companies/" + companyId);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("DELETE");
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

                int responseCode = conn.getResponseCode();

                InputStream is = responseCode < HttpURLConnection.HTTP_BAD_REQUEST
                        ? conn.getInputStream()
                        : conn.getErrorStream();

                if (is != null) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    is.close();
                    System.out.println("Resposta (delete): " + response.toString());
                }

                if (responseCode == 200 || responseCode == 204) {
                    new Handler(Looper.getMainLooper()).post(callback::onSuccess);
                } else {
                    new Handler(Looper.getMainLooper()).post(() -> callback.onFailure("Erro ao excluir empresa. Código: " + responseCode));
                }

            } catch (Exception e) {
                e.printStackTrace();
                new Handler(Looper.getMainLooper()).post(() -> callback.onFailure("Erro: " + e.getMessage()));
            }
        }).start();
    }
}
