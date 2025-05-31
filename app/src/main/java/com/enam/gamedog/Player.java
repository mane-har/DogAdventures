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
    private float gravity = 0.8f;
    private float groundY;
    private float topGroundY;
    private float screenY;
    private float velocityY;
    private float velocityX;
    private float jumpForce = -4f;
    private float jumpAcceleration = 0.1f;
    private float maxJumpSpeed = 2f;
    private float switchSpeed = 0.2f;
    private boolean isSwitching = false;
    private float switchProgress = 0f;
    private float targetY;
    private int screenX;
    private Bitmap idleSprite;
    private Bitmap jumpSprite;
    private RectF collisionBox;
    private boolean isOnTopGround = false;
    private float bottomGroundY;
    private boolean isFlipped = false;
    
    // Animation variables
    private int currentFrame = 0;
    private long lastFrameTime = 0;
    private static final int FRAME_DELAY = 100;
    private static final int IDLE_FRAMES = 4;
    private static final int JUMP_FRAMES = 6;
    private int frameWidth;
    private int frameHeight;

    private boolean isMovingForward = false;
    private float moveForwardSpeed = 15f;

    public Player(int screenX, int screenY, GameResources resources) {
        this.screenX = screenX;
        this.screenY = screenY;
        this.idleSprite = resources.getDogIdleSprite();
        this.jumpSprite = resources.getDogJumpSprite();
        this.frameWidth = idleSprite.getWidth() / IDLE_FRAMES;
        this.frameHeight = idleSprite.getHeight();
        this.x = 100;
        this.bottomGroundY = 760;
        this.topGroundY = 20;
        this.y = bottomGroundY;
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


            if (y >= (isTopGround ? topGroundY : bottomGroundY)) {
                y = isTopGround ? topGroundY : bottomGroundY;
                isJumping = false;
                jumpVelocity = 0;
            }
        }


        long currentTime = System.currentTimeMillis();
        if (currentTime - lastFrameTime > FRAME_DELAY) {
            lastFrameTime = currentTime;
            if (isJumping) {
                currentFrame = (currentFrame + 1) % JUMP_FRAMES;
            } else {
                currentFrame = (currentFrame + 1) % IDLE_FRAMES;
            }
        }


        if (isSwitching) {
            switchProgress += switchSpeed;
            if (switchProgress >= 1f) {

                isSwitching = false;
                switchProgress = 0f;
                y = targetY;
                isOnTopGround = !isOnTopGround;
                isFlipped = !isFlipped;
                gravity = -gravity;
            } else {

                float startY = isOnTopGround ? topGroundY : bottomGroundY;
                float endY = isOnTopGround ? bottomGroundY : topGroundY;
                y = startY + (endY - startY) * switchProgress;
            }
        } else {

            velocityY += gravity;
            y += velocityY;


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


        if (!isMovingForward) {
            x = screenX / 6;
        }

        updateCollisionBox();
    }

    public void jump() {
        if (!isJumping) {
            isJumping = true;
            jumpVelocity = -25;
            currentFrame = 0;
        }
    }

    public void switchGround() {
        if (!isJumping && !isSwitching) {
            isSwitching = true;
            switchProgress = 0f;
            targetY = isOnTopGround ? bottomGroundY : topGroundY;
            velocityY = 0;
        }
    }

    public void draw(Canvas canvas) {

        Bitmap currentSprite = isJumping ? jumpSprite : idleSprite;
        int totalFrames = isJumping ? JUMP_FRAMES : IDLE_FRAMES;
        

        int currentSpriteWidth = currentSprite.getWidth() / totalFrames;
        

        Rect srcRect = new Rect(
            currentFrame * currentSpriteWidth,
            0,
            (currentFrame + 1) * currentSpriteWidth,
            frameHeight
        );
        

        RectF destRect = new RectF(
            x,
            y,
            x + frameWidth,
            y + frameHeight
        );
        

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

        float widthInset = frameWidth * 0.15f;
        float heightInset = frameHeight * 0.15f;
        

        if (isJumping) {

            widthInset = frameWidth * 0.2f;
            heightInset = frameHeight * 0.25f;
        }
        
        collisionBox.set(
            x + widthInset,
            y + heightInset,
            x + frameWidth - widthInset,
            y + frameHeight - heightInset
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

    public void startMovingForward() {
        isMovingForward = true;
    }

    public void updateForwardMovement() {
        if (isMovingForward) {
            x += moveForwardSpeed;
        }
    }
}