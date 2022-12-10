package com.nativegame.animalspop.game.player.dot;

import com.nativegame.animalspop.R;
import com.nativegame.animalspop.game.Layer;
import com.nativegame.animalspop.game.bubble.Bubble;
import com.nativegame.animalspop.game.player.booster.BombBubble;
import com.nativegame.nattyengine.Game;
import com.nativegame.nattyengine.entity.sprite.Sprite;

/**
 * Created by Oscar Liang on 2022/09/18
 */

public class BombDotSystem extends DotSystem {

    private final BoosterHint mBoosterHint;

    public BombDotSystem(BombBubble bombBubble, Game game) {
        super(bombBubble, game);
        mBoosterHint = new BoosterHint(game);
        setDotBitmap(R.drawable.dot_bomb);
    }

    @Override
    public void onUpdate(long elapsedMillis) {
        super.onUpdate(elapsedMillis);
        checkHint();
    }

    private void checkHint() {
        if (!mIsAddToScreen) {
            return;
        }
        for (Dot currentDot : mDotPool) {
            // We check the first dot collide with bubble
            Dot nextDot = currentDot.mNextDot;
            if (nextDot != null && nextDot.mIsCollide) {
                // Get the bubble collide with previous dot
                Bubble bubble = nextDot.mCollideBubble.getCollidedBubble(currentDot);
                mBoosterHint.setPosition(bubble.mX + bubble.mWidth / 2f,
                        bubble.mY + bubble.mHeight / 2f);
                mBoosterHint.mIsVisible = true;
                return;   // We only check the first one found
            }
        }
        // Hide the hint if the dot not collide with any bubble
        mBoosterHint.mIsVisible = false;
    }

    @Override
    protected void addDot() {
        super.addDot();
        mBoosterHint.addToGame();
    }

    @Override
    protected void removeDot() {
        super.removeDot();
        mBoosterHint.removeFromGame();
    }

    private static class BoosterHint extends Sprite {

        public BoosterHint(Game game) {
            super(game, R.drawable.bomb_hint);
            mLayer = Layer.EFFECT_LAYER;
        }

        public void setPosition(float x, float y) {
            mX = x - mWidth / 2f;
            mY = y - mHeight / 2f;
        }

        @Override
        public void onUpdate(long elapsedMillis) {
        }

    }

}
