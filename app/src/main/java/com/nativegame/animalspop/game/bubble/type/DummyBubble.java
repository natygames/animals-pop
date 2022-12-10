package com.nativegame.animalspop.game.bubble.type;

import com.nativegame.animalspop.game.bubble.Bubble;
import com.nativegame.animalspop.game.bubble.BubbleColor;
import com.nativegame.nattyengine.Game;

/**
 * Created by Oscar Liang on 2022/12/10
 */

public class DummyBubble extends Bubble {

    public Bubble mTargetBubble;   // A reference to the target bubble

    public DummyBubble(Game game) {
        super(game, BubbleColor.DUMMY);
        // We use DUMMY in bubble color here, so it will not be detected as floater
    }

    @Override
    public void popBubble() {
        if (mTargetBubble != null) {
            mTargetBubble.popBubble();
        } else {
            super.popBubble();
        }
    }

    @Override
    public void popFloater() {
        if (mTargetBubble != null) {
            // The target bubble will do the works
            return;
        }
        super.popFloater();
    }

}
