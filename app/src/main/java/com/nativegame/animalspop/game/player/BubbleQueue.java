package com.nativegame.animalspop.game.player;

import com.nativegame.animalspop.game.bubble.BubbleColor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Oscar Liang on 2022/09/18
 */

public class BubbleQueue {

    private static final int EXTRA_MOVES = 5;

    private final char[] mBubbleChars;

    private final List<BubbleColor> mBubbleList = new ArrayList<>();

    public BubbleQueue(char[] array) {
        mBubbleChars = array;
        // Init the bubble
        for (char color : array) {
            mBubbleList.add(getBubbleColor(color));
        }
    }

    private BubbleColor getBubbleColor(char color) {
        switch (color) {
            case 'r':
                return BubbleColor.RED;
            case 'b':
                return BubbleColor.BLUE;
            case 'y':
                return BubbleColor.YELLOW;
            case 'g':
                return BubbleColor.GREEN;
        }
        return null;
    }

    //--------------------------------------------------------
    // Methods to change state of queue
    //--------------------------------------------------------

    public BubbleColor popBubble() {
        if (hasBubble()) {
            return mBubbleList.remove(0);
        } else {
            return null;
        }
    }

    public BubbleColor getBubble() {
        if (hasBubble()) {
            return mBubbleList.get(0);
        } else {
            return null;
        }
    }

    public BubbleColor getNextBubble() {
        if (mBubbleList.size() >= 2) {
            return mBubbleList.get(1);
        } else {
            return null;
        }
    }

    public void switchBubble() {
        if (mBubbleList.size() < 2) {
            return;
        }
        BubbleColor temp = mBubbleList.get(0);
        mBubbleList.set(0, mBubbleList.get(1));
        mBubbleList.set(1, temp);
    }

    public boolean hasBubble() {
        return !mBubbleList.isEmpty();
    }

    //=================================================================================

    public void initExtraBubble() {
        // Init the extra bubble
        int start = mBubbleChars.length - 5;
        for (int i = 0; i < EXTRA_MOVES; i++) {
            char color = mBubbleChars[start + i];
            mBubbleList.add(getBubbleColor(color));
        }
    }

}
