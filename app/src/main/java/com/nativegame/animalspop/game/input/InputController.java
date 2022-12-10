package com.nativegame.animalspop.game.input;

import android.view.View;

import com.nativegame.animalspop.game.MyGameEvent;
import com.nativegame.animalspop.R;
import com.nativegame.nattyengine.Game;
import com.nativegame.nattyengine.input.TouchController;

/**
 * Created by Oscar Liang on 2022/09/18
 */

public class InputController extends TouchController implements View.OnClickListener {

    private final float mShootThreshold;   // Stop shooting when pass threshold

    private final Game mGame;

    public InputController(Game game) {
        mGame = game;
        mShootThreshold = game.getScreenHeight() * 4 / 5f - game.getPixelFactor() * 500;
        game.getGameActivity().findViewById(R.id.txt_move).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        mGame.getGameEngine().onGameEvent(MyGameEvent.SWITCH_BUBBLE);
    }

    @Override
    protected void onActionDown() {
        if (mYDown < mShootThreshold) {
            mTouching = true;
        }
    }

    @Override
    protected void onActionMove() {
        if (mYDown < mShootThreshold) {
            mTouching = true;
        } else {
            mTouching = false;
        }
    }

    @Override
    protected void onActionUp() {
        mTouching = false;
        if (mYUp < mShootThreshold) {
            mGame.getGameEngine().onGameEvent(MyGameEvent.SHOOT_BUBBLE);
        }
    }

}
