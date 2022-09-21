package com.nativegame.animalspop.game.player.dot;

import android.graphics.Canvas;

import com.nativegame.animalspop.Utils;
import com.nativegame.animalspop.game.player.PlayerBubble;
import com.nativegame.engine.GameEngine;
import com.nativegame.engine.GameObject;
import com.nativegame.engine.input.InputController;

import java.util.ArrayList;

/**
 * Created by Oscar Liang on 2022/09/18
 */

public class DotSystem extends GameObject {

    protected static final int MAX_DOT = 50;

    protected final PlayerBubble mParent;
    protected final float mInitialX, mInitialY;
    protected final float mInterval;   // Interval between each dot
    protected final ArrayList<Dot> mDotPool = new ArrayList<>(MAX_DOT);

    protected boolean mActivate = false;

    public DotSystem(PlayerBubble playerBubble, GameEngine gameEngine) {
        mParent = playerBubble;
        mInitialX = gameEngine.mScreenWidth / 2f;
        mInitialY = gameEngine.mScreenHeight * 4 / 5f - gameEngine.mPixelFactor * Utils.BUBBLE_WIDTH;
        mInterval = gameEngine.mPixelFactor * 200f;   // We want 200px between each dot

        // Init Dot
        for (int i = 0; i < MAX_DOT; i++) {
            mDotPool.add(new Dot(gameEngine));
        }
        for (int i = 0; i < MAX_DOT - 1; i++) {
            mDotPool.get(i).setNextDot(mDotPool.get(i + 1));
        }
    }

    public void setDotBitmap(int drawableResId) {
        for (int i = 0; i < MAX_DOT; i++) {
            mDotPool.get(i).setBitmap(drawableResId);
        }
    }

    @Override
    public void startGame(GameEngine gameEngine) {
    }

    @Override
    public void removeFromGameEngine(GameEngine gameEngine) {
        super.removeFromGameEngine(gameEngine);
        // We make sure we remove them
        if (mActivate) {
            removeDot(gameEngine);
            mActivate = false;
        }
    }

    @Override
    public void onUpdate(long elapsedMillis, GameEngine gameEngine) {
        checkAiming(gameEngine, gameEngine.mInputController);
    }

    private void checkAiming(GameEngine gameEngine, InputController inputController) {
        if (inputController.mTouching && mParent.getEnable()) {
            // We activate the pool one time
            if (!mActivate) {
                addDot(gameEngine);
                mActivate = true;
            }

            // Get the shooting angle
            double angle = Utils.getAngle(inputController.mXDown - mInitialX,
                    inputController.mYDown - mInitialY);

            // We convert angle to dot position
            for (int i = 0; i < MAX_DOT; i++) {
                double positionX = mInitialX + i * mInterval * Math.cos(angle);
                double positionY = mInitialY + i * mInterval * Math.sin(angle);
                setDotPosition(mDotPool.get(i), (float) positionX, (float) positionY);
            }
        } else {
            // We return the dots to the pool one time
            if (mActivate) {
                removeDot(gameEngine);
                mActivate = false;
            }
        }
    }

    protected void setDotPosition(Dot dot, float x, float y) {
        dot.setPosition(x, y);
    }

    protected void addDot(GameEngine gameEngine) {
        for (int i = 0; i < MAX_DOT; i++) {
            mDotPool.get(i).addToGameEngine(gameEngine, 1);
        }
        mParent.showHint();
    }

    protected void removeDot(GameEngine gameEngine) {
        for (int i = 0; i < MAX_DOT; i++) {
            mDotPool.get(i).removeFromGameEngine(gameEngine);
        }
        mParent.removeHint();
    }

    @Override
    public void onDraw(Canvas canvas) {
        // The dots draw by themself
    }

}
