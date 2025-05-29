package com.enam.gamedog;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.SystemClock;

public class Player {
    private float x, y;
    private float width, height;
    private Bitmap bitmap;
    private boolean isJumping;
    private boolean isTopGround;
    private float jumpVelocity;
    private float gravity = 0.8f;        // Single gravity declaration
    private float groundY;
    private float topGroundY;
    private float screenY;
    private float velocityY;
    private float velocityX;
    private float jumpForce = -4f;      // Reduced from -8f for much lower jump
    private float jumpAcceleration = 0.1f;
    private float maxJumpSpeed = 2f;    // Reduced from 4f for lower maximum jump speed
    private float switchSpeed = 0.2f;  // Reduced from 0.5f for slower switching
    private boolean isSwitching = false;  // Track if player is switching grounds
    private float switchProgress = 0f;    // Progress of switching animation
    private float targetY;                // Target Y position for switching
    private int screenX;
    private Bitmap idleSprite;
    private Bitmap jumpSprite;
    private RectF collisionBox;
    private boolean isOnTopGround = false;  // New variable to track which ground we're on
    private float bottomGroundY;            // Y position of bottom ground
    private boolean isFlipped = false;      // Track if player is flipped
    
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
        this.frameWidth = idleSprite.getWidth() / IDLE_FRAMES;
        this.frameHeight = idleSprite.getHeight();
        this.x = 100;
        this.bottomGroundY = 760;  // Made even higher for bottom ground
        this.topGroundY = 20;      // Lowered from 0 to 20 for top ground
        this.y = bottomGroundY;    // Start on bottom ground
        this.isJumping = false;
        this.isTopGround = false;
        this.jumpVelocity = 0;
        
        reset();
    }

    public void reset() {
        x = screenX / 6;
        y = bottomGroundY;
        velocityY = 0;
        velocityX = 0;
        isJumping = false;
        isOnTopGround = false;
        isFlipped = false;
        currentFrame = 0;
        updateCollisionBox();
    }

    public void update() {
        if (isJumping) {
            y += jumpVelocity;
            jumpVelocity += gravity;

            // Check if landed on ground
            if (y >= (isTopGround ? topGroundY : bottomGroundY)) {
                y = isTopGround ? topGroundY : bottomGroundY;
                isJumping = false;
                jumpVelocity = 0;
            }
        }

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

        // Handle ground switching animation
        if (isSwitching) {
            switchProgress += switchSpeed;
            if (switchProgress >= 1f) {
                // Switch complete
                isSwitching = false;
                switchProgress = 0f;
                y = targetY;
                isOnTopGround = !isOnTopGround;
                isFlipped = !isFlipped;
                gravity = -gravity;
            } else {
                // Interpolate position during switch
                float startY = isOnTopGround ? topGroundY : bottomGroundY;
                float endY = isOnTopGround ? bottomGroundY : topGroundY;
                y = startY + (endY - startY) * switchProgress;
            }
        } else {
            // Normal gravity and movement
            velocityY += gravity;
            y += velocityY;

            // Ground collision
            if (isOnTopGround) {
                if (y < topGroundY) {
                    y = topGroundY;
                    velocityY = 0;
                    velocityX = 0;
                    isJumping = false;
                }
            } else {
                if (y > bottomGroundY) {
                    y = bottomGroundY;
                    velocityY = 0;
                    velocityX = 0;
                    isJumping = false;
                }
            }
        }

        // Keep x position fixed
        x = screenX / 6;

        updateCollisionBox();
    }

    public void jump() {
        if (!isJumping) {
            isJumping = true;
            jumpVelocity = -20;
            currentFrame = 0;
        }
    }

    public void switchGround() {
        if (!isJumping && !isSwitching) {  // Only allow switching when not jumping or already switching
            isSwitching = true;
            switchProgress = 0f;
            targetY = isOnTopGround ? bottomGroundY : topGroundY;
            velocityY = 0;  // Stop any current movement
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
        
        // Apply flip if needed
        if (isFlipped) {
            canvas.save();
            canvas.scale(1, -1, x + frameWidth/2, y + frameHeight/2);
            canvas.drawBitmap(currentSprite, srcRect, destRect, null);
            canvas.restore();
        } else {
            canvas.drawBitmap(currentSprite, srcRect, destRect, null);
        }
    }

    private void updateCollisionBox() {
        if (collisionBox == null) {
            collisionBox = new RectF();
        }
        // Make collision box more accurate and slightly smaller
        float widthInset = frameWidth * 0.15f;   // Reduced from 0.2f for more accurate width
        float heightInset = frameHeight * 0.15f; // Reduced from 0.2f for more accurate height
        
        // Adjust collision box based on jumping state
        if (isJumping) {
            // Make collision box slightly smaller during jumps for better gameplay
            widthInset = frameWidth * 0.2f;
            heightInset = frameHeight * 0.25f;
        }
        
        collisionBox.set(
            x + widthInset,           // Left
            y + heightInset,          // Top
            x + frameWidth - widthInset,  // Right
            y + frameHeight - heightInset // Bottom
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

    public boolean isTopGround() {
        return isTopGround;
    }

    public void setX(float x) {
        this.x = x;
        updateCollisionBox();
    }
}