package com.nativegame.engine.particles.initializer;

import com.nativegame.engine.particles.Particle;

import java.util.Random;

public interface ParticleInitializer {

    void initParticle(Particle particle, Random random);

}
