package com.enam.gamedog;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;

public class Obstacle {
    private float x;
    private float y;
    private float speed;
    private int screenX, screenY;
    private Bitmap bitmap;
    private RectF collisionBox;

    public Obstacle(int screenX, int screenY, Bitmap bitmap) {
        this.screenX = screenX;
        this.screenY = screenY;
        this.bitmap = bitmap;
        speed = 5f;
        reset();
    }

    public void reset() {
        // Position fence at the right edge of screen
        x = screenX + (float)(Math.random() * screenX * 0.5f);
        // Position fence on the ground (60 pixels from bottom)
        y = screenY - bitmap.getHeight() - 60;
        updateCollisionBox();
    }

    public void update() {
        x -= speed;
        if (x + bitmap.getWidth() < 0) {
            reset();
        }
        updateCollisionBox();
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(bitmap, x, y, null);
    }

    private void updateCollisionBox() {
        if (collisionBox == null) {
            collisionBox = new RectF();
        }
        collisionBox.set(
            x + bitmap.getWidth() * 0.1f,  // 10% inset from left
            y,                             // No inset from top
            x + bitmap.getWidth() * 0.9f,  // 10% inset from right
            y + bitmap.getHeight()         // No inset from bottom
        );
    }

    public boolean isColliding(Player player) {
        return RectF.intersects(collisionBox, player.getCollisionBox());
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
        updateCollisionBox();
    }
}