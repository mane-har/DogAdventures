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
    private float gameSpeed = 15f;        // Increased from 12f for faster initial speed
    private float maxGameSpeed = 35f;    // Increased from 25f for faster maximum speed
    private float speedIncrease = 0.003f; // Increased from 0.002f for faster progression
    private int currentLevel;
    private float playerDistance = 0;    // Track total distance
    private boolean isGamePaused = false;
    private float speedIncreaseInterval = 800f; // Decreased from 1000f to increase speed more frequently

    // Game objects
    private Player player;
    private Obstacle[] obstacles;
    private Treat[] treats;
    private Key levelKey;

    private boolean isKeyVisible = false;
    private float backgroundSpeed = 15f;  // Increased from 12f to match game speed
    private float maxBackgroundSpeed = 35f;  // Increased from 25f to match max game speed
    private float backgroundSpeedIncreaseRate = 0.002f;
    private float lastSpeedIncreaseTime = 0;
    private float speedIncreaseRate = 0.002f;
    private int obstaclesPassed = 0;
    private int obstaclesUntilKey = 15;  // Increased from 10 to make game longer
    private boolean isGameWon = false;
    private Bitmap heartBitmap;
    private int playerHealth = 3; // starts with 3 hearts




    private boolean isTouchingScreen = false;
    private float playerMoveSpeed = 15f; // Speed at which player moves when touching screen

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

        // Initialize game immediately
        initGame();
    }

    private void initGame() {
        player = new Player(screenX, screenY, resources);
        obstacles = new Obstacle[8];  // 8 obstacles total (4 for each ground)
        treats = new Treat[5];
        levelKey = new Key(screenX, screenY, resources.getKeyBitmap());
        
        if (currentLevel >= 4) {
            // Levels 4-6 have different mechanics
            gameSpeed = 18f;
            maxGameSpeed = 32f;
            backgroundSpeed = 18f;
            maxBackgroundSpeed = 32f;
        } else if (currentLevel == 3) {
            gameSpeed = 22f;
            maxGameSpeed = 40f;
            backgroundSpeed = 22f;
            maxBackgroundSpeed = 40f;
        } else if (currentLevel == 2) {
            gameSpeed = 20f;
            maxGameSpeed = 38f;
            backgroundSpeed = 20f;
            maxBackgroundSpeed = 38f;
        } else {
            gameSpeed = 15f;
            maxGameSpeed = 35f;
            backgroundSpeed = 15f;
            maxBackgroundSpeed = 35f;
        }
        
        playerDistance = 0;
        levelCompleted = false;
        isKeyVisible = false;
        obstaclesPassed = 0;
        isGameWon = false;

        // Initialize obstacles
        if (currentLevel >= 4) {
            // Create obstacles for both grounds simultaneously
            float lastTopX = screenX + 100;
            float lastBottomX = screenX + 100;
            
            for (int i = 0; i < obstacles.length; i++) {
                obstacles[i] = new Obstacle(screenX, screenY, resources.getFenceBitmap());
                
                float randomSpacing;
                float random = (float) Math.random();
                if (random < 0.2) {
                    randomSpacing = 800f + (float)(Math.random() * 300f);  // Increased from 400f + 200f
                } else if (random < 0.6) {
                    randomSpacing = 1000f + (float)(Math.random() * 400f);  // Increased from 600f + 300f
                } else {
                    randomSpacing = 1300f + (float)(Math.random() * 500f);  // Increased from 900f + 400f
                }
                
                // Alternate between top and bottom ground
                if (i % 2 == 0) {
                    // Top ground obstacle
                    obstacles[i].setPosition(lastTopX + randomSpacing);
                    obstacles[i].setTopGround(true);
                    lastTopX = lastTopX + randomSpacing;
                } else {
                    // Bottom ground obstacle
                    obstacles[i].setPosition(lastBottomX + randomSpacing);
                    obstacles[i].setTopGround(false);
                    lastBottomX = lastBottomX + randomSpacing;
                }
            }
        } else {
            // Original obstacle generation for levels 1-3
            for (int i = 0; i < obstacles.length; i++) {
                obstacles[i] = new Obstacle(screenX, screenY, resources.getFenceBitmap());
                if (i > 0) {
                    float randomSpacing;
                    float random = (float) Math.random();
                    if (random < 0.2) {
                        randomSpacing = 800f + (float)(Math.random() * 300f);  // Increased from 400f + 200f
                    } else if (random < 0.6) {
                        randomSpacing = 1000f + (float)(Math.random() * 400f);  // Increased from 600f + 300f
                    } else {
                        randomSpacing = 1300f + (float)(Math.random() * 500f);  // Increased from 900f + 400f
                    }
                    obstacles[i].setPosition(obstacles[i-1].getX() + randomSpacing);
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
        // Create and start the game thread
        thread = new GameThread(holder, this);
        thread.setRunning(true);
        thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // Update screen dimensions if needed
        screenX = width;
        screenY = height;
        screenRatioX = 1920f / screenX;
        screenRatioY = 1080f / screenY;
        
        // Reinitialize game with new dimensions
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

        // Update game speed only if key is not visible
        float currentTime = System.currentTimeMillis();
        if (currentTime - lastSpeedIncreaseTime > speedIncreaseInterval && !isKeyVisible) {
            gameSpeed = Math.min(gameSpeed + speedIncreaseRate, maxGameSpeed);
            backgroundSpeed = Math.min(backgroundSpeed + backgroundSpeedIncreaseRate, maxBackgroundSpeed);
            lastSpeedIncreaseTime = currentTime;
        }

        // Update background only if key is not visible
        if (!isKeyVisible) {
            backgroundX -= backgroundSpeed;
            if (backgroundX <= -screenX) {
                backgroundX = 0;
            }
        }

        // Update player
        player.update();
        
        // When key is visible, continuously move the player forward
        if (isKeyVisible) {
            player.setX(player.getX() + 10f);
        }

        // Update obstacles only if key is not visible
        if (!isKeyVisible) {
            for (int i = 0; i < obstacles.length; i++) {
                if (obstacles[i] != null) {
                    obstacles[i].update(gameSpeed);
                    
                    // Check collision with more precise detection
                    if (player.getCollisionBox().intersect(obstacles[i].getCollisionBox())) {
                        // Only take damage if the collision is significant
                        RectF playerBox = player.getCollisionBox();
                        RectF obstacleBox = obstacles[i].getCollisionBox();
                        
                        // Calculate intersection area
                        float intersectionLeft = Math.max(playerBox.left, obstacleBox.left);
                        float intersectionTop = Math.max(playerBox.top, obstacleBox.top);
                        float intersectionRight = Math.min(playerBox.right, obstacleBox.right);
                        float intersectionBottom = Math.min(playerBox.bottom, obstacleBox.bottom);
                        
                        float intersectionWidth = intersectionRight - intersectionLeft;
                        float intersectionHeight = intersectionBottom - intersectionTop;
                        
                        // Only trigger collision if intersection is significant
                        if (intersectionWidth > 0 && intersectionHeight > 0) {
                            float intersectionArea = intersectionWidth * intersectionHeight;
                            float playerArea = playerBox.width() * playerBox.height();
                            
                            // Adjust collision threshold based on player state
                            float collisionThreshold = player.isJumping() ? 0.2f : 0.3f;
                            
                            // If intersection is more than threshold of player's area, trigger collision
                            if (intersectionArea > playerArea * collisionThreshold) {
                                takeDamage();
                                // Move the obstacle off screen after collision
                                obstacles[i].setPosition(-obstacles[i].getWidth());
                                return;
                            }
                        }
                    }

                    // Remove obstacles that are off screen
                    if (obstacles[i].getX() + obstacles[i].getWidth() < 0) {
                        obstacles[i] = null;
                        obstaclesPassed++;
                        
                        // Check if we should show the key
                        if (obstaclesPassed >= obstaclesUntilKey) {
                            isKeyVisible = true;
                            // Place key at the end of the screen
                            levelKey.reset(screenX - 100, screenY - levelKey.getHeight() - 100);
                            // Stop all obstacles
                            for (int j = 0; j < obstacles.length; j++) {
                                if (obstacles[j] != null) {
                                    obstacles[j].setShouldReset(false);
                                }
                            }
                            return;
                        }

                        // Generate new obstacle only if we haven't reached the key yet
                        if (obstaclesPassed < obstaclesUntilKey) {
                            // Find the rightmost obstacle for each ground
                            float maxTopX = 0;
                            float maxBottomX = 0;
                            for (Obstacle obs : obstacles) {
                                if (obs != null) {
                                    if (obs.getY() < screenY * 0.5f) { // Top ground
                                        maxTopX = Math.max(maxTopX, obs.getX());
                                    } else { // Bottom ground
                                        maxBottomX = Math.max(maxBottomX, obs.getX());
                                    }
                                }
                            }
                            
                            // Create new obstacle
                            obstacles[i] = new Obstacle(screenX, screenY, resources.getFenceBitmap());
                            float randomSpacing;
                            float random = (float) Math.random();
                            if (random < 0.2) {
                                randomSpacing = 800f + (float)(Math.random() * 300f);  // Increased from 400f + 200f
                            } else if (random < 0.6) {
                                randomSpacing = 1000f + (float)(Math.random() * 400f);  // Increased from 600f + 300f
                            } else {
                                randomSpacing = 1300f + (float)(Math.random() * 500f);  // Increased from 900f + 400f
                            }

                            if (currentLevel >= 4) {
                                // For levels 4-6, maintain obstacles on both grounds
                                if (i % 2 == 0) {
                                    // Top ground obstacle
                                    obstacles[i].setPosition(maxTopX + randomSpacing);
                                    obstacles[i].setTopGround(true);
                                } else {
                                    // Bottom ground obstacle
                                    obstacles[i].setPosition(maxBottomX + randomSpacing);
                                    obstacles[i].setTopGround(false);
                                }
                            } else {
                                obstacles[i].setPosition(maxTopX + randomSpacing);
                            }
                        }
                    }
                }
            }
        }

        // Check key collection
        if (isKeyVisible && player.getCollisionBox().intersect(levelKey.getCollisionBox())) {
            levelCompleted = true;
            isGameWon = true;
            isPlaying = false;
        }

        // If player goes off screen without collecting key, game over
        if (isKeyVisible && player.getX() > screenX) {
            gameOver();
        }
    }

    private void generateObstacle() {
        // Implementation of generateObstacle method
    }

    private void generateTreat() {
        // Implementation of generateTreat method
    }

    private void gameOver() {
        isGameOver = true;
        isPlaying = false;
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void draw(Canvas canvas) {
        if (canvas != null) {
            // Clear the canvas with white background
            canvas.drawColor(Color.WHITE);

            // Draw background based on current level
            Bitmap currentBackground = resources.getBackgroundForLevel(currentLevel);
            if (currentBackground != null) {
                Rect srcRect = new Rect(0, 0, currentBackground.getWidth(),
                        currentBackground.getHeight());
                RectF destRect = new RectF(backgroundX, 0,
                        backgroundX + screenX, screenY);
                canvas.drawBitmap(currentBackground, srcRect, destRect, null);

                // Draw second background for seamless scrolling
                destRect.left = backgroundX + screenX;
                destRect.right = backgroundX + screenX * 2;
                canvas.drawBitmap(currentBackground, srcRect, destRect, null);
            }

            // Draw grounds
            paint.setColor(Color.rgb(101, 67, 33));
            if (currentLevel >= 4) {
                // Draw both grounds for levels 4-6
                canvas.drawRect(0, screenY * 0.02f - 120, screenX, screenY * 0.02f, paint);  // Top ground
                canvas.drawRect(0, screenY - 60, screenX, screenY, paint);  // Bottom ground
            } else {
                // Draw single ground for levels 1-3
                canvas.drawRect(0, screenY - 60, screenX, screenY, paint);
            }

            // Draw game objects
            if (player != null) {
                player.draw(canvas);
            }
            
            // Draw obstacles if key is not visible
            if (!isKeyVisible && obstacles != null) {
                for (Obstacle obstacle : obstacles) {
                    if (obstacle != null) {
                        obstacle.draw(canvas);
                    }
                }
            }

            // Draw treats
            if (treats != null) {
                for (Treat treat : treats) {
                    if (treat != null) {
                        treat.draw(canvas);
                    }
                }
            }

            // Draw key if visible
            if (isKeyVisible && levelKey != null) {
                levelKey.draw(canvas);
            }

            // Draw level
            paint.setColor(Color.WHITE);
            paint.setTextSize(50);
            paint.setShadowLayer(3, 3, 3, Color.BLACK);
            canvas.drawText("Level: " + currentLevel, 50, 160, paint);

            // Draw hearts
            if (heartBitmap != null) {
                for (int i = 0; i < playerHealth; i++) {
                    canvas.drawBitmap(heartBitmap, 20 + i * (heartBitmap.getWidth() + 10), 20, null);
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
        // Draw semi-transparent black overlay
        paint.setColor(Color.argb(120, 0, 0, 0));
        canvas.drawRect(0, 0, screenX, screenY, paint);

        // Calculate window dimensions
        float windowWidth = screenX * 0.8f;
        float windowHeight = screenY * 0.4f;
        float windowX = (screenX - windowWidth) / 2;
        float windowY = (screenY - windowHeight) / 2;

        // Draw window background
        RectF windowRect = new RectF(windowX, windowY, windowX + windowWidth, windowY + windowHeight);
        paint.setColor(Color.WHITE);
        paint.setAlpha(255); // Ensure full opacity for the window
        canvas.drawBitmap(resources.getGameOverWindow(), null, windowRect, paint);

        // Draw text inside window
        paint.setColor(Color.WHITE);
        paint.setTextSize(windowHeight * 0.3f);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setShadowLayer(10, 5, 5, Color.BLACK);
        canvas.drawText("GAME OVER", screenX/2, windowY + windowHeight * 0.4f, paint);

        paint.setTextSize(windowHeight * 0.15f);
        canvas.drawText("Tap to Try Again", screenX/2, windowY + windowHeight * 0.7f, paint);
    }

    private void drawGameWon(Canvas canvas) {
        // Draw semi-transparent black overlay
        paint.setColor(Color.argb(120, 0, 0, 0));
        canvas.drawRect(0, 0, screenX, screenY, paint);

        // Calculate window dimensions
        float windowWidth = screenX * 0.8f;
        float windowHeight = screenY * 0.4f;
        float windowX = (screenX - windowWidth) / 2;
        float windowY = (screenY - windowHeight) / 2;

        // Draw window background
        RectF windowRect = new RectF(windowX, windowY, windowX + windowWidth, windowY + windowHeight);
        paint.setColor(Color.WHITE);
        paint.setAlpha(255); // Ensure full opacity for the window
        canvas.drawBitmap(resources.getWinWindow(), null, windowRect, paint);

        // Draw text inside window
        paint.setColor(Color.rgb(255, 215, 0)); // Gold color
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
                        player.switchGround();  // Switch grounds for levels 4-6
                } else {
                        player.jump();  // Jump for levels 1-3
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
        playerHealth = 3; // Reset health when starting new game
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