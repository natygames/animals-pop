package com.nativegame.animalspop.game.counter;

import android.graphics.Canvas;
import android.widget.TextView;

import com.nativegame.animalspop.R;
import com.nativegame.animalspop.game.MyGameEvent;
import com.nativegame.animalspop.level.MyLevel;
import com.nativegame.engine.GameEngine;
import com.nativegame.engine.GameEvent;
import com.nativegame.engine.GameObject;

/**
 * Created by Oscar Liang on 2022/09/18
 */

public class MoveCounter extends GameObject {

    private static final int EXTRA_MOVES = 5;

    private final MyLevel mLevel;
    private final TextView mText;
    private int mMoves;
    private boolean mMovesHaveChanged = false;

    public MoveCounter(GameEngine gameEngine) {
        mLevel = (MyLevel) gameEngine.mLevel;
        mText = (TextView) gameEngine.mActivity.findViewById(R.id.txt_move);
        mMoves = mLevel.mMove;
    }

    @Override
    public void startGame(GameEngine gameEngine) {
        mText.post(mUpdateTextRunnable);   // We update UI when start
    }

    private final Runnable mUpdateTextRunnable = new Runnable() {
        @Override
        public void run() {
            mText.setText(String.valueOf(mMoves));
        }
    };

    @Override
    public void onUpdate(long elapsedMillis, GameEngine gameEngine) {
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (mMovesHaveChanged) {
            mText.post(mUpdateTextRunnable);
            mMovesHaveChanged = false;
        }
    }

    @Override
    public void onGameEvent(GameEvent gameEvents) {
        switch ((MyGameEvent) gameEvents) {
            case BUBBLE_SHOT:
                mMoves--;
                mLevel.mMove = mMoves;
                mMovesHaveChanged = true;
                break;
            case ADD_EXTRA_MOVE:
                mMoves += EXTRA_MOVES;
                mLevel.mMove = mMoves;
                mMovesHaveChanged = true;
                break;
        }
    }

}
