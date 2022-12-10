package com.nativegame.animalspop.game.counter.combo;

import com.nativegame.animalspop.game.MyGameEvent;
import com.nativegame.nattyengine.Game;
import com.nativegame.nattyengine.event.GameEvent;
import com.nativegame.nattyengine.entity.GameObject;

/**
 * Created by Oscar Liang on 2022/09/18
 */

public class ComboCounter extends GameObject {

    private static final int COMBO_WOW = 15;
    private static final int COMBO_GOOD = 25;
    private static final int COMBO_WONDERFUL = 30;

    private final ComboText mComboText;

    private int mConsecutiveHits;
    private long mTotalTime;
    private boolean mComboHaveChanged = false;

    public ComboCounter(Game game) {
        super(game);
        mComboText = new ComboText(game);
    }

    @Override
    public void onStart() {
        mConsecutiveHits = 0;
        mTotalTime = 0;
    }

    @Override
    public void onUpdate(long elapsedMillis) {
        if (mComboHaveChanged) {
            mTotalTime += elapsedMillis;
            if (mTotalTime >= 500) {
                mComboText.activate(getCombo());
                mComboHaveChanged = false;
                mConsecutiveHits = 0;
                mTotalTime = 0;
            }
        }
    }

    private Combo getCombo() {
        if (mConsecutiveHits >= COMBO_WONDERFUL) {
            gameEvent(MyGameEvent.EMIT_CONFETTI);
            return Combo.WONDERFUL;
        } else if (mConsecutiveHits >= COMBO_GOOD) {
            return Combo.GOOD;
        } else {
            return Combo.WOW;
        }
    }

    @Override
    public void onGameEvent(GameEvent gameEvents) {
        switch ((MyGameEvent) gameEvents) {
            case BUBBLE_POP:
                mConsecutiveHits++;
                break;
            case BUBBLE_CONSUMED:
            case BOOSTER_CONSUMED:
                if (mConsecutiveHits < COMBO_WOW) {
                    mConsecutiveHits = 0;
                } else {
                    mComboHaveChanged = true;
                }
                break;
            case GAME_WIN:
            case GAME_OVER:
                removeFromGame();
                break;
        }
    }

}
