package com.enam.gamedog;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

public class Owner extends GameObject {
    private float groundY;

    public Owner(int screenX, int screenY, Bitmap bitmap) {
        super(screenX, screenY, bitmap);
        groundY = screenY * 0.8f;
        reset();
    }

    public void reset() {
        x = screenX * 0.8f;
        y = groundY;
    }

    public void update() {
        // Owner stays in place, just update position if needed
    }

    public void draw(Canvas canvas, Paint paint) {

        paint.setColor(Color.rgb(70, 130, 180)); // Steel blue color
        canvas.drawRect(x, y - height, x + width, y, paint);


        float headSize = width * 0.8f;
        canvas.drawRect(x + width * 0.1f, y - height * 1.2f,
                x + width * 0.9f, y - height * 0.4f, paint);


        float armWidth = width * 0.3f;
        float armHeight = height * 0.4f;
        canvas.drawRect(x - armWidth * 0.5f, y - height * 0.8f,
                x + armWidth * 0.5f, y - height * 0.4f, paint);
        canvas.drawRect(x + width - armWidth * 0.5f, y - height * 0.8f,
                x + width + armWidth * 0.5f, y - height * 0.4f, paint);


        float legWidth = width * 0.2f;
        float legHeight = height * 0.3f;
        canvas.drawRect(x + width * 0.2f, y - legHeight,
                x + width * 0.4f, y, paint);
        canvas.drawRect(x + width * 0.6f, y - legHeight,
                x + width * 0.8f, y, paint);
    }
}
