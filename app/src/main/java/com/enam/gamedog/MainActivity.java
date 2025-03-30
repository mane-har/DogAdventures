package com.enam.gamedog;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private GameView gameView;
    private int currentLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        currentLevel = getIntent().getIntExtra("level", 1);

        gameView = new GameView(this, currentLevel);

        RelativeLayout layout = new RelativeLayout(this);
        
        layout.addView(gameView);

        ImageButton backButton = new ImageButton(this);
        backButton.setImageResource(R.drawable.arrow_left);
        backButton.setBackgroundResource(android.R.color.transparent);
        
        RelativeLayout.LayoutParams buttonParams = new RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        buttonParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        buttonParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        buttonParams.setMargins(16, 16, 16, 16);
        
        backButton.setLayoutParams(buttonParams);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        
        layout.addView(backButton);

        setContentView(layout);
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        gameView.resume();
    }
}