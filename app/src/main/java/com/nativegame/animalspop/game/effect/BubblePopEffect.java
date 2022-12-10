package com.nativegame.animalspop.game.effect;

import android.graphics.Bitmap;

import com.nativegame.animalspop.R;
import com.nativegame.animalspop.game.Layer;
import com.nativegame.nattyengine.Game;
import com.nativegame.nattyengine.entity.sprite.AnimatedSprite;

/**
 * Created by Oscar Liang on 2022/09/18
 */

public class BubblePopEffect extends AnimatedSprite {

    private final float mScaleSpeed;

    public BubblePopEffect(Game game) {
        super(game, R.drawable.bubble_pop_01);
        mScaleSpeed = 6 / 1000f;
        mLayer = Layer.EFFECT_LAYER;
        // Init the animation
        mAnimatedBitmaps = new Bitmap[]{
                getBitmapFromId(R.drawable.bubble_pop_01),
                getBitmapFromId(R.drawable.bubble_pop_02),
                getBitmapFromId(R.drawable.bubble_pop_03),
                getBitmapFromId(R.drawable.bubble_pop_04),
                getBitmapFromId(R.drawable.bubble_pop_05)};
        mTimePreFrame = 100;
    }

    public void activate(float x, float y) {
        mX = x - mWidth / 2f;
        mY = y - mHeight / 2f;
        addToGame();
        startAnimation();
    }

    @Override
    public void onUpdate(long elapsedMillis) {
        mScale += mScaleSpeed * elapsedMillis;
    }

    @Override
    protected void onAnimationRepeat() {
        stopAnimation();
        removeFromGame();
    }

}
