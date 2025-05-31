package com.enam.gamedog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Iterator;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
    private GameThread thread;
    private boolean isPlaying;
    private boolean isGameOver;
    private boolean levelCompleted;
    private SurfaceHolder holder;
    private Paint paint;
    private int screenX, screenY;
    private float screenRatioX, screenRatioY;
    private GameResources resources;
    private float backgroundX;
    private float gameSpeed = 20f;
    private float maxGameSpeed = 40f;
    private float speedIncrease = 0.002f;
    private int currentLevel;
    private float playerDistance = 0;
    private boolean isGamePaused = false;
    private float speedIncreaseInterval = 1500f;

    private Player player;
    private Obstacle[] obstacles;
    private Treat[] treats;
    private Key levelKey;

    private boolean isKeyVisible = false;
    private float backgroundSpeed = 20f;
    private float maxBackgroundSpeed = 40f;
    private float backgroundSpeedIncreaseRate = 0.002f;
    private float lastSpeedIncreaseTime = 0;
    private float speedIncreaseRate = 0.002f;
    private int obstaclesPassed = 0;
    private int obstaclesUntilKey;
    private boolean isGameWon = false;
    private Bitmap heartBitmap;
    private int playerHealth = 3;

    private boolean isTouchingScreen = false;
    private float playerMoveSpeed = 15f;

    // Reusable objects to reduce garbage collection
    private RectF playerBox = new RectF();
    private RectF obstacleBox = new RectF();
    private RectF intersectionBox = new RectF();
    private Rect srcRect = new Rect();
    private RectF destRect = new RectF();

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
        heartBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.heart);
        heartBitmap = Bitmap.createScaledBitmap(heartBitmap, 80, 80, true);

        initGame();
    }

    private void initGame() {
        player = new Player(screenX, screenY, resources);
        obstacles = new Obstacle[12];
        treats = new Treat[5];
        levelKey = new Key(screenX, screenY, resources.getKeyBitmap());
        
        float baseSpacing = 900f;
        float randomVariation = 200f;
        
        if (currentLevel >= 4) {
            gameSpeed = 22f;
            maxGameSpeed = 35f;
            backgroundSpeed = 22f;
            maxBackgroundSpeed = 35f;
            obstaclesUntilKey = 15;
            speedIncreaseRate = 0.002f;
            backgroundSpeedIncreaseRate = 0.002f;
            
            float lastBottomX = screenX + 100;
            int bottomObstacleCount = 0;
            
            for (int i = 0; i < 6; i++) {
                obstacles[i] = new Obstacle(screenX, screenY, resources.getFenceBitmap());
                float spacing = baseSpacing + ((float)Math.random() * randomVariation);
                obstacles[i].setPosition(lastBottomX + spacing);
                obstacles[i].setTopGround(false);
                lastBottomX += spacing;
                bottomObstacleCount++;
            }
            
            int topObstacleCount = 0;
            for (int i = 0; i < bottomObstacleCount - 1; i++) {
                if (Math.random() < 0.5 && topObstacleCount < 6) {
                    obstacles[6 + topObstacleCount] = new Obstacle(screenX, screenY, resources.getFenceBitmap());
                    float midPoint = (obstacles[i].getX() + obstacles[i + 1].getX()) * 0.5f;
                    float randomOffset = ((float)Math.random() * randomVariation) - (randomVariation * 0.5f);
                    obstacles[6 + topObstacleCount].setPosition(midPoint + randomOffset);
                    obstacles[6 + topObstacleCount].setTopGround(true);
                    topObstacleCount++;
                }
            }
        } else {
            for (int i = 0; i < obstacles.length; i++) {
                obstacles[i] = new Obstacle(screenX, screenY, resources.getFenceBitmap());
                if (i > 0) {
                    float randomSpacing;
                    float random = (float) Math.random();
                    if (random < 0.2) {
                        randomSpacing = 800f + ((float)Math.random() * 300f);
                    } else if (random < 0.6) {
                        randomSpacing = 1000f + ((float)Math.random() * 400f);
                    } else {
                        randomSpacing = 1300f + ((float)Math.random() * 500f);
                    }
                    obstacles[i].setPosition(obstacles[i-1].getX() + randomSpacing);
                } else {
                    obstacles[i].setPosition(screenX + 500);
                }
            }
        }

        for (int i = 0; i < treats.length; i++) {
            treats[i] = new Treat(screenX, screenY, resources.getBoneBitmap());
        }

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
        screenX = width;
        screenY = height;
        screenRatioX = 1920f / screenX;
        screenRatioY = 1080f / screenY;
        initGame();
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
        if (isGamePaused) return;

        float currentTime = System.currentTimeMillis();
        if (currentTime - lastSpeedIncreaseTime > speedIncreaseInterval && !isKeyVisible) {
            gameSpeed = Math.min(gameSpeed + speedIncreaseRate, maxGameSpeed);
            backgroundSpeed = Math.min(backgroundSpeed + backgroundSpeedIncreaseRate, maxBackgroundSpeed);
            lastSpeedIncreaseTime = currentTime;
        }

        if (!isKeyVisible) {
            backgroundX -= backgroundSpeed;
            if (backgroundX <= -screenX) {
                backgroundX = 0;
            }
        }

        player.update();
        
        if (isKeyVisible) {
            player.startMovingForward();
            player.updateForwardMovement();
        }

        if (!isKeyVisible) {
            for (int i = 0; i < obstacles.length; i++) {
                if (obstacles[i] != null) {
                    obstacles[i].update(gameSpeed);
                    
                    if (player.getCollisionBox().intersect(obstacles[i].getCollisionBox())) {
                        playerBox.set(player.getCollisionBox());
                        obstacleBox.set(obstacles[i].getCollisionBox());
                        
                        intersectionBox.left = Math.max(playerBox.left, obstacleBox.left);
                        intersectionBox.top = Math.max(playerBox.top, obstacleBox.top);
                        intersectionBox.right = Math.min(playerBox.right, obstacleBox.right);
                        intersectionBox.bottom = Math.min(playerBox.bottom, obstacleBox.bottom);
                        
                        float intersectionWidth = intersectionBox.width();
                        float intersectionHeight = intersectionBox.height();
                        
                        if (intersectionWidth > 0 && intersectionHeight > 0) {
                            float intersectionArea = intersectionWidth * intersectionHeight;
                            float playerArea = playerBox.width() * playerBox.height();
                            
                            float collisionThreshold = player.isJumping() ? 0.2f : 0.3f;
                            
                            if (intersectionArea > playerArea * collisionThreshold) {
                                takeDamage();
                                obstacles[i].setPosition(-obstacles[i].getWidth());
                            }
                        }
                    }

                    if (obstacles[i].getX() + obstacles[i].getWidth() < 0) {
                        obstacles[i] = null;
                        obstaclesPassed++;
                        
                        if (obstaclesPassed >= obstaclesUntilKey) {
                            isKeyVisible = true;
                            levelKey.reset(screenX - 100, screenY - levelKey.getHeight() - 100);
                            for (int j = 0; j < obstacles.length; j++) {
                                if (obstacles[j] != null) {
                                    obstacles[j].setShouldReset(false);
                                }
                            }
                            return;
                        }

                        if (obstaclesPassed < obstaclesUntilKey) {
                            float maxX = 0;
                            for (Obstacle obs : obstacles) {
                                if (obs != null) {
                                    maxX = Math.max(maxX, obs.getX());
                                }
                            }
                            
                            obstacles[i] = new Obstacle(screenX, screenY, resources.getFenceBitmap());
                            float randomSpacing;
                            float random = (float) Math.random();
                            if (random < 0.2) {
                                randomSpacing = 800f + (float)(Math.random() * 300f);
                            } else if (random < 0.6) {
                                randomSpacing = 1000f + (float)(Math.random() * 400f);
                            } else {
                                randomSpacing = 1300f + (float)(Math.random() * 500f);
                            }

                            if (currentLevel >= 4) {
                                if (i % 2 == 0) {
                                    obstacles[i].setPosition(maxX + randomSpacing);
                                    obstacles[i].setTopGround(true);
                                } else {
                                    obstacles[i].setPosition(maxX + randomSpacing);
                                    obstacles[i].setTopGround(false);
                                }
                            } else {
                                obstacles[i].setPosition(maxX + randomSpacing);
                            }
                        }
                    }
                }
            }
        }

        if (isKeyVisible && player.getCollisionBox().intersect(levelKey.getCollisionBox())) {
            levelCompleted = true;
            isGameWon = true;
            isPlaying = false;
        }

        if (isKeyVisible && player.getX() > screenX) {
            gameOver();
        }
    }

    private void gameOver() {
        isGameOver = true;
        isPlaying = false;
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void draw(Canvas canvas) {
        if (canvas != null) {
            canvas.drawColor(Color.WHITE);

            Bitmap currentBackground = resources.getBackgroundForLevel(currentLevel);
            if (currentBackground != null) {
                int bgWidth = currentBackground.getWidth();
                int bgHeight = currentBackground.getHeight();
                
                srcRect.set(0, 0, bgWidth, bgHeight);
                destRect.set(backgroundX, 0, backgroundX + screenX, screenY);
                
                canvas.drawBitmap(currentBackground, srcRect, destRect, null);

                destRect.left = backgroundX + screenX;
                destRect.right = backgroundX + screenX * 2;
                canvas.drawBitmap(currentBackground, srcRect, destRect, null);
            }

            paint.setColor(Color.rgb(101, 67, 33));
            if (currentLevel >= 4) {
                float topGroundY = screenY * 0.02f;
                canvas.drawRect(0, topGroundY - 120, screenX, topGroundY, paint);
                canvas.drawRect(0, screenY - 60, screenX, screenY, paint);
            } else {
                canvas.drawRect(0, screenY - 60, screenX, screenY, paint);
            }

            if (player != null) {
                player.draw(canvas);
            }
            
            if (!isKeyVisible && obstacles != null) {
                for (Obstacle obstacle : obstacles) {
                    if (obstacle != null) {
                        obstacle.draw(canvas);
                    }
                }
            }

            if (treats != null) {
                for (Treat treat : treats) {
                    if (treat != null) {
                        treat.draw(canvas);
                    }
                }
            }

            if (isKeyVisible && levelKey != null) {
                levelKey.draw(canvas);
            }

            paint.setColor(Color.WHITE);
            paint.setTextSize(50);
            paint.setShadowLayer(3, 3, 3, Color.BLACK);
            canvas.drawText("Level: " + currentLevel, 50, 160, paint);

            if (heartBitmap != null) {
                int heartWidth = heartBitmap.getWidth();
                int heartSpacing = heartWidth + 10;
                for (int i = 0; i < playerHealth; i++) {
                    canvas.drawBitmap(heartBitmap, 20 + i * heartSpacing, 20, null);
                }
            }

            if (isGameOver) {
                drawGameOver(canvas);
            } else if (isGameWon) {
                drawGameWon(canvas);
            }
        }
    }

    private void drawGameOver(Canvas canvas) {
        paint.setColor(Color.argb(120, 0, 0, 0));
        canvas.drawRect(0, 0, screenX, screenY, paint);

        float windowWidth = screenX * 0.8f;
        float windowHeight = screenY * 0.4f;
        float windowX = (screenX - windowWidth) / 2;
        float windowY = (screenY - windowHeight) / 2;

        destRect.set(windowX, windowY, windowX + windowWidth, windowY + windowHeight);
        paint.setColor(Color.WHITE);
        paint.setAlpha(255);
        canvas.drawBitmap(resources.getGameOverWindow(), null, destRect, paint);

        paint.setColor(Color.WHITE);
        paint.setTextSize(windowHeight * 0.3f);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setShadowLayer(10, 5, 5, Color.BLACK);
        canvas.drawText("GAME OVER", screenX/2, windowY + windowHeight * 0.4f, paint);

        paint.setTextSize(windowHeight * 0.15f);
        canvas.drawText("Tap to Try Again", screenX/2, windowY + windowHeight * 0.7f, paint);
    }

    private void drawGameWon(Canvas canvas) {
        paint.setColor(Color.argb(120, 0, 0, 0));
        canvas.drawRect(0, 0, screenX, screenY, paint);

        float windowWidth = screenX * 0.8f;
        float windowHeight = screenY * 0.4f;
        float windowX = (screenX - windowWidth) / 2;
        float windowY = (screenY - windowHeight) / 2;

        destRect.set(windowX, windowY, windowX + windowWidth, windowY + windowHeight);
        paint.setColor(Color.WHITE);
        paint.setAlpha(255);
        canvas.drawBitmap(resources.getWinWindow(), null, destRect, paint);

        paint.setColor(Color.rgb(255, 215, 0));
        paint.setTextSize(windowHeight * 0.25f);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setShadowLayer(10, 5, 5, Color.BLACK);
        canvas.drawText("YOU WIN!", screenX/2, windowY + windowHeight * 0.4f, paint);

        paint.setColor(Color.WHITE);
        paint.setTextSize(windowHeight * 0.15f);
        canvas.drawText("Level " + currentLevel + " Complete!", screenX/2, windowY + windowHeight * 0.6f, paint);
        canvas.drawText("Tap to Continue", screenX/2, windowY + windowHeight * 0.8f, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isTouchingScreen = true;
                if (isGameOver) {
                    resetGame();
                } else if (isPlaying) {
                    if (currentLevel >= 4) {
                        player.switchGround();
                    } else {
                        player.jump();
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                isTouchingScreen = false;
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

    private void resetGame() {
        playerHealth = 3;
        isGameOver = false;
        isPlaying = true;
        initGame();
    }

    private void takeDamage() {
        if (!isGameOver) {
            playerHealth--;
            if (playerHealth <= 0) {
                playerHealth = 0;
                gameOver();
            }
        }
    }
}