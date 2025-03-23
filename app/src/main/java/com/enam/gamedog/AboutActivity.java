package com.enam.gamedog;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class AboutActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        TextView aboutText = findViewById(R.id.aboutText);
        aboutText.setText("Game Dog is an exciting platformer game where you control a dog through various levels.\n\n" +
                "Features:\n" +
                "• 9 exciting levels across 3 different worlds\n" +
                "• City, Forest, and Park environments\n" +
                "• Collect treats and avoid obstacles\n" +
                "• Simple one-touch controls\n\n" +
                "Enjoy the adventure!");
    }
} 