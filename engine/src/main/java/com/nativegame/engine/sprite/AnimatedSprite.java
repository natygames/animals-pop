package com.nativegame.engine.sprite;

import android.graphics.Bitmap;

import com.nativegame.engine.GameEngine;

public abstract class AnimatedSprite extends Sprite {

    private Bitmap[] mBitmaps;
    private long mTimePreFrame;
    private long mTotalMillis;
    private int mIndex;
    private boolean mIsRunning = false;

    protected AnimatedSprite(GameEngine gameEngine, int drawableRes, BodyType bodyType) {
        super(gameEngine, drawableRes, bodyType);
    }

    public void setIndex(int index) {
        mIndex = index;
    }

    public void setTimePreFrame(long timePreFrame) {
        mTimePreFrame = timePreFrame;
    }

    public void setAnimatedBitmaps(Bitmap[] bitmaps) {
        mBitmaps = bitmaps;
    }

    public int getIndex() {
        return mIndex;
    }

    public long getTimePreFrame() {
        return mTimePreFrame;
    }

    public Bitmap[] getBitmaps() {
        return mBitmaps;
    }

    public boolean isRunning() {
        return mIsRunning;
    }

    //--------------------------------------------------------
    // Methods to change state of animation
    // start, stop, pause, resume, repeat
    //--------------------------------------------------------

    public void start() {
        mIsRunning = true;
        mIndex = 0;
        mTotalMillis = 0;
        onAnimationStart();
    }

    public void stop(GameEngine gameEngine) {
        mIsRunning = false;
        removeFromGameEngine(gameEngine);
        onAnimationStop();
    }

    public void pause() {
        mIsRunning = false;
        onAnimationPause();
    }

    public void resume() {
        mIsRunning = true;
        onAnimationResume();
    }

    public void repeat() {
        mIsRunning = true;
        mIndex = 0;
        onAnimationRepeat();
    }

    //=================================================================================

    @Override
    public void onUpdate(long elapsedMillis, GameEngine gameEngine) {
        if (!mIsRunning) {
            return;
        }
        mTotalMillis += elapsedMillis;
        if (mTotalMillis >= mTimePreFrame) {
            if (mIndex < mBitmaps.length - 1) {
                mIndex++;
            } else {
                onAnimationEnd(gameEngine);
            }
            mBitmap = mBitmaps[mIndex];
            mTotalMillis = 0;
        }
    }

    protected void onAnimationStart() {
    }

    protected void onAnimationStop() {
    }

    protected void onAnimationPause() {
    }

    protected void onAnimationResume() {
    }

    protected void onAnimationRepeat() {
    }

    protected void onAnimationEnd(GameEngine gameEngine) {
        stop(gameEngine);
    }

}
