package com.nativegame.animalspop.game.bubble.type;

import com.nativegame.animalspop.R;
import com.nativegame.animalspop.game.bubble.Bubble;
import com.nativegame.animalspop.game.bubble.BubbleColor;
import com.nativegame.animalspop.sound.MySoundEvent;
import com.nativegame.engine.GameEngine;
import com.nativegame.engine.particles.ParticleSystem;

/**
 * Created by Oscar Liang on 2022/09/18
 */

public class ObstacleBubble extends Bubble {

    private static final int EXPLOSION_PARTICLES = 10;

    private final ParticleSystem mExplosionParticleSystem;
    public boolean mIsObstacle = true;

    public ObstacleBubble(GameEngine gameEngine, int row, int col) {
        super(gameEngine, row, col, BubbleColor.OBSTACLE);

        mExplosionParticleSystem = new ParticleSystem(gameEngine, R.drawable.wood_particle_01, EXPLOSION_PARTICLES)
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
        if (mIsObstacle) {
            // We pop the obstacle if it hasn't been collected
            popObstacle(gameEngine);
            gameEngine.mSoundManager.playSound(MySoundEvent.WOOD_EXPLODE);
        } else {
            // Otherwise, we pop the bubble
            super.popBubble(gameEngine);
        }
    }

    @Override
    public void popFloater(GameEngine gameEngine) {
        if (mIsObstacle) {
            // We pop the obstacle if it hasn't been collected
            popObstacle(gameEngine);
        } else {
            // Otherwise, we pop the floater
            super.popFloater(gameEngine);
        }
    }

    private void popObstacle(GameEngine gameEngine) {
        mExplosionParticleSystem.oneShot(gameEngine, mX + mWidth / 2f, mY + mHeight / 2f, EXPLOSION_PARTICLES);
        setBubbleColor(BubbleColor.BLANK);
        mIsObstacle = false;
    }

}
