package com.nativegame.engine.particles;

import android.graphics.Canvas;

import com.nativegame.engine.GameEngine;
import com.nativegame.engine.GameObject;
import com.nativegame.engine.particles.initializer.AccelerationXInitializer;
import com.nativegame.engine.particles.initializer.AccelerationYInitializer;
import com.nativegame.engine.particles.initializer.AlphaInitializer;
import com.nativegame.engine.particles.initializer.ParticleInitializer;
import com.nativegame.engine.particles.initializer.RotationInitializer;
import com.nativegame.engine.particles.initializer.RotationSpeedInitializer;
import com.nativegame.engine.particles.initializer.ScaleInitializer;
import com.nativegame.engine.particles.initializer.SpeedAngleInitializer;
import com.nativegame.engine.particles.initializer.SpeedXInitializer;
import com.nativegame.engine.particles.initializer.SpeedYInitializer;

import java.util.ArrayList;
import java.util.Random;

public class ParticleSystem extends GameObject {

    public static final long RATE_LOW = 100;
    public static final long RATE_NORMAL = 50;
    public static final long RATE_HIGH = 20;

    private final float mPixelFactor;
    private final Random mRandom;

    private final ArrayList<ParticleInitializer> mInitializers = new ArrayList<>();
    private final ArrayList<Particle> mParticlePool = new ArrayList<>();

    private float mEmissionX;
    private float mEmissionY;
    private float mEmissionMinX;
    private float mEmissionMaxX;
    private float mEmissionMinY;
    private float mEmissionMaxY;

    private long mTimeBetweenParticles = RATE_NORMAL;
    private long mTotalDuration;
    private long mCurrentDuration;
    private long mTotalMillis;

    private boolean mIsEmitting;

    public ParticleSystem(GameEngine gameEngine, int[] id, int size) {
        mPixelFactor = gameEngine.mPixelFactor;
        mRandom = gameEngine.mRandom;
        // We add them to the pool now
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < id.length; j++) {
                mParticlePool.add(new Particle(this, gameEngine, id[j]));
            }
        }
    }

    public ParticleSystem(GameEngine gameEngine, int id, int size) {
        this(gameEngine, new int[]{id}, size);
    }

    public ParticleSystem setLayer(int layer) {
        mLayer = layer;
        return this;
    }

    public ParticleSystem setTotalDuration(long totalDuration) {
        mTotalDuration = totalDuration;
        return this;
    }

    public ParticleSystem setDuration(long duration) {
        int size = mParticlePool.size();
        for (int i = 0; i < size; i++) {
            mParticlePool.get(i).mDuration = duration;
        }
        return this;
    }

    public ParticleSystem setEmissionRate(long timeRate) {
        mTimeBetweenParticles = timeRate;
        return this;
    }

    public ParticleSystem setEmissionPosition(float x, float y) {
        mEmissionX = x;
        mEmissionY = y;
        return this;
    }

    public ParticleSystem setEmissionPositionX(float x) {
        mEmissionX = x;
        return this;
    }

    public ParticleSystem setEmissionPositionY(float y) {
        mEmissionY = y;
        return this;
    }

    public ParticleSystem setEmissionRangeX(float minX, float maxX) {
        mEmissionMinX = minX;
        mEmissionMaxX = maxX;
        return this;
    }

    public ParticleSystem setEmissionRangeY(float minY, float maxY) {
        mEmissionMinY = minY;
        mEmissionMaxY = maxY;
        return this;
    }

    public ParticleSystem setSpeedAngle(float speedMin, float speedMax) {
        mInitializers.add(new SpeedAngleInitializer(speedMin * mPixelFactor,
                speedMax * mPixelFactor, 0, 360));
        return this;
    }

    public ParticleSystem setSpeedAngle(float speedMin, float speedMax, int minAngle, int maxAngle) {
        mInitializers.add(new SpeedAngleInitializer(speedMin * mPixelFactor,
                speedMax * mPixelFactor, minAngle, maxAngle));
        return this;
    }

    public ParticleSystem setSpeedX(float speedX) {
        mInitializers.add(new SpeedXInitializer(speedX * mPixelFactor, speedX * mPixelFactor));
        return this;
    }

    public ParticleSystem setSpeedX(float speedMinX, float speedMaxX) {
        mInitializers.add(new SpeedXInitializer(speedMinX * mPixelFactor, speedMaxX * mPixelFactor));
        return this;
    }

    public ParticleSystem setSpeedY(float speedY) {
        mInitializers.add(new SpeedYInitializer(speedY * mPixelFactor, speedY * mPixelFactor));
        return this;
    }

    public ParticleSystem setSpeedY(float speedMinY, float speedMaxY) {
        mInitializers.add(new SpeedYInitializer(speedMinY * mPixelFactor, speedMaxY * mPixelFactor));
        return this;
    }

    public ParticleSystem setAccelerationX(float accelerateX) {
        mInitializers.add(new AccelerationXInitializer(accelerateX * mPixelFactor, accelerateX * mPixelFactor));
        return this;
    }

    public ParticleSystem setAccelerationX(float minAccelerateX, float maxAccelerateX) {
        mInitializers.add(new AccelerationXInitializer(minAccelerateX * mPixelFactor, maxAccelerateX * mPixelFactor));
        return this;
    }

    public ParticleSystem setAccelerationY(float accelerateY) {
        mInitializers.add(new AccelerationYInitializer(accelerateY * mPixelFactor, accelerateY * mPixelFactor));
        return this;
    }

    public ParticleSystem setAccelerationY(float minAccelerateY, float maxAccelerateY) {
        mInitializers.add(new AccelerationYInitializer(minAccelerateY * mPixelFactor, maxAccelerateY * mPixelFactor));
        return this;
    }

    public ParticleSystem setInitialRotation(int minAngle, int maxAngle) {
        mInitializers.add(new RotationInitializer(minAngle, maxAngle));
        return this;
    }

    public ParticleSystem setRotationSpeed(float minRotationSpeed, float maxRotationSpeed) {
        mInitializers.add(new RotationSpeedInitializer(minRotationSpeed, maxRotationSpeed));
        return this;
    }

    public ParticleSystem setAlpha(float initialValue, float finalValue) {
        mInitializers.add(new AlphaInitializer(initialValue, finalValue, 0));
        return this;
    }

    public ParticleSystem setAlpha(float initialValue, float finalValue, long startDelay) {
        mInitializers.add(new AlphaInitializer(initialValue, finalValue, startDelay));
        return this;
    }

    public ParticleSystem setScale(float initialValue, float finalValue) {
        mInitializers.add(new ScaleInitializer(initialValue, finalValue, 0));
        return this;
    }

    public ParticleSystem setScale(float initialValue, float finalValue, long startDelay) {
        mInitializers.add(new ScaleInitializer(initialValue, finalValue, startDelay));
        return this;
    }

    public ParticleSystem addInitializer(ParticleInitializer initializer) {
        mInitializers.add(initializer);
        return this;
    }

    @Override
    public void startGame(GameEngine gameEngine) {
    }

    public void emit() {
        mIsEmitting = true;
        mTotalMillis = 0;
        mCurrentDuration = 0;
    }

    public void stopEmit() {
        mIsEmitting = false;
    }

    public void oneShot(GameEngine gameEngine, float x, float y, int numParticles) {
        mIsEmitting = false;
        // We create particles based on the parameters
        for (int i = 0; !mParticlePool.isEmpty() && i < numParticles; i++) {
            addOneParticle(gameEngine, x, y);
        }
    }

    @Override
    public void onUpdate(long elapsedMillis, GameEngine gameEngine) {
        if (!mIsEmitting) {
            return;
        }
        mTotalMillis += elapsedMillis;
        // Check is enough particles in pool, and time between particles
        if (!mParticlePool.isEmpty() && mTotalMillis >= mTimeBetweenParticles) {
            // Activate a new particle
            updatePosition();
            addOneParticle(gameEngine, mEmissionX, mEmissionY);
            mTotalMillis = 0;
        }

        // Update ParticleSystem duration
        if (mTotalDuration != 0) {
            mCurrentDuration += elapsedMillis;
            if (mCurrentDuration >= mTotalDuration) {
                stopEmit();
            }
        }
    }

    private void addOneParticle(GameEngine gameEngine, float x, float y) {
        Particle p = mParticlePool.remove(0);
        int size = mInitializers.size();
        for (int i = 0; i < size; i++) {
            mInitializers.get(i).initParticle(p, mRandom);
        }
        p.activate(gameEngine, x, y, mLayer);
    }

    private void updatePosition() {
        if (mEmissionMinX != mEmissionMaxX) {
            // Generate random emit x position
            mEmissionX = mRandom.nextFloat() * (mEmissionMaxX - mEmissionMinX) + mEmissionMinX;
        }
        if (mEmissionMinY != mEmissionMaxY) {
            // Generate random emit Y position
            mEmissionY = mRandom.nextFloat() * (mEmissionMaxY - mEmissionMinY) + mEmissionMinY;
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        // The particles draw by themself
    }

    public void returnToPool(Particle particle) {
        mParticlePool.add(particle);
    }

    public ParticleSystem clearInitializers() {
        mInitializers.clear();
        return this;
    }

}
