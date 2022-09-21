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

public class ScoreCounter extends GameObject {

    private static final int POINTS_GAINED_PER_BUBBLE = 10;

    private final MyLevel mLevel;
    private final TextView mText;
    private int mPoints;
    private boolean mPointsHaveChanged = false;

    public ScoreCounter(GameEngine gameEngine) {
        mLevel = (MyLevel) gameEngine.mLevel;
        mText = (TextView) gameEngine.mActivity.findViewById(R.id.txt_score);
    }

    @Override
    public void startGame(GameEngine gameEngine) {
        mText.post(mUpdateTextRunnable);   // We update UI when start
        mPoints = 0;
    }

    private final Runnable mUpdateTextRunnable = new Runnable() {
        @Override
        public void run() {
            mText.setText(String.valueOf(mPoints));
        }
    };

    @Override
    public void onUpdate(long elapsedMillis, GameEngine gameEngine) {
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (mPointsHaveChanged) {
            mText.post(mUpdateTextRunnable);
            mPointsHaveChanged = false;
        }
    }

    @Override
    public void onGameEvent(GameEvent gameEvents) {
        if (gameEvents == MyGameEvent.BUBBLE_POP) {
            mPoints += POINTS_GAINED_PER_BUBBLE;
            mLevel.mScore = mPoints;
            mPointsHaveChanged = true;
        }
    }

}
