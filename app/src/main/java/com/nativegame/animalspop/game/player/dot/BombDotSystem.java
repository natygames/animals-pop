package com.nativegame.animalspop.game.player.dot;

import com.nativegame.animalspop.R;
import com.nativegame.animalspop.game.bubble.Bubble;
import com.nativegame.animalspop.game.player.booster.BombBubble;
import com.nativegame.engine.GameEngine;
import com.nativegame.engine.sprite.BodyType;
import com.nativegame.engine.sprite.Sprite;

/**
 * Created by Oscar Liang on 2022/09/18
 */

public class BombDotSystem extends DotSystem {

    private final BoosterHint mBoosterHint;

    public BombDotSystem(BombBubble bombBubble, GameEngine gameEngine) {
        super(bombBubble, gameEngine);
        mBoosterHint = new BoosterHint(gameEngine);
    }

    @Override
    public void onUpdate(long elapsedMillis, GameEngine gameEngine) {
        super.onUpdate(elapsedMillis, gameEngine);
        checkHint();
    }

    private void checkHint() {
        if (!mActivate) {
            return;
        }
        for (Dot dot : mDotPool) {
            // We check the first dot collide with bubble
            Dot nextDot = dot.mNextDot;
            if (nextDot != null && !nextDot.mVisible) {
                // Get the closest bubble collide with previous dot
                Bubble bubble = getClosestBubble(nextDot.mCollideBubble, dot);
                if (bubble != null) {
                    // Update hint position
                    mBoosterHint.setPosition(bubble.mX + bubble.mWidth / 2f,
                            bubble.mY + bubble.mHeight / 2f);
                    return;   // We only check the first found
                }
            }
        }
        // Hide the hint if the dot not collide with any bubble
        mBoosterHint.hide();
    }

    private Bubble getClosestBubble(Bubble bubble, Dot dot) {
        if (bubble == null) {
            return null;
        }
        for (Bubble b : bubble.mEdges) {
            if (b.checkCollision(dot)) {
                return b;
            }
        }
        return null;
    }

    @Override
    protected void addDot(GameEngine gameEngine) {
        super.addDot(gameEngine);
        mBoosterHint.addToGameEngine(gameEngine, 3);
    }

    @Override
    protected void removeDot(GameEngine gameEngine) {
        super.removeDot(gameEngine);
        mBoosterHint.removeFromGameEngine(gameEngine);
    }

    private static class BoosterHint extends Sprite {

        public BoosterHint(GameEngine gameEngine) {
            super(gameEngine, R.drawable.bomb_hint, BodyType.None);
        }

        public void setPosition(float x, float y) {
            mX = x - mWidth / 2f;
            mY = y - mHeight / 2f;
        }

        public void hide() {
            mX = -mWidth;
            mY = -mHeight;
        }

        @Override
        public void startGame(GameEngine gameEngine) {
        }

        @Override
        public void onUpdate(long elapsedMillis, GameEngine gameEngine) {
            if (mY <= -mHeight / 2f) {
                hide();
            }
        }

    }

}
