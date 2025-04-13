package com.enam.gamedog;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;

public class Key {
    private float x;
    private float y;
    private Bitmap sprite;
    private RectF collisionBox;

    public Key(float x, float y, Bitmap sprite) {
        this.x = x;
        this.y = y;
        this.sprite = sprite;
        updateCollisionBox();
    }

    public void reset(float x, float y) {
        this.x = x;
        this.y = y;
        updateCollisionBox();
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(sprite, x, y, null);
    }

    private void updateCollisionBox() {
        if (collisionBox == null) {
            collisionBox = new RectF();
        }

        collisionBox.set(
                x + sprite.getWidth() * 0.2f,
                y + sprite.getHeight() * 0.2f,
                x + sprite.getWidth() * 0.8f,
                y + sprite.getHeight() * 0.8f
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

    public float getWidth() {
        return sprite.getWidth();
    }

    public float getHeight() {
        return sprite.getHeight();
    }
}