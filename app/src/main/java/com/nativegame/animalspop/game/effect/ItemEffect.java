package com.nativegame.animalspop.game.effect;

import com.nativegame.animalspop.game.Layer;
import com.nativegame.nattyengine.Game;
import com.nativegame.nattyengine.entity.sprite.Sprite;

/**
 * Created by Oscar Liang on 2022/09/18
 */

public class ItemEffect extends Sprite {

    private final float mTargetX;
    private final float mTargetY;
    private final float mSpeed;
    private final float mScaleSpeed;

    private float mSpeedX;
    private float mSpeedY;
    private long mTotalTime;

    public ItemEffect(Game game, int drawableId) {
        super(game, drawableId);
        // Init the target position
        mTargetX = mGame.getScreenWidth() * 0.84f - mWidth / 2f;
        mTargetY = -mHeight / 2f;
        mSpeed = mPixelFactor * 3000 / 1000f;
        mScaleSpeed = 2 / 1000f;
        mLayer = Layer.EFFECT_LAYER;
    }

    public void activate(float x, float y) {
        mX = x - mWidth / 2f;
        mY = y - mHeight / 2f;
        // We convert angle to x speed and y speed
        double angle = Math.atan2(mTargetY - y, mTargetX - x);
        mSpeedX = (float) (mSpeed * Math.cos(angle));
        mSpeedY = (float) (mSpeed * Math.sin(angle));
        addToGame();
        mTotalTime = 0;
    }

    @Override
    public void onUpdate(long elapsedMillis) {
        mTotalTime += elapsedMillis;
        if (mTotalTime < 200) {
            mScale += mScaleSpeed * 2f * elapsedMillis;
        } else if (mTotalTime >= 400) {
            mX += mSpeedX * elapsedMillis;
            mY += mSpeedY * elapsedMillis;
            if (mScale > 1) {
                mScale -= mScaleSpeed * elapsedMillis;
            }
            // Check is reach target position
            if (mX >= (mTargetX - mWidth / 2f) && mY <= (mTargetY - mHeight / 2f)) {
                removeFromGame();
                mTotalTime = 0;
            }
        }
    }

}
