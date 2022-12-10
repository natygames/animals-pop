package com.nativegame.animalspop.game.player.dot;

import com.nativegame.animalspop.game.player.PlayerBubble;
import com.nativegame.nattyengine.Game;
import com.nativegame.nattyengine.entity.GameObject;
import com.nativegame.nattyengine.input.TouchController;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Oscar Liang on 2022/09/18
 */

public class DotSystem extends GameObject {

    protected static final int MAX_DOT = 50;

    protected final PlayerBubble mParent;
    protected final float mStartX;
    protected final float mStartY;
    protected final float mInterval;   // Interval between each dot

    protected final List<Dot> mDotPool = new ArrayList<>(MAX_DOT);

    protected boolean mIsAddToScreen = false;

    public DotSystem(PlayerBubble playerBubble, Game game) {
        super(game);
        mParent = playerBubble;
        mStartX = game.getScreenWidth() / 2f;
        mStartY = game.getScreenHeight() * 4 / 5f - game.getPixelFactor() * 300;
        mInterval = game.getPixelFactor() * 200f;   // We want 200px between each dot

        // Init Dot
        for (int i = 0; i < MAX_DOT; i++) {
            mDotPool.add(new Dot(game));
        }
        for (int i = 0; i < MAX_DOT - 1; i++) {
            mDotPool.get(i).mNextDot = mDotPool.get(i + 1);
        }
    }

    public void setDotBitmap(int drawableId) {
        for (int i = 0; i < MAX_DOT; i++) {
            mDotPool.get(i).setSpriteBitmap(drawableId);
        }
    }

    @Override
    public void onRemove() {
        // We make sure we remove them
        if (mIsAddToScreen) {
            removeDot();
            mIsAddToScreen = false;
        }
    }

    @Override
    public void onUpdate(long elapsedMillis) {
        checkAiming(mGame.getTouchController());
    }

    private void checkAiming(TouchController touchController) {
        if (touchController.mTouching && mParent.getEnable()) {
            // We add the dots one time
            if (!mIsAddToScreen) {
                addDot();
                mIsAddToScreen = true;
            }

            // Get the shooting angle
            double angle = Math.atan2(touchController.mYDown - mStartY,
                    touchController.mXDown - mStartX);

            // We convert angle to dot position
            for (int i = 0; i < MAX_DOT; i++) {
                double x = mStartX + i * mInterval * Math.cos(angle);
                double y = mStartY + i * mInterval * Math.sin(angle);
                setDotPosition(mDotPool.get(i), (float) x, (float) y);
            }
        } else {
            // We remove the dots one time
            if (mIsAddToScreen) {
                removeDot();
                mIsAddToScreen = false;
            }
        }
    }

    protected void setDotPosition(Dot dot, float x, float y) {
        dot.setPosition(x, y);
    }

    protected void addDot() {
        for (int i = 0; i < MAX_DOT; i++) {
            mDotPool.get(i).addToGame();
        }
        // Show the hint when dot activated
        mParent.showHint();
    }

    protected void removeDot() {
        for (int i = 0; i < MAX_DOT; i++) {
            mDotPool.get(i).removeFromGame();
        }
        // Remove the hint when dot removed
        mParent.removeHint();
    }

}
