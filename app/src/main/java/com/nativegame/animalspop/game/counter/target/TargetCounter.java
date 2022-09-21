package com.nativegame.animalspop.game.counter.target;

import android.graphics.Canvas;
import android.widget.ImageView;
import android.widget.TextView;

import com.nativegame.animalspop.R;
import com.nativegame.animalspop.game.MyGameEvent;
import com.nativegame.animalspop.level.MyLevel;
import com.nativegame.engine.GameEngine;
import com.nativegame.engine.GameObject;

/**
 * Created by Oscar Liang on 2022/09/18
 */

public abstract class TargetCounter extends GameObject {

    protected final TextView mText;
    protected final int mTarget;

    protected int mPoints;
    protected boolean mTargetHaveChanged = false;
    protected boolean mTargetHaveReached = false;

    protected TargetCounter(GameEngine gameEngine) {
        mText = (TextView) gameEngine.mActivity.findViewById(R.id.txt_target);
        mTarget = ((MyLevel) gameEngine.mLevel).mTarget;
        // Init target image
        ImageView imageTarget = (ImageView) gameEngine.mActivity.findViewById(R.id.image_target);
        imageTarget.setImageResource(((MyLevel) gameEngine.mLevel).mLevelType.getImageResId());
    }

    @Override
    public void startGame(GameEngine gameEngine) {
        mText.post(mUpdateTextRunnable);   // We update UI when start
    }

    private final Runnable mUpdateTextRunnable = new Runnable() {
        @Override
        public void run() {
            onUpdateText();
        }
    };

    protected abstract void onUpdateText();

    @Override
    public void onUpdate(long elapsedMillis, GameEngine gameEngine) {
        if (mTargetHaveReached) {
            // Notify the GameController and remove from engine
            gameEngine.onGameEvent(MyGameEvent.GAME_WIN);
            removeFromGameEngine(gameEngine);
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (mTargetHaveChanged) {
            mText.post(mUpdateTextRunnable);
            mTargetHaveChanged = false;
        }
    }

}
