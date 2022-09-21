package com.nativegame.animalspop.game.bubble.effect;

import android.view.animation.OvershootInterpolator;

import com.nativegame.engine.sprite.BodyType;
import com.nativegame.engine.GameEngine;
import com.nativegame.engine.sprite.Sprite;

/**
 * Created by Oscar Liang on 2022/09/18
 */

public class ScoreEffect extends Sprite {

    private static final int TIME_TO_ENLARGE = 500;

    private final OvershootInterpolator mInterpolator = new OvershootInterpolator();
    private final float mSpeedY;
    private final float mAlphaSpeed;

    private long mTotalMillis;

    public ScoreEffect(GameEngine gameEngine, int drawableResId) {
        super(gameEngine, drawableResId, BodyType.None);
        mSpeedY = -mPixelFactor * 500 / 1000f;
        mAlphaSpeed = -350 / 1000f;
    }

    @Override
    public void startGame(GameEngine gameEngine) {
    }

    public void activate(GameEngine gameEngine, float x, float y, int layer) {
        mX = x - mWidth / 2f;
        mY = y - mHeight / 2f;
        mScale = 0;
        mAlpha = 255;
        addToGameEngine(gameEngine, layer);
        mTotalMillis = 0;
    }

    @Override
    public void onUpdate(long elapsedMillis, GameEngine gameEngine) {
        mTotalMillis += elapsedMillis;
        if (mTotalMillis >= 1200) {
            removeFromGameEngine(gameEngine);
        } else {
            if (mTotalMillis <= TIME_TO_ENLARGE) {
                mScale = mInterpolator.getInterpolation(mTotalMillis * 1f / TIME_TO_ENLARGE);
            } else {
                mY += mSpeedY * elapsedMillis;
                mAlpha += mAlphaSpeed * elapsedMillis;
            }
        }
    }

}
