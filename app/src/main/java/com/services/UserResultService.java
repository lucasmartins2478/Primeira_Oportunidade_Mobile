package com.services;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import com.models.UserResult;

public class UserResultService {

    public static List<UserResult> fetchUserResults() {
        List<UserResult> resultList = new ArrayList<>();

        try {
            URL url = new URL("https://backend-po.onrender.com/user-test-results"); // Atualize com sua URL real
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

            // Formato de data ISO 8601
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);

                int id = obj.getInt("id");
                int userId = obj.getInt("user_id");
                int testId = obj.getInt("test_id");
                int score = obj.getInt("score");
                int totalQuestions = obj.getInt("total_questions");
                int timeTakenSeconds = obj.getInt("time_taken_seconds");
                Date createdAt = sdf.parse(obj.getString("created_at"));

                UserResult result = new UserResult(id, userId, testId, score, totalQuestions, timeTakenSeconds, createdAt);
                resultList.add(result);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultList;
    }
}
