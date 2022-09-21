package com.nativegame.animalspop.game.player.dot;

import com.nativegame.animalspop.R;
import com.nativegame.animalspop.Utils;
import com.nativegame.engine.sprite.BodyType;
import com.nativegame.engine.GameEngine;
import com.nativegame.engine.sprite.Sprite;
import com.nativegame.animalspop.game.bubble.Bubble;
import com.nativegame.animalspop.game.bubble.BubbleColor;

/**
 * Created by Oscar Liang on 2022/09/18
 */

public class Dot extends Sprite {

    private final float mMinX, mMaxX;
    private final float mRange;   // Dot moving range

    public Dot mNextDot;
    public Bubble mCollideBubble;
    public boolean mVisible = true;

    public Dot(GameEngine gameEngine) {
        super(gameEngine, R.drawable.dot_blue, BodyType.Circular);

        float bubbleRadius = (gameEngine.mPixelFactor * Utils.BUBBLE_WIDTH) / 2;
        mMinX = bubbleRadius;   // Left bound
        mMaxX = gameEngine.mScreenWidth - bubbleRadius;   // Right bound
        mRange = mMaxX - mMinX;   // Range between left and right bound
    }

    public void setNextDot(Dot dot) {
        mNextDot = dot;
    }

    public void setPosition(float x, float y) {
        if (x > mMaxX) {
            float diff = x - mMaxX;
            if ((int) (diff / mRange) % 2 == 0) {
                mX = mMaxX - diff % mRange - mWidth / 2f;   // Reflect when even
            } else {
                mX = mMinX + diff % mRange - mWidth / 2f;   // Translate when odd
            }
        } else if (x < mMinX) {
            float diff = mMinX - x;
            if ((int) (diff / mRange) % 2 == 0) {
                mX = mMinX + diff % mRange - mWidth / 2f;   // Reflect when even
            } else {
                mX = mMaxX - diff % mRange - mWidth / 2f;   // Translate when odd
            }
        } else {
            mX = x - mWidth / 2f;
        }

        mY = y - mHeight / 2f;
    }

    @Override
    public void startGame(GameEngine gameEngine) {
    }

    @Override
    public void onUpdate(long elapsedMillis, GameEngine gameEngine) {
        checkVisible();
        mCollideBubble = null;   // Reset bubble collide on
        mVisible = true;   // Reset visible
    }

    private void checkVisible() {
        if (!mVisible) {
            if (mNextDot != null) {
                mNextDot.mVisible = false;   // Hide next dot
            }
            mAlpha = 0;
        } else {
            mAlpha = 255;
        }
    }

    @Override
    public void onCollision(GameEngine gameEngine, Sprite otherObject) {
        if (otherObject instanceof Bubble) {
            Bubble bubble = (Bubble) otherObject;
            // We hide the dot when collide with colored bubble
            if (bubble.mBubbleColor != BubbleColor.BLANK) {
                mCollideBubble = bubble;   // We also set the bubble the dot collided on
                mVisible = false;
            }
        }
    }

}
