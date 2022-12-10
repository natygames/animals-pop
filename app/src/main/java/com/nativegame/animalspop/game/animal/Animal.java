package com.nativegame.animalspop.game.animal;

import android.graphics.Bitmap;

import com.nativegame.animalspop.R;
import com.nativegame.animalspop.game.Layer;
import com.nativegame.animalspop.game.MyGameEvent;
import com.nativegame.nattyengine.Game;
import com.nativegame.nattyengine.event.GameEvent;
import com.nativegame.nattyengine.entity.sprite.AnimatedSprite;
import com.nativegame.nattyengine.entity.sprite.Sprite;

/**
 * Created by Oscar Liang on 2022/09/18
 */

public class Animal extends AnimatedSprite {

    private static final int IDLE_TIME_PER_FRAME = 80;
    private static final int JUMP_TIME_PER_FRAME = 25;

    private final Bitmap mPreparingBitmap = getBitmapFromId(R.drawable.fox_jump_01);

    private final Bitmap[] mIdleBitmaps = new Bitmap[]{
            getBitmapFromId(R.drawable.fox_idle_01),
            getBitmapFromId(R.drawable.fox_idle_02),
            getBitmapFromId(R.drawable.fox_idle_03),
            getBitmapFromId(R.drawable.fox_idle_04),
            getBitmapFromId(R.drawable.fox_idle_05),
            getBitmapFromId(R.drawable.fox_idle_06),
            getBitmapFromId(R.drawable.fox_idle_07),
            getBitmapFromId(R.drawable.fox_idle_08),
            getBitmapFromId(R.drawable.fox_idle_09),
            getBitmapFromId(R.drawable.fox_idle_10)};

    private final Bitmap[] mJumpBitmaps = new Bitmap[]{
            getBitmapFromId(R.drawable.fox_jump_01),
            getBitmapFromId(R.drawable.fox_jump_02),
            getBitmapFromId(R.drawable.fox_jump_03),
            getBitmapFromId(R.drawable.fox_jump_04),
            getBitmapFromId(R.drawable.fox_jump_05),
            getBitmapFromId(R.drawable.fox_jump_06),
            getBitmapFromId(R.drawable.fox_jump_07),
            getBitmapFromId(R.drawable.fox_jump_08)};

    private final Bitmap[] mFallBitmaps = new Bitmap[]{
            getBitmapFromId(R.drawable.fox_fall_01),
            getBitmapFromId(R.drawable.fox_fall_02),
            getBitmapFromId(R.drawable.fox_fall_03),
            getBitmapFromId(R.drawable.fox_fall_04),
            getBitmapFromId(R.drawable.fox_fall_05),
            getBitmapFromId(R.drawable.fox_fall_06),
            getBitmapFromId(R.drawable.fox_fall_07),
            getBitmapFromId(R.drawable.fox_fall_08)};

    private final AnimalShadow mAnimalShadow;
    private final float mStartX;
    private final float mStartY;
    private final float mSpeedY;

    private AnimalState mAnimalState;
    private boolean mGameOver = false;

    private enum AnimalState {
        IDLE,
        JUMP,
        FALL
    }

    public Animal(Game game) {
        super(game, R.drawable.fox_idle_01);
        mAnimalShadow = new AnimalShadow(game);
        // Init the position base on bg image
        Bitmap ground = getBitmapFromId(R.drawable.bg_front);
        float groundPixelFactor = game.getScreenWidth() * 1f / ground.getWidth();
        mStartX = game.getScreenWidth() / 5f - mWidth / 2f;
        mStartY = game.getScreenHeight() - groundPixelFactor * ground.getHeight() * 2 / 3f - mHeight / 2f;
        mSpeedY = mPixelFactor * 2500 / 1000f;
        mLayer = Layer.BACKGROUND_LAYER;
        // Init the animation
        mAnimatedBitmaps = mIdleBitmaps;
        mTimePreFrame = IDLE_TIME_PER_FRAME;
    }

    @Override
    public void onStart() {
        mX = mStartX;
        mY = mStartY;
        mScale = 3;
        mAnimalState = AnimalState.IDLE;
        mAnimalShadow.addToGame();
        startAnimation();
    }

    @Override
    public void onRemove() {
        mAnimalShadow.removeFromGame();
    }

    @Override
    public void onUpdate(long elapsedMillis) {
        checkPreparing();
        updatePosition(elapsedMillis);
    }

    private void checkPreparing() {
        if (mAnimalState != AnimalState.IDLE || mGameOver) {
            return;
        }
        // Check player touch event
        if (mGame.getTouchController().mTouching) {
            if (isRunning()) {
                stopAnimation();
                mBitmap = mPreparingBitmap;
            }
        } else {
            if (!isRunning()) {
                resumeAnimation();
            }
        }
    }

    private void updatePosition(long elapsedMillis) {
        switch (mAnimalState) {
            case JUMP:
                mY -= mSpeedY * elapsedMillis;
                break;
            case FALL:
                mY += mSpeedY * elapsedMillis;
                break;
        }
    }

    @Override
    protected void onAnimationRepeat() {
        switch (mAnimalState) {
            case JUMP:
                mAnimalState = AnimalState.FALL;
                mAnimatedBitmaps = mFallBitmaps;
                break;
            case FALL:
                mAnimalState = AnimalState.IDLE;
                mAnimatedBitmaps = mIdleBitmaps;
                mTimePreFrame = IDLE_TIME_PER_FRAME;
                mY = mStartY;   // We make sure it fall to start position
                break;
        }
    }

    @Override
    public void onGameEvent(GameEvent gameEvent) {
        switch ((MyGameEvent) gameEvent) {
            case BUBBLE_SHOT:
            case BOOSTER_SHOT:
            case COLLECT_ITEM:
            case EMIT_CONFETTI:
                jump();
                break;
            case GAME_WIN:
            case GAME_OVER:
                // Prevent player touch event
                mGameOver = true;
                break;
            case SHOW_WIN_DIALOG:
                // Stop and remove the animal
                stopAnimation();
                removeFromGame();
                break;
        }
    }

    private void jump() {
        if (mAnimalState != AnimalState.IDLE) {
            return;
        }
        mAnimalState = AnimalState.JUMP;
        mAnimatedBitmaps = mJumpBitmaps;
        mTimePreFrame = JUMP_TIME_PER_FRAME;
        mFrame = 0;
    }

    private class AnimalShadow extends Sprite {

        public AnimalShadow(Game game) {
            super(game, R.drawable.player_shadow);
            mLayer = Animal.this.mLayer - 1;
        }

        @Override
        public void onStart() {
            mX = mStartX + Animal.this.mWidth / 2f - mWidth * 0.6f;
            mY = mStartY + Animal.this.mHeight * 2 - mHeight * 0.8f;
        }

        @Override
        public void onUpdate(long elapsedMillis) {
        }

    }

}
