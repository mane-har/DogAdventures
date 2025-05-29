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
    private Bitmap fourthLevelBackground;
    private Bitmap fifthLevelBackground;
    private Bitmap sixthLevelBackground;
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

        // Load level 4 background
        fourthLevelBackground = BitmapFactory.decodeResource(context.getResources(), R.drawable.second_w_first);
        fourthLevelBackground = Bitmap.createScaledBitmap(
            fourthLevelBackground,
            context.getResources().getDisplayMetrics().widthPixels,
            context.getResources().getDisplayMetrics().heightPixels,
            false
        );

        // Load level 5 background
        fifthLevelBackground = BitmapFactory.decodeResource(context.getResources(), R.drawable.second_w_second);
        fifthLevelBackground = Bitmap.createScaledBitmap(
            fifthLevelBackground,
            context.getResources().getDisplayMetrics().widthPixels,
            context.getResources().getDisplayMetrics().heightPixels,
            false
        );

        // Load level 6 background
        sixthLevelBackground = BitmapFactory.decodeResource(context.getResources(), R.drawable.second_w_third);
        sixthLevelBackground = Bitmap.createScaledBitmap(
            sixthLevelBackground,
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

        // Scale dog sprites - make them larger (1/4 of screen height for better quality)
        int targetHeight = context.getResources().getDisplayMetrics().heightPixels / 4;
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
        int fenceHeight = (int)(targetHeight * 0.4f); // Reduced from 0.6f to 0.4f (40% of dog height)
        float fenceScale = (float) fenceHeight / fenceBitmap.getHeight();
        fenceBitmap = Bitmap.createScaledBitmap(
            fenceBitmap,
            (int)(fenceBitmap.getWidth() * fenceScale * 0.9f), // Reduced from 0.5f to 0.4f for narrower fence
            fenceHeight,
            true
        );

        // Load bone with better quality
        boneBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.bone);
        boneBitmap = Bitmap.createScaledBitmap(boneBitmap, 100, 100, true); // Increased size and using true for better quality

        // Load key with better quality
        keyBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.key);
        keyBitmap = Bitmap.createScaledBitmap(keyBitmap, 120, 120, true); // Increased size and using true for better quality

        // Load heart with better quality
        heartBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.heart);
        heartBitmap = Bitmap.createScaledBitmap(heartBitmap, 80, 80, true); // Increased size and using true for better quality
    }

    public Bitmap getBackgroundForLevel(int level) {
        switch (level) {
            case 2:
                return secondLevelBackground;
            case 3:
                return thirdLevelBackground;
            case 4:
                return fourthLevelBackground;
            case 5:
                return fifthLevelBackground;
            case 6:
                return sixthLevelBackground;
            default:
                return backgroundBitmap;
        }
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
        if (fourthLevelBackground != null) fourthLevelBackground.recycle();
        if (fifthLevelBackground != null) fifthLevelBackground.recycle();
        if (sixthLevelBackground != null) sixthLevelBackground.recycle();
        if (dogIdleSprite != null) dogIdleSprite.recycle();
        if (dogJumpSprite != null) dogJumpSprite.recycle();
        if (fenceBitmap != null) fenceBitmap.recycle();
        if (boneBitmap != null) boneBitmap.recycle();
        if (keyBitmap != null) keyBitmap.recycle();
        if (gameOverWindow != null) gameOverWindow.recycle();
        if (winWindow != null) winWindow.recycle();
    }
}