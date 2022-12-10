package com.nativegame.animalspop.game.effect;

import android.view.animation.OvershootInterpolator;

import com.nativegame.animalspop.game.Layer;
import com.nativegame.nattyengine.Game;
import com.nativegame.nattyengine.entity.sprite.Sprite;

/**
 * Created by Oscar Liang on 2022/09/18
 */

public class ScoreEffect extends Sprite {

    private static final int TIME_TO_ENLARGE = 500;

    private final float mSpeedY;
    private final float mAlphaSpeed;

    private final OvershootInterpolator mInterpolator = new OvershootInterpolator();

    private long mTotalTime;

    public ScoreEffect(Game game, int drawableResId) {
        super(game, drawableResId);
        mSpeedY = -mPixelFactor * 500 / 1000f;
        mAlphaSpeed = -350 / 1000f;
        mLayer = Layer.EFFECT_LAYER;
    }

    public void activate(float x, float y) {
        mX = x - mWidth / 2f;
        mY = y - mHeight / 2f;
        mScale = 0;
        mAlpha = 255;
        addToGame();
        mTotalTime = 0;
    }

    @Override
    public void onUpdate(long elapsedMillis) {
        mTotalTime += elapsedMillis;
        if (mTotalTime >= 1200) {
            removeFromGame();
            mTotalTime = 0;
        } else {
            if (mTotalTime <= TIME_TO_ENLARGE) {
                mScale = mInterpolator.getInterpolation(mTotalTime * 1f / TIME_TO_ENLARGE);
            } else {
                mY += mSpeedY * elapsedMillis;
                mAlpha += mAlphaSpeed * elapsedMillis;
            }
        }
    }

}
