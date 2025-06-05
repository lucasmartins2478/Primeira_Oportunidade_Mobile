package com.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Community extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_community);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    public void openSection1(View view){
        Intent intent = new Intent(Community.this, CommunityStep1.class);
        startActivity(intent);
    }
    public void openSection2(View view){
        Intent intent = new Intent(Community.this, CommunityStep2.class);
        startActivity(intent);
    }
    public void openSection3(View view){
        Intent intent = new Intent(Community.this, CommunityStep3.class);
        startActivity(intent);
    }
    public void openSection4(View view){
        Intent intent = new Intent(Community.this, CommunityStep4.class);
        startActivity(intent);
    }


    public void openChat(View view){
        Intent intent = new Intent(Community.this, Chat.class);
        startActivity(intent);
    }
    public void openAptitudeTests(View view){
        Intent intent = new Intent(Community.this, SkillTests.class);
        startActivity(intent);
    }
}