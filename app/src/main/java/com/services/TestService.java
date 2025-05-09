package com.services;

import android.os.StrictMode;

import com.models.Test;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.ArrayList;
import java.util.List;

public class TestService {

    public static List<Test> fetchTests() {
        List<Test> testList = new ArrayList<>();

        // Apenas para facilitar o exemplo (não usar em produção)
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            URL url = new URL("https://backend-po.onrender.com/tests"); // atualize com sua URL real
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream())
            );

            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            reader.close();

            JSONArray jsonArray = new JSONArray(response.toString());

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);

                Test test = new Test();
                test.setId(obj.getInt("id"));
                test.setTitle(obj.getString("title"));
                test.setDescription(obj.getString("description"));
                test.setDurationMinutes(obj.getInt("duration_minutes"));
                String dateStr = obj.getString("created_at");
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date date = sdf.parse(dateStr);
                test.setCreatedAt(date);

                testList.add(test);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return testList;
    }
}
