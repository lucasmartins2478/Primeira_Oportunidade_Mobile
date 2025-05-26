package com.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adapters.TestAdapter;
import com.models.QuestionTest;
import com.models.Test;
import com.models.TestWithQuestionCount;
import com.models.UserResult;
import com.services.QuestionTestService;
import com.services.TestService;
import com.activities.R;
import com.services.UserResultService;

import java.util.ArrayList;
import java.util.List;

public class AptitudeTests extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TestAdapter adapter;
    private List<TestWithQuestionCount> testWithCounts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aptitude_tests);

        recyclerView = findViewById(R.id.testRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadData();
    }

    private void loadData() {
        new Thread(() -> {
            List<Test> tests = TestService.fetchTests();
            List<QuestionTest> questions = QuestionTestService.fetchQuestions();
            List<UserResult> userResults = UserResultService.fetchUserResults(); // busca os resultados
            int userId = getSharedPreferences("UserPrefs", MODE_PRIVATE).getInt("candidateId", -1);

            testWithCounts.clear();

            for (Test test : tests) {
                int questionCount = 0;
                for (QuestionTest q : questions) {
                    if (q.getTest_id() == test.getId()) {
                        questionCount++;
                    }
                }

                // Verifica se o usuário tem resultado para esse teste
                Integer scorePercent = null;
                for (UserResult result : userResults) {
                    if (result.getUser_id() == userId && result.getTest_id() == test.getId()) {
                        scorePercent = (int) ((result.getScore() / (float) result.getTotal_questions()) * 100);
                        break;
                    }
                }

                testWithCounts.add(new TestWithQuestionCount(test, questionCount, scorePercent));
            }

            runOnUiThread(() -> {
                adapter = new TestAdapter(testWithCounts, AptitudeTests.this);
                recyclerView.setAdapter(adapter);
            });
        }).start();
    }


    public void onBackPressed(View view) {
        finish();
    }
}
