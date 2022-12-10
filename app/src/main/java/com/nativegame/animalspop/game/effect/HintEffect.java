package com.nativegame.animalspop.game.effect;

import com.nativegame.animalspop.R;
import com.nativegame.animalspop.game.Layer;
import com.nativegame.nattyengine.Game;
import com.nativegame.nattyengine.entity.sprite.Sprite;

/**
 * Created by Oscar Liang on 2022/09/18
 */

public class HintEffect extends Sprite {

    private final HintEffectBg mHintEffectBg;

    public HintEffect(Game game) {
        super(game, R.drawable.bubble_blank_debug);
        mHintEffectBg = new HintEffectBg(game);
        mLayer = Layer.EFFECT_LAYER;
    }

    public void activate(float x, float y) {
        mX = x - mWidth / 2f;
        mY = y - mHeight / 2f;
        mHintEffectBg.activate(x, y);
        addToGame();
    }

    @Override
    public void onRemove() {
        mHintEffectBg.removeFromGame();
    }

    @Override
    public void onUpdate(long elapsedMillis) {
    }

    private static class HintEffectBg extends Sprite {

        public HintEffectBg(Game game) {
            super(game, R.drawable.bubble_bg);
            mLayer = Layer.BACKGROUND_LAYER;
        }

        public void activate(float x, float y) {
            mX = x - mWidth / 2f;
            mY = y - mHeight / 2f;
            addToGame();
        }

        @Override
        public void onUpdate(long elapsedMillis) {
        }

    }

}
