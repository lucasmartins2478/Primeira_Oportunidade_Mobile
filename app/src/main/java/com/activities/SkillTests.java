package com.activities;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adapters.TestAdapter;
import com.models.QuestionTest;
import com.models.Test;
import com.models.TestWithQuestionCount;
import com.models.UserResult;
import com.services.QuestionTestService;
import com.services.TestService;
import com.services.UserResultService;

import java.util.ArrayList;
import java.util.List;

public class SkillTests extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TestAdapter adapter;
    private List<TestWithQuestionCount> testWithCounts = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_skill_tests);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

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
                adapter = new TestAdapter(testWithCounts, SkillTests.this);
                recyclerView.setAdapter(adapter);
            });
        }).start();
    }


    public void onBackPressed(View view) {
        finish();
    }
}