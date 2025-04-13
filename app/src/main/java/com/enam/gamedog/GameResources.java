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
    private Bitmap secondLevelBackground;
    private Bitmap thirdLevelBackground;
    private Bitmap dogIdleSprite;
    private Bitmap dogJumpSprite;
    private Bitmap fenceBitmap;
    private Bitmap boneBitmap;
    private Bitmap keyBitmap;
    private Bitmap gameOverWindow;
    private Bitmap winWindow;
    private Bitmap heartBitmap;

    public GameResources(Context context) {
        this.context = context;
        loadResources();
    }

    private void loadResources() {
        // Load backgrounds
        backgroundBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.background);
        backgroundBitmap = Bitmap.createScaledBitmap(
            backgroundBitmap,
            context.getResources().getDisplayMetrics().widthPixels,
            context.getResources().getDisplayMetrics().heightPixels,
            false
        );

        // Load level 2 background
        secondLevelBackground = BitmapFactory.decodeResource(context.getResources(), R.drawable.sec_l_bg);
        secondLevelBackground = Bitmap.createScaledBitmap(
            secondLevelBackground,
            context.getResources().getDisplayMetrics().widthPixels,
            context.getResources().getDisplayMetrics().heightPixels,
            false
        );

        // Load level 3 background
        thirdLevelBackground = BitmapFactory.decodeResource(context.getResources(), R.drawable.third_l_bg);
        thirdLevelBackground = Bitmap.createScaledBitmap(
            thirdLevelBackground,
            context.getResources().getDisplayMetrics().widthPixels,
            context.getResources().getDisplayMetrics().heightPixels,
            false
        );

        // Load window for game over and win screens
        Bitmap windowBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.window);
        
        // Create game over window (red tint)
        gameOverWindow = Bitmap.createBitmap(windowBitmap.getWidth(), windowBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(gameOverWindow);
        Paint paint = new Paint();
        paint.setColorFilter(null);
        canvas.drawBitmap(windowBitmap, 0, 0, paint);
        paint.setColor(Color.argb(120, 255, 0, 0)); // Semi-transparent red
        canvas.drawRect(0, 0, windowBitmap.getWidth(), windowBitmap.getHeight(), paint);

        // Create win window (golden tint)
        winWindow = Bitmap.createBitmap(windowBitmap.getWidth(), windowBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        canvas = new Canvas(winWindow);
        paint = new Paint();
        paint.setColorFilter(null);
        canvas.drawBitmap(windowBitmap, 0, 0, paint);
        paint.setColor(Color.argb(120, 255, 215, 0)); // Semi-transparent gold
        canvas.drawRect(0, 0, windowBitmap.getWidth(), windowBitmap.getHeight(), paint);

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

        // Load and scale fence - make it proportional to dog height
        fenceBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.fence);
        int fenceHeight = (int)(targetHeight * 0.5f); // Fence is 50% of dog height (increased from 40%)
        float fenceScale = (float) fenceHeight / fenceBitmap.getHeight();
        fenceBitmap = Bitmap.createScaledBitmap(
            fenceBitmap,
            (int)(fenceBitmap.getWidth() * fenceScale * 0.45f), // Slightly wider fence
            fenceHeight,
            true
        );

        // Load bone
        boneBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.bone);
        boneBitmap = Bitmap.createScaledBitmap(boneBitmap, 80, 80, false);

        // Load key
        keyBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.key);
        keyBitmap = Bitmap.createScaledBitmap(keyBitmap, 100, 100, false);

        //load sirt
        heartBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.heart);
        heartBitmap = Bitmap.createScaledBitmap(heartBitmap, 70, 70, false);
    }

    public Bitmap getBackgroundForLevel(int level) {
        if (level == 2) {
            return secondLevelBackground;
        } else if (level == 3) {
            return thirdLevelBackground;
        }
        return backgroundBitmap;
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

    public Bitmap getKeyBitmap() {
        return keyBitmap;
    }

    public Bitmap getGameOverWindow() {
        return gameOverWindow;
    }

    public Bitmap getWinWindow() {
        return winWindow;
    }

    public void recycle() {
        if (backgroundBitmap != null) backgroundBitmap.recycle();
        if (secondLevelBackground != null) secondLevelBackground.recycle();
        if (thirdLevelBackground != null) thirdLevelBackground.recycle();
        if (dogIdleSprite != null) dogIdleSprite.recycle();
        if (dogJumpSprite != null) dogJumpSprite.recycle();
        if (fenceBitmap != null) fenceBitmap.recycle();
        if (boneBitmap != null) boneBitmap.recycle();
        if (keyBitmap != null) keyBitmap.recycle();
        if (gameOverWindow != null) gameOverWindow.recycle();
        if (winWindow != null) winWindow.recycle();
    }
}