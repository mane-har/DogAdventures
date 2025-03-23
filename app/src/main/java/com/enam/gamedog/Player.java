package com.enam.gamedog;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;

public class Player {
    private float x, y;
    private float velocityY;
    private float gravity;
    private float jumpForce;
    private boolean isJumping;
    private float speed;
    private float screenX, screenY;
    private GameResources resources;
    private int frameIndex;
    private long lastFrameTime;
    private int frameDelay;
    private RectF collisionBox;

    public Player(float screenX, float screenY, GameResources resources) {
        this.screenX = screenX;
        this.screenY = screenY;
        this.resources = resources;
        reset();
    }

    public void reset() {
        x = screenX * 0.2f;
        y = screenY * 0.8f;
        velocityY = 0;
        gravity = 0.8f;
        jumpForce = -20f;
        isJumping = false;
        speed = 5f;
        frameIndex = 0;
        lastFrameTime = System.currentTimeMillis();
        frameDelay = 100; // Faster animation
        collisionBox = new RectF();
        updateCollisionBox();
    }

    public void update() {
        velocityY += gravity;
        y += velocityY;

        if (y > screenY * 0.8f) {
            y = screenY * 0.8f;
            velocityY = 0;
            isJumping = false;
        }

        // Update animation
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastFrameTime > frameDelay) {
            frameIndex = (frameIndex + 1) % resources.getDogFrames().length;
            lastFrameTime = currentTime;
        }

        // Move forward
        x += speed;

        updateCollisionBox();
    }

    public void jump() {
        if (!isJumping) {
            velocityY = jumpForce;
            isJumping = true;
        }
    }

    public void draw(Canvas canvas) {
        Bitmap currentFrame = resources.getDogFrames()[frameIndex];
        Rect srcRect = new Rect(0, 0, currentFrame.getWidth(), currentFrame.getHeight());
        RectF destRect = new RectF(x, y, x + currentFrame.getWidth() * 0.5f, y + currentFrame.getHeight() * 0.5f);
        canvas.drawBitmap(currentFrame, srcRect, destRect, null);
    }

    private void updateCollisionBox() {
        Bitmap currentFrame = resources.getDogFrames()[frameIndex];
        float width = currentFrame.getWidth() * 0.5f;
        float height = currentFrame.getHeight() * 0.5f;
        collisionBox.set(x, y, x + width, y + height);
    }

    public RectF getCollisionBox() {
        return collisionBox;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}