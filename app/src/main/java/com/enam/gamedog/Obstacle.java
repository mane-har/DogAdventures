package com.enam.gamedog;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;

public class Obstacle {
    private float x, y;
    private float speed;
    private float screenX, screenY;
    private Bitmap bitmap;
    private RectF collisionBox;

    public Obstacle(float screenX, float screenY, Bitmap bitmap) {
        this.screenX = screenX;
        this.screenY = screenY;
        this.bitmap = bitmap;
        this.speed = 5f;
        this.collisionBox = new RectF();
        reset();
    }

    public void reset() {
        x = screenX + bitmap.getWidth();
        y = screenY * 0.8f;
        updateCollisionBox();
    }

    public void update() {
        x -= speed;
        if (x < -bitmap.getWidth()) {
            reset();
        }
        updateCollisionBox();
    }

    public void draw(Canvas canvas) {
        Rect srcRect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        RectF destRect = new RectF(x, y, x + bitmap.getWidth() * 0.5f, y + bitmap.getHeight() * 0.5f);
        canvas.drawBitmap(bitmap, srcRect, destRect, null);
    }

    private void updateCollisionBox() {
        float width = bitmap.getWidth() * 0.5f;
        float height = bitmap.getHeight() * 0.5f;
        collisionBox.set(x, y, x + width, y + height);
    }

    public boolean isColliding(Player player) {
        return RectF.intersects(collisionBox, player.getCollisionBox());
    }
}