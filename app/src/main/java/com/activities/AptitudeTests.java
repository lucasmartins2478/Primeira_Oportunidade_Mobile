package com.activities;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adapters.TestAdapter;
import com.models.QuestionTest;
import com.models.Test;
import com.models.TestWithQuestionCount;
import com.services.QuestionTestService;
import com.services.TestService;
import com.activities.R;

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

            for (Test test : tests) {
                int count = 0;
                for (QuestionTest q : questions) {
                    if (q.getTest_id() == test.getId()) {
                        count++;
                    }
                }
                testWithCounts.add(new TestWithQuestionCount(test, count));
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
