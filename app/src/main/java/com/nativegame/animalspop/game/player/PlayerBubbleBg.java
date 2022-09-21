package com.nativegame.animalspop.game.player;

import com.nativegame.engine.GameEngine;
import com.nativegame.engine.sprite.BodyType;
import com.nativegame.engine.sprite.Sprite;

/**
 * Created by Oscar Liang on 2022/09/18
 */

public class PlayerBubbleBg extends Sprite {

    public PlayerBubbleBg(GameEngine gameEngine, int drawableResId) {
        super(gameEngine, drawableResId, BodyType.None);
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
