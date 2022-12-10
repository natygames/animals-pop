package com.nativegame.animalspop.game.counter.target;

import com.nativegame.animalspop.game.MyGameEvent;
import com.nativegame.nattyengine.Game;
import com.nativegame.nattyengine.event.GameEvent;

/**
 * Created by Oscar Liang on 2022/09/18
 */

public class PopTargetCounter extends TargetCounter {

    public PopTargetCounter(Game game) {
        super(game);
    }

    @Override
    protected boolean isTargetReach() {
        return mPoints == 0;
    }

    @Override
    public void onStart() {
        super.onStart();
        mPoints = mTarget;
    }

    @Override
    protected void onDrawUI() {
        mText.setText(String.valueOf(mPoints));
    }

    @Override
    public void onGameEvent(GameEvent gameEvents) {
        super.onGameEvent(gameEvents);
        switch ((MyGameEvent) gameEvents) {
            case BUBBLE_POP:
                mPoints--;
                drawUI();

                if (isTargetReach()) {
                    mTargetHaveReached = true;
                }
                break;
            case BUBBLE_HIT:
                mPoints++;
                drawUI();
                break;
        }
    }

}
