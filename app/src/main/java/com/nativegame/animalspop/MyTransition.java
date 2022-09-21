package com.nativegame.animalspop;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

/**
 * Created by Oscar Liang on 2022/09/18
 */

public class MyTransition implements View.OnTouchListener, Animation.AnimationListener {

    private final Activity mParent;
    private ViewGroup mRootLayout;
    private TransitionListener mListener;

    private boolean mIsShowing;

    public MyTransition(Activity activity) {
        mParent = activity;
    }

    public void setListener(TransitionListener listener) {
        mListener = listener;
    }

    protected View findViewById(int id) {
        return mRootLayout.findViewById(id);
    }

    public void show() {
        mIsShowing = true;
        ViewGroup activityRoot = (ViewGroup) mParent.findViewById(android.R.id.content);
        mRootLayout = (ViewGroup) LayoutInflater.from(mParent).inflate(R.layout.view_transition, activityRoot, false);
        mRootLayout.setOnTouchListener(this);
        activityRoot.addView(mRootLayout);
        startShowAnimation();
    }

    private void hide() {
        ViewGroup activityRoot = (ViewGroup) mParent.findViewById(android.R.id.content);
        activityRoot.removeView(mRootLayout);
    }

    private void startShowAnimation() {
        Animation left = AnimationUtils.loadAnimation(mParent, R.anim.transition_show_left);
        Animation right = AnimationUtils.loadAnimation(mParent, R.anim.transition_show_right);
        right.setAnimationListener(this);
        findViewById(R.id.image_translate_left).startAnimation(left);
        findViewById(R.id.image_translate_right).startAnimation(right);
    }

    private void startHideAnimation() {
        Animation left = AnimationUtils.loadAnimation(mParent, R.anim.transition_hide_left);
        Animation right = AnimationUtils.loadAnimation(mParent, R.anim.transition_hide_right);
        right.setAnimationListener(this);
        findViewById(R.id.image_translate_left).startAnimation(left);
        findViewById(R.id.image_translate_right).startAnimation(right);
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        // Ignoring touch events on the gray outside
        return true;
    }

    @Override
    public void onAnimationStart(Animation animation) {
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        if (mIsShowing) {
            startHideAnimation();
            mListener.onTransition();
        } else {
            hide();
        }
        mIsShowing = false;
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
    }

    public interface TransitionListener {
        void onTransition();
    }

}
