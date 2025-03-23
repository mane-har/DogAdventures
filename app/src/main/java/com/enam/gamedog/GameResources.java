package com.enam.gamedog;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

public class GameResources {
    private Context context;
    private Bitmap backgroundBitmap;
    private Bitmap[] dogFrames;
    private Bitmap fenceBitmap;
    private Bitmap boneBitmap;

    public GameResources(Context context) {
        this.context = context;
        loadBitmaps();
    }

    private void loadBitmaps() {
        // Load background
        backgroundBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.background);
        
        // Load dog frames
        dogFrames = new Bitmap[4];
        for (int i = 0; i < 4; i++) {
            int resourceId = context.getResources().getIdentifier(
                "dog_frame_" + (i + 1), "drawable", context.getPackageName());
            dogFrames[i] = BitmapFactory.decodeResource(context.getResources(), resourceId);
        }
        
        // Load fence
        fenceBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.fence);
        
        // Load bone
        boneBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.bone);
    }

    public Bitmap getBackgroundBitmap() {
        return backgroundBitmap;
    }

    public Bitmap[] getDogFrames() {
        return dogFrames;
    }

    public Bitmap getFenceBitmap() {
        return fenceBitmap;
    }

    public Bitmap getBoneBitmap() {
        return boneBitmap;
    }

    public void recycle() {
        if (backgroundBitmap != null) backgroundBitmap.recycle();
        if (dogFrames != null) {
            for (Bitmap frame : dogFrames) {
                if (frame != null) frame.recycle();
            }
        }
        if (fenceBitmap != null) fenceBitmap.recycle();
        if (boneBitmap != null) boneBitmap.recycle();
    }
}