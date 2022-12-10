package com.nativegame.animalspop.ui.dialog;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;

import com.nativegame.animalspop.AdManager;
import com.nativegame.animalspop.MainActivity;
import com.nativegame.animalspop.R;
import com.nativegame.animalspop.ui.UIEffect;
import com.nativegame.animalspop.database.DatabaseHelper;
import com.nativegame.animalspop.item.prize.Prize;
import com.nativegame.animalspop.item.prize.PrizeManager;
import com.nativegame.animalspop.sound.MySoundEvent;
import com.nativegame.animalspop.timer.WheelTimer;
import com.nativegame.nattyengine.ui.GameActivity;
import com.nativegame.nattyengine.ui.GameDialog;

import java.util.Random;

/**
 * Created by Oscar Liang on 2022/09/18
 */

public class WheelDialog extends GameDialog implements View.OnClickListener,
        Animation.AnimationListener, AdManager.AdRewardListener {

    private final PrizeManager mPrizeManager;
    private final WheelTimer mWheelTimer;
    private final boolean mWheelReady;

    private int mDegree;

    public WheelDialog(GameActivity activity, WheelTimer wheelTimer) {
        super(activity);
        setContentView(R.layout.dialog_wheel);
        setRootLayoutId(R.layout.dialog_container);
        setEnterAnimationId(R.anim.enter_from_center);
        mPrizeManager = new PrizeManager();
        mWheelTimer = wheelTimer;
        mWheelReady = mWheelTimer.isWheelReady();
        init();
    }

    private void init() {
        // Init button
        ImageButton btnCancel = (ImageButton) findViewById(R.id.btn_cancel);
        ImageButton btnPlay = (ImageButton) findViewById(R.id.btn_play);
        btnCancel.setOnClickListener(this);
        btnPlay.setOnClickListener(this);
        UIEffect.createButtonEffect(btnCancel);
        UIEffect.createButtonEffect(btnPlay);

        // Show ad button if wheel not ready
        if (!mWheelReady) {
            btnPlay.setBackgroundResource(R.drawable.btn_watch_ad);
        }

        // Init pop up
        UIEffect.createPopUpEffect(btnCancel);
        UIEffect.createPopUpEffect(findViewById(R.id.txt_bonus), 2);
        UIEffect.createPopUpEffect(btnPlay, 4);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_cancel) {
            mParent.getSoundManager().playSound(MySoundEvent.BUTTON_CLICK);
            dismiss();
        } else if (id == R.id.btn_play) {
            mParent.getSoundManager().playSound(MySoundEvent.BUTTON_CLICK);
            if (mWheelReady) {
                // Save current time and spin wheel
                mWheelTimer.setWheelTime(System.currentTimeMillis());
                spinWheel();
            } else {
                showAd();
            }
            // Hide play button
            view.setVisibility(View.INVISIBLE);
        }
    }

    private void spinWheel() {
        // Generate a random degree from 0 to 360
        int random = new Random().nextInt(360);
        // We want to rotate at least 3 times
        mDegree = random + 720;

        // Wheel rotate animation
        RotateAnimation wheelAnimation = new RotateAnimation(0, mDegree,
                1, 0.5f, 1, 0.5f);
        wheelAnimation.setDuration(4000);
        wheelAnimation.setFillAfter(true);
        wheelAnimation.setInterpolator(new DecelerateInterpolator());
        wheelAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Dismiss and show prize
                dismiss();
                showPrize();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        findViewById(R.id.image_wheel).startAnimation(wheelAnimation);

        // Pointer rotate animation
        RotateAnimation pointerAnimation = new RotateAnimation(0, -30,
                1, 0.5f, 1, 0.35f);
        pointerAnimation.setDuration(200);
        pointerAnimation.setRepeatMode(Animation.REVERSE);
        pointerAnimation.setRepeatCount(15);
        findViewById(R.id.image_wheel_pointer).startAnimation(pointerAnimation);

        // Play spinning sound
        mParent.getSoundManager().playSound(MySoundEvent.WHEEL_SPIN);
    }

    private void showPrize() {
        // Get degree
        mDegree = mDegree % 360;

        // Save prizes base on degree
        Prize prize = getPrize(mDegree);
        savePrizes(prize.getName(), prize.getNum());
        updateCoin();

        // Show prize dialog
        NewBoosterDialog newBoosterDialog = new NewBoosterDialog(mParent, prize.getDrawableResId()) {
            @Override
            public void showDialog() {
                // Show the level dialog after dismiss the dialog
                showLevel();
            }
        };
        mParent.showDialog(newBoosterDialog);
    }

    private Prize getPrize(int degree) {
        if (degree < 72) {   // Hammer
            return mPrizeManager.getPrize(PrizeManager.PRIZE_FIREBALL);
        } else if (degree < 144) {   // Bomb
            return mPrizeManager.getPrize(PrizeManager.PRIZE_BOMB);
        } else if (degree < 216) {   // 50 coin
            return mPrizeManager.getPrize(PrizeManager.PRIZE_COIN_50);
        } else if (degree < 288) {   // Gloves
            return mPrizeManager.getPrize(PrizeManager.PRIZE_COLOR_BALL);
        } else {   // 150 coin
            return mPrizeManager.getPrize(PrizeManager.PRIZE_COIN_150);
        }
    }

    private void savePrizes(String name, int amount) {
        DatabaseHelper databaseHelper = ((MainActivity) mParent).getDatabaseHelper();
        int saving = databaseHelper.getItemNum(name);
        databaseHelper.updateItemNum(name, saving + amount);
    }

    private void showAd() {
        AdManager ad = ((MainActivity) mParent).getAdManager();
        ad.setListener(this);
        boolean isConnect = ad.showRewardAd();
        // Show error dialog if no internet connect
        if (!isConnect) {
            ErrorDialog dialog = new ErrorDialog(mParent) {
                @Override
                public void retry() {
                    ((MainActivity) mParent).getAdManager().requestAd();
                    showAd();
                }
            };
            mParent.showDialog(dialog);
        }
    }

    @Override
    public void onEarnReward() {
        spinWheel();
    }

    @Override
    public void onLossReward() {
        // We do nothing
    }

    @Override
    protected void onShow() {
        mParent.getSoundManager().playSound(MySoundEvent.SWEEP_IN);
    }

    public void updateCoin() {
        // Override this method to update coin num
    }

    public void showLevel() {
        // Override this method to show level dialog
    }

}
