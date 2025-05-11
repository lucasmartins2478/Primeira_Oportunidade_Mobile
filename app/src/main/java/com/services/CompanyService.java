package com.services;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.models.Company;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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

    public void fetchCompanyFromApi(int userId,String token, CompanyCallback callback){
        new Thread(()->{
            String url = apiUrl + "/" +userId;

            Request request = new Request.Builder().url(url).addHeader("Authorization", "Bearer " + token).build();

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

    public interface CompanyListCallback {
        void onSuccess(List<Company> companies);
        void onFailure(String error);
    }
    public void fetchAllCompanies(String token, CompanyListCallback callback) {
        new Thread(() -> {
            Request request = new Request.Builder().url(apiUrl).get().addHeader("Authorization", "Bearer " + token).build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    String responseData = response.body().string();
                    JSONArray jsonArray = new JSONArray(responseData);

                    List<Company> companies = new ArrayList<>();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject companyJson = jsonArray.getJSONObject(i);

                        int userId = companyJson.optInt("userId", -1); // retorna -1 se null


                        Company company = new Company(
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
                                companyJson.isNull("url") ? null : companyJson.getString("url"),
                                companyJson.isNull("logo") ? null : companyJson.getString("logo"),
                                companyJson.optInt("userId", -1)  // aqui está o fix
                        );

                        companies.add(company);
                    }

                    // Postando de volta na thread principal
                    new Handler(Looper.getMainLooper()).post(() -> callback.onSuccess(companies));

                } else {
                    new Handler(Looper.getMainLooper()).post(() ->
                            callback.onFailure("Erro na resposta: " + response.code()));
                }
            } catch (Exception e) {
                e.printStackTrace();
                new Handler(Looper.getMainLooper()).post(() ->
                        callback.onFailure("Erro ao conectar: " + e.getMessage()));
            }
        }).start();
    }




    public void registerCompany(Company company,String token, CompanyCallback callback){

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


                Request request = new Request.Builder().url(apiUrl).post(body).addHeader("Authorization", "Bearer " + token).build();

                Response response = client.newCall(request).execute();



                if(response.isSuccessful()){

                    String responseData = response.body().string();
                    JSONObject responseJson = new JSONObject(responseData);


                    int id = responseJson.getInt("id");
                    int userId = responseJson.getInt("userId");
                    String companyName = responseJson.getString("name");
                    String cnpj = responseJson.getString("cnpj");
                    String segment = responseJson.getString("segment");
                    String responsible = responseJson.getString("responsible");
                    String phone = responseJson.getString("phoneNumber");
                    String city = responseJson.getString("city");
                    String cep = responseJson.getString("cep");
                    String address = responseJson.getString("address");
                    int addressNumber = responseJson.getInt("addressNumber");
                    String uf = responseJson.getString("uf");
                    String url = responseJson.getString("url");
                    String logo = responseJson.getString("logo");



                    Company createdCompany = new Company(id, companyName, cnpj, segment, responsible, phone, city, cep, address, addressNumber, uf, url, logo, userId);
                    callback.onSuccess(createdCompany);
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
    public void updateCompany(Company company, String token, RegisterCallback callback){

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


                Request request = new Request.Builder().url(apiUrl+"/"+company.getUserId()).put(body).addHeader("Authorization", "Bearer " + token).build();

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
    public  void deleteAllCompanyData(Context context, int companyId, String token, CompanyService.RegisterCallback callback) {
        new Thread(() -> {
            try {
                URL url = new URL("https://backend-po.onrender.com/company/" + companyId);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("DELETE");
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setRequestProperty("Authorization", "Bearer " + token);

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
