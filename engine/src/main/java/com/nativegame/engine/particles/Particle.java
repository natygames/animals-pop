package com.nativegame.engine.particles;

import com.nativegame.engine.GameEngine;
import com.nativegame.engine.sprite.BodyType;
import com.nativegame.engine.sprite.Sprite;

public class Particle extends Sprite {

    private final ParticleSystem mParent;

    public float mSpeedX;
    public float mSpeedY;
    public float mRotationSpeed;
    public float mScaleSpeed;
    public float mAlphaSpeed;

    public float mAccelerationX;
    public float mAccelerationY;

    private long mTotalMillis;
    public long mDuration;
    public long mScaleStartDelay;
    public long mAlphaStartDelay;

    public Particle(ParticleSystem particleSystem, GameEngine gameEngine, int drawableResId) {
        super(gameEngine, drawableResId, BodyType.None);
        mParent = particleSystem;
    }

    @Override
    public void startGame(GameEngine gameEngine) {
    }

    public void activate(GameEngine gameEngine, float x, float y, int layer) {
        mX = x - mWidth / 2f;
        mY = y - mHeight / 2f;
        addToGameEngine(gameEngine, layer);
        mTotalMillis = 0;
    }

    @Override
    public void onRemovedFromGameEngine() {
        mParent.backToPool(this);
    }

    @Override
    public void onUpdate(long elapsedMillis, GameEngine gameEngine) {
        mTotalMillis += elapsedMillis;
        if (mTotalMillis >= mDuration) {
            // Remove from engine and return it to the pool
            removeFromGameEngine(gameEngine);
        } else {
            updateParticle(elapsedMillis);
        }
    }

    private void updateParticle(long elapsedMillis) {
        mX += mSpeedX * elapsedMillis;
        mY += mSpeedY * elapsedMillis;
        mSpeedX += mAccelerationX * elapsedMillis;
        mSpeedY += mAccelerationY * elapsedMillis;
        mRotation += mRotationSpeed * elapsedMillis;

        // Update alpha after delay time
        if (mTotalMillis >= mAlphaStartDelay) {
            mAlpha += mAlphaSpeed * elapsedMillis;
            // We make sure no negative value
            if (mAlpha < 0) {
                mAlpha = 0;
            }
        }

        // Update scale after delay time
        if (mTotalMillis >= mScaleStartDelay) {
            mScale += mScaleSpeed * elapsedMillis;
            // We make sure no negative value
            if (mScale < 0) {
                mScale = 0;
            }
        }
    }

}
