package com.enam.gamedog;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

public class MainMenuActivity extends AppCompatActivity {

    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        videoView = findViewById(R.id.videoView);
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.sky_anim);
        videoView.setVideoURI(videoUri);
        videoView.start();


        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);


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


        Button playButton = findViewById(R.id.playButton);
        Button aboutButton = findViewById(R.id.aboutButton);


        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainMenuActivity.this, LevelsActivity.class));
            }
        });

    aboutButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(MainMenuActivity.this, AboutActivity.class);
            startActivity(i);
        }
    });}

        @Override
        protected void onResume() {
            super.onResume();
            videoView.start();
        }

        @Override
        protected void onPause() {
            super.onPause();
            videoView.pause();
        }
    }
