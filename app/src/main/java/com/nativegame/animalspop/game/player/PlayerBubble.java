package com.nativegame.animalspop.game.player;

import com.nativegame.animalspop.R;
import com.nativegame.animalspop.Utils;
import com.nativegame.animalspop.game.MyGameEvent;
import com.nativegame.animalspop.game.bubble.Bubble;
import com.nativegame.animalspop.game.bubble.BubbleSystem;
import com.nativegame.animalspop.game.player.dot.DotSystem;
import com.nativegame.engine.GameEngine;
import com.nativegame.engine.GameEvent;
import com.nativegame.engine.input.InputController;
import com.nativegame.engine.sprite.BodyType;
import com.nativegame.engine.sprite.Sprite;

/**
 * Created by Oscar Liang on 2022/09/18
 */

public abstract class PlayerBubble extends Sprite {

    protected final BubbleSystem mBubbleSystem;
    protected final DotSystem mDotSystem;
    protected final PlayerBubbleBg mPlayerBubbleBg;
    protected final float mStartX, mStartY;
    private final float mMaxX, mMaxY;
    private final float mSpeed;

    private float mRotationSpeed;
    private float mSpeedX, mSpeedY;
    private boolean mShoot = false;
    private boolean mSwitch = false;
    private boolean mEnable = false;

    protected PlayerBubble(BubbleSystem bubbleSystem, GameEngine gameEngine, int drawableResId) {
        super(gameEngine, drawableResId, BodyType.Circular);
        mBubbleSystem = bubbleSystem;
        mDotSystem = getDotSystem(gameEngine);
        mPlayerBubbleBg = new PlayerBubbleBg(gameEngine, R.drawable.bubble_bg);

        // Init player position
        float radius = mPixelFactor * Utils.BUBBLE_WIDTH;
        mStartX = gameEngine.mScreenWidth / 2f;
        mStartY = gameEngine.mScreenHeight * 4 / 5f - radius;

        // Player bound
        mMaxX = gameEngine.mScreenWidth - mWidth;
        mMaxY = gameEngine.mScreenHeight;

        mSpeed = mPixelFactor * 8000 / 1000;   // We want to move at 8000px per second
    }

    //--------------------------------------------------------
    // Getter and Setter to change state of player
    //--------------------------------------------------------
    public void setEnable(boolean enable) {
        mEnable = enable;
    }

    public boolean getEnable() {
        return mEnable && !mBubbleSystem.isShifting();   // Check is the bubble still shifting
    }
    //========================================================

    @Override
    public void addToGameEngine(GameEngine gameEngine, int layer) {
        super.addToGameEngine(gameEngine, layer);
        mDotSystem.addToGameEngine(gameEngine, 0);
        mPlayerBubbleBg.addToGameEngine(gameEngine, layer - 1);
    }

    @Override
    public void removeFromGameEngine(GameEngine gameEngine) {
        super.removeFromGameEngine(gameEngine);
        mDotSystem.removeFromGameEngine(gameEngine);
        mPlayerBubbleBg.removeFromGameEngine(gameEngine);
    }

    @Override
    public void onUpdate(long elapsedMillis, GameEngine gameEngine) {
        checkShooting(gameEngine, gameEngine.mInputController);
        checkSwitching(gameEngine);
        updatePosition(elapsedMillis, gameEngine);
        mRotation += mRotationSpeed * elapsedMillis;
        mPlayerBubbleBg.setPosition(mX + mWidth / 2f, mY + mHeight / 2f);
    }

    private void checkShooting(GameEngine gameEngine, InputController inputController) {
        if (mShoot) {
            // We convert angle to x speed and y speed
            double angle = Utils.getAngle(inputController.mXUp - mStartX,
                    inputController.mYUp - mStartY);
            mSpeedX = (float) (mSpeed * Math.cos(angle));
            mSpeedY = (float) (mSpeed * Math.sin(angle));
            mRotationSpeed = 360 / 1000f;   // Start rotation

            setEnable(false);
            onBubbleShoot(gameEngine);
            mShoot = false;
        }
    }

    private void checkSwitching(GameEngine gameEngine) {
        if (mSwitch) {
            onBubbleSwitch(gameEngine);
            mSwitch = false;
        }
    }

    private void updatePosition(long elapsedMillis, GameEngine gameEngine) {
        mX += mSpeedX * elapsedMillis;
        if (mX <= 0) {
            bounceRight(gameEngine);
        }
        if (mX >= mMaxX) {
            bounceLeft(gameEngine);
        }

        mY += mSpeedY * elapsedMillis;
        if (mY <= -mHeight) {
            reset(gameEngine);
        }
        if (mY >= mMaxY) {
            reset(gameEngine);
        }
    }

    protected void bounceRight(GameEngine gameEngine) {
        mX = 0;
        mSpeedX = -mSpeedX;
    }

    protected void bounceLeft(GameEngine gameEngine) {
        mX = mMaxX;
        mSpeedX = -mSpeedX;
    }

    protected abstract DotSystem getDotSystem(GameEngine gameEngine);

    protected abstract void onBubbleShoot(GameEngine gameEngine);

    protected abstract void onBubbleSwitch(GameEngine gameEngine);

    protected abstract void onBubbleHit(GameEngine gameEngine, Bubble bubble);

    protected abstract void onBubbleReset(GameEngine gameEngine);

    public void reset(GameEngine gameEngine) {
        // Stop the bubble
        mSpeedX = 0;
        mSpeedY = 0;
        // Reset rotation
        mRotation = 0;
        mRotationSpeed = 0;
        setEnable(true);
        onBubbleReset(gameEngine);
    }

    public void showHint() {
    }

    public void removeHint() {
    }

    @Override
    public void onCollision(GameEngine gameEngine, Sprite otherObject) {
        if (otherObject instanceof Bubble) {
            Bubble bubble = (Bubble) otherObject;
            onBubbleHit(gameEngine, bubble);
        }
    }

    @Override
    public void onGameEvent(GameEvent gameEvent) {
        switch ((MyGameEvent) gameEvent) {
            case SHOOT_BUBBLE:
                // We prevent shooting multiple bubble at a time
                if (getEnable()) {
                    mShoot = true;
                }
                break;
            case SWITCH_BUBBLE:
                // We prevent switching bubble when bubble still moving
                if (getEnable()) {
                    mSwitch = true;
                }
                break;
        }
    }

}
