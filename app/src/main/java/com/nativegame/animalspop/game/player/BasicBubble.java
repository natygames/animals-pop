package com.nativegame.animalspop.game.player;

import android.content.Context;

import com.nativegame.animalspop.R;
import com.nativegame.animalspop.Utils;
import com.nativegame.animalspop.game.MyGameEvent;
import com.nativegame.animalspop.game.bubble.Bubble;
import com.nativegame.animalspop.game.bubble.BubbleColor;
import com.nativegame.animalspop.game.bubble.BubbleSystem;
import com.nativegame.animalspop.game.player.dot.DotSystem;
import com.nativegame.animalspop.level.MyLevel;
import com.nativegame.animalspop.sound.MySoundEvent;
import com.nativegame.engine.sprite.BodyType;
import com.nativegame.engine.GameEngine;
import com.nativegame.engine.GameEvent;
import com.nativegame.engine.sprite.Sprite;
import com.nativegame.engine.particles.ParticleSystem;

/**
 * Created by Oscar Liang on 2022/09/18
 */

public class BasicBubble extends PlayerBubble {

    private final BubbleQueue mBubbleQueue;
    private final NextBubble mNextBubble;
    private final CircleBg mCircleBg;
    private final ParticleSystem mTrailParticleSystem;
    private final boolean mHintEnable;

    public BubbleColor mBubbleColor;

    public BasicBubble(BubbleSystem bubbleSystem, GameEngine gameEngine) {
        super(bubbleSystem, gameEngine, BubbleColor.BLANK.getImageResId());
        mBubbleQueue = new BubbleQueue(((MyLevel) gameEngine.mLevel).mPlayer.toCharArray());
        mNextBubble = new NextBubble(gameEngine);
        mCircleBg = new CircleBg(gameEngine);
        mTrailParticleSystem = new ParticleSystem(gameEngine, R.drawable.sparkle, 50)
                .setDuration(300)
                .setEmissionRate(ParticleSystem.RATE_HIGH)
                .setAccelerationX(-2, 2)
                .setInitialRotation(0, 360)
                .setRotationSpeed(-720, 720)
                .setAlpha(255, 0)
                .setScale(2, 4);
        mHintEnable = gameEngine.mActivity.getSharedPreferences("prefs_setting", Context.MODE_PRIVATE)
                .getBoolean("hint", true);
    }

    public void init(GameEngine gameEngine) {
        mX = mStartX - mWidth / 2f;
        mY = mStartY - mHeight / 2f;
        addToGameEngine(gameEngine, 2);
        mNextBubble.init(gameEngine);
        mCircleBg.init(gameEngine);
        updateBubbleColor();
    }

    public void setBubbleColor(BubbleColor bubbleColor) {
        mBubbleColor = bubbleColor;
        setBitmap(bubbleColor.getImageResId());
    }

    public BubbleQueue getBubbleQueue() {
        return mBubbleQueue;
    }

    @Override
    protected DotSystem getDotSystem(GameEngine gameEngine) {
        return new DotSystem(this, gameEngine);
    }

    @Override
    public void startGame(GameEngine gameEngine) {
    }

    @Override
    public void addToGameEngine(GameEngine gameEngine, int layer) {
        super.addToGameEngine(gameEngine, layer);
        mTrailParticleSystem.addToGameEngine(gameEngine, layer - 1);
    }

    @Override
    public void removeFromGameEngine(GameEngine gameEngine) {
        super.removeFromGameEngine(gameEngine);
        mNextBubble.removeFromGameEngine(gameEngine);
        mTrailParticleSystem.removeFromGameEngine(gameEngine);
    }

    @Override
    public void onUpdate(long elapsedMillis, GameEngine gameEngine) {
        super.onUpdate(elapsedMillis, gameEngine);
        mTrailParticleSystem.setEmissionPosition(mX + mWidth / 2f, mY + mHeight / 2f);
    }

    @Override
    protected void onBubbleShoot(GameEngine gameEngine) {
        mBubbleQueue.popBubble();   // Update queue
        mTrailParticleSystem.emit();
        gameEngine.onGameEvent(MyGameEvent.BUBBLE_SHOT);   // Notify the move counter
        gameEngine.mSoundManager.playSound(MySoundEvent.BUBBLE_SHOOT);
    }

    @Override
    protected void onBubbleSwitch(GameEngine gameEngine) {
        mBubbleQueue.switchBubble();   // Update queue
        updateBubbleColor();
        gameEngine.mSoundManager.playSound(MySoundEvent.BUBBLE_SWITCH);
    }

    @Override
    protected void onBubbleHit(GameEngine gameEngine, Bubble bubble) {
        if (bubble.mBubbleColor != BubbleColor.BLANK
                && mY >= bubble.mY) {
            // Notify the target counter
            gameEngine.onGameEvent(MyGameEvent.BUBBLE_HIT);
            // Add one bubble to bubble system
            mBubbleSystem.addBubble(this, bubble);
            gameEngine.mSoundManager.playSound(MySoundEvent.BUBBLE_HIT);
            reset(gameEngine);
        }
    }

    @Override
    protected void onBubbleReset(GameEngine gameEngine) {
        mX = mStartX - mWidth / 2f;
        mY = mStartY - mHeight / 2f;
        mTrailParticleSystem.stopEmit();
        updateBubbleColor();
        gameEngine.onGameEvent(MyGameEvent.BUBBLE_CONSUMED);
    }

    private void updateBubbleColor() {
        // Update current bubble
        BubbleColor bubbleColor = mBubbleQueue.getBubble();
        if (bubbleColor != null) {
            setBubbleColor(bubbleColor);
            mDotSystem.setDotBitmap(bubbleColor.getDotResId());
        } else {
            hide();
        }
        // Update next bubble
        BubbleColor nextBubbleColor = mBubbleQueue.getNextBubble();
        if (nextBubbleColor != null) {
            mNextBubble.setBubbleColor(nextBubbleColor);
        } else {
            mNextBubble.hide();
        }
    }

    @Override
    public void onGameEvent(GameEvent gameEvent) {
        super.onGameEvent(gameEvent);
        switch ((MyGameEvent) gameEvent) {
            case BOOSTER_ADDED:
                // Update next bubble to current bubble color
                BubbleColor currentBubbleColor = mBubbleQueue.getBubble();
                if (currentBubbleColor != null) {
                    mNextBubble.setBubbleColor(currentBubbleColor);
                }
                // Hide current bubble
                hide();
                setEnable(false);
                break;
            case BOOSTER_REMOVED:
            case BOOSTER_CONSUMED:
                // Update next bubble
                BubbleColor nextBubbleColor = mBubbleQueue.getNextBubble();
                if (nextBubbleColor != null) {
                    mNextBubble.setBubbleColor(nextBubbleColor);
                }
                // show current bubble
                show();
                setEnable(true);
                break;
            case ADD_EXTRA_MOVE:
                addExtraBubble();
                break;
        }
    }

    private void hide() {
        mAlpha = 0;
        mPlayerBubbleBg.mAlpha = 0;
    }

    private void show() {
        mAlpha = 255;
        mPlayerBubbleBg.mAlpha = 255;
    }

    @Override
    public void showHint() {
        if (mHintEnable) {
            mBubbleSystem.addBubbleHint(mBubbleColor);
        }
    }

    @Override
    public void removeHint() {
        if (mHintEnable) {
            mBubbleSystem.removeBubbleHint(mBubbleColor);
        }
    }

    private void addExtraBubble() {
        // Add extra bubble to queue
        mBubbleQueue.initExtraBubble();
        updateBubbleColor();
        show();
        mNextBubble.show();
    }

    private class NextBubble extends Sprite {

        public NextBubble(GameEngine gameEngine) {
            super(gameEngine, BubbleColor.BLANK.getImageResId(), BodyType.None);
        }

        public void setBubbleColor(BubbleColor bubbleColor) {
            setBitmap(bubbleColor.getImageResId());
        }

        public void init(GameEngine gameEngine) {
            float radius = mPixelFactor * Utils.BUBBLE_WIDTH;
            mX = mStartX - mWidth / 2f + radius;
            mY = mStartY - mHeight / 2f + radius;
            mScale = 0.8f;
            addToGameEngine(gameEngine, 1);
        }

        @Override
        public void startGame(GameEngine gameEngine) {
        }

        @Override
        public void onUpdate(long elapsedMillis, GameEngine gameEngine) {
            // Nothing should be done here
        }

        private void hide() {
            mAlpha = 0;
        }

        private void show() {
            mAlpha = 255;
        }

    }

    private class CircleBg extends Sprite {

        private final float mRotationSpeed;

        public CircleBg(GameEngine gameEngine) {
            super(gameEngine, R.drawable.player_circle, BodyType.None);
            mRotationSpeed = -50 / 1000f;   // We want to rotate at 50 degree anticlockwise per second
        }

        public void init(GameEngine gameEngine) {
            mX = gameEngine.mScreenWidth / 2f - mWidth / 2f;
            mY = gameEngine.mScreenHeight * 4 / 5f - mHeight / 2f;
            addToGameEngine(gameEngine, 0);
        }

        @Override
        public void startGame(GameEngine gameEngine) {
        }

        @Override
        public void onUpdate(long elapsedMillis, GameEngine gameEngine) {
            mRotation += mRotationSpeed * elapsedMillis;
        }

        @Override
        public void onGameEvent(GameEvent gameEvent) {
            if (gameEvent == MyGameEvent.SHOW_WIN_DIALOG) {
                // Hide the circle
                mAlpha = 0;
            }
        }

    }

}
