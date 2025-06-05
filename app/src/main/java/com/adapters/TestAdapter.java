package com.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.activities.R;
import com.activities.Test;
import com.models.TestWithQuestionCount;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.Button;
import android.widget.TextView;

public class TestAdapter extends RecyclerView.Adapter<TestAdapter.TestViewHolder> {

    private List<TestWithQuestionCount> testList;
    private Context context;

    public TestAdapter(List<TestWithQuestionCount> testList, Context context) {
        this.testList = testList;
        this.context = context;
    }

    @NonNull
    @Override
    public TestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_test_card, parent, false);
        return new TestViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull TestViewHolder holder, int position) {
        TestWithQuestionCount item = testList.get(position);

        holder.title.setText(item.getTest().getTitle());
        holder.questionCount.setText(item.getQuestionCount() + " perguntas");

        Integer userScore = item.getUserScore();
        if (userScore != null) {
            holder.testScore.setText(userScore + "%");
            holder.testScore.setVisibility(View.VISIBLE);

            holder.btnStart.setText("Refazer Teste");
            holder.btnStart.setBackgroundResource(R.drawable.button_orange); // ou use estilo ButtonOrange
        } else {
            holder.testScore.setVisibility(View.GONE);
            holder.btnStart.setText("Realizar Teste");
            holder.btnStart.setBackgroundResource(R.drawable.button_blue);
        }

        holder.btnStart.setOnClickListener(v -> {
            Intent intent = new Intent(context, Test.class);
            intent.putExtra("testId", item.getTest().getId());
            context.startActivity(intent);
        });
    }


    @Override
    public int getItemCount() {
        return testList.size();
    }

    public static class TestViewHolder extends RecyclerView.ViewHolder {
        TextView title, questionCount, testScore;
        Button btnStart;

        public TestViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.testTitle);
            questionCount = itemView.findViewById(R.id.questionCount);
            testScore = itemView.findViewById(R.id.testScore); // novo
            btnStart = itemView.findViewById(R.id.btnStart);
        }
    }



}
