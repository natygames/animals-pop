package com.nativegame.animalspop.game.player;

import com.nativegame.animalspop.R;
import com.nativegame.animalspop.game.Layer;
import com.nativegame.animalspop.game.MyGameEvent;
import com.nativegame.nattyengine.Game;
import com.nativegame.nattyengine.entity.sprite.Sprite;
import com.nativegame.nattyengine.event.GameEvent;

/**
 * Created by Oscar Liang on 2022/09/18
 */

public class CircleBg extends Sprite {

    private final float mRotationSpeed;

    public CircleBg(Game game) {
        super(game, R.drawable.player_circle);
        mRotationSpeed = -50 / 1000f;   // We want to rotate at 50 degree per second
        mLayer = Layer.BACKGROUND_LAYER;
    }

    @Override
    public void onStart() {
        mX = mGame.getScreenWidth() / 2f - mWidth / 2f;
        mY = mGame.getScreenHeight() * 4 / 5f - mHeight / 2f;
    }

    @Override
    public void onUpdate(long elapsedMillis) {
        mRotation += mRotationSpeed * elapsedMillis;
    }

    @Override
    public void onGameEvent(GameEvent gameEvent) {
        if (gameEvent == MyGameEvent.SHOW_WIN_DIALOG) {
            removeFromGame();
        }
    }

}
