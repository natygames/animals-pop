package com.nativegame.animalspop.game.player.booster;

import com.nativegame.animalspop.game.MyGameEvent;
import com.nativegame.animalspop.game.bubble.BubbleSystem;
import com.nativegame.animalspop.game.player.PlayerBubble;
import com.nativegame.animalspop.sound.MySoundEvent;
import com.nativegame.nattyengine.Game;

/**
 * Created by Oscar Liang on 2022/12/10
 */

public abstract class BoosterBubble extends PlayerBubble {

    protected boolean mConsume;

    protected BoosterBubble(BubbleSystem bubbleSystem, Game game, int drawableId) {
        super(bubbleSystem, game, drawableId);
    }

    @Override
    public void onStart() {
        super.onStart();
        mConsume = false;
        gameEvent(MyGameEvent.BOOSTER_ADDED);
        mGame.getSoundManager().playSound(MySoundEvent.ADD_BOOSTER);
    }

    @Override
    public void onRemove() {
        super.onRemove();
        if (mConsume) {
            // Update player state and notify the booster manager
            gameEvent(MyGameEvent.BOOSTER_CONSUMED);
        } else {
            // Update player state
            gameEvent(MyGameEvent.BOOSTER_REMOVED);
        }
    }

    @Override
    protected void onBubbleShoot() {
        gameEvent(MyGameEvent.BOOSTER_SHOT);
        mGame.getSoundManager().playSound(MySoundEvent.BUBBLE_SHOOT);
    }

    @Override
    protected void onBubbleSwitch() {
        // We do not switch booster here
    }

    @Override
    protected void onBubbleReset() {
        // Pop floater and shift
        mBubbleSystem.popFloater();
        mBubbleSystem.shiftBubble();
        mConsume = true;
        removeFromGame();
    }

}
