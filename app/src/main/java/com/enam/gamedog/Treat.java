package com.enam.gamedog;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;

public class Treat {
    private float x;
    private float y;
    private float speed;
    private int screenX, screenY;
    private Bitmap sprite;
    private RectF collisionBox;
    private boolean isActive;

    public Treat(int screenX, int screenY, Bitmap sprite) {
        this.screenX = screenX;
        this.screenY = screenY;
        this.sprite = sprite;
        this.speed = 0;
        this.isActive = false;
        reset();
    }

    public void reset() {
        // Position treat just off the right side of screen
        x = screenX + 100;
        // Position treat at a height that requires a jump
        y = screenY - sprite.getHeight() - 150;
        isActive = true;
        updateCollisionBox();
    }

    public void update(float gameSpeed) {
        if (!isActive) return;
        
        this.speed = gameSpeed;
        x -= speed;
        
        // If treat goes off screen, reset it
        if (x < -sprite.getWidth()) {
            reset();
        }
        
        updateCollisionBox();
    }

    public void draw(Canvas canvas) {
        if (isActive) {
            canvas.drawBitmap(sprite, x, y, null);
        }
    }

    private void updateCollisionBox() {
        if (collisionBox == null) {
            collisionBox = new RectF();
        }
        // Make collision box slightly smaller than the sprite for better gameplay
        collisionBox.set(
            x + sprite.getWidth() * 0.2f,   // 20% inset from left
            y + sprite.getHeight() * 0.2f,  // 20% inset from top
            x + sprite.getWidth() * 0.8f,   // 20% inset from right
            y + sprite.getHeight() * 0.8f   // 20% inset from bottom
        );
    }

    public boolean isColliding(Player player) {
        return isActive && RectF.intersects(collisionBox, player.getCollisionBox());
    }

    public void collect() {
        isActive = false;
    }

    public boolean isActive() {
        return isActive;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getWidth() {
        return sprite.getWidth();
    }

    public float getHeight() {
        return sprite.getHeight();
    }

    public void setPosition(float x) {
        this.x = x;
        updateCollisionBox();
    }
}