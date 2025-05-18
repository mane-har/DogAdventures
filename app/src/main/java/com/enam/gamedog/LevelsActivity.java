package com.enam.gamedog;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

public class LevelsActivity extends AppCompatActivity {

    VideoView videoView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_levels);

        videoView = findViewById(R.id.videoView);
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.sky_anim);
        videoView.setVideoURI(videoUri);
        videoView.start();

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);

                // Scale video to fill screen (center crop behavior)
                float videoRatio = (float) mp.getVideoWidth() / mp.getVideoHeight();
                float screenRatio = (float) videoView.getWidth() / videoView.getHeight();
                float scaleX = 1f;
                float scaleY = 1f;

                if (videoRatio > screenRatio) {
                    scaleY = videoRatio / screenRatio;
                } else {
                    scaleX = screenRatio / videoRatio;
                }

                videoView.setScaleX(scaleX);
                videoView.setScaleY(scaleY);
            }
        });

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

    @Override
    protected void onResume() {
        super.onResume();
        videoView.start(); // resume video if user returns
    }

    @Override
    protected void onPause() {
        super.onPause();
        videoView.pause(); // pause video when leaving activity
    }
    private void startGame(int level) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("level", level);
        startActivity(intent);
    }
} 