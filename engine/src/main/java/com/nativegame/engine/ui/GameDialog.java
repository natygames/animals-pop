package com.nativegame.engine.ui;

import static android.view.View.OnTouchListener;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class GameDialog implements OnTouchListener, Animation.AnimationListener {

    protected final GameActivity mParent;
    private ViewGroup mRootLayout;
    private View mRootView;
    private int mRootLayoutResId;

    private int mEnterAnimationResId = android.R.anim.fade_in;
    private int mExitAnimationResId = android.R.anim.fade_out;

    private boolean mIsShowing = false;
    private boolean mIsHiding = true;

    protected GameDialog(GameActivity activity) {
        mParent = activity;
    }

    protected void setContentView(int dialogResId) {
        ViewGroup activityRoot = (ViewGroup) mParent.findViewById(android.R.id.content);
        mRootView = LayoutInflater.from(mParent).inflate(dialogResId, activityRoot, false);
    }

    protected void setRootLayoutResId(int rootLayoutResId) {
        mRootLayoutResId = rootLayoutResId;
    }

    protected void setEnterAnimationResId(int enterAnimatorResId) {
        mEnterAnimationResId = enterAnimatorResId;
    }

    protected void setExitAnimationResId(int exitAnimatorResId) {
        mExitAnimationResId = exitAnimatorResId;
    }

    public void show() {
        if (mIsShowing) {
            return;
        }
        mIsShowing = true;
        mIsHiding = false;

        ViewGroup activityRoot = (ViewGroup) mParent.findViewById(android.R.id.content);
        mRootLayout = (ViewGroup) LayoutInflater.from(mParent).inflate(mRootLayoutResId, activityRoot, false);
        activityRoot.addView(mRootLayout);
        mRootLayout.setOnTouchListener(this);
        mRootLayout.addView(mRootView);
        startShowAnimation();
    }

    private void startShowAnimation() {
        Animation dialogEnter = AnimationUtils.loadAnimation(mParent, mEnterAnimationResId);
        mRootView.startAnimation(dialogEnter);
    }

    public void dismiss() {
        if (!mIsShowing) {
            return;
        }
        if (mIsHiding) {
            return;
        }
        mIsHiding = true;
        mParent.dismissDialog();
        startHideAnimation();
    }

    protected void onDismiss() {
    }

    private void startHideAnimation() {
        Animation dialogExit = AnimationUtils.loadAnimation(mParent, mExitAnimationResId);
        dialogExit.setAnimationListener(this);
        mRootView.startAnimation(dialogExit);
    }

    private void hideViews() {
        mRootLayout.removeView(mRootView);
        ViewGroup activityRoot = (ViewGroup) mParent.findViewById(android.R.id.content);
        activityRoot.removeView(mRootLayout);
    }

    protected View findViewById(int id) {
        return mRootView.findViewById(id);
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        // Ignoring touch events on the gray outside
        return true;
    }

    public boolean isShowing() {
        return mIsShowing;
    }

    @Override
    public void onAnimationStart(Animation animation) {
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        hideViews();
        mIsShowing = false;
        onDismiss();
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
    }

}
