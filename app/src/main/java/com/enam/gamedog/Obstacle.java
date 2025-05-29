package com.enam.gamedog;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;

public class Obstacle {
    private float x, y;
    private float width, height;
    private Bitmap bitmap;
    private boolean isTopGround;
    private boolean shouldReset = true;

    public Obstacle(int screenX, int screenY, Bitmap bitmap) {
        this.bitmap = bitmap;
        this.width = bitmap.getWidth();
        this.height = bitmap.getHeight();
        this.x = screenX;
        this.y = screenY - height - 60; // Default position for bottom ground
    }

    public void update(float speed) {
        x -= speed;
    }

    public void draw(Canvas canvas) {
        if (bitmap != null) {
            canvas.drawBitmap(bitmap, x, y, null);
        }
    }

    public void setPosition(float x) {
        this.x = x;
    }

    public void setTopGround(boolean isTopGround) {
        this.isTopGround = isTopGround;
        if (isTopGround) {
            // Position for top ground (2% of screen height)
            this.y = 20; // Keep top ground position the same
        } else {
            // Position for bottom ground
            this.y = 920; // Lowered from 900 to 920 for bottom ground
        }
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public RectF getCollisionBox() {
        return new RectF(x, y, x + width, y + height);
    }

    public void setShouldReset(boolean shouldReset) {
        this.shouldReset = shouldReset;
    }

    public boolean shouldReset() {
        return shouldReset;
    }
}