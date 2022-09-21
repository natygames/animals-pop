package com.nativegame.animalspop.game.bubble.type;

import com.nativegame.animalspop.R;
import com.nativegame.animalspop.game.bubble.Bubble;
import com.nativegame.animalspop.game.bubble.BubbleColor;
import com.nativegame.animalspop.sound.MySoundEvent;
import com.nativegame.engine.GameEngine;
import com.nativegame.engine.particles.ParticleSystem;

import java.util.ArrayList;

/**
 * Created by Oscar Liang on 2022/09/18
 */

public class LargeObstacleBubble extends Bubble {

    private static final int EXPLOSION_PARTICLES = 10;
    private static final float ITEM_SCALE = 3.2f;

    private final ParticleSystem mExplosionParticleSystem;
    private final ArrayList<DummyBubble> mDummyBubble = new ArrayList<>(2);

    private int mCollisionNum;
    public boolean mIsObstacle = true;

    public LargeObstacleBubble(GameEngine gameEngine, int row, int col) {
        super(gameEngine, row, col, BubbleColor.LARGE_OBSTACLE);

        mExplosionParticleSystem = new ParticleSystem(gameEngine, R.drawable.wood_particle_02, EXPLOSION_PARTICLES)
                .setLayer(4)
                .setDuration(600)
                .setSpeedX(-2500, 2500)
                .setSpeedY(-2500, 200)
                .setAccelerationY(5)
                .setInitialRotation(0, 360)
                .setRotationSpeed(-720, 720)
                .setAlpha(255, 0, 300)
                .setScale(1, 0, 300);
    }

    public void addDummyBubble(DummyBubble dummyBubble) {
        dummyBubble.mTargetBubble = this;
        mDummyBubble.add(dummyBubble);
    }

    @Override
    public void startGame(GameEngine gameEngine) {
        super.startGame(gameEngine);
        mScale = ITEM_SCALE;
        mCollisionNum = 0;
    }

    @Override
    public void addToGameEngine(GameEngine gameEngine, int layer) {
        super.addToGameEngine(gameEngine, layer - 1);
    }

    @Override
    public void popBubble(GameEngine gameEngine) {
        if (mIsObstacle) {
            mCollisionNum++;
            // If the time of collision is enough
            if (mCollisionNum >= 2) {
                // We pop the obstacle
                popLargeObstacle(gameEngine);
            } else {
                setBitmap(R.drawable.bubble_large_wood_02);
            }
            gameEngine.mSoundManager.playSound(MySoundEvent.WOOD_EXPLODE);
        } else {
            // Otherwise, we pop the bubble
            super.popBubble(gameEngine);
        }
    }

    @Override
    public void popFloater(GameEngine gameEngine) {
        if (mIsObstacle) {
            popLargeObstacle(gameEngine);
        } else {
            super.popFloater(gameEngine);
        }
    }

    private void popLargeObstacle(GameEngine gameEngine) {
        mExplosionParticleSystem.oneShot(gameEngine, mX + mWidth / 2f, mY + mHeight / 2f, EXPLOSION_PARTICLES);
        setBubbleColor(BubbleColor.BLANK);
        clearDummyBubble();
        mScale = 1;
        mIsObstacle = false;
    }

    private void clearDummyBubble() {
        for (DummyBubble dummyBubble : mDummyBubble) {
            dummyBubble.setBubbleColor(BubbleColor.BLANK);
            dummyBubble.mTargetBubble = null;
        }
    }

}
