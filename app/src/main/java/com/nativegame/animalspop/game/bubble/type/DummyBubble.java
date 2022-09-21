package com.nativegame.animalspop.game.bubble.type;

import com.nativegame.animalspop.game.bubble.Bubble;
import com.nativegame.animalspop.game.bubble.BubbleColor;
import com.nativegame.engine.GameEngine;

/**
 * Created by Oscar Liang on 2022/09/18
 */

public class DummyBubble extends Bubble {

    public Bubble mTargetBubble;   // A reference to the target bubble

    public DummyBubble(GameEngine gameEngine, int row, int col) {
        super(gameEngine, row, col, BubbleColor.DUMMY);
        // We use DUMMY in bubble color here, so it will not be detected as floater in dfs
    }

    @Override
    public void popBubble(GameEngine gameEngine) {
        if (mTargetBubble != null) {
            mTargetBubble.popBubble(gameEngine);   // Notify the target bubble
        } else {
            super.popBubble(gameEngine);
        }
    }

    @Override
    public void popFloater(GameEngine gameEngine) {
        if (mTargetBubble != null) {
            // The target bubble will do the works
        } else {
            super.popFloater(gameEngine);
        }
    }

}
