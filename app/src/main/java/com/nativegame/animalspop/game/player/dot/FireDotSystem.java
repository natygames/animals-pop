package com.nativegame.animalspop.game.player.dot;

import com.nativegame.animalspop.Utils;
import com.nativegame.animalspop.game.player.booster.FireBubble;
import com.nativegame.engine.GameEngine;

/**
 * Created by Oscar Liang on 2022/09/18
 */

public class FireDotSystem extends DotSystem {

    private final float mMinX;
    private final float mMaxX;

    public FireDotSystem(FireBubble fireBubble, GameEngine gameEngine) {
        super(fireBubble, gameEngine);
        float radius = gameEngine.mPixelFactor * Utils.BUBBLE_WIDTH / 2;
        mMinX = radius;
        mMaxX = gameEngine.mScreenWidth - radius;
    }

    @Override
    protected void setDotPosition(Dot dot, float x, float y) {
        if (x <= mMinX || x >= mMaxX) {
            dot.setPosition(-dot.mWidth, -dot.mHeight);
        } else {
            dot.setPosition(x, y);
        }
    }

}
