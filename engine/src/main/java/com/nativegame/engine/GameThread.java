package com.nativegame.engine;

public class GameThread extends Thread {

    protected final GameEngine mGameEngine;

    private volatile boolean mIsGameRunning;
    private volatile boolean mIsGamePause;

    private final Object mLock = new Object();

    public GameThread(GameEngine gameEngine) {
        mGameEngine = gameEngine;
    }

    protected void doIt(long elapsedMillis) {
    }

    @Override
    public void start() {
        mIsGameRunning = true;
        mIsGamePause = false;
        super.start();
    }

    public void stopGame() {
        mIsGameRunning = false;
        resumeGame();
    }

    @Override
    public void run() {
        long elapsedMillis;
        long currentTimeMillis;
        long previousTimeMillis = System.currentTimeMillis();

        while (mIsGameRunning) {
            currentTimeMillis = System.currentTimeMillis();
            elapsedMillis = currentTimeMillis - previousTimeMillis;
            if (mIsGamePause) {
                while (mIsGamePause) {
                    try {
                        synchronized (mLock) {
                            mLock.wait();
                        }
                    } catch (InterruptedException e) {
                        // We stay on the loop
                    }
                }
                currentTimeMillis = System.currentTimeMillis();
            }
            doIt(elapsedMillis);
            previousTimeMillis = currentTimeMillis;
        }
    }

    public void pauseGame() {
        mIsGamePause = true;
    }

    public void resumeGame() {
        if (mIsGamePause) {
            mIsGamePause = false;
            synchronized (mLock) {
                mLock.notify();
            }
        }
    }

    public boolean isGameRunning() {
        return mIsGameRunning;
    }

    public boolean isGamePaused() {
        return mIsGamePause;
    }

}
