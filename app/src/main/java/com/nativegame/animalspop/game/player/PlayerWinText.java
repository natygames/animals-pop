package com.nativegame.animalspop.game.player;

import android.view.animation.AnticipateInterpolator;
import android.view.animation.OvershootInterpolator;

import com.nativegame.animalspop.R;
import com.nativegame.engine.GameEngine;
import com.nativegame.engine.sprite.BodyType;
import com.nativegame.engine.sprite.Sprite;

/**
 * Created by Oscar Liang on 2022/09/18
 */

public class PlayerWinText {

    private static final int TIME_TO_LIVE = 400;

    private final OvershootInterpolator mOvershootInterpolator = new OvershootInterpolator();
    private final AnticipateInterpolator mAnticipateInterpolator = new AnticipateInterpolator();

    private final TextBg mTextBg;
    private final YouText mYouText;
    private final WinText mWinText;

    public PlayerWinText(GameEngine gameEngine) {
        mTextBg = new TextBg(gameEngine, R.drawable.light_bg);
        mYouText = new YouText(gameEngine, R.drawable.text_you);
        mWinText = new WinText(gameEngine, R.drawable.text_win);
    }

    public void init(GameEngine gameEngine) {
        mTextBg.init(gameEngine, 5);
        mYouText.init(gameEngine, 5);
        mWinText.init(gameEngine, 5);
    }

    private class TextBg extends Sprite {

        private final float mRotationSpeed;
        private final float mAlphaSpeed;

        private long mTotalMillis;

        public TextBg(GameEngine gameEngine, int drawableResId) {
            super(gameEngine, drawableResId, BodyType.None);
            mRotationSpeed = 120 / 1000f;
            mAlphaSpeed = -10 / 1000f;
        }

        public void init(GameEngine gameEngine, int layer) {
            mX = gameEngine.mScreenWidth / 2f - mWidth / 2f;
            mY = gameEngine.mScreenHeight / 3f - mHeight / 2f;
            mScale = 4;
            addToGameEngine(gameEngine, layer);
            mTotalMillis = 0;
        }

        @Override
        public void startGame(GameEngine gameEngine) {
        }

        @Override
        public void onUpdate(long elapsedMillis, GameEngine gameEngine) {
            mTotalMillis += elapsedMillis;
            if (mTotalMillis >= 1600) {
                removeFromGameEngine(gameEngine);
            } else {
                if (mTotalMillis <= TIME_TO_LIVE) {
                    float percentageValue = mOvershootInterpolator.getInterpolation(mTotalMillis * 1f / TIME_TO_LIVE);
                    mScale = 4 * percentageValue;
                } else if (mTotalMillis >= 1200) {
                    float percentageValue = mAnticipateInterpolator.getInterpolation((mTotalMillis - 1200) * 1f / TIME_TO_LIVE);
                    mScale = 4 - 4 * percentageValue;
                    mAlpha += mAlphaSpeed * elapsedMillis;
                }
                mRotation += mRotationSpeed * elapsedMillis;
            }
        }

    }

    private class YouText extends Sprite {

        private final float mInitialX;
        private final float mFinalX;
        private final float mValueIncrement;

        private long mTotalMillis;

        public YouText(GameEngine gameEngine, int drawableResId) {
            super(gameEngine, drawableResId, BodyType.None);
            mInitialX = -mWidth;
            mFinalX = gameEngine.mScreenWidth / 2f - mWidth / 2f;
            mValueIncrement = mFinalX - mInitialX;
        }

        public void init(GameEngine gameEngine, int layer) {
            mX = mInitialX;
            mY = gameEngine.mScreenHeight / 3f - mHeight;
            mScale = 2;
            addToGameEngine(gameEngine, layer);
            mTotalMillis = 0;
        }

        @Override
        public void startGame(GameEngine gameEngine) {
        }

        @Override
        public void onUpdate(long elapsedMillis, GameEngine gameEngine) {
            mTotalMillis += elapsedMillis;
            if (mTotalMillis >= 1600) {
                removeFromGameEngine(gameEngine);
            } else {
                if (mTotalMillis <= TIME_TO_LIVE) {
                    float percentageValue = mOvershootInterpolator.getInterpolation(mTotalMillis * 1f / TIME_TO_LIVE);
                    mX = mInitialX + mValueIncrement * percentageValue;
                } else if (mTotalMillis >= 1200) {
                    float percentageValue = mAnticipateInterpolator.getInterpolation((mTotalMillis - 1200) * 1f / TIME_TO_LIVE);
                    mX = mFinalX - mValueIncrement * percentageValue;
                }
            }
        }

    }

    private class WinText extends Sprite {

        private final float mInitialX;
        private final float mFinalX;
        private final float mValueIncrement;

        private long mTotalMillis;

        public WinText(GameEngine gameEngine, int drawableResId) {
            super(gameEngine, drawableResId, BodyType.None);
            mInitialX = gameEngine.mScreenWidth;
            mFinalX = gameEngine.mScreenWidth / 2f - mWidth / 2f;
            mValueIncrement = mInitialX - mFinalX;
        }

        public void init(GameEngine gameEngine, int layer) {
            mX = mInitialX;
            mY = gameEngine.mScreenHeight / 3f + mHeight / 2f;
            mScale = 2;
            addToGameEngine(gameEngine, layer);
            mTotalMillis = 0;
        }

        @Override
        public void startGame(GameEngine gameEngine) {
        }

        @Override
        public void onUpdate(long elapsedMillis, GameEngine gameEngine) {
            mTotalMillis += elapsedMillis;
            if (mTotalMillis >= 1600) {
                removeFromGameEngine(gameEngine);
            } else {
                if (mTotalMillis <= TIME_TO_LIVE) {
                    float percentageValue = mOvershootInterpolator.getInterpolation(mTotalMillis * 1f / TIME_TO_LIVE);
                    mX = mInitialX - mValueIncrement * percentageValue;
                } else if (mTotalMillis >= 1200) {
                    float percentageValue = mAnticipateInterpolator.getInterpolation((mTotalMillis - 1200) * 1f / TIME_TO_LIVE);
                    mX = mFinalX + mValueIncrement * percentageValue;
                }
            }
        }

    }

}
