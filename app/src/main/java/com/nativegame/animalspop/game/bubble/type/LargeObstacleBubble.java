package com.nativegame.animalspop.game.bubble.type;

import com.nativegame.animalspop.R;
import com.nativegame.animalspop.game.Layer;
import com.nativegame.animalspop.game.bubble.Bubble;
import com.nativegame.animalspop.game.bubble.BubbleColor;
import com.nativegame.animalspop.sound.MySoundEvent;
import com.nativegame.nattyengine.Game;
import com.nativegame.nattyengine.collision.shape.CircleCollisionShape;
import com.nativegame.nattyengine.collision.shape.RectCollisionShape;
import com.nativegame.nattyengine.entity.particles.ParticleSystem;
import com.nativegame.nattyengine.entity.sprite.CollidableSprite;

/**
 * Created by Oscar Liang on 2022/09/18
 */

public class LargeObstacleBubble extends CompositeBubble {

    private static final int EXPLOSION_PARTICLES = 10;

    private final ParticleSystem mExplosionParticleSystem;

    private int mCollisionNum = 0;
    public boolean mIsObstacle = true;

    public LargeObstacleBubble(Game game) {
        super(game, BubbleColor.LARGE_OBSTACLE);
        setCollisionShape(new RectCollisionShape(mWidth, mHeight));
        mExplosionParticleSystem = new ParticleSystem(game, R.drawable.wood_particle_02, EXPLOSION_PARTICLES)
                .setDurationPerParticle(600)
                .setSpeedX(-2500, 2500)
                .setSpeedY(-2500, 200)
                .setAccelerationY(5)
                .setInitialRotation(0, 360)
                .setRotationSpeed(-720, 720)
                .setAlpha(255, 0, 300)
                .setScale(1, 0, 300)
                .setLayer(Layer.EFFECT_LAYER);
        mLayer--;   // We want the obstacle lay below bubbles
    }

    @Override
    public void onStart() {
        super.onStart();
        mX -= mWidth / 3f;
        mScale = 1.05f;
        // Init the DummyBubble
        addDummyBubble((DummyBubble) mEdges[2]);   // Left
        addDummyBubble((DummyBubble) mEdges[3]);   // Right
    }

    @Override
    public void popBubble() {
        if (mIsObstacle) {
            mCollisionNum++;
            // If the time of collision is enough
            if (mCollisionNum >= 2) {
                // We pop the obstacle
                popLargeObstacle();
            } else {
                setSpriteBitmap(R.drawable.bubble_large_wood_02);
            }
            mGame.getSoundManager().playSound(MySoundEvent.WOOD_EXPLODE);
        } else {
            // Otherwise, we pop the bubble
            super.popBubble();
        }
    }

    @Override
    public void popFloater() {
        if (mIsObstacle) {
            // We pop the obstacle if it hasn't been collected
            popLargeObstacle();
        } else {
            // Otherwise, we pop the floater
            super.popFloater();
        }
    }

    private void popLargeObstacle() {
        mExplosionParticleSystem.oneShot(mX + mWidth / 2f, mY + mHeight / 2f, EXPLOSION_PARTICLES);
        setBubbleColor(BubbleColor.BLANK);
        setCollisionShape(new CircleCollisionShape(mWidth, mHeight));   // Update collision box
        clearDummyBubble();
        mX += mWidth;
        mLayer++;
        mIsObstacle = false;
    }

    @Override
    public Bubble getCollidedBubble(CollidableSprite collidableSprite) {
        if (mIsObstacle) {
            // Check player collide bubble at bottom or side
            if (collidableSprite.mY > mY + mHeight / 2f) {
                // Check player collide bubble at right or left bottom
                if (collidableSprite.mX > mX + mWidth * 3 / 4f) {
                    return mEdges[5].mEdges[3];
                } else if (collidableSprite.mX > mX + mWidth / 2f) {
                    return mEdges[5];
                } else if (collidableSprite.mX > mX + mWidth / 4f) {
                    return mEdges[4];
                } else {
                    return mEdges[4].mEdges[2];
                }
                // [==|====|====|==]
                //  0.25  0.5  0.75
            } else {
                // Check player collide bubble at right or left side
                if (collidableSprite.mX > mX + mWidth / 2f) {
                    return mEdges[3].mEdges[3];
                } else {
                    return mEdges[2].mEdges[2];
                }
            }
        } else {
            return super.getCollidedBubble(collidableSprite);
        }
    }

}
