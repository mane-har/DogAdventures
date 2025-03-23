package com.enam.gamedog;


import android.graphics.Bitmap;
import android.graphics.Rect;

public class SpriteAnimation {
    private Bitmap spriteSheet;
    private int frameWidth;
    private int frameHeight;
    private int frameCount;
    private int currentFrame;
    private long frameTime;
    private long lastFrameTime;
    private boolean isPlaying;

    public SpriteAnimation(Bitmap spriteSheet, int frameWidth, int frameHeight, int frameCount, int fps) {
        this.spriteSheet = spriteSheet;
        this.frameWidth = frameWidth;
        this.frameHeight = frameHeight;
        this.frameCount = frameCount;
        this.currentFrame = 0;
        this.frameTime = 1000 / fps; // Convert fps to milliseconds
        this.lastFrameTime = System.currentTimeMillis();
        this.isPlaying = true;
    }

    public void update() {
        if (!isPlaying) return;

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastFrameTime > frameTime) {
            currentFrame = (currentFrame + 1) % frameCount;
            lastFrameTime = currentTime;
        }
    }

    public Rect getCurrentFrameRect() {
        return new Rect(
                currentFrame * frameWidth,
                0,
                (currentFrame + 1) * frameWidth,
                frameHeight
        );
    }

    public Bitmap getSpriteSheet() {
        return spriteSheet;
    }

    public void play() {
        isPlaying = true;
    }

    public void pause() {
        isPlaying = false;
    }

    public void setFrame(int frame) {
        currentFrame = frame % frameCount;
    }
}