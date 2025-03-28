package com.enam.gamedog;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;

public class Player {
    private float x, y;
    private float velocityY;
    private float gravity = 0.4f;    // Reduced gravity for longer jump duration
    private float jumpForce = -17f;  // Keep same jump force for same height
    private boolean isJumping = false;
    private int screenX, screenY;
    private Bitmap idleSprite;
    private Bitmap jumpSprite;
    private int frameWidth;
    private int frameHeight;
    private int currentFrame = 0;
    private long lastFrameTime = 0;
    private static final int FRAME_DELAY = 100; // milliseconds between frames
    private static final int IDLE_FRAMES = 4;
    private static final int JUMP_FRAMES = 6;

    public Player(int screenX, int screenY, GameResources resources) {
        this.screenX = screenX;
        this.screenY = screenY;
        
        // Load sprites
        idleSprite = resources.getDogIdleSprite();
        jumpSprite = resources.getDogJumpSprite();
        
        // Calculate frame dimensions for idle sprite (both sprites should have same frame height)
        frameWidth = idleSprite.getWidth() / IDLE_FRAMES;
        frameHeight = idleSprite.getHeight();
        
        // Set initial position - place dog just above the ground at the bottom
        x = screenX / 4;
        y = screenY - frameHeight - 60; // Position above the 60-pixel ground
    }

    public void update() {
        // Update animation frame
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastFrameTime > FRAME_DELAY) {
            lastFrameTime = currentTime;
            if (isJumping) {
                currentFrame = (currentFrame + 1) % JUMP_FRAMES;
            } else {
                currentFrame = (currentFrame + 1) % IDLE_FRAMES;
            }
        }

        // Update physics
        if (isJumping) {
            velocityY += gravity;
            y += velocityY;

            // Check ground collision with bottom ground
            float groundY = screenY - frameHeight - 60; // Ground is 60 pixels high at bottom
            if (y >= groundY) {
                y = groundY;
                isJumping = false;
                velocityY = 0;
            }
        }
    }

    public void draw(Canvas canvas) {
        // Select current sprite sheet based on state
        Bitmap currentSprite = isJumping ? jumpSprite : idleSprite;
        int totalFrames = isJumping ? JUMP_FRAMES : IDLE_FRAMES;
        int currentSpriteWidth = currentSprite.getWidth() / totalFrames;
        
        // Source rectangle for current frame
        Rect srcRect = new Rect(
            currentFrame * currentSpriteWidth,
            0,
            (currentFrame + 1) * currentSpriteWidth,
            frameHeight
        );
        
        // Destination rectangle for drawing
        RectF destRect = new RectF(
            x,
            y,
            x + frameWidth,
            y + frameHeight
        );
        
        canvas.drawBitmap(currentSprite, srcRect, destRect, null);
    }

    public void jump() {
        if (!isJumping) {
            isJumping = true;
            velocityY = jumpForce;
            currentFrame = 0; // Reset animation frame when starting jump
        }
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public int getWidth() {
        return frameWidth;
    }

    public int getHeight() {
        return frameHeight;
    }

    public RectF getCollisionBox() {
        return new RectF(
            x + frameWidth * 0.2f,  // 20% inset from left
            y + frameHeight * 0.2f, // 20% inset from top
            x + frameWidth * 0.8f,  // 20% inset from right
            y + frameHeight * 0.8f  // 20% inset from bottom
        );
    }
}