package com.nativegame.animalspop.game.bubble;

import com.nativegame.animalspop.R;
import com.nativegame.animalspop.game.Layer;
import com.nativegame.animalspop.game.MyGameEvent;
import com.nativegame.animalspop.game.bubble.type.DummyBubble;
import com.nativegame.animalspop.game.bubble.type.LargeObstacleBubble;
import com.nativegame.animalspop.game.bubble.type.LockedBubble;
import com.nativegame.animalspop.game.bubble.type.ObstacleBubble;
import com.nativegame.animalspop.game.effect.FloaterEffect;
import com.nativegame.animalspop.game.effect.HintEffect;
import com.nativegame.animalspop.game.effect.ScoreEffect;
import com.nativegame.animalspop.sound.MySoundEvent;
import com.nativegame.nattyengine.Game;
import com.nativegame.nattyengine.collision.CollisionType;
import com.nativegame.nattyengine.collision.shape.CircleCollisionShape;
import com.nativegame.nattyengine.entity.sprite.CollidableSprite;
import com.nativegame.nattyengine.entity.particles.ParticleSystem;

import java.util.Arrays;

/**
 * Created by Oscar Liang on 2022/09/18
 */

public class Bubble extends CollidableSprite {

    private static final int EXPLOSION_PARTICLES = 6;

    //--------------------------------------------------------
    // Variables for graph algorithm
    //--------------------------------------------------------
    public int mRow;
    public int mCol;
    public BubbleColor mBubbleColor;
    public int mDepth = -1;   // For bfs
    public boolean mDiscover = false;   // For dfs
    public final Bubble[] mEdges = new Bubble[6];
    //========================================================

    private final ParticleSystem mExplosionParticleSystem;
    private final ParticleSystem mLightParticleSystem;
    private final ScoreEffect mScoreEffect;
    private final FloaterEffect mFloaterEffect;
    private final HintEffect mHintEffect;

    private final float mSpeedY;
    private float mScaleSpeed;
    private float mAlphaSpeed;
    private float mShiftPosition;
    private long mShiftTotalTime;
    private long mPopTotalTime;

    private boolean mShiftDown = false;
    private boolean mShiftUp = false;
    private boolean mPop = false;

    public Bubble(Game game, BubbleColor bubbleColor) {
        super(game, bubbleColor.getDrawableId());
        setCollisionShape(new CircleCollisionShape(mWidth, mHeight));
        setCollisionType(bubbleColor);
        mBubbleColor = bubbleColor;
        mSpeedY = mPixelFactor * 3000 / 1000f;   // We want to move at 3000px per second
        mExplosionParticleSystem = new ParticleSystem(game, R.drawable.sparkle, EXPLOSION_PARTICLES)
                .setDurationPerParticle(800)
                .setSpeedX(-600, 600)
                .setSpeedY(-600, 600)
                .setAccelerationY(1)
                .setInitialRotation(0, 360)
                .setRotationSpeed(-720, 720)
                .setAlpha(255, 0, 400)
                .setScale(1, 0, 400)
                .setLayer(Layer.EFFECT_LAYER);
        mLightParticleSystem = new ParticleSystem(game, R.drawable.circle_light, 1)
                .setDurationPerParticle(400)
                .setAlpha(255, 0, 200)
                .setScale(0, 2)
                .setLayer(Layer.EFFECT_LAYER);
        mScoreEffect = new ScoreEffect(game, bubbleColor.getScoreDrawableId());
        mFloaterEffect = new FloaterEffect(game, bubbleColor.getDrawableId());
        mHintEffect = new HintEffect(game);
        mLayer = Layer.BUBBLE_LAYER;
    }

    public void setBubbleColor(BubbleColor bubbleColor) {
        mBubbleColor = bubbleColor;
        setSpriteBitmap(bubbleColor.getDrawableId());
        setCollisionType(bubbleColor);
        // Update effect color
        if (bubbleColor != BubbleColor.BLANK) {
            mScoreEffect.setSpriteBitmap(bubbleColor.getScoreDrawableId());
            mFloaterEffect.setSpriteBitmap(bubbleColor.getDrawableId());
        }
    }

    private void setCollisionType(BubbleColor bubbleColor) {
        if (bubbleColor == BubbleColor.BLANK || bubbleColor == BubbleColor.DUMMY) {
            setCollisionType(CollisionType.NONE);
        } else {
            setCollisionType(CollisionType.PASSIVE);
        }
    }

    public void setPosition(float x, float y, int row, int col) {
        mX = x;
        mY = y;
        mRow = row;
        mCol = col;
    }

    @Override
    public void onStart() {
        mFloaterEffect.addToGame();
    }

    @Override
    public void onRemove() {
        mFloaterEffect.removeFromGame();
    }

    @Override
    public void onUpdate(long elapsedMillis) {
        checkPopBubble(elapsedMillis);
        updatePosition(elapsedMillis);
        updateShape(elapsedMillis);
    }

    private void checkPopBubble(long elapsedTimeMillis) {
        if (mPop) {
            mPopTotalTime += elapsedTimeMillis;
            if (mPopTotalTime > mDepth * 80L) {
                // We activate the effect after wait time
                mExplosionParticleSystem.oneShot(mX + mWidth / 2f, mY + mHeight / 2f, EXPLOSION_PARTICLES);
                mLightParticleSystem.oneShot(mX + mWidth / 2f, mY + mHeight / 2f, 1);
                mScoreEffect.activate(mX + mWidth / 2f, mY + mHeight / 2f);
                // Check is locked bubble in adjacent edge
                checkLockedBubble();
                mScaleSpeed = -2 / 1000f;   // We want to shrink at 1 per 500 ms
                mAlphaSpeed = -500 / 1000f;
                // Player bubble pop sound
                mGame.getSoundManager().playSound(MySoundEvent.BUBBLE_POP);
                // Reset depth
                mDepth = -1;
                mPop = false;
            }
        }
    }

    private void updatePosition(long elapsedTimeMillis) {
        if (mShiftDown) {
            mShiftTotalTime += elapsedTimeMillis;
            if (mShiftTotalTime > 800) {
                mY += mSpeedY * elapsedTimeMillis;
                if (mY >= mShiftPosition) {   // Stop shifting
                    mShiftPosition = 0;
                    mShiftDown = false;
                }
            }
        }
        if (mShiftUp) {
            mY -= mSpeedY * elapsedTimeMillis;
            if (mY <= mShiftPosition) {   // Stop shifting
                mShiftPosition = 0;
                mShiftUp = false;
            }
        }
    }

    private void updateShape(long elapsedTimeMillis) {
        mScale += mScaleSpeed * elapsedTimeMillis;
        mAlpha += mAlphaSpeed * elapsedTimeMillis;
        if (mScale <= 0) {
            setBubbleColor(BubbleColor.BLANK);   // We update the bitmap after shrinking
            mScaleSpeed = 0;
            mAlphaSpeed = 0;
            mScale = 1;
            mAlpha = 255;
        }
    }

    //--------------------------------------------------------
    // Methods for graph algorithm
    //--------------------------------------------------------
    public void popBubble() {
        // We prevent popping multiple times
        if (mBubbleColor == BubbleColor.BLANK) {
            return;
        }
        mBubbleColor = BubbleColor.BLANK;   // We set it to blank now, so the BubbleSystem will sync
        gameEvent(MyGameEvent.BUBBLE_POP);   // We notify the target counter
        mPopTotalTime = 0;
        mPop = true;
    }

    public void popFloater() {
        // We prevent popping multiple times
        if (mBubbleColor == BubbleColor.BLANK) {
            return;
        }
        mFloaterEffect.activate(mX + mWidth / 2f, mY + mHeight / 2f);
        gameEvent(MyGameEvent.BUBBLE_POP);
        setBubbleColor(BubbleColor.BLANK);
    }

    public void checkLockedBubble() {
        for (Bubble b : mEdges) {
            // Check is any LockedBubble in adjacent edge
            if (b instanceof LockedBubble) {
                LockedBubble lockedBubble = (LockedBubble) b;
                // Check is it still locked
                if (lockedBubble.mIsLocked) {
                    lockedBubble.popBubble();
                }
            }
        }
    }

    public void checkObstacleBubble() {
        for (Bubble b : mEdges) {
            // Check is any ObstacleBubble in adjacent edge
            if (b instanceof ObstacleBubble) {
                ObstacleBubble obstacleBubble = (ObstacleBubble) b;
                // Check is it still obstacle
                if (obstacleBubble.mIsObstacle) {
                    // We pop the obstacle and bubble
                    obstacleBubble.popBubble();
                    popBubble();
                }
            } else if (b instanceof LargeObstacleBubble) {
                LargeObstacleBubble largeObstacleBubble = (LargeObstacleBubble) b;
                // Check is it still obstacle
                if (largeObstacleBubble.mIsObstacle) {
                    // We pop the obstacle and bubble
                    largeObstacleBubble.popBubble();
                    popBubble();
                }
            } else if (b instanceof DummyBubble) {
                // Check is DummyBubble has target
                DummyBubble dummyBubble = (DummyBubble) b;
                Bubble targetBubble = dummyBubble.mTargetBubble;
                // We make sure the target is not in edges, or it will detect twice
                if (targetBubble instanceof LargeObstacleBubble
                        && !Arrays.asList(mEdges).contains(targetBubble)) {
                    // We pop the target and bubble
                    dummyBubble.popBubble();
                    popBubble();
                }
            }
        }
    }

    public Bubble getCollidedBubble(CollidableSprite collidableSprite) {
        // Check player collide bubble at bottom or side
        if (collidableSprite.mY > mY + mHeight / 2f) {
            // Check player collide bubble at right or left bottom
            if (collidableSprite.mX > mX) {
                return mEdges[5];
            } else {
                return mEdges[4];
            }
        } else {
            // Check player collide bubble at right or left side
            if (collidableSprite.mX > mX) {
                return mEdges[3];
            } else {
                return mEdges[2];
            }
        }
    }

    public void shiftBubble(float shiftDistance) {
        if (shiftDistance > 0) {
            mShiftDown = true;
        } else {
            mShiftUp = true;
        }
        mShiftPosition = mY + shiftDistance;
        mShiftTotalTime = 0;
    }

    public boolean isShifting() {
        return mShiftUp || mShiftDown;
    }
    //========================================================

    public void addHint() {
        mHintEffect.activate(mX + mWidth / 2f, mY + mHeight / 2f);
    }

    public void removeHint() {
        mHintEffect.removeFromGame();
    }

}
