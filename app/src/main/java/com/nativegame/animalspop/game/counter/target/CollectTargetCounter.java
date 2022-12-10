package com.nativegame.animalspop.game.counter.target;

import com.nativegame.animalspop.game.MyGameEvent;
import com.nativegame.nattyengine.Game;
import com.nativegame.nattyengine.event.GameEvent;

/**
 * Created by Oscar Liang on 2022/09/18
 */

public class CollectTargetCounter extends TargetCounter {

    private final String mTargetText;

    public CollectTargetCounter(Game game) {
        super(game);
        mTargetText = "/" + mTarget;
    }

    @Override
    protected boolean isTargetReach() {
        return mPoints == mTarget;
    }

    @Override
    public void onStart() {
        super.onStart();
        mPoints = 0;
    }

    @Override
    protected void onDrawUI() {
        mText.setText(mPoints + mTargetText);
    }

    @Override
    public void onGameEvent(GameEvent gameEvents) {
        super.onGameEvent(gameEvents);
        if ((MyGameEvent) gameEvents == MyGameEvent.COLLECT_ITEM) {
            mPoints++;
            drawUI();

            if (isTargetReach()) {
                mTargetHaveReached = true;
            }
        }
    }

}
