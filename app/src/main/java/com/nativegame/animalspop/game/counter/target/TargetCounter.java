package com.nativegame.animalspop.game.counter.target;

import android.widget.TextView;

import com.nativegame.animalspop.R;
import com.nativegame.animalspop.game.MyGameEvent;
import com.nativegame.animalspop.level.MyLevel;
import com.nativegame.nattyengine.Game;
import com.nativegame.nattyengine.event.GameEvent;
import com.nativegame.nattyengine.entity.UIGameObject;

/**
 * Created by Oscar Liang on 2022/09/18
 */

public abstract class TargetCounter extends UIGameObject {

    protected final TextView mText;
    protected final int mTarget;

    protected int mPoints;
    protected boolean mTargetHaveReached = false;
    private boolean mMoveHaveChanged = false;

    protected TargetCounter(Game game) {
        super(game);
        mText = (TextView) game.getGameActivity().findViewById(R.id.txt_target);
        mTarget = ((MyLevel) game.getLevel()).mTarget;
    }

    protected abstract boolean isTargetReach();

    @Override
    public void onStart() {
        drawUI();
    }

    @Override
    public void onUpdate(long elapsedMillis) {
        if (mTargetHaveReached) {
            // We notify the GameController and remove
            gameEvent(MyGameEvent.GAME_WIN);
            removeFromGame();
        }
        if (mMoveHaveChanged) {
            if (((MyLevel) mGame.getLevel()).mMove == 0 && !isTargetReach()) {
                // We notify the GameController
                gameEvent(MyGameEvent.GAME_OVER);
            }
            mMoveHaveChanged = false;
        }
    }

    @Override
    public void onGameEvent(GameEvent gameEvents) {
        if ((MyGameEvent) gameEvents == MyGameEvent.BUBBLE_CONSUMED) {
            mMoveHaveChanged = true;
        }
    }

}
