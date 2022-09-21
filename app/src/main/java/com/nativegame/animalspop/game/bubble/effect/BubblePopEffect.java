package com.nativegame.animalspop.game.bubble.effect;

import android.graphics.Bitmap;

import com.nativegame.animalspop.R;
import com.nativegame.engine.GameEngine;
import com.nativegame.engine.sprite.AnimatedSprite;
import com.nativegame.engine.sprite.BodyType;

/**
 * Created by Oscar Liang on 2022/09/18
 */

public class BubblePopEffect extends AnimatedSprite {

    private final float mScaleSpeed;

    public BubblePopEffect(GameEngine gameEngine) {
        super(gameEngine, R.drawable.bubble_pop_01, BodyType.None);
        mScaleSpeed = 6 / 1000f;
        setTimePreFrame(100);
        setAnimatedBitmaps(new Bitmap[]{
                getBitmap(R.drawable.bubble_pop_01),
                getBitmap(R.drawable.bubble_pop_02),
                getBitmap(R.drawable.bubble_pop_03),
                getBitmap(R.drawable.bubble_pop_04),
                getBitmap(R.drawable.bubble_pop_05)});
    }

    public void activate(GameEngine gameEngine, float x, float y, int layer) {
        mX = x - mWidth / 2f;
        mY = y - mHeight / 2f;
        addToGameEngine(gameEngine, layer);
        start();
    }

    @Override
    public void startGame(GameEngine gameEngine) {
    }

    @Override
    public void onUpdate(long elapsedMillis, GameEngine gameEngine) {
        super.onUpdate(elapsedMillis, gameEngine);
        mScale += mScaleSpeed * elapsedMillis;
    }

}
