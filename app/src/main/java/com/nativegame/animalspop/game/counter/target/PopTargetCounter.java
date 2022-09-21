package com.nativegame.animalspop.game.counter.target;

import com.nativegame.animalspop.game.MyGameEvent;
import com.nativegame.animalspop.level.MyLevel;
import com.nativegame.engine.GameEngine;
import com.nativegame.engine.GameEvent;

/**
 * Created by Oscar Liang on 2022/09/18
 */

public class PopTargetCounter extends TargetCounter {

    private boolean mMoveHaveChanged = false;

    public PopTargetCounter(GameEngine gameEngine) {
        super(gameEngine);
    }

    @Override
    public void startGame(GameEngine gameEngine) {
        super.startGame(gameEngine);
        mPoints = mTarget;
    }

    @Override
    protected void onUpdateText() {
        mText.setText(String.valueOf(mPoints));
    }

    @Override
    public void onUpdate(long elapsedMillis, GameEngine gameEngine) {
        super.onUpdate(elapsedMillis, gameEngine);
        if (mMoveHaveChanged) {
            if (((MyLevel) gameEngine.mLevel).mMove == 0 && mPoints > 0) {
                // Notify the GameController
                gameEngine.onGameEvent(MyGameEvent.GAME_OVER);
            }
            mMoveHaveChanged = false;
        }
    }

    @Override
    public void onGameEvent(GameEvent gameEvents) {
        switch ((MyGameEvent) gameEvents) {
            case BUBBLE_POP:
                mPoints--;
                if (mPoints <= 0) {
                    mTargetHaveReached = true;
                }
                mTargetHaveChanged = true;
                break;
            case BUBBLE_HIT:
                mPoints++;
                mTargetHaveChanged = true;
                break;
            case BUBBLE_CONSUMED:
                mMoveHaveChanged = true;
                break;
        }
    }

}
