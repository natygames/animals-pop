package com.nativegame.animalspop.game.counter.combo;

import android.view.animation.OvershootInterpolator;

import com.nativegame.animalspop.R;
import com.nativegame.animalspop.sound.MySoundEvent;
import com.nativegame.engine.GameEngine;
import com.nativegame.engine.sprite.BodyType;
import com.nativegame.engine.sprite.Sprite;

/**
 * Created by Oscar Liang on 2022/09/18
 */

public class ComboText extends Sprite {

    private static final int TIME_TO_SHRINK = 300;

    private final OvershootInterpolator mInterpolator = new OvershootInterpolator();
    private final float mAlphaSpeed;

    private long mTotalMillis;

    public ComboText(GameEngine gameEngine) {
        super(gameEngine, R.drawable.text_wow, BodyType.None);
        mAlphaSpeed = -800 / 1000f;
    }

    public void init(GameEngine gameEngine, Combo combo) {
        mX = gameEngine.mScreenWidth / 2f - mWidth / 2f;
        mY = gameEngine.mScreenHeight / 3f - mHeight / 2f;
        mScale = 5;
        mAlpha = 255;
        setBitmap(combo.getImageResId());
        addToGameEngine(gameEngine, 5);
        gameEngine.mSoundManager.playSound(MySoundEvent.COMBO);
        mTotalMillis = 0;
    }

    @Override
    public void startGame(GameEngine gameEngine) {
    }

    @Override
    public void onUpdate(long elapsedMillis, GameEngine gameEngine) {
        mTotalMillis += elapsedMillis;
        if (mTotalMillis >= 1300) {
            removeFromGameEngine(gameEngine);
        } else {
            if (mTotalMillis <= TIME_TO_SHRINK) {
                mScale = 5 - 3 * mInterpolator.getInterpolation(mTotalMillis * 1f / TIME_TO_SHRINK);
            } else if (mTotalMillis >= 1000) {
                mAlpha += mAlphaSpeed * elapsedMillis;
            }
        }
    }

}
