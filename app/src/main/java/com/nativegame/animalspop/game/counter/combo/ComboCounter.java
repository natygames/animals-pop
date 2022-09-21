package com.nativegame.animalspop.game.counter.combo;

import android.graphics.Canvas;

import com.nativegame.animalspop.game.MyGameEvent;
import com.nativegame.engine.GameEngine;
import com.nativegame.engine.GameEvent;
import com.nativegame.engine.GameObject;

/**
 * Created by Oscar Liang on 2022/09/18
 */

public class ComboCounter extends GameObject {

    private static final int COMBO_WOW = 15;
    private static final int COMBO_GOOD = 25;
    private static final int COMBO_WONDERFUL = 30;


    private final ComboText mComboText;
    private int mConsecutiveHits;
    private boolean mComboHaveChanged = false;
    private boolean mIsActive = true;
    private long mTotalMillis;

    public ComboCounter(GameEngine gameEngine) {
        mComboText = new ComboText(gameEngine);
    }

    @Override
    public void startGame(GameEngine gameEngine) {
        mConsecutiveHits = 0;
        mTotalMillis = 0;
    }

    @Override
    public void onUpdate(long elapsedMillis, GameEngine gameEngine) {
        if (mComboHaveChanged) {
            mTotalMillis += elapsedMillis;
            if (mTotalMillis >= 500) {
                mComboText.init(gameEngine, getCombo(gameEngine));
                mComboHaveChanged = false;
                mConsecutiveHits = 0;
                mTotalMillis = 0;
            }
        }
        if (!mIsActive) {
            removeFromGameEngine(gameEngine);
        }
    }

    private Combo getCombo(GameEngine gameEngine) {
        if (mConsecutiveHits >= COMBO_WONDERFUL) {
            gameEngine.onGameEvent(MyGameEvent.EMIT_CONFETTI);
            return Combo.WONDERFUL;
        } else if (mConsecutiveHits >= COMBO_GOOD) {
            return Combo.GOOD;
        } else {
            return Combo.WOW;
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
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
                mIsActive = false;
                break;
        }
    }

}
