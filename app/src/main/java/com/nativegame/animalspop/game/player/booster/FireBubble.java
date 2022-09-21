package com.nativegame.animalspop.game.player.booster;

import com.nativegame.animalspop.R;
import com.nativegame.animalspop.game.MyGameEvent;
import com.nativegame.animalspop.game.bubble.Bubble;
import com.nativegame.animalspop.game.bubble.BubbleColor;
import com.nativegame.animalspop.game.bubble.BubbleSystem;
import com.nativegame.animalspop.game.player.dot.DotSystem;
import com.nativegame.animalspop.game.player.PlayerBubble;
import com.nativegame.animalspop.game.player.dot.FireDotSystem;
import com.nativegame.animalspop.sound.MySoundEvent;
import com.nativegame.engine.GameEngine;
import com.nativegame.engine.particles.ParticleSystem;

/**
 * Created by Oscar Liang on 2022/09/18
 */

public class FireBubble extends PlayerBubble {

    private final ParticleSystem mTrailParticleSystem;
    private final ParticleSystem mSparkleParticleSystem;
    private boolean mConsume;

    public FireBubble(BubbleSystem bubbleSystem, GameEngine gameEngine) {
        super(bubbleSystem, gameEngine, R.drawable.fire_bubble);
        mTrailParticleSystem = new ParticleSystem(gameEngine, R.drawable.fire_flame, 40)
                .setDuration(400)
                .setEmissionRate(ParticleSystem.RATE_HIGH)
                .setSpeedX(-800, 800)
                .setSpeedY(-800, 800)
                .setInitialRotation(0, 360)
                .setRotationSpeed(-720, 720)
                .setAlpha(255, 0)
                .setScale(1, 2);
        mSparkleParticleSystem = new ParticleSystem(gameEngine, R.drawable.fire_particle, 10)
                .setDuration(400)
                .setSpeedX(-1000, 1000)
                .setSpeedY(-1000, 1000)
                .setInitialRotation(0, 360)
                .setRotationSpeed(-720, 720)
                .setAlpha(255, 0)
                .setScale(1, 0.5f);
        // Init dot and bg color
        mDotSystem.setDotBitmap(R.drawable.dot_fire);
        mPlayerBubbleBg.setBitmap(R.drawable.fire_bubble_bg);
    }

    public void init(GameEngine gameEngine) {
        mX = mStartX - mWidth / 2f;
        mY = mStartY - mHeight / 2f;
        mConsume = false;
        addToGameEngine(gameEngine, 2);
        // Start emitting
        mTrailParticleSystem.emit();
        mSparkleParticleSystem.emit();
    }

    @Override
    protected DotSystem getDotSystem(GameEngine gameEngine) {
        return new FireDotSystem(this, gameEngine);
    }

    @Override
    public void startGame(GameEngine gameEngine) {
    }

    @Override
    public void addToGameEngine(GameEngine gameEngine, int layer) {
        super.addToGameEngine(gameEngine, layer);
        mTrailParticleSystem.addToGameEngine(gameEngine, layer - 1);
        mSparkleParticleSystem.addToGameEngine(gameEngine, layer - 1);
        gameEngine.onGameEvent(MyGameEvent.BOOSTER_ADDED);
        gameEngine.mSoundManager.playSound(MySoundEvent.ADD_BOOSTER);
    }

    @Override
    public void removeFromGameEngine(GameEngine gameEngine) {
        super.removeFromGameEngine(gameEngine);
        mTrailParticleSystem.removeFromGameEngine(gameEngine);
        mSparkleParticleSystem.removeFromGameEngine(gameEngine);
        if (mConsume) {
            // Update player state and notify the booster manager
            gameEngine.onGameEvent(MyGameEvent.BOOSTER_CONSUMED);
        } else {
            // Update player state
            gameEngine.onGameEvent(MyGameEvent.BOOSTER_REMOVED);
        }
    }

    @Override
    public void onUpdate(long elapsedMillis, GameEngine gameEngine) {
        super.onUpdate(elapsedMillis, gameEngine);
        mTrailParticleSystem.setEmissionPosition(mX + mWidth / 2f, mY + mHeight / 2f);
        mSparkleParticleSystem.setEmissionPosition(mX + mWidth / 2f, mY + mHeight / 2f);
    }

    @Override
    protected void bounceRight(GameEngine gameEngine) {
        // We do not bounce fire bubble
        if (mX <= -mWidth) {
            reset(gameEngine);
        }
    }

    @Override
    protected void bounceLeft(GameEngine gameEngine) {
        // We do not bounce fire bubble
        if (mX >= gameEngine.mScreenWidth) {
            reset(gameEngine);
        }
    }

    @Override
    protected void onBubbleShoot(GameEngine gameEngine) {
        gameEngine.onGameEvent(MyGameEvent.BOOSTER_SHOT);
        gameEngine.mSoundManager.playSound(MySoundEvent.FIRE_BUBBLE_SHOOT);
    }

    @Override
    protected void onBubbleSwitch(GameEngine gameEngine) {
        // We do not switch booster here
    }

    @Override
    protected void onBubbleHit(GameEngine gameEngine, Bubble bubble) {
        if (bubble.mBubbleColor != BubbleColor.BLANK && mY >= 0) {
            // We pop every bubble hit
            bubble.popBubble(gameEngine);
        }
    }

    @Override
    protected void onBubbleReset(GameEngine gameEngine) {
        // Pop floater and shift
        mBubbleSystem.popFloater();
        mBubbleSystem.shiftBubble();
        // Stop emitting
        mTrailParticleSystem.stopEmit();
        mSparkleParticleSystem.stopEmit();
        mConsume = true;
        removeFromGameEngine(gameEngine);
    }

}
