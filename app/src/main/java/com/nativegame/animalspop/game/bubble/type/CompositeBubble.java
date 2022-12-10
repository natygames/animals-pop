package com.nativegame.animalspop.game.bubble.type;

import com.nativegame.animalspop.game.bubble.Bubble;
import com.nativegame.animalspop.game.bubble.BubbleColor;
import com.nativegame.nattyengine.Game;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Oscar Liang on 2022/12/10
 */

public class CompositeBubble extends Bubble {

    private final List<DummyBubble> mDummyBubbles = new ArrayList<>();

    public CompositeBubble(Game game, BubbleColor bubbleColor) {
        super(game, bubbleColor);
    }

    protected void addDummyBubble(DummyBubble dummyBubble) {
        dummyBubble.mTargetBubble = this;
        mDummyBubbles.add(dummyBubble);
    }

    protected void clearDummyBubble() {
        for (DummyBubble d : mDummyBubbles) {
            d.setBubbleColor(BubbleColor.BLANK);
            d.mTargetBubble = null;
        }
    }

}
