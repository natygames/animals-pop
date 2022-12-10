package com.nativegame.animalspop.game.player;

import com.nativegame.animalspop.R;
import com.nativegame.animalspop.game.Layer;
import com.nativegame.animalspop.game.MyGameEvent;
import com.nativegame.animalspop.game.bubble.Bubble;
import com.nativegame.animalspop.game.bubble.BubbleSystem;
import com.nativegame.animalspop.game.player.dot.DotSystem;
import com.nativegame.nattyengine.Game;
import com.nativegame.nattyengine.collision.shape.CircleCollisionShape;
import com.nativegame.nattyengine.event.GameEvent;
import com.nativegame.nattyengine.collision.Collidable;
import com.nativegame.nattyengine.input.TouchController;
import com.nativegame.nattyengine.entity.sprite.Sprite;
import com.nativegame.nattyengine.entity.sprite.CollidableSprite;

/**
 * Created by Oscar Liang on 2022/09/18
 */

public abstract class PlayerBubble extends CollidableSprite {

    protected final BubbleSystem mBubbleSystem;
    protected final DotSystem mDotSystem;
    protected final PlayerBubbleBg mPlayerBubbleBg;
    protected final float mStartX;
    protected final float mStartY;
    private final float mMaxX;
    private final float mMaxY;
    private final float mSpeed;

    private float mSpeedX;
    private float mSpeedY;
    private float mRotationSpeed;
    private boolean mShoot = false;
    private boolean mSwitch = false;
    private boolean mEnable = false;

    protected PlayerBubble(BubbleSystem bubbleSystem, Game game, int drawableId) {
        super(game, drawableId);
        setCollisionShape(new CircleCollisionShape(mWidth, mHeight));
        mBubbleSystem = bubbleSystem;
        mDotSystem = getDotSystem();
        mPlayerBubbleBg = new PlayerBubbleBg(game, R.drawable.bubble_bg);
        // Init bubble position
        mStartX = game.getScreenWidth() / 2f;
        mStartY = game.getScreenHeight() * 4 / 5f - mPixelFactor * 300;
        // Player shooting range
        mMaxX = game.getScreenWidth() - mWidth;
        mMaxY = game.getScreenHeight();
        mSpeed = mPixelFactor * 8000 / 1000;   // We want to move at 8000px per second
        mLayer = Layer.BUBBLE_LAYER;
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
    public void onStart() {
        mX = mStartX - mWidth / 2f;
        mY = mStartY - mHeight / 2f;
        mDotSystem.addToGame();
        mPlayerBubbleBg.addToGame();
    }

    @Override
    public void onRemove() {
        mDotSystem.removeFromGame();
        mPlayerBubbleBg.removeFromGame();
    }

    @Override
    public void onUpdate(long elapsedMillis) {
        checkShooting(mGame.getTouchController());
        checkSwitching();
        updatePosition(elapsedMillis);
        mRotation += mRotationSpeed * elapsedMillis;
        mPlayerBubbleBg.setPosition(mX + mWidth / 2f, mY + mHeight / 2f);
    }

    private void checkShooting(TouchController touchController) {
        if (mShoot) {
            // We convert angle to x speed and y speed
            double angle = Math.atan2(touchController.mYUp - mStartY,
                    touchController.mXUp - mStartX);
            mSpeedX = (float) (mSpeed * Math.cos(angle));
            mSpeedY = (float) (mSpeed * Math.sin(angle));
            mRotationSpeed = 360 / 1000f;   // Start rotation

            setEnable(false);
            onBubbleShoot();
            mShoot = false;
        }
    }

    private void checkSwitching() {
        if (mSwitch) {
            onBubbleSwitch();
            mSwitch = false;
        }
    }

    private void updatePosition(long elapsedMillis) {
        mX += mSpeedX * elapsedMillis;
        if (mX <= 0) {
            bounceRight();
        }
        if (mX >= mMaxX) {
            bounceLeft();
        }

        mY += mSpeedY * elapsedMillis;
        if (mY <= -mHeight) {
            reset();
        }
        if (mY >= mMaxY) {
            reset();
        }
    }

    protected void bounceRight() {
        mX = 0;
        mSpeedX = -mSpeedX;
    }

    protected void bounceLeft() {
        mX = mMaxX;
        mSpeedX = -mSpeedX;
    }

    protected abstract DotSystem getDotSystem();

    protected abstract void onBubbleShoot();

    protected abstract void onBubbleSwitch();

    protected abstract void onBubbleHit(Bubble bubble);

    protected abstract void onBubbleReset();

    public void reset() {
        // Stop the bubble
        mSpeedX = 0;
        mSpeedY = 0;
        // Reset rotation
        mRotation = 0;
        mRotationSpeed = 0;
        setEnable(true);
        onBubbleReset();
    }

    public void showHint() {
    }

    public void removeHint() {
    }

    @Override
    public void onCollision(Collidable otherObject) {
        if (otherObject instanceof Bubble) {
            Bubble bubble = (Bubble) otherObject;
            onBubbleHit(bubble);
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

    protected static class PlayerBubbleBg extends Sprite {

        public PlayerBubbleBg(Game game, int drawableResId) {
            super(game, drawableResId);
        }

        public void setPosition(float x, float y) {
            mX = x - mWidth / 2f;
            mY = y - mHeight / 2f;
        }

        @Override
        public void onStart() {
            mLayer = Layer.BACKGROUND_LAYER;
        }

        @Override
        public void onUpdate(long elapsedMillis) {
        }

    }

}
