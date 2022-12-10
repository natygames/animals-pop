package com.nativegame.animalspop.game.bonus;

import com.nativegame.animalspop.game.MyGameEvent;
import com.nativegame.animalspop.game.bubble.BubbleColor;
import com.nativegame.animalspop.sound.MySoundEvent;
import com.nativegame.nattyengine.Game;
import com.nativegame.nattyengine.entity.GameObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Oscar Liang on 2022/09/18
 */

public class BonusSystem extends GameObject implements BonusBubble.BonusTimeEndListener {

    private final float mStartX;
    private final float mStartY;

    private final List<BonusBubble> mBonusBubbles = new ArrayList<>();

    private long mTotalTime;

    public BonusSystem(Game game) {
        super(game);
        mStartX = game.getScreenWidth() / 2f;
        mStartY = game.getScreenHeight() * 4 / 5f - game.getPixelFactor() * 300;
    }

    public void addBonusBubble(BubbleColor bubbleColor) {
        mBonusBubbles.add(new BonusBubble(mGame, bubbleColor));
    }

    @Override
    public void onStart() {
        // We set the listener on last bonus bubble
        BonusBubble lastBubble = mBonusBubbles.get(mBonusBubbles.size() - 1);
        lastBubble.setBonusTimeEndListener(this);
    }

    @Override
    public void onUpdate(long elapsedMillis) {
        mTotalTime += elapsedMillis;
        // Add one bonus bubble every 100ms
        if (mTotalTime >= 100) {
            if (!mBonusBubbles.isEmpty()) {
                addOneBonusBubble();
            } else {
                removeFromGame();
            }
            mTotalTime = 0;
        }
    }

    private void addOneBonusBubble() {
        BonusBubble bonusBubble = mBonusBubbles.remove(0);
        bonusBubble.activate(mStartX, mStartY);
        gameEvent(MyGameEvent.BUBBLE_SHOT);   // We notify the move counter
        mGame.getSoundManager().playSound(MySoundEvent.BUBBLE_HIT);
    }

    @Override
    public void onBonusTimeEnd() {
        // Show win dialog when the last bonus bubble removed
        gameEvent(MyGameEvent.SHOW_WIN_DIALOG);
    }

}
