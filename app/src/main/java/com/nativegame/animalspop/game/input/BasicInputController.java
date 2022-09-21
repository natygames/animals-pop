package com.nativegame.animalspop.game.input;

import android.view.View;

import com.nativegame.animalspop.Utils;
import com.nativegame.animalspop.game.MyGameEvent;
import com.nativegame.animalspop.R;
import com.nativegame.engine.GameEngine;
import com.nativegame.engine.input.TouchInputController;

/**
 * Created by Oscar Liang on 2022/09/18
 */

public class BasicInputController extends TouchInputController {

    private final float mShootThreshold;   // Stop shooting when pass threshold

    public BasicInputController(GameEngine gameEngine) {
        super(gameEngine, R.id.game_view);

        float bubbleWidth = gameEngine.mPixelFactor * Utils.BUBBLE_WIDTH;
        mShootThreshold = gameEngine.mScreenHeight * 4 / 5f - bubbleWidth * 3 / 2f;
        mGameEngine.mActivity.findViewById(R.id.txt_move).setOnClickListener(mSwitchBubbleListener);
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
            mGameEngine.onGameEvent(MyGameEvent.SHOOT_BUBBLE);
        }
    }

    private final View.OnClickListener mSwitchBubbleListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mGameEngine.onGameEvent(MyGameEvent.SWITCH_BUBBLE);
        }
    };

}
