package com.enam.gamedog;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;

public abstract class GameObject {
    protected float x, y;
    protected float width, height;
    protected int screenX, screenY;
    protected Bitmap bitmap;
    protected SpriteAnimation animation;
    protected boolean isFlipped;

    public GameObject(int screenX, int screenY, Bitmap bitmap) {
        this.screenX = screenX;
        this.screenY = screenY;
        this.bitmap = bitmap;
        width = screenX * 0.15f;
        height = screenY * 0.15f;
        isFlipped = false;
    }

    public abstract void update();
    public abstract void reset();

    public void draw(Canvas canvas) {
        if (animation != null) {

            Rect sourceRect = animation.getCurrentFrameRect();
            RectF destRect = new RectF(x, y - height, x + width, y);

            if (isFlipped) {
                canvas.save();
                canvas.scale(-1, 1, x + width/2, y - height/2);
                canvas.drawBitmap(animation.getSpriteSheet(), sourceRect, destRect, null);
                canvas.restore();
            } else {
                canvas.drawBitmap(animation.getSpriteSheet(), sourceRect, destRect, null);
            }
        } else if (bitmap != null) {

            RectF destRect = new RectF(x, y - height, x + width, y);
            if (isFlipped) {
                canvas.save();
                canvas.scale(-1, 1, x + width/2, y - height/2);
                canvas.drawBitmap(bitmap, null, destRect, null);
                canvas.restore();
            } else {
                canvas.drawBitmap(bitmap, null, destRect, null);
            }
        }
    }

    public boolean isColliding(GameObject other) {
        return x < other.getX() + other.getWidth() &&
                x + width > other.getX() &&
                y - height < other.getY() &&
                y > other.getY() - other.getHeight();
    }

    public float getX() { return x; }
    public float getY() { return y; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }

    public void setFlipped(boolean flipped) {
        this.isFlipped = flipped;
    }
}