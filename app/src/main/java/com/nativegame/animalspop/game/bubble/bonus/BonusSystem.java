package com.nativegame.animalspop.game.bubble.bonus;

import android.graphics.Canvas;

import com.nativegame.animalspop.Utils;
import com.nativegame.animalspop.game.MyGameEvent;
import com.nativegame.animalspop.game.bubble.BubbleColor;
import com.nativegame.animalspop.sound.MySoundEvent;
import com.nativegame.engine.GameEngine;
import com.nativegame.engine.GameObject;

import java.util.ArrayList;

/**
 * Created by Oscar Liang on 2022/09/18
 */

public class BonusSystem extends GameObject {

    private static final int TIME_BETWEEN_BUBBLE = 100;

    private final float mInitialX;
    private final float mInitialY;

    private final ArrayList<BonusBubble> mBonusBubbles = new ArrayList<>();

    private long mTotalMillis;

    public BonusSystem(GameEngine gameEngine) {
        mInitialX = gameEngine.mScreenWidth / 2f;
        mInitialY = gameEngine.mScreenHeight * 4 / 5f - gameEngine.mPixelFactor * Utils.BUBBLE_WIDTH;
    }

    public void addBonusBubble(GameEngine gameEngine, BubbleColor bubbleColor) {
        mBonusBubbles.add(new BonusBubble(gameEngine, bubbleColor));
    }

    @Override
    public void startGame(GameEngine gameEngine) {
    }

    @Override
    public void onUpdate(long elapsedMillis, GameEngine gameEngine) {
        mTotalMillis += elapsedMillis;
        if (mTotalMillis >= TIME_BETWEEN_BUBBLE) {
            if (!mBonusBubbles.isEmpty()) {
                addOneBonusBubble(gameEngine);
            } else {
                gameEngine.onGameEvent(MyGameEvent.SHOW_WIN_DIALOG);
                removeFromGameEngine(gameEngine);
            }
            mTotalMillis = 0;
        }
    }

    private void addOneBonusBubble(GameEngine gameEngine) {
        BonusBubble bonusBubble = mBonusBubbles.remove(0);
        bonusBubble.activate(gameEngine, mInitialX, mInitialY, gameEngine.mRandom, 4);
        gameEngine.onGameEvent(MyGameEvent.BUBBLE_SHOT);   // We notify the move counter
        gameEngine.mSoundManager.playSound(MySoundEvent.BUBBLE_HIT);
    }

    @Override
    public void onDraw(Canvas canvas) {
        // The bonus bubble draw by themself
    }

}
