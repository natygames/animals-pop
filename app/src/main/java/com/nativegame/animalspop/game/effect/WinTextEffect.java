package com.nativegame.animalspop.game.effect;

import android.view.animation.AnticipateInterpolator;
import android.view.animation.OvershootInterpolator;

import com.nativegame.animalspop.R;
import com.nativegame.animalspop.game.Layer;
import com.nativegame.nattyengine.Game;
import com.nativegame.nattyengine.entity.sprite.Sprite;

/**
 * Created by Oscar Liang on 2022/09/18
 */

public class WinTextEffect {

    private static final int TIME_TO_LIVE = 400;

    private final TextBg mTextBg;
    private final YouText mYouText;
    private final WinText mWinText;

    private final OvershootInterpolator mOvershootInterpolator = new OvershootInterpolator();
    private final AnticipateInterpolator mAnticipateInterpolator = new AnticipateInterpolator();

    public WinTextEffect(Game game) {
        mTextBg = new TextBg(game, R.drawable.light_bg);
        mYouText = new YouText(game, R.drawable.text_you);
        mWinText = new WinText(game, R.drawable.text_win);
    }

    public void activate() {
        mTextBg.addToGame();
        mYouText.addToGame();
        mWinText.addToGame();
    }

    private class TextBg extends Sprite {

        private final float mRotationSpeed;
        private final float mAlphaSpeed;

        private long mTotalTime;

        public TextBg(Game game, int drawableId) {
            super(game, drawableId);
            mRotationSpeed = 120 / 1000f;
            mAlphaSpeed = -10 / 1000f;
            mLayer = Layer.TEXT_LAYER;
        }

        @Override
        public void onStart() {
            mX = mGame.getScreenWidth() / 2f - mWidth / 2f;
            mY = mGame.getScreenHeight() / 3f - mHeight / 2f;
            mScale = 4;
        }

        @Override
        public void onUpdate(long elapsedMillis) {
            mTotalTime += elapsedMillis;
            if (mTotalTime >= 1600) {
                removeFromGame();
                mTotalTime = 0;
            } else {
                if (mTotalTime <= TIME_TO_LIVE) {
                    float percentageValue = mOvershootInterpolator.getInterpolation(mTotalTime * 1f / TIME_TO_LIVE);
                    mScale = 4 * percentageValue;
                } else if (mTotalTime >= 1200) {
                    float percentageValue = mAnticipateInterpolator.getInterpolation((mTotalTime - 1200) * 1f / TIME_TO_LIVE);
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

        private long mTotalTime;

        public YouText(Game game, int drawableId) {
            super(game, drawableId);
            mInitialX = -mWidth;
            mFinalX = game.getScreenWidth() / 2f - mWidth / 2f;
            mValueIncrement = mFinalX - mInitialX;
            mLayer = Layer.TEXT_LAYER;
        }

        @Override
        public void onStart() {
            mX = mInitialX;
            mY = mGame.getScreenHeight() / 3f - mHeight;
            mScale = 2;
        }

        @Override
        public void onUpdate(long elapsedMillis) {
            mTotalTime += elapsedMillis;
            if (mTotalTime >= 1600) {
                removeFromGame();
                mTotalTime = 0;
            } else {
                if (mTotalTime <= TIME_TO_LIVE) {
                    float percentageValue = mOvershootInterpolator.getInterpolation(mTotalTime * 1f / TIME_TO_LIVE);
                    mX = mInitialX + mValueIncrement * percentageValue;
                } else if (mTotalTime >= 1200) {
                    float percentageValue = mAnticipateInterpolator.getInterpolation((mTotalTime - 1200) * 1f / TIME_TO_LIVE);
                    mX = mFinalX - mValueIncrement * percentageValue;
                }
            }
        }

    }

    private class WinText extends Sprite {

        private final float mInitialX;
        private final float mFinalX;
        private final float mValueIncrement;

        private long mTotalTime;

        public WinText(Game game, int drawableId) {
            super(game, drawableId);
            mInitialX = game.getScreenWidth();
            mFinalX = game.getScreenWidth() / 2f - mWidth / 2f;
            mValueIncrement = mInitialX - mFinalX;
            mLayer = Layer.TEXT_LAYER;
        }

        @Override
        public void onStart() {
            mX = mInitialX;
            mY = mGame.getScreenHeight() / 3f + mHeight / 2f;
            mScale = 2;
        }

        @Override
        public void onUpdate(long elapsedMillis) {
            mTotalTime += elapsedMillis;
            if (mTotalTime >= 1600) {
                removeFromGame();
                mTotalTime = 0;
            } else {
                if (mTotalTime <= TIME_TO_LIVE) {
                    float percentageValue = mOvershootInterpolator.getInterpolation(mTotalTime * 1f / TIME_TO_LIVE);
                    mX = mInitialX - mValueIncrement * percentageValue;
                } else if (mTotalTime >= 1200) {
                    float percentageValue = mAnticipateInterpolator.getInterpolation((mTotalTime - 1200) * 1f / TIME_TO_LIVE);
                    mX = mFinalX + mValueIncrement * percentageValue;
                }
            }
        }

    }

}
