package com.nativegame.animalspop.game.bubble.type;

import com.nativegame.animalspop.R;
import com.nativegame.animalspop.game.bubble.Bubble;
import com.nativegame.animalspop.game.bubble.BubbleColor;
import com.nativegame.nattyengine.Game;
import com.nativegame.nattyengine.entity.particles.ParticleSystem;

/**
 * Created by Oscar Liang on 2022/09/18
 */

public class LockedBubble extends Bubble {

    private static final int EXPLOSION_PARTICLES = 8;

    private final BubbleColor mUnlockBubbleColor;
    private final ParticleSystem mExplosionParticleSystem;

    public boolean mIsLocked = true;

    public LockedBubble(Game game, BubbleColor bubbleColor) {
        super(game, BubbleColor.LOCKED);
        mUnlockBubbleColor = bubbleColor;
        mExplosionParticleSystem = new ParticleSystem(game, R.drawable.ice_particle, EXPLOSION_PARTICLES)
                .setLayer(4)
                .setDurationPerParticle(600)
                .setSpeedX(-800, 800)
                .setSpeedY(-800, 800)
                .setInitialRotation(0, 360)
                .setRotationSpeed(-720, 720)
                .setAlpha(255, 0, 300)
                .setScale(1, 0, 300);
    }

    @Override
    public void popBubble() {
        if (mIsLocked) {
            // We unlock this bubble if it is locked
            unlock();
        } else {
            // Otherwise, we pop the bubble
            super.popBubble();
        }
    }

    @Override
    public void popFloater() {
        // We unlock this bubble before popping the bubble
        if (mIsLocked) {
            mIsLocked = false;
        }
        super.popFloater();
    }

    private void unlock() {
        mExplosionParticleSystem.oneShot(mX + mWidth / 2f, mY + mHeight / 2f, EXPLOSION_PARTICLES);
        setBubbleColor(mUnlockBubbleColor);   // We show the bubble below
        mIsLocked = false;
    }

}
