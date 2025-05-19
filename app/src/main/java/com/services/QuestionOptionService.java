package com.services;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.models.QuestionOption;

public class QuestionOptionService {

    public static List<QuestionOption> fetchQuestionOptions() {
        List<QuestionOption> optionList = new ArrayList<>();

        try {
            URL url = new URL("https://backend-po.onrender.com/options"); // Atualize com sua URL real
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

                int id = obj.getInt("id");
                int question_test_id = obj.getInt("question_test_id");
                String optionText = obj.getString("option_text");
                boolean isCorrect = obj.getInt("is_correct") == 1;

                Log.d("OPTION_DEBUG", "id: " + id + ", question_test_id: " + question_test_id + ", texto: " + optionText + ", correta: " + isCorrect);


                QuestionOption option = new QuestionOption(id, question_test_id, optionText, isCorrect);
                optionList.add(option);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return optionList;
    }
}