package com.nativegame.animalspop.game.bubble;

import com.nativegame.animalspop.R;
import com.nativegame.animalspop.game.MyGameEvent;
import com.nativegame.animalspop.game.bubble.effect.FloaterEffect;
import com.nativegame.animalspop.game.bubble.effect.HintEffect;
import com.nativegame.animalspop.game.bubble.effect.ScoreEffect;
import com.nativegame.animalspop.game.bubble.type.DummyBubble;
import com.nativegame.animalspop.game.bubble.type.LargeObstacleBubble;
import com.nativegame.animalspop.game.bubble.type.LockedBubble;
import com.nativegame.animalspop.game.bubble.type.ObstacleBubble;
import com.nativegame.animalspop.sound.MySoundEvent;
import com.nativegame.engine.sprite.BodyType;
import com.nativegame.engine.GameEngine;
import com.nativegame.engine.sprite.Sprite;
import com.nativegame.engine.particles.ParticleSystem;

import java.util.ArrayList;

/**
 * Created by Oscar Liang on 2022/09/18
 */

public class Bubble extends Sprite {

    private static final int EXPLOSION_PARTICLES = 6;
    private static final long WAIT_TIME_BEFORE_SHIFT = 800;
    private static final long WAIT_TIME_BEFORE_POP = 80;

    //--------------------------------------------------------
    // Variables for graph operation
    //--------------------------------------------------------
    public final int mRow, mCol;
    public BubbleColor mBubbleColor;
    public int mDepth = -1;
    public int mOrder = -1;
    public boolean mDiscover = false;
    public final ArrayList<Bubble> mEdges = new ArrayList<>(6);
    //=====================================================================

    private final ParticleSystem mExplosionParticleSystem;
    private final ParticleSystem mLightParticleSystem;
    private final ScoreEffect mScoreEffect;
    private final FloaterEffect mFloaterEffect;
    private final HintEffect mHintEffect;

    private final float mSpeedY;
    private float mScaleSpeed;
    private float mAlphaSpeed;

    private float mShiftPosition;
    private long mShiftTotalMillis;
    private boolean mShiftDown = false;
    private boolean mShiftUp = false;

    private long mPopTotalMillis;
    private boolean mPop = false;

    public Bubble(GameEngine gameEngine, int row, int col, BubbleColor bubbleColor) {
        super(gameEngine, bubbleColor.getImageResId(), BodyType.Circular);
        mRow = row;
        mCol = col;
        mBubbleColor = bubbleColor;
        mSpeedY = mPixelFactor * 3000 / 1000f;   // We want to move at 3000px per second

        mExplosionParticleSystem = new ParticleSystem(gameEngine, R.drawable.sparkle, EXPLOSION_PARTICLES)
                .setLayer(4)
                .setDuration(800)
                .setSpeedX(-600, 600)
                .setSpeedY(-600, 600)
                .setAccelerationY(1)
                .setInitialRotation(0, 360)
                .setRotationSpeed(-720, 720)
                .setAlpha(255, 0, 400)
                .setScale(1, 0, 400);
        mLightParticleSystem = new ParticleSystem(gameEngine, R.drawable.circle_light, 1)
                .setLayer(4)
                .setDuration(400)
                .setAlpha(255, 0, 200)
                .setScale(0, 2);
        mScoreEffect = new ScoreEffect(gameEngine, bubbleColor.getScoreResId());
        mFloaterEffect = new FloaterEffect(gameEngine, bubbleColor.getImageResId());
        mHintEffect = new HintEffect(gameEngine);
    }

    public void setBubbleColor(BubbleColor bubbleColor) {
        mBubbleColor = bubbleColor;
        setBitmap(bubbleColor.getImageResId());
        // Update particle color
        if (bubbleColor != BubbleColor.BLANK) {
            mScoreEffect.setBitmap(bubbleColor.getScoreResId());
            mFloaterEffect.setBitmap(bubbleColor.getImageResId());
        }
    }

    @Override
    public void startGame(GameEngine gameEngine) {
        mX = mCol * mWidth;
        mY = mRow * mHeight * 0.85f;
        // We adjust x interval at odd row
        if ((mRow % 2) != 0) {
            mX += mWidth / 2f;
        }
    }

    @Override
    public void addToGameEngine(GameEngine gameEngine, int layer) {
        super.addToGameEngine(gameEngine, layer);
        mFloaterEffect.addToGameEngine(gameEngine, 4);
    }

    @Override
    public void onUpdate(long elapsedMillis, GameEngine gameEngine) {
        checkPopBubble(elapsedMillis, gameEngine);
        updatePosition(elapsedMillis);
        updateShape(elapsedMillis);
    }

    private void checkPopBubble(long elapsedMillis, GameEngine gameEngine) {
        if (mPop) {
            mPopTotalMillis += elapsedMillis;
            if (mPopTotalMillis > mDepth * WAIT_TIME_BEFORE_POP) {
                // We activate the effect after wait time
                mExplosionParticleSystem.oneShot(gameEngine, mX + mWidth / 2f, mY + mHeight / 2f, EXPLOSION_PARTICLES);
                mLightParticleSystem.oneShot(gameEngine, mX + mWidth / 2f, mY + mHeight / 2f, 1);
                mScoreEffect.activate(gameEngine, mX + mWidth / 2f, mY + mHeight / 2f, 4);
                // Check is locked bubble in adjacent edge
                checkLockedBubble(gameEngine);
                mScaleSpeed = -2 / 1000f;   // We want to shrink at 1 per 500 ms
                mAlphaSpeed = -500 / 1000f;
                // Player bubble pop sound
                if (mOrder <= 4) {
                    gameEngine.mSoundManager.playSound(MySoundEvent.BUBBLE_POP);
                }
                // Reset depth and order
                mDepth = -1;
                mOrder = -1;
                mPop = false;
            }
        }
    }

    private void updatePosition(long elapsedMillis) {
        if (mShiftDown) {
            mShiftTotalMillis += elapsedMillis;
            if (mShiftTotalMillis > WAIT_TIME_BEFORE_SHIFT) {
                mY += mSpeedY * elapsedMillis;
                if (mY >= mShiftPosition) {   // Stop shifting
                    mShiftPosition = 0;
                    mShiftDown = false;
                }
            }
        }
        if (mShiftUp) {
            mY -= mSpeedY * elapsedMillis;
            if (mY <= mShiftPosition) {   // Stop shifting
                mShiftPosition = 0;
                mShiftUp = false;
            }
        }
    }

    private void updateShape(long elapsedMillis) {
        mScale += mScaleSpeed * elapsedMillis;
        mAlpha += mAlphaSpeed * elapsedMillis;
        if (mScale <= 0) {
            setBitmap(BubbleColor.BLANK.getImageResId());   // We update the bitmap after shrinking
            mScaleSpeed = 0;
            mAlphaSpeed = 0;
            mScale = 1;
            mAlpha = 255;
        }
    }

    public void checkLockedBubble(GameEngine gameEngine) {
        for (Bubble bubble : mEdges) {
            // If we have locked bubble in adjacent edge
            if (bubble instanceof LockedBubble) {
                LockedBubble lockedBubble = (LockedBubble) bubble;
                // And it is locked
                if (lockedBubble.mIsLocked) {
                    // We unlock it
                    lockedBubble.popBubble(gameEngine);
                }
            }
        }
    }

    public void checkObstacleBubble(GameEngine gameEngine) {
        for (Bubble bubble : mEdges) {
            // If we have obstacle bubble in adjacent edge
            if (bubble instanceof ObstacleBubble) {
                ObstacleBubble obstacleBubble = (ObstacleBubble) bubble;
                // And it is not popped yet
                if (obstacleBubble.mIsObstacle) {
                    // We pop it
                    obstacleBubble.popBubble(gameEngine);
                    popBubble(gameEngine);
                }
            } else if (bubble instanceof LargeObstacleBubble) {
                LargeObstacleBubble largeObstacleBubble = (LargeObstacleBubble) bubble;
                // And it is not popped yet
                if (largeObstacleBubble.mIsObstacle) {
                    // We pop it
                    largeObstacleBubble.popBubble(gameEngine);
                    popBubble(gameEngine);
                }
            } else if (bubble instanceof DummyBubble) {
                DummyBubble dummyBubble = (DummyBubble) bubble;
                Bubble targetBubble = dummyBubble.mTargetBubble;
                // If the dummy bubble has target
                if (targetBubble == null) {
                    continue;
                }
                boolean isTargetInEdges = false;   // Check is the target in edges
                for (Bubble sameBubble : mEdges) {
                    if (targetBubble.equals(sameBubble)) {
                        isTargetInEdges = true;   // We skip it and let the target do its works
                        break;
                    }
                }
                // And the target is not in edges
                if (!isTargetInEdges) {
                    // We pop the target bubble
                    targetBubble.popBubble(gameEngine);
                    popBubble(gameEngine);
                }
            }
        }
    }

    public void popBubble(GameEngine gameEngine) {
        // We prevent popping multiple times
        if (mBubbleColor == BubbleColor.BLANK) {
            return;
        }
        mBubbleColor = BubbleColor.BLANK;   // We set it to blank now, so the BubbleSystem will synchronized
        gameEngine.onGameEvent(MyGameEvent.BUBBLE_POP);   // Notify the target counter
        mPopTotalMillis = 0;
        mPop = true;
    }

    public void popFloater(GameEngine gameEngine) {
        // We prevent popping multiple times
        if (mBubbleColor == BubbleColor.BLANK) {
            return;
        }
        mFloaterEffect.activate(mX + mWidth / 2f, mY + mHeight / 2f, gameEngine.mRandom);
        gameEngine.onGameEvent(MyGameEvent.BUBBLE_POP);
        setBubbleColor(BubbleColor.BLANK);
    }

    public void shiftBubble(float shiftDistance) {
        if (shiftDistance > 0) {
            mShiftDown = true;
        } else {
            mShiftUp = true;
        }
        mShiftPosition = mY + shiftDistance;
        mShiftTotalMillis = 0;
    }

    public boolean isShifting() {
        return mShiftUp || mShiftDown;
    }

    public void addHint(GameEngine gameEngine) {
        mHintEffect.init(gameEngine, mX + mWidth / 2f, mY + mHeight / 2f, mLayer + 1);
    }

    public void removeHint(GameEngine gameEngine) {
        mHintEffect.removeFromGameEngine(gameEngine);
    }

}
