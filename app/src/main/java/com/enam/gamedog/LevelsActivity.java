package com.enam.gamedog;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LevelsActivity extends AppCompatActivity {
    private SharedPreferences prefs;
    private static final String PREF_NAME = "GameDogPrefs";
    private static final String KEY_UNLOCKED_LEVEL = "unlockedLevel";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_levels);

        prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        int unlockedLevel = prefs.getInt(KEY_UNLOCKED_LEVEL, 1); // Start with level 1 unlocked

        // Setup level buttons
        setupLevelButtons(unlockedLevel);
    }

    private void setupLevelButtons(int unlockedLevel) {
        // City World Levels (1-3)
        Button level1Button = findViewById(R.id.level1Button);
        Button level2Button = findViewById(R.id.level2Button);
        Button level3Button = findViewById(R.id.level3Button);

        // Forest World Levels (4-6)
        Button level4Button = findViewById(R.id.level4Button);
        Button level5Button = findViewById(R.id.level5Button);
        Button level6Button = findViewById(R.id.level6Button);

        // Park World Levels (7-9)
        Button level7Button = findViewById(R.id.level7Button);
        Button level8Button = findViewById(R.id.level8Button);
        Button level9Button = findViewById(R.id.level9Button);

        // Setup click listeners for all buttons
        level1Button.setOnClickListener(v -> startGame(1));
        level2Button.setOnClickListener(v -> handleLevelClick(2, unlockedLevel));
        level3Button.setOnClickListener(v -> handleLevelClick(3, unlockedLevel));
        level4Button.setOnClickListener(v -> handleLevelClick(4, unlockedLevel));
        level5Button.setOnClickListener(v -> handleLevelClick(5, unlockedLevel));
        level6Button.setOnClickListener(v -> handleLevelClick(6, unlockedLevel));
        level7Button.setOnClickListener(v -> handleLevelClick(7, unlockedLevel));
        level8Button.setOnClickListener(v -> handleLevelClick(8, unlockedLevel));
        level9Button.setOnClickListener(v -> handleLevelClick(9, unlockedLevel));

        // Disable locked levels
        if (unlockedLevel < 2) level2Button.setEnabled(false);
        if (unlockedLevel < 3) level3Button.setEnabled(false);
        if (unlockedLevel < 4) level4Button.setEnabled(false);
        if (unlockedLevel < 5) level5Button.setEnabled(false);
        if (unlockedLevel < 6) level6Button.setEnabled(false);
        if (unlockedLevel < 7) level7Button.setEnabled(false);
        if (unlockedLevel < 8) level8Button.setEnabled(false);
        if (unlockedLevel < 9) level9Button.setEnabled(false);
    }

    private void handleLevelClick(int level, int unlockedLevel) {
        if (level <= unlockedLevel) {
            startGame(level);
        } else {
            Toast.makeText(this, "Level " + level + " is locked!", Toast.LENGTH_SHORT).show();
        }
    }

    private void startGame(int level) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("level", level);
        startActivity(intent);
        finish();
    }
} 