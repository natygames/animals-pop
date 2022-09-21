package com.nativegame.animalspop.game.bubble.effect;

import com.nativegame.engine.sprite.BodyType;
import com.nativegame.engine.GameEngine;
import com.nativegame.engine.sprite.Sprite;

import java.util.Random;

/**
 * Created by Oscar Liang on 2022/09/18
 */

public class FloaterEffect extends Sprite {

    private final float mMinSpeedX;
    private final float mMaxSpeedX;
    private final float mMinSpeedY;
    private final float mMaxSpeedY;
    private final float mGravity;

    private float mSpeedX, mSpeedY;
    private boolean mIsActive = false;

    public FloaterEffect(GameEngine gameEngine, int drawableResId) {
        super(gameEngine, drawableResId, BodyType.None);
        mMinSpeedX = -mPixelFactor * 1000 / 1000f;
        mMaxSpeedX = mPixelFactor * 1000 / 1000f;
        mMinSpeedY = -mPixelFactor * 4000 / 1000f;
        mMaxSpeedY = -mPixelFactor * 7000 / 1000f;
        mGravity = mPixelFactor * 20 / 1000f;
        // We hide the floater initially
        removeFromScreen();
    }

    @Override
    public void startGame(GameEngine gameEngine) {
    }

    public void activate(float x, float y, Random r) {
        mX = x - mWidth / 2f;
        mY = y - mHeight / 2f;
        // Generate random speed
        mSpeedX = r.nextFloat() * (mMaxSpeedX - mMinSpeedX) + mMinSpeedX;
        mSpeedY = r.nextFloat() * (mMaxSpeedY - mMinSpeedY) + mMinSpeedY;
        addToScreen();
    }

    @Override
    public void onUpdate(long elapsedMillis, GameEngine gameEngine) {
        if (!mIsActive) {
            return;
        }
        mX += mSpeedX * elapsedMillis;
        mY += mSpeedY * elapsedMillis;
        // Update gravity
        mSpeedY += mGravity * elapsedMillis;
        // Remove floater when out of screen
        if (mY > gameEngine.mScreenHeight) {
            removeFromScreen();
        }
    }

    private void addToScreen() {
        mAlpha = 255;
        mIsActive = true;
    }

    private void removeFromScreen() {
        // We only hide the floater not remove it
        mX = -mWidth;
        mY = -mHeight;
        mAlpha = 0;
        mIsActive = false;
    }

}
