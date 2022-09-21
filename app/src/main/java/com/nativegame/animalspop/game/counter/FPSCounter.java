package com.nativegame.animalspop.game.counter;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

import androidx.core.content.res.ResourcesCompat;

import com.nativegame.animalspop.R;
import com.nativegame.engine.GameEngine;
import com.nativegame.engine.GameObject;

/**
 * Created by Oscar Liang on 2022/09/18
 */

public class FPSCounter extends GameObject {

    private final float mPositionX;
    private final float mPositionY;
    private final Paint mPaint = new Paint();

    private long mTotalMillis;
    private int mDraws;

    private String mFpsText = "";

    public FPSCounter(GameEngine gameEngine) {
        // Init position
        int textWidth = (int) (gameEngine.mPixelFactor * 250);
        mPositionX = textWidth;
        mPositionY = gameEngine.mScreenHeight - textWidth / 6f;
        // Init text
        Typeface typeface = ResourcesCompat.getFont(gameEngine.getContext(), R.font.baloo);
        mPaint.setTypeface(typeface);
        mPaint.setColor(Color.WHITE);
        mPaint.setTextSize(textWidth / 2f);
        mPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    public void startGame(GameEngine gameEngine) {
        mTotalMillis = 0;
        mDraws = 0;
    }

    @Override
    public void onUpdate(long elapsedMillis, GameEngine gameEngine) {
        mTotalMillis += elapsedMillis;
        if (mTotalMillis > 1000) {
            int fps = (int) (mDraws * 1000 / mTotalMillis);
            mFpsText = fps + "fps";
            mTotalMillis = 0;
            mDraws = 0;
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawText(mFpsText, mPositionX, mPositionY, mPaint);
        mDraws++;
    }

}
