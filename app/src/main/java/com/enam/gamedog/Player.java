package com.enam.gamedog;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.SystemClock;

public class Player {
    private float x;
    private float y;
    private float velocityY;
    private float velocityX;
    private float gravity = 0.8f;        // Increased from 0.6f for faster fall
    private float jumpForce = -18f;      // Reduced from -22f for lower jump
    private float jumpAcceleration = 0.1f;
    private float maxJumpSpeed = 6f;
    private int screenX, screenY;
    private Bitmap idleSprite;
    private Bitmap jumpSprite;
    private RectF collisionBox;
    private boolean isJumping = false;
    
    // Animation variables
    private int currentFrame = 0;
    private long lastFrameTime = 0;
    private static final int FRAME_DELAY = 100; // milliseconds between frames
    private static final int IDLE_FRAMES = 4;   // Number of frames in idle animation
    private static final int JUMP_FRAMES = 6;   // Number of frames in jump animation
    private int frameWidth;    // Width of a single frame
    private int frameHeight;   // Height of a single frame

    public Player(int screenX, int screenY, GameResources resources) {
        this.screenX = screenX;
        this.screenY = screenY;
        this.idleSprite = resources.getDogIdleSprite();
        this.jumpSprite = resources.getDogJumpSprite();
        
        // Calculate frame dimensions from idle sprite
        frameWidth = idleSprite.getWidth() / IDLE_FRAMES;
        frameHeight = idleSprite.getHeight();
        
        reset();
    }

    public void reset() {
        x = screenX / 6; // Fixed starting position
        y = screenY - frameHeight - 60;
        velocityY = 0;
        velocityX = 0;
        isJumping = false;
        currentFrame = 0;
        updateCollisionBox();
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

        // Apply gravity
        velocityY += gravity;
        y += velocityY;

        // Ground collision
        if (y > screenY - frameHeight - 60) {
            y = screenY - frameHeight - 60;
            velocityY = 0;
            velocityX = 0;
            isJumping = false;
        }

        // Keep x position fixed
        x = screenX / 6;

        updateCollisionBox();
    }

    public void jump() {
        if (!isJumping) {
            velocityY = jumpForce;
            isJumping = true;
            currentFrame = 0;
        }
    }

    public void draw(Canvas canvas) {
        // Select current sprite sheet based on state
        Bitmap currentSprite = isJumping ? jumpSprite : idleSprite;
        int totalFrames = isJumping ? JUMP_FRAMES : IDLE_FRAMES;
        
        // Calculate the width of one frame in current sprite sheet
        int currentSpriteWidth = currentSprite.getWidth() / totalFrames;
        
        // Source rectangle for current frame in sprite sheet
        Rect srcRect = new Rect(
            currentFrame * currentSpriteWidth,  // Left of current frame
            0,                                  // Top of sprite
            (currentFrame + 1) * currentSpriteWidth,  // Right of current frame
            frameHeight                         // Bottom of sprite
        );
        
        // Destination rectangle for drawing the frame
        RectF destRect = new RectF(
            x,                  // Left
            y,                  // Top
            x + frameWidth,     // Right
            y + frameHeight     // Bottom
        );
        
        canvas.drawBitmap(currentSprite, srcRect, destRect, null);
    }

    private void updateCollisionBox() {
        if (collisionBox == null) {
            collisionBox = new RectF();
        }
        // Make collision box slightly smaller than the sprite for better gameplay
        collisionBox.set(
            x + frameWidth * 0.2f,   // 20% inset from left
            y + frameHeight * 0.2f,  // 20% inset from top
            x + frameWidth * 0.8f,   // 20% inset from right
            y + frameHeight * 0.8f   // 20% inset from bottom
        );
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

    public boolean isJumping() {
        return isJumping;
    }

    public void setX(float x) {
        this.x = x;
        updateCollisionBox();
    }
}