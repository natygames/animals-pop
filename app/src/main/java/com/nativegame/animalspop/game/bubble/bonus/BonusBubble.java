package com.nativegame.animalspop.game.bubble.bonus;

import com.nativegame.animalspop.game.MyGameEvent;
import com.nativegame.animalspop.game.bubble.BubbleColor;
import com.nativegame.animalspop.game.bubble.effect.ScoreEffect;
import com.nativegame.animalspop.sound.MySoundEvent;
import com.nativegame.engine.GameEngine;
import com.nativegame.engine.particles.ParticleSystem;
import com.nativegame.engine.sprite.BodyType;
import com.nativegame.engine.sprite.Sprite;

import java.util.Random;

/**
 * Created by Oscar Liang on 2022/09/18
 */

public class BonusBubble extends Sprite {

    private static final int FIREWORK_PARTICLES = 30;

    private final ParticleSystem mFireworkSystem;
    private final ScoreEffect mScoreEffect;

    private final float mMinSpeedX;
    private final float mMaxSpeedX;
    private final float mMinSpeedY;
    private final float mMaxSpeedY;
    private final float mScaleSpeed;
    private final float mAlphaSpeed;

    private float mSpeedX, mSpeedY;
    private final float mGravity;

    private boolean mPop = false;

    public BonusBubble(GameEngine gameEngine, BubbleColor bubbleColor) {
        super(gameEngine, bubbleColor.getImageResId(), BodyType.None);
        mFireworkSystem = new ParticleSystem(gameEngine, bubbleColor.getFirework(), FIREWORK_PARTICLES)
                .setDuration(400)
                .setSpeedAngle(2000, 2000)
                .setInitialRotation(0, 360)
                .setRotationSpeed(-720, 720)
                .setAlpha(255, 0, 200)
                .setScale(1, 0, 200);
        mScoreEffect = new ScoreEffect(gameEngine, bubbleColor.getBonusScoreResId());

        mMinSpeedX = -mPixelFactor * 1000 / 1000f;
        mMaxSpeedX = mPixelFactor * 1000 / 1000f;
        mMinSpeedY = -mPixelFactor * 9000 / 1000f;
        mMaxSpeedY = -mPixelFactor * 6000 / 1000f;
        mScaleSpeed = -2 / 1000f;
        mAlphaSpeed = -500 / 1000f;

        mGravity = mPixelFactor * 10 / 1000f;
    }

    @Override
    public void startGame(GameEngine gameEngine) {
    }

    public void activate(GameEngine gameEngine, float x, float y, Random r, int layer) {
        mX = x - mWidth / 2f;
        mY = y - mHeight / 2f;
        // Generate random speed
        mSpeedX = r.nextFloat() * (mMaxSpeedX - mMinSpeedX) + mMinSpeedX;
        mSpeedY = r.nextFloat() * (mMaxSpeedY - mMinSpeedY) + mMinSpeedY;
        addToGameEngine(gameEngine, layer);
    }

    @Override
    public void onUpdate(long elapsedMillis, GameEngine gameEngine) {
        updatePosition(elapsedMillis, gameEngine);
        updateShape(elapsedMillis, gameEngine);
    }

    private void updatePosition(long elapsedMillis, GameEngine gameEngine) {
        if (mPop) {
            return;
        }
        mX += mSpeedX * elapsedMillis;
        mY += mSpeedY * elapsedMillis;
        mSpeedY += mGravity * elapsedMillis;
        // We pop the bubble after falling for 500ms
        if (mSpeedY >= 0) {
            explode(gameEngine);
            mSpeedX = 0;
            mSpeedY = 0;
            mPop = true;
        }
    }

    private void updateShape(long elapsedMillis, GameEngine gameEngine) {
        if (mPop) {
            mScale += mScaleSpeed * elapsedMillis;
            mAlpha += mAlphaSpeed * elapsedMillis;
            if (mScale <= 0) {
                removeFromGameEngine(gameEngine);
            }
        }
    }

    private void explode(GameEngine gameEngine) {
        mFireworkSystem.oneShot(gameEngine, mX + mWidth / 2f, mY + mHeight / 2f, FIREWORK_PARTICLES);
        mScoreEffect.activate(gameEngine, mX + mWidth / 2f, mY + mHeight / 2f, 4);
        for (int i = 0; i < 3; i++) {
            gameEngine.onGameEvent(MyGameEvent.BUBBLE_POP);   // We notify the score counter
        }
        gameEngine.mSoundManager.playSound(MySoundEvent.BUBBLE_POP);
    }

}
