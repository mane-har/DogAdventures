package com.enam.gamedog;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

public class LevelsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_levels);

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        setupLevelButtons();
    }

    private void setupLevelButtons() {
        int[] buttonIds = {
            R.id.level1, R.id.level2, R.id.level3,
            R.id.level4, R.id.level5, R.id.level6,
            R.id.level7, R.id.level8, R.id.level9
        };

        for (int i = 0; i < buttonIds.length; i++) {
            final int level = i + 1;
            ImageButton button = findViewById(buttonIds[i]);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startGame(level);
                }
            });
        }
    }

    private void startGame(int level) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("level", level);
        startActivity(intent);
    }
} 