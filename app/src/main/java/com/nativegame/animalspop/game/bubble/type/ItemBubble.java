package com.nativegame.animalspop.game.bubble.type;

import com.nativegame.animalspop.R;
import com.nativegame.animalspop.game.MyGameEvent;
import com.nativegame.animalspop.game.bubble.Bubble;
import com.nativegame.animalspop.game.bubble.BubbleColor;
import com.nativegame.animalspop.game.effect.BubblePopEffect;
import com.nativegame.animalspop.game.effect.ItemEffect;
import com.nativegame.animalspop.sound.MySoundEvent;
import com.nativegame.nattyengine.Game;

/**
 * Created by Oscar Liang on 2022/09/18
 */

public class ItemBubble extends Bubble {

    private static final float ITEM_SCALE = 1.3f;

    private final ItemEffect mItemEffect;
    private final BubblePopEffect mBubblePopEffect;

    private boolean mIsItem = true;

    public ItemBubble(Game game) {
        super(game, BubbleColor.ITEM);
        mItemEffect = new ItemEffect(game, R.drawable.nut);
        mBubblePopEffect = new BubblePopEffect(game);
        mLayer++;   // We want the item float on top of bubbles
    }

    @Override
    public void onStart() {
        super.onStart();
        // Init scale
        mScale = ITEM_SCALE;
        mItemEffect.mScale = ITEM_SCALE;
        mBubblePopEffect.mScale = ITEM_SCALE;
    }

    @Override
    public void popBubble() {
        if (mIsItem) {
            // We pop the item if it hasn't been collected
            popItem();
        } else {
            // Otherwise, we pop the bubble
            super.popBubble();
        }
    }

    @Override
    public void popFloater() {
        if (mIsItem) {
            // We pop the item if it hasn't been collected
            popItem();
        } else {
            // Otherwise, we pop the floater
            super.popFloater();
        }
    }

    private void popItem() {
        mItemEffect.activate(mX + mWidth / 2f, mY + mHeight / 2f);
        mBubblePopEffect.activate(mX + mWidth / 2f, mY + mHeight / 2f);
        setBubbleColor(BubbleColor.BLANK);
        gameEvent(MyGameEvent.COLLECT_ITEM);
        mGame.getSoundManager().playSound(MySoundEvent.COLLECT_ITEM);
        mScale = 1;
        mIsItem = false;
    }

}
