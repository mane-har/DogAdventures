package com.enam.gamedog;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;

public class Treat {
    private float x;
    private float y;
    private float speed;
    private int screenX, screenY;
    private Bitmap bitmap;
    private RectF collisionBox;

    public Treat(int screenX, int screenY, Bitmap bitmap) {
        this.screenX = screenX;
        this.screenY = screenY;
        this.bitmap = bitmap;
        speed = 5f;
        reset();
    }

    public void reset() {
        // Position bone at the right edge of screen with random spacing
        x = screenX + (float)(Math.random() * screenX);
        
        // Check for fence collisions and adjust position
        boolean tooClose;
        do {
            tooClose = false;
            for (Obstacle fence : GameView.obstacles) {
                // If bone is too close to a fence, move it further
                if (Math.abs(fence.getX() - x) < screenX * 0.3f) { // 30% of screen width minimum distance
                    x = fence.getX() + screenX * 0.3f;
                    tooClose = true;
                    break;
                }
            }
        } while (tooClose);
        
        // Position bone just above the ground (60 pixels from bottom)
        y = screenY - bitmap.getHeight() - 65; // 5 pixels above ground
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
            y + bitmap.getHeight() * 0.1f, // 10% inset from top
            x + bitmap.getWidth() * 0.9f,  // 10% inset from right
            y + bitmap.getHeight() * 0.9f  // 10% inset from bottom
        );
    }

    public boolean isColliding(Player player) {
        return RectF.intersects(collisionBox, player.getCollisionBox());
    }

    // Add getter for x position
    public float getX() {
        return x;
    }
}