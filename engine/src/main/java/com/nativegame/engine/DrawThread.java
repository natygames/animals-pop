package com.nativegame.engine;

public class DrawThread extends GameThread {

    private static final int DELTA_TIME = 16;   // This is 60 fps

    public DrawThread(GameEngine gameEngine) {
        super(gameEngine);
    }

    protected void doIt(long elapsedMillis) {
        if (elapsedMillis < DELTA_TIME) {
            try {
                Thread.sleep(DELTA_TIME - elapsedMillis);
            } catch (InterruptedException e) {
                // We just continue
            }
        }
        mGameEngine.onDraw();
    }

}
