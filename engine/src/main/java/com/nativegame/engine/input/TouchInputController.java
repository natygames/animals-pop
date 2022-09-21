package com.nativegame.engine.input;

import android.view.MotionEvent;
import android.view.View;

import com.nativegame.engine.GameEngine;

public class TouchInputController extends InputController implements View.OnTouchListener {

    protected final GameEngine mGameEngine;

    public TouchInputController(GameEngine gameEngine, int viewResId) {
        mGameEngine = gameEngine;
        gameEngine.mActivity.findViewById(viewResId).setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            mXDown = (int) event.getX();
            mYDown = (int) event.getY();
            onActionDown();
        } else if (action == MotionEvent.ACTION_MOVE) {
            mXDown = (int) event.getX();
            mYDown = (int) event.getY();
            onActionMove();
        } else if (action == MotionEvent.ACTION_UP) {
            mXUp = (int) event.getX();
            mYUp = (int) event.getY();
            onActionUp();
        }
        return true;
    }


    protected void onActionDown() {
        mTouching = true;
    }

    protected void onActionMove() {
        mTouching = true;
    }

    protected void onActionUp() {
        mTouching = false;
    }

}
