package com.nativegame.engine.sprite;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;

import com.nativegame.engine.GameEngine;
import com.nativegame.engine.GameObject;

public abstract class Sprite extends GameObject {

    private static final boolean DEBUG_MODE = false;

    public final int mWidth;
    public final int mHeight;
    private final float mRadius;

    public float mX;
    public float mY;
    public float mRotation;
    public float mScale = 1;
    public float mAlpha = 255;

    public BodyType mBodyType;

    protected final float mPixelFactor;
    protected Bitmap mBitmap;
    private final Resources mResources;
    private final Matrix mMatrix = new Matrix();
    private final Paint mPaint = new Paint();
    public final Rect mHitBoxRect = new Rect(-1, -1, -1, -1);

    protected Sprite(GameEngine gameEngine, int drawableResId, BodyType bodyType) {
        mPixelFactor = gameEngine.mPixelFactor;
        mBodyType = bodyType;
        mResources = gameEngine.getContext().getResources();
        mBitmap = getBitmap(drawableResId);
        mWidth = (int) (mBitmap.getWidth() * mPixelFactor);
        mHeight = (int) (mBitmap.getHeight() * mPixelFactor);
        mRadius = Math.max(mHeight, mWidth) / 2f;
    }

    protected Bitmap getBitmap(int drawableResId) {
        return ((BitmapDrawable) mResources.getDrawable(drawableResId)).getBitmap();
    }

    public void setBitmap(int drawableResId) {
        mBitmap = getBitmap(drawableResId);
    }

    public boolean checkCollision(Sprite otherSprite) {
        if (mBodyType == BodyType.Circular && otherSprite.mBodyType == BodyType.Circular) {
            return checkCircularCollision(otherSprite);
        } else if (mBodyType == BodyType.Rectangular && otherSprite.mBodyType == BodyType.Rectangular) {
            return checkRectangularCollision(otherSprite);
        } else {
            return checkMixedCollision(otherSprite);
        }
    }

    private boolean checkRectangularCollision(Sprite otherSprite) {
        return Rect.intersects(mHitBoxRect, otherSprite.mHitBoxRect);
    }

    private boolean checkCircularCollision(Sprite otherSprite) {
        double distanceX = (mX + mWidth / 2f) - (otherSprite.mX + otherSprite.mWidth / 2f);
        double distanceY = (mY + mHeight / 2f) - (otherSprite.mY + otherSprite.mHeight / 2f);
        double squareDistance = distanceX * distanceX + distanceY * distanceY;
        double collisionDistance = (mRadius + otherSprite.mRadius);
        return squareDistance <= collisionDistance * collisionDistance;
    }

    private boolean checkMixedCollision(Sprite otherSprite) {
        Sprite circularSprite;
        Sprite rectangularSprite;
        if (mBodyType == BodyType.Rectangular) {
            circularSprite = this;
            rectangularSprite = otherSprite;
        } else {
            circularSprite = otherSprite;
            rectangularSprite = this;
        }

        double circleCenterX = circularSprite.mX + circularSprite.mWidth / 2f;
        double positionXToCheck = circleCenterX;
        if (circleCenterX < rectangularSprite.mX) {
            positionXToCheck = rectangularSprite.mX;
        } else if (circleCenterX > rectangularSprite.mX + rectangularSprite.mWidth) {
            positionXToCheck = rectangularSprite.mX + rectangularSprite.mWidth;
        }
        double distanceX = circleCenterX - positionXToCheck;

        double circleCenterY = circularSprite.mY + circularSprite.mHeight / 2f;
        double positionYToCheck = circleCenterY;
        if (circleCenterY < rectangularSprite.mY) {
            positionYToCheck = rectangularSprite.mY;
        } else if (circleCenterY > rectangularSprite.mY + rectangularSprite.mHeight) {
            positionYToCheck = rectangularSprite.mY + rectangularSprite.mHeight;
        }
        double distanceY = circleCenterY - positionYToCheck;
        double squareDistance = distanceX * distanceX + distanceY * distanceY;
        return squareDistance <= circularSprite.mRadius * circularSprite.mRadius;
    }

    public void onCollision(GameEngine gameEngine, Sprite otherObject) {
    }

    @Override
    public void onPostUpdate() {
        mHitBoxRect.set(
                (int) mX,
                (int) mY,
                (int) mX + mWidth,
                (int) mY + mHeight);
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (mX > canvas.getWidth()
                || mY > canvas.getHeight()
                || mX < -mWidth
                || mY < -mHeight) {
            return;
        }

        if (DEBUG_MODE) {
            mPaint.setColor(Color.YELLOW);
            canvas.drawRect(mHitBoxRect, mPaint);
        }

        float scaleFactor = mPixelFactor * mScale;
        float translateFactor = (1 - mScale) / 2;
        mMatrix.reset();
        mMatrix.postScale(scaleFactor, scaleFactor);
        mMatrix.postTranslate(mX + translateFactor * mWidth, mY + translateFactor * mHeight);
        mMatrix.postRotate(mRotation, mX + mWidth / 2f, mY + mHeight / 2f);
        mPaint.setAlpha((int) mAlpha);
        canvas.drawBitmap(mBitmap, mMatrix, mPaint);
    }

}
