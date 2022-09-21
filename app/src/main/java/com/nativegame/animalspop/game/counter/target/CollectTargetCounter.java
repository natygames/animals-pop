package com.nativegame.animalspop.game.counter.target;

import com.nativegame.animalspop.game.MyGameEvent;
import com.nativegame.animalspop.level.MyLevel;
import com.nativegame.engine.GameEngine;
import com.nativegame.engine.GameEvent;

/**
 * Created by Oscar Liang on 2022/09/18
 */

public class CollectTargetCounter extends TargetCounter {

    private final String mTargetText;
    private boolean mMoveHaveChanged = false;

    public CollectTargetCounter(GameEngine gameEngine) {
        super(gameEngine);
        mTargetText = "/" + mTarget;
    }

    @Override
    public void startGame(GameEngine gameEngine) {
        super.startGame(gameEngine);
        mPoints = 0;
    }

    @Override
    protected void onUpdateText() {
        mText.setText(mPoints + mTargetText);
    }

    @Override
    public void onUpdate(long elapsedMillis, GameEngine gameEngine) {
        super.onUpdate(elapsedMillis, gameEngine);
        if (mMoveHaveChanged) {
            if (((MyLevel) gameEngine.mLevel).mMove == 0 && mPoints < mTarget) {
                // Notify the GameController
                gameEngine.onGameEvent(MyGameEvent.GAME_OVER);
            }
            mMoveHaveChanged = false;
        }
    }

    @Override
    public void onGameEvent(GameEvent gameEvents) {
        switch ((MyGameEvent) gameEvents) {
            case COLLECT_ITEM:
                mPoints++;
                if (mPoints >= mTarget) {
                    mTargetHaveReached = true;
                }
                mTargetHaveChanged = true;
                break;
            case BUBBLE_CONSUMED:
                mMoveHaveChanged = true;
                break;
        }
    }

}
