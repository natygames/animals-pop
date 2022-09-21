package com.nativegame.animalspop.game.bubble.effect;

import com.nativegame.animalspop.Utils;
import com.nativegame.engine.sprite.BodyType;
import com.nativegame.engine.GameEngine;
import com.nativegame.engine.sprite.Sprite;

/**
 * Created by Oscar Liang on 2022/09/18
 */

public class ItemEffect extends Sprite {

    private final float mTargetX, mTargetY;
    private final float mSpeed;
    private final float mScaleSpeed;

    private float mSpeedX;
    private float mSpeedY;
    private long mTotalMillis;

    public ItemEffect(GameEngine gameEngine, int drawableResId) {
        super(gameEngine, drawableResId, BodyType.None);

        mTargetX = gameEngine.mScreenWidth * 0.84f - mWidth / 2f;
        mTargetY = -mHeight / 2f;

        mSpeed = mPixelFactor * 3000 / 1000f;
        mScaleSpeed = 2 / 1000f;
    }

    @Override
    public void startGame(GameEngine gameEngine) {
    }

    public void activate(GameEngine gameEngine, float x, float y, int layer) {
        mX = x - mWidth / 2f;
        mY = y - mHeight / 2f;
        // We convert angle to x speed and y speed
        double angle = Utils.getAngle(mTargetX - x, mTargetY - y);
        mSpeedX = (float) (mSpeed * Math.cos(angle));
        mSpeedY = (float) (mSpeed * Math.sin(angle));
        addToGameEngine(gameEngine, layer);
        mTotalMillis = 0;
    }

    @Override
    public void onUpdate(long elapsedMillis, GameEngine gameEngine) {
        mTotalMillis += elapsedMillis;
        if (mTotalMillis < 200) {
            mScale += mScaleSpeed * 2f * elapsedMillis;
        } else if (mTotalMillis >= 400) {
            mX += mSpeedX * elapsedMillis;
            mY += mSpeedY * elapsedMillis;
            if (mScale > 1) {
                mScale -= mScaleSpeed * elapsedMillis;
            }
            // Check is reach target position
            if (mX >= (mTargetX - mWidth / 2f) && mY <= (mTargetY - mHeight / 2f)) {
                removeFromGameEngine(gameEngine);
                mTotalMillis = 0;
            }
        }
    }

}
