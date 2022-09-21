package com.nativegame.animalspop.game.bubble.type;

import com.nativegame.animalspop.R;
import com.nativegame.animalspop.game.bubble.Bubble;
import com.nativegame.animalspop.game.bubble.BubbleColor;
import com.nativegame.engine.GameEngine;
import com.nativegame.engine.particles.ParticleSystem;

/**
 * Created by Oscar Liang on 2022/09/18
 */

public class LockedBubble extends Bubble {

    private static final int EXPLOSION_PARTICLES = 8;

    private final BubbleColor mUnlockBubbleColor;
    private final ParticleSystem mExplosionParticleSystem;
    public boolean mIsLocked = true;

    public LockedBubble(GameEngine gameEngine, int row, int col, BubbleColor bubbleColor) {
        super(gameEngine, row, col, BubbleColor.LOCKED);
        mUnlockBubbleColor = bubbleColor;
        mExplosionParticleSystem = new ParticleSystem(gameEngine, R.drawable.ice_particle, EXPLOSION_PARTICLES)
                .setLayer(4)
                .setDuration(600)
                .setSpeedX(-800, 800)
                .setSpeedY(-800, 800)
                .setInitialRotation(0, 360)
                .setRotationSpeed(-720, 720)
                .setAlpha(255, 0, 300)
                .setScale(1, 0, 300);
    }

    @Override
    public void popBubble(GameEngine gameEngine) {
        if (mIsLocked) {
            // We unlock this bubble if it is locked
            unlock(gameEngine);
        } else {
            // Otherwise, we pop the bubble
            super.popBubble(gameEngine);
        }
    }

    @Override
    public void popFloater(GameEngine gameEngine) {
        // We unlock this bubble before popping the bubble
        if (mIsLocked) {
            mIsLocked = false;
        }
        super.popFloater(gameEngine);
    }

    private void unlock(GameEngine gameEngine) {
        mExplosionParticleSystem.oneShot(gameEngine, mX + mWidth / 2f, mY + mHeight / 2f, EXPLOSION_PARTICLES);
        setBubbleColor(mUnlockBubbleColor);   // We show the bubble below
        mIsLocked = false;
    }

}
