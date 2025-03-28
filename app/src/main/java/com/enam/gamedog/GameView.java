package com.enam.gamedog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
    private GameThread thread;
    private boolean isPlaying;
    private boolean isGameOver;
    private SurfaceHolder holder;
    private Paint paint;
    private int screenX, screenY;
    private float screenRatioX, screenRatioY;
    private GameResources resources;
    private float backgroundX;
    private float backgroundScrollSpeed;
    private int currentLevel;

    // Game objects
    private Player player;
    public static Obstacle[] obstacles;  // Keep static for now
    private Treat[] treats;
    private int score;
    private int distanceToWin;

    public GameView(Context context, int level) {
        super(context);
        holder = getHolder();
        holder.addCallback(this);
        paint = new Paint();
        screenX = getResources().getDisplayMetrics().widthPixels;
        screenY = getResources().getDisplayMetrics().heightPixels;
        screenRatioX = 1920f / screenX;
        screenRatioY = 1080f / screenY;
        resources = new GameResources(context);
        currentLevel = level;
        backgroundX = 0;
        backgroundScrollSpeed = 4f;
        distanceToWin = 1000;

        initGame();
    }

    private void initGame() {
        player = new Player(screenX, screenY, resources);
        obstacles = new Obstacle[3];
        treats = new Treat[5];

        // Initialize obstacles with spacing
        for (int i = 0; i < obstacles.length; i++) {
            obstacles[i] = new Obstacle(screenX, screenY, resources.getFenceBitmap());
            if (i > 0) {
                // Ensure minimum spacing between obstacles
                obstacles[i].setX(obstacles[i-1].getX() + screenX);
            }
        }

        // Initialize treats
        for (int i = 0; i < treats.length; i++) {
            treats[i] = new Treat(screenX, screenY, resources.getBoneBitmap());
        }

        score = 0;
        isGameOver = false;
        isPlaying = true;
        backgroundX = 0;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        thread = new GameThread(holder, this);
        thread.setRunning(true);
        thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        while (retry) {
            try {
                thread.setRunning(false);
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void update() {
        if (!isGameOver && isPlaying) {
            // Update background position
            backgroundX -= backgroundScrollSpeed;
            if (backgroundX <= -screenX) {
                backgroundX = 0;
            }

            player.update();

            // Update obstacles
            for (Obstacle obstacle : obstacles) {
                obstacle.update();
                if (obstacle.isColliding(player)) {
                    isGameOver = true;
                    break;
                }
            }

            // Update treats
            for (Treat treat : treats) {
                treat.update();
                if (treat.isColliding(player)) {
                    score += 10;
                    treat.reset();
                }
            }
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void draw(Canvas canvas) {
        if (canvas != null) {
            canvas.drawColor(Color.WHITE);

            // Draw background
            Rect srcRect = new Rect(0, 0, resources.getBackgroundBitmap().getWidth(),
                    resources.getBackgroundBitmap().getHeight());
            RectF destRect = new RectF(backgroundX, 0,
                    backgroundX + screenX, screenY);
            canvas.drawBitmap(resources.getBackgroundBitmap(), srcRect, destRect, null);

            // Draw second background for seamless scrolling
            destRect.left = backgroundX + screenX;
            destRect.right = backgroundX + screenX * 2;
            canvas.drawBitmap(resources.getBackgroundBitmap(), srcRect, destRect, null);

            // Draw ground at the very bottom of the screen
            paint.setColor(Color.rgb(101, 67, 33));
            canvas.drawRect(0, screenY - 60, screenX, screenY, paint);

            // Draw game objects
            player.draw(canvas);

            for (Obstacle obstacle : obstacles) {
                obstacle.draw(canvas);
            }

            for (Treat treat : treats) {
                treat.draw(canvas);
            }

            // Draw score
            paint.setColor(Color.WHITE);
            paint.setTextSize(50);
            paint.setShadowLayer(3, 3, 3, Color.BLACK);
            canvas.drawText("Score: " + score, 50, 100, paint);
            canvas.drawText("Level: " + currentLevel, 50, 160, paint);

            if (isGameOver) {
                drawGameOver(canvas);
            }
        }
    }

    private void drawGameOver(Canvas canvas) {
        // Draw semi-transparent overlay
        paint.setColor(Color.argb(180, 0, 0, 0));
        canvas.drawRect(0, 0, screenX, screenY, paint);

        // Draw game over text
        paint.setColor(Color.WHITE);
        paint.setTextSize(70);
        paint.setTextAlign(Paint.Align.CENTER);
        String message = player.getX() >= distanceToWin ? "Level Complete!" : "Game Over";
        canvas.drawText(message, screenX/2, screenY/2 - 50, paint);
        paint.setTextSize(50);
        canvas.drawText("Score: " + score, screenX/2, screenY/2 + 50, paint);
        canvas.drawText("Tap to Restart", screenX/2, screenY/2 + 150, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (isGameOver) {
                    initGame();
                } else {
                    player.jump();
                }
                break;
        }
        return true;
    }

    public void pause() {
        isPlaying = false;
        try {
            thread.setRunning(false);
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void resume() {
        isPlaying = true;
        thread = new GameThread(holder, this);
        thread.setRunning(true);
        thread.start();
    }
}