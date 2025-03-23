package com.enam.gamedog;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameView extends SurfaceView implements Runnable {
    private Thread gameThread;
    private volatile boolean isPlaying;
    private boolean isGameOver;
    private SurfaceHolder holder;
    private Paint paint;
    private int screenX, screenY;
    private float screenRatioX, screenRatioY;
    private GameResources resources;
    private float backgroundX;
    private float backgroundScrollSpeed;
    private float groundY;

    // Game objects
    private Player player;
    private Obstacle[] obstacles;
    private Treat[] treats;
    private int score;
    private int level;
    private int distanceToWin;

    public GameView(Context context) {
        super(context);
        holder = getHolder();
        paint = new Paint();
        screenX = getResources().getDisplayMetrics().widthPixels;
        screenY = getResources().getDisplayMetrics().heightPixels;
        screenRatioX = 1920f / screenX;
        screenRatioY = 1080f / screenY;
        resources = new GameResources(context);
        backgroundX = 0;
        backgroundScrollSpeed = 4f; // Increased speed
        groundY = screenY * 0.8f;
        distanceToWin = 1000; // Distance to reach to win

        initGame();
    }

    private void initGame() {
        player = new Player(screenX, screenY, resources);
        obstacles = new Obstacle[3];
        treats = new Treat[5];

        for (int i = 0; i < obstacles.length; i++) {
            obstacles[i] = new Obstacle(screenX, screenY, resources.getFenceBitmap());
        }

        for (int i = 0; i < treats.length; i++) {
            treats[i] = new Treat(screenX, screenY, resources.getBoneBitmap());
        }

        score = 0;
        level = 1;
        isGameOver = false;
        backgroundX = 0;
    }

    private void update() {
        if (!isGameOver) {
            // Update background position
            backgroundX -= backgroundScrollSpeed;

            // Reset background position when it scrolls off screen
            if (backgroundX <= -screenX) {
                backgroundX = 0;
            }

            player.update();

            // Update obstacles
            for (Obstacle obstacle : obstacles) {
                obstacle.update();
                if (obstacle.isColliding(player)) {
                    isGameOver = true;
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

            // Check if player reached the win distance
            if (player.getX() >= distanceToWin) {
                isGameOver = true;
            }
        }
    }

    private void draw() {
        if (holder.getSurface().isValid()) {
            Canvas canvas = holder.lockCanvas();

            // Draw scrolling background
            Rect srcRect = new Rect(0, 0, resources.getBackgroundBitmap().getWidth(),
                    resources.getBackgroundBitmap().getHeight());
            RectF destRect = new RectF(backgroundX, 0,
                    backgroundX + screenX, screenY);
            canvas.drawBitmap(resources.getBackgroundBitmap(), srcRect, destRect, null);

            // Draw second background for seamless scrolling
            destRect.left = backgroundX + screenX;
            destRect.right = backgroundX + screenX * 2;
            canvas.drawBitmap(resources.getBackgroundBitmap(), srcRect, destRect, null);

            // Draw ground platform
            paint.setColor(Color.rgb(101, 67, 33));  // Dark brown for main platform
            canvas.drawRect(0, groundY, screenX, groundY + 60, paint);

            // Draw top edge of ground
            paint.setColor(Color.rgb(139, 69, 19));  // Lighter brown for top edge
            canvas.drawRect(0, groundY, screenX, groundY + 5, paint);

            // Draw grass details
            paint.setColor(Color.rgb(34, 139, 34));  // Forest green
            float grassSpacing = 30;
            for (float x = 0; x < screenX; x += grassSpacing) {
                canvas.drawRect(x, groundY - 2, x + 4, groundY, paint);
            }

            // Draw game objects
            player.draw(canvas);

            for (Obstacle obstacle : obstacles) {
                obstacle.draw(canvas);
            }

            for (Treat treat : treats) {
                treat.draw(canvas);
            }

            // Draw score and level
            paint.setColor(Color.WHITE);
            paint.setTextSize(50);
            paint.setShadowLayer(3, 3, 3, Color.BLACK);
            canvas.drawText("Score: " + score, 50, 100, paint);
            canvas.drawText("Level: " + level, 50, 160, paint);

            // Draw progress bar
            float progress = player.getX() / distanceToWin;
            paint.setColor(Color.GREEN);
            canvas.drawRect(50, 200, 50 + (screenX - 100) * progress, 220, paint);
            paint.setColor(Color.WHITE);
            canvas.drawRect(50, 200, screenX - 50, 220, paint);

            if (isGameOver) {
                // Draw semi-transparent overlay for the whole screen
                paint.setColor(Color.argb(180, 0, 0, 0));
                canvas.drawRect(0, 0, screenX, screenY, paint);

                // Draw dialog window
                float windowWidth = screenX * 0.8f;
                float windowHeight = screenY * 0.4f;
                float windowX = (screenX - windowWidth) / 2;
                float windowY = (screenY - windowHeight) / 2;

                // Draw window background with border
                paint.setColor(Color.rgb(48, 48, 48));  // Dark gray background
                canvas.drawRect(windowX, windowY, windowX + windowWidth, windowY + windowHeight, paint);

                // Draw border
                paint.setColor(Color.rgb(200, 200, 200));  // Light gray border
                paint.setStrokeWidth(4);
                paint.setStyle(Paint.Style.STROKE);
                canvas.drawRect(windowX + 2, windowY + 2, windowX + windowWidth - 2, windowY + windowHeight - 2, paint);
                paint.setStyle(Paint.Style.FILL);

                // Draw header bar
                paint.setColor(Color.rgb(70, 70, 70));  // Slightly lighter gray for header
                canvas.drawRect(windowX, windowY, windowX + windowWidth, windowY + 60, paint);

                // Draw text
                paint.setColor(Color.WHITE);
                paint.setTextSize(60);
                paint.setTextAlign(Paint.Align.CENTER);
                canvas.drawText("Game Over!", screenX / 2, windowY + 45, paint);

                paint.setTextSize(50);
                String message = player.getX() >= distanceToWin ? "You Win!" : "Try Again!";
                canvas.drawText(message, screenX / 2, windowY + windowHeight/2, paint);

                // Draw score
                paint.setTextSize(40);
                canvas.drawText("Final Score: " + score, screenX / 2, windowY + windowHeight/2 + 60, paint);

                // Draw restart button
                float buttonWidth = windowWidth * 0.6f;
                float buttonHeight = 60;
                float buttonX = (screenX - buttonWidth) / 2;
                float buttonY = windowY + windowHeight - buttonHeight - 20;

                paint.setColor(Color.rgb(76, 175, 80));  // Material Design green
                canvas.drawRect(buttonX, buttonY, buttonX + buttonWidth, buttonY + buttonHeight, paint);

                paint.setColor(Color.WHITE);
                paint.setTextSize(40);
                canvas.drawText("Tap to Restart", screenX / 2, buttonY + 42, paint);
            }

            holder.unlockCanvasAndPost(canvas);
        }
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

    @Override
    public void run() {
        while (isPlaying) {
            update();
            draw();
            sleep();
        }
    }

    private void sleep() {
        try {
            Thread.sleep(17); // Approximately 60 FPS
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void resume() {
        isPlaying = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void pause() {
        isPlaying = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (resources != null) {
            resources.recycle();
        }
    }
}