package com.nativegame.animalspop.game.player;

import android.graphics.Bitmap;

import com.nativegame.animalspop.R;
import com.nativegame.animalspop.game.MyGameEvent;
import com.nativegame.engine.GameEngine;
import com.nativegame.engine.GameEvent;
import com.nativegame.engine.input.InputController;
import com.nativegame.engine.sprite.AnimatedSprite;
import com.nativegame.engine.sprite.BodyType;
import com.nativegame.engine.sprite.Sprite;

/**
 * Created by Oscar Liang on 2022/09/18
 */

public class Player extends AnimatedSprite {

    private final Bitmap[] mIdleBitmaps = new Bitmap[]{
            getBitmap(R.drawable.fox_idle_01),
            getBitmap(R.drawable.fox_idle_02),
            getBitmap(R.drawable.fox_idle_03),
            getBitmap(R.drawable.fox_idle_04),
            getBitmap(R.drawable.fox_idle_05),
            getBitmap(R.drawable.fox_idle_06),
            getBitmap(R.drawable.fox_idle_07),
            getBitmap(R.drawable.fox_idle_08),
            getBitmap(R.drawable.fox_idle_09),
            getBitmap(R.drawable.fox_idle_10)};
    private final Bitmap[] mJumpBitmaps = new Bitmap[]{
            getBitmap(R.drawable.fox_jump_01),
            getBitmap(R.drawable.fox_jump_02),
            getBitmap(R.drawable.fox_jump_03),
            getBitmap(R.drawable.fox_jump_04),
            getBitmap(R.drawable.fox_jump_05),
            getBitmap(R.drawable.fox_jump_06),
            getBitmap(R.drawable.fox_jump_07),
            getBitmap(R.drawable.fox_jump_08)};
    private final Bitmap[] mFallBitmaps = new Bitmap[]{
            getBitmap(R.drawable.fox_fall_01),
            getBitmap(R.drawable.fox_fall_02),
            getBitmap(R.drawable.fox_fall_03),
            getBitmap(R.drawable.fox_fall_04),
            getBitmap(R.drawable.fox_fall_05),
            getBitmap(R.drawable.fox_fall_06),
            getBitmap(R.drawable.fox_fall_07),
            getBitmap(R.drawable.fox_fall_08)};

    private final PlayerShadow mPlayerShadow;
    private final float mStartX;
    private final float mStartY;
    private final float mSpeedY;

    private PlayerState mPlayerState;

    private enum PlayerState {
        IDLE,
        JUMP,
        FALL
    }

    public Player(GameEngine gameEngine) {
        super(gameEngine, R.drawable.fox_idle_01, BodyType.None);
        setTimePreFrame(80);
        setAnimatedBitmaps(mIdleBitmaps);
        mPlayerShadow = new PlayerShadow(gameEngine);
        // Init the position base on bg image
        Bitmap ground = getBitmap(R.drawable.bg_front);
        float groundPixelFactor = gameEngine.mScreenWidth * 1f / ground.getWidth();
        mStartX = gameEngine.mScreenWidth / 5f - mWidth / 2f;
        mStartY = gameEngine.mScreenHeight - groundPixelFactor * ground.getHeight() * 2 / 3f - mHeight / 2f;
        mSpeedY = -mPixelFactor * 2000 / 1000;
    }

    @Override
    public void startGame(GameEngine gameEngine) {
        mX = mStartX;
        mY = mStartY;
        mScale = 3;
        mPlayerState = PlayerState.IDLE;
        start();
    }

    @Override
    public void addToGameEngine(GameEngine gameEngine, int layer) {
        super.addToGameEngine(gameEngine, layer);
        mPlayerShadow.addToGameEngine(gameEngine, layer - 1);
    }

    @Override
    public void onUpdate(long elapsedMillis, GameEngine gameEngine) {
        super.onUpdate(elapsedMillis, gameEngine);
        checkPreparing(gameEngine.mInputController);
        updatePosition(elapsedMillis);
    }

    private void checkPreparing(InputController inputController) {
        if (mPlayerState == PlayerState.JUMP || mPlayerState == PlayerState.FALL) {
            return;
        }
        if (inputController.mTouching) {
            if (isRunning()) {
                pause();
                setBitmap(R.drawable.fox_jump_01);
            }
        } else {
            resume();
        }
    }

    private void updatePosition(long elapsedMillis) {
        switch (mPlayerState) {
            case IDLE:
                break;
            case JUMP:
                if (mY > mStartY - mHeight) {
                    mY += mSpeedY * elapsedMillis;
                }
                break;
            case FALL:
                if (mY < mStartY) {
                    mY -= mSpeedY * elapsedMillis;
                }
                break;
        }
    }

    @Override
    protected void onAnimationEnd(GameEngine gameEngine) {
        switch (mPlayerState) {
            case IDLE:
                break;
            case JUMP:
                setAnimatedBitmaps(mFallBitmaps);
                mPlayerState = PlayerState.FALL;
                break;
            case FALL:
                setAnimatedBitmaps(mIdleBitmaps);
                setTimePreFrame(100);
                mPlayerState = PlayerState.IDLE;
                break;
        }
        repeat();
    }

    @Override
    public void onGameEvent(GameEvent gameEvent) {
        switch ((MyGameEvent) gameEvent) {
            case BUBBLE_SHOT:
            case BOOSTER_SHOT:
            case COLLECT_ITEM:
            case EMIT_CONFETTI:
            case GAME_WIN:
                jump();
                break;
            case REMOVE_PLAYER:
                // Hide the player
                mAlpha = 0;
                break;
        }
    }

    private void jump() {
        if (mPlayerState == PlayerState.JUMP || mPlayerState == PlayerState.FALL) {
            return;
        }
        setAnimatedBitmaps(mJumpBitmaps);
        setTimePreFrame(25);
        setIndex(0);
        mPlayerState = PlayerState.JUMP;
    }

    private class PlayerShadow extends Sprite {

        public PlayerShadow(GameEngine gameEngine) {
            super(gameEngine, R.drawable.player_shadow, BodyType.None);
        }

        @Override
        public void startGame(GameEngine gameEngine) {
            mX = mStartX + Player.this.mWidth / 2f - mWidth * 0.6f;
            mY = mStartY + Player.this.mHeight * 2 - mHeight * 0.8f;
        }

        @Override
        public void onUpdate(long elapsedMillis, GameEngine gameEngine) {
        }

        @Override
        public void onGameEvent(GameEvent gameEvent) {
            if (gameEvent == MyGameEvent.REMOVE_PLAYER) {
                // Hide the player
                mAlpha = 0;
            }
        }

    }

}
