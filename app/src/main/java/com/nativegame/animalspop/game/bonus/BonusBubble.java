package com.nativegame.animalspop.game.bonus;

import com.nativegame.animalspop.game.Layer;
import com.nativegame.animalspop.game.MyGameEvent;
import com.nativegame.animalspop.game.bubble.BubbleColor;
import com.nativegame.animalspop.game.effect.ScoreEffect;
import com.nativegame.animalspop.sound.MySoundEvent;
import com.nativegame.nattyengine.Game;
import com.nativegame.nattyengine.entity.particles.ParticleSystem;
import com.nativegame.nattyengine.entity.sprite.Sprite;

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
    private final float mGravity;

    private float mSpeedX;
    private float mSpeedY;
    private boolean mPop = false;

    private BonusTimeEndListener mBonusTimeEndListener;

    public BonusBubble(Game game, BubbleColor bubbleColor) {
        super(game, bubbleColor.getDrawableId());
        mFireworkSystem = new ParticleSystem(game, bubbleColor.getFireworkDrawableId(), FIREWORK_PARTICLES)
                .setDurationPerParticle(400)
                .setSpeedAngle(2000, 2000)
                .setInitialRotation(0, 360)
                .setRotationSpeed(-720, 720)
                .setAlpha(255, 0, 200)
                .setScale(1, 0, 200)
                .setLayer(Layer.EFFECT_LAYER);
        mScoreEffect = new ScoreEffect(game, bubbleColor.getBonusScoreDrawableId());

        mMinSpeedX = -mPixelFactor * 1000 / 1000f;
        mMaxSpeedX = mPixelFactor * 1000 / 1000f;
        mMinSpeedY = -mPixelFactor * 9000 / 1000f;
        mMaxSpeedY = -mPixelFactor * 6000 / 1000f;
        mScaleSpeed = -2 / 1000f;
        mAlphaSpeed = -500 / 1000f;
        mGravity = mPixelFactor * 10 / 1000f;
        mLayer = Layer.BUBBLE_LAYER;
    }

    public void setBonusTimeEndListener(BonusTimeEndListener listener) {
        mBonusTimeEndListener = listener;
    }

    public void activate(float x, float y) {
        mX = x - mWidth / 2f;
        mY = y - mHeight / 2f;
        // Generate random speed
        mSpeedX = mGame.getRandom().nextFloat() * (mMaxSpeedX - mMinSpeedX) + mMinSpeedX;
        mSpeedY = mGame.getRandom().nextFloat() * (mMaxSpeedY - mMinSpeedY) + mMinSpeedY;
        addToGame();
    }

    @Override
    public void onRemove() {
        if (mBonusTimeEndListener != null) {
            mBonusTimeEndListener.onBonusTimeEnd();
        }
    }

    @Override
    public void onUpdate(long elapsedMillis) {
        updatePosition(elapsedMillis);
        updateShape(elapsedMillis);
    }

    private void updatePosition(long elapsedMillis) {
        if (mPop) {
            return;
        }
        mX += mSpeedX * elapsedMillis;
        mY += mSpeedY * elapsedMillis;
        mSpeedY += mGravity * elapsedMillis;
        // We pop the bubble after falling for 500ms
        if (mSpeedY >= 0) {
            explode();
            mSpeedX = 0;
            mSpeedY = 0;
            mPop = true;
        }
    }

    private void updateShape(long elapsedMillis) {
        if (mPop) {
            mScale += mScaleSpeed * elapsedMillis;
            mAlpha += mAlphaSpeed * elapsedMillis;
            if (mScale <= 0) {
                removeFromGame();
            }
        }
    }

    private void explode() {
        mFireworkSystem.oneShot(mX + mWidth / 2f, mY + mHeight / 2f, FIREWORK_PARTICLES);
        mScoreEffect.activate(mX + mWidth / 2f, mY + mHeight / 2f);
        // Each bonus bubble is 30 points
        for (int i = 0; i < 3; i++) {
            gameEvent(MyGameEvent.BUBBLE_POP);   // We notify the score counter
        }
        mGame.getSoundManager().playSound(MySoundEvent.BUBBLE_POP);
    }

    public interface BonusTimeEndListener {
        void onBonusTimeEnd();
    }

}
