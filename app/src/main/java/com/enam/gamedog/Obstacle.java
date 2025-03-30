package com.enam.gamedog;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;

public class Obstacle {
    private float x;
    private float y;
    private float speed;
    private int screenX, screenY;
    private Bitmap sprite;
    private RectF collisionBox;
    private static final float INITIAL_SPACING = 800f; // Space between fences at start
    private static final float MIN_SPACING = 500f; // Minimum space between fences as game speeds up
    private boolean shouldReset = true;

    public Obstacle(int screenX, int screenY, Bitmap sprite) {
        this.screenX = screenX;
        this.screenY = screenY;
        this.sprite = sprite;
        this.speed = 0;
        reset();
    }

    public void reset() {
        // Position fence just off the right side of screen
        x = screenX + 100;
        // Position fence on the ground
        y = screenY - sprite.getHeight() - 60;
        updateCollisionBox();
    }

    public void update(float gameSpeed) {
        if (x + sprite.getWidth() < 0) {
            if (shouldReset) {
                x = screenX + 100;
            }
            return;
        }
        x -= gameSpeed;
        updateCollisionBox();
    }

    public void setPosition(float x) {
        this.x = x;
        updateCollisionBox();
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(sprite, x, y, null);
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
        return RectF.intersects(collisionBox, player.getCollisionBox());
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

    public float getWidth() {
        return sprite.getWidth();
    }

    public float getHeight() {
        return sprite.getHeight();
    }

    public void setShouldReset(boolean shouldReset) {
        this.shouldReset = shouldReset;
    }
}