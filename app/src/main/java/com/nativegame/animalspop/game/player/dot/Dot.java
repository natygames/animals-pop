package com.nativegame.animalspop.game.player.dot;

import com.nativegame.animalspop.R;
import com.nativegame.animalspop.game.Layer;
import com.nativegame.nattyengine.Game;
import com.nativegame.nattyengine.collision.Collidable;
import com.nativegame.nattyengine.collision.shape.CircleCollisionShape;
import com.nativegame.nattyengine.entity.sprite.CollidableSprite;
import com.nativegame.animalspop.game.bubble.Bubble;
import com.nativegame.animalspop.game.bubble.BubbleColor;

/**
 * Created by Oscar Liang on 2022/09/18
 */

public class Dot extends CollidableSprite {

    public final float mMinX;   // Left bound
    public final float mMaxX;   // Right bound
    private final float mRange;   // Dot moving range

    public Dot mNextDot;
    public Bubble mCollideBubble;
    public boolean mIsCollide;

    public Dot(Game game) {
        super(game, R.drawable.dot_blue);
        // We want the collision box equal bubble size
        int collisionBoxWidth = mWidth * 3;   // Bubble width
        int collisionBoxHeight = mHeight * 3;   // Bubble height
        setCollisionShape(new CircleCollisionShape(collisionBoxWidth, collisionBoxHeight));
        mMinX = collisionBoxWidth / 2f;
        mMaxX = mGame.getScreenWidth() - collisionBoxWidth / 2f;
        mRange = mMaxX - mMinX;
        mLayer = Layer.BACKGROUND_LAYER;
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
                mX = mMinX + diff % mRange - mWidth / 2f;   // Translate when even
            } else {
                mX = mMaxX - diff % mRange - mWidth / 2f;   // Reflect when odd
            }
        } else {
            mX = x - mWidth / 2f;
        }

        mY = y - mHeight / 2f;
    }

    @Override
    public void onUpdate(long elapsedMillis) {
        if (mIsCollide) {
            if (mNextDot != null) {
                mNextDot.mIsCollide = true;   // Hide next dot
            }
            mIsVisible = false;
        } else {
            mIsVisible = true;
        }
        // Reset bubble collide on
        mCollideBubble = null;
        mIsCollide = false;
    }

    @Override
    public void onCollision(Collidable otherObject) {
        if (otherObject instanceof Bubble) {
            Bubble bubble = (Bubble) otherObject;
            // We hide the dot when collide with colored bubble
            if (bubble.mBubbleColor != BubbleColor.BLANK) {
                mCollideBubble = bubble;   // We also set the bubble the dot collided on
                mIsCollide = true;
            }
        }
    }

}
