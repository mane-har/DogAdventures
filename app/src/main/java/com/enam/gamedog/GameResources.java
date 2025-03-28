package com.enam.gamedog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import java.io.IOException;
import java.io.InputStream;

public class GameResources {
    private Context context;
    private Bitmap backgroundBitmap;
    private Bitmap dogIdleSprite;
    private Bitmap dogJumpSprite;
    private Bitmap fenceBitmap;
    private Bitmap boneBitmap;

    public GameResources(Context context) {
        this.context = context;
        loadResources();
    }

    private void loadResources() {
        // Load background
        backgroundBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.background);
        backgroundBitmap = Bitmap.createScaledBitmap(
            backgroundBitmap,
            backgroundBitmap.getWidth() * 2,
            backgroundBitmap.getHeight() * 2,
            true
        );

        // Load dog sprites
        dogIdleSprite = BitmapFactory.decodeResource(context.getResources(), R.drawable.dog_idle);
        dogJumpSprite = BitmapFactory.decodeResource(context.getResources(), R.drawable.dog_jump);

        // Scale dog sprites - make them larger (1/5 of screen height)
        int targetHeight = context.getResources().getDisplayMetrics().heightPixels / 5;
        float scale = (float) targetHeight / dogIdleSprite.getHeight();
        
        dogIdleSprite = Bitmap.createScaledBitmap(
            dogIdleSprite,
            (int)(dogIdleSprite.getWidth() * scale),
            (int)(dogIdleSprite.getHeight() * scale),
            true
        );
        
        dogJumpSprite = Bitmap.createScaledBitmap(
            dogJumpSprite,
            (int)(dogJumpSprite.getWidth() * scale),
            (int)(dogJumpSprite.getHeight() * scale),
            true
        );

        fenceBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.fence);
        boneBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.bone);
        
        // Scale fence - make it taller (1/4 of screen height)
        int fenceHeight = context.getResources().getDisplayMetrics().heightPixels / 15 ;
        float fenceScale = (float) fenceHeight / fenceBitmap.getHeight();
        fenceBitmap = Bitmap.createScaledBitmap(
            fenceBitmap,
            (int)(fenceBitmap.getWidth() * fenceScale),
            fenceHeight,
            true
        );
        
        // Scale bone - make it smaller (1/16 of screen height)
        int boneHeight = context.getResources().getDisplayMetrics().heightPixels / 12;
        float boneScale = (float) boneHeight / boneBitmap.getHeight();
        boneBitmap = Bitmap.createScaledBitmap(
            boneBitmap,
            (int)(boneBitmap.getWidth() * boneScale),
            boneHeight,
            true
        );

        
    }

    public Bitmap getBackgroundBitmap() {
        return backgroundBitmap;
    }

    public Bitmap getDogIdleSprite() {
        return dogIdleSprite;
    }

    public Bitmap getDogJumpSprite() {
        return dogJumpSprite;
    }

    public Bitmap getFenceBitmap() {
        return fenceBitmap;
    }

    public Bitmap getBoneBitmap() {
        return boneBitmap;
    }

    public void recycle() {
        if (backgroundBitmap != null) backgroundBitmap.recycle();
        if (dogIdleSprite != null) dogIdleSprite.recycle();
        if (dogJumpSprite != null) dogJumpSprite.recycle();
        if (fenceBitmap != null) fenceBitmap.recycle();
        if (boneBitmap != null) boneBitmap.recycle();
    }
}