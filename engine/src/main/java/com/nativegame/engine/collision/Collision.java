package com.nativegame.engine.collision;

import com.nativegame.engine.sprite.Sprite;

import java.util.ArrayList;
import java.util.List;

public class Collision {

    public Sprite mObjectA;
    public Sprite mObjectB;

    private static final List<Collision> sCollisionPool = new ArrayList<Collision>();

    public static Collision init(Sprite objectA, Sprite objectB) {
        if (sCollisionPool.isEmpty()) {
            return new Collision(objectA, objectB);
        }
        Collision c = sCollisionPool.remove(0);
        c.mObjectA = objectA;
        c.mObjectB = objectB;
        return c;
    }

    public static void returnToPool(Collision c) {
        c.mObjectA = null;
        c.mObjectB = null;
        sCollisionPool.add(c);
    }

    public Collision(Sprite objectA, Sprite objectB) {
        mObjectA = objectA;
        mObjectB = objectB;
    }

    public boolean equals(Collision c) {
        return (mObjectA == c.mObjectA && mObjectB == c.mObjectB)
                || (mObjectA == c.mObjectB && mObjectB == c.mObjectA);
    }

}
