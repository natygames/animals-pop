package com.nativegame.animalspop.ui;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.nativegame.animalspop.R;

/**
 * Created by Oscar Liang on 2022/09/18
 */

public class TransitionEffect implements View.OnTouchListener, Animation.AnimationListener {

    private final Activity mParent;
    private ViewGroup mRootLayout;
    private OnTransitionListener mListener;

    private boolean mIsShowing;

    public TransitionEffect(Activity activity) {
        mParent = activity;
    }

    public void setListener(OnTransitionListener listener) {
        mListener = listener;
    }

    public void show() {
        mIsShowing = true;
        ViewGroup activityRoot = (ViewGroup) findViewById(android.R.id.content);
        mRootLayout = (ViewGroup) LayoutInflater.from(mParent).inflate(R.layout.view_transition, activityRoot, false);
        mRootLayout.setOnTouchListener(this);
        activityRoot.addView(mRootLayout);
        startShowAnimation();
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

    private void hideViews() {
        ViewGroup activityRoot = (ViewGroup) findViewById(android.R.id.content);
        activityRoot.removeView(mRootLayout);
    }

    protected View findViewById(int id) {
        return mParent.findViewById(id);
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        // We ignore touch events outside the dialog
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
            mIsShowing = false;
        } else {
            hideViews();
        }
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
    }

    public interface OnTransitionListener {
        void onTransition();
    }

}
