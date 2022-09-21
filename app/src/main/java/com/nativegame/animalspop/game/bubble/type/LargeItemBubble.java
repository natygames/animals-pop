package com.nativegame.animalspop.game.bubble.type;

import com.nativegame.animalspop.R;
import com.nativegame.animalspop.game.MyGameEvent;
import com.nativegame.animalspop.game.bubble.Bubble;
import com.nativegame.animalspop.game.bubble.BubbleColor;
import com.nativegame.animalspop.game.bubble.effect.BubblePopEffect;
import com.nativegame.animalspop.game.bubble.effect.ItemEffect;
import com.nativegame.animalspop.sound.MySoundEvent;
import com.nativegame.engine.GameEngine;

import java.util.ArrayList;

/**
 * Created by Oscar Liang on 2022/09/18
 */

public class LargeItemBubble extends Bubble {

    private static final float ITEM_SCALE = 3;
    private static final int ITEMS_PER_BUBBLE = 5;
    private static final long TIME_BETWEEN_ITEMS = 150;

    private final BubblePopEffect mBubblePopEffect;
    private final ArrayList<ItemEffect> mItemEffectsPool = new ArrayList<>(ITEMS_PER_BUBBLE);

    private boolean mIsItem = true;

    private long mTotalMillis;
    private boolean mCollected = false;

    public LargeItemBubble(GameEngine gameEngine, int row, int col) {
        super(gameEngine, row, col, BubbleColor.LARGE_ITEM);
        // We add item to the pool now
        mBubblePopEffect = new BubblePopEffect(gameEngine);
        for (int i = 0; i < ITEMS_PER_BUBBLE; i++) {
            mItemEffectsPool.add(new ItemEffect(gameEngine, R.drawable.nut));
        }
    }

    @Override
    public void startGame(GameEngine gameEngine) {
        super.startGame(gameEngine);
        mScale = ITEM_SCALE;
        mBubblePopEffect.mScale = ITEM_SCALE;
    }

    @Override
    public void addToGameEngine(GameEngine gameEngine, int layer) {
        super.addToGameEngine(gameEngine, layer + 1);
    }

    @Override
    public void onUpdate(long elapsedMillis, GameEngine gameEngine) {
        super.onUpdate(elapsedMillis, gameEngine);
        if (mCollected) {
            mTotalMillis += elapsedMillis;
            if (mTotalMillis >= TIME_BETWEEN_ITEMS) {
                if (!mItemEffectsPool.isEmpty()) {
                    activateItemEffect(gameEngine);
                } else {
                    mCollected = false;
                }
                mTotalMillis = 0;
            }
        }
    }

    @Override
    public void popBubble(GameEngine gameEngine) {
        if (mIsItem) {
            // We pop this item if it hasn't been collected
            popLargeItem(gameEngine);
        } else {
            // Otherwise, we pop the bubble
            super.popBubble(gameEngine);
        }
    }

    @Override
    public void popFloater(GameEngine gameEngine) {
        if (mIsItem) {
            // We pop this item if it hasn't been collected
            popLargeItem(gameEngine);
        } else {
            // Otherwise, we pop the floater
            super.popFloater(gameEngine);
        }
    }

    private void popLargeItem(GameEngine gameEngine) {
        mBubblePopEffect.activate(gameEngine, mX + mWidth / 2f, mY + mHeight / 2f, 4);
        activateItemEffect(gameEngine);
        setBubbleColor(BubbleColor.BLANK);
        // We notify the target counter now, so it will sync
        for (int i = 0; i < ITEMS_PER_BUBBLE; i++) {
            gameEngine.onGameEvent(MyGameEvent.COLLECT_ITEM);   // We have 5 items
        }
        gameEngine.mSoundManager.playSound(MySoundEvent.COLLECT_ITEM);
        mCollected = true;
        mScale = 1;
        mIsItem = false;
    }

    private void activateItemEffect(GameEngine gameEngine) {
        mItemEffectsPool.remove(0)
                .activate(gameEngine, mX + mWidth / 2f, mY + mHeight / 2f, 4);
    }

}
