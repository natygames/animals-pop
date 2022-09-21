package com.nativegame.animalspop.game.bubble.type;

import com.nativegame.animalspop.R;
import com.nativegame.animalspop.game.MyGameEvent;
import com.nativegame.animalspop.game.bubble.Bubble;
import com.nativegame.animalspop.game.bubble.BubbleColor;
import com.nativegame.animalspop.game.bubble.effect.BubblePopEffect;
import com.nativegame.animalspop.game.bubble.effect.ItemEffect;
import com.nativegame.animalspop.sound.MySoundEvent;
import com.nativegame.engine.GameEngine;

/**
 * Created by Oscar Liang on 2022/09/18
 */

public class ItemBubble extends Bubble {

    private static final float ITEM_SCALE = 1.3f;

    private final ItemEffect mItemEffect;
    private final BubblePopEffect mBubblePopEffect;

    private boolean mIsItem = true;

    public ItemBubble(GameEngine gameEngine, int row, int col) {
        super(gameEngine, row, col, BubbleColor.ITEM);

        mItemEffect = new ItemEffect(gameEngine, R.drawable.nut);
        mBubblePopEffect = new BubblePopEffect(gameEngine);
    }

    @Override
    public void startGame(GameEngine gameEngine) {
        super.startGame(gameEngine);
        mScale = ITEM_SCALE;
        mItemEffect.mScale = ITEM_SCALE;
        mBubblePopEffect.mScale = ITEM_SCALE;
    }

    @Override
    public void addToGameEngine(GameEngine gameEngine, int layer) {
        super.addToGameEngine(gameEngine, layer + 1);
    }

    @Override
    public void popBubble(GameEngine gameEngine) {
        if (mIsItem) {
            // We pop this item if it hasn't been collected
            popItem(gameEngine);
        } else {
            // Otherwise, we pop the bubble
            super.popBubble(gameEngine);
        }
    }

    @Override
    public void popFloater(GameEngine gameEngine) {
        if (mIsItem) {
            // We pop this item if it hasn't been collected
            popItem(gameEngine);
        } else {
            // Otherwise, we pop the floater
            super.popFloater(gameEngine);
        }
    }

    private void popItem(GameEngine gameEngine) {
        mItemEffect.activate(gameEngine, mX + mWidth / 2f, mY + mHeight / 2f, 4);
        mBubblePopEffect.activate(gameEngine, mX + mWidth / 2f, mY + mHeight / 2f, 4);
        setBubbleColor(BubbleColor.BLANK);
        gameEngine.onGameEvent(MyGameEvent.COLLECT_ITEM);
        gameEngine.mSoundManager.playSound(MySoundEvent.COLLECT_ITEM);
        mScale = 1;
        mIsItem = false;
    }

}
