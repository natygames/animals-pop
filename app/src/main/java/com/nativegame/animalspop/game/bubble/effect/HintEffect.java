package com.nativegame.animalspop.game.bubble.effect;

import com.nativegame.animalspop.R;
import com.nativegame.engine.GameEngine;
import com.nativegame.engine.sprite.BodyType;
import com.nativegame.engine.sprite.Sprite;

/**
 * Created by Oscar Liang on 2022/09/18
 */

public class HintEffect extends Sprite {

    private final HintEffectBg mHintEffectBg;

    public HintEffect(GameEngine gameEngine) {
        super(gameEngine, R.drawable.bubble_blank_debug, BodyType.None);
        mHintEffectBg = new HintEffectBg(gameEngine);
    }

    public void init(GameEngine gameEngine, float x, float y, int layer) {
        mX = x - mWidth / 2f;
        mY = y - mHeight / 2f;
        mHintEffectBg.setPosition(x, y);
        addToGameEngine(gameEngine, layer);
    }

    @Override
    public void startGame(GameEngine gameEngine) {
    }

    @Override
    public void addToGameEngine(GameEngine gameEngine, int layer) {
        super.addToGameEngine(gameEngine, layer);
        mHintEffectBg.addToGameEngine(gameEngine, layer - 2);
    }

    @Override
    public void removeFromGameEngine(GameEngine gameEngine) {
        super.removeFromGameEngine(gameEngine);
        mHintEffectBg.removeFromGameEngine(gameEngine);
    }

    @Override
    public void onUpdate(long elapsedMillis, GameEngine gameEngine) {
    }

    private static class HintEffectBg extends Sprite {

        public HintEffectBg(GameEngine gameEngine) {
            super(gameEngine, R.drawable.bubble_bg, BodyType.None);
        }

        public void setPosition(float x, float y) {
            mX = x - mWidth / 2f;
            mY = y - mHeight / 2f;
        }

        @Override
        public void startGame(GameEngine gameEngine) {
        }

        @Override
        public void onUpdate(long elapsedMillis, GameEngine gameEngine) {
        }

    }

}
