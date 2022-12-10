package com.nativegame.animalspop.ui.dialog;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.nativegame.animalspop.MainActivity;
import com.nativegame.animalspop.R;
import com.nativegame.animalspop.ui.TransitionEffect;
import com.nativegame.animalspop.ui.UIEffect;
import com.nativegame.animalspop.database.DatabaseHelper;
import com.nativegame.animalspop.level.MyLevel;
import com.nativegame.animalspop.sound.MySoundEvent;
import com.nativegame.nattyengine.ui.GameActivity;
import com.nativegame.nattyengine.ui.GameDialog;

/**
 * Created by Oscar Liang on 2022/09/18
 */

public class WinDialog extends GameDialog implements View.OnClickListener,
        TransitionEffect.OnTransitionListener {

    private static final int EXPLODE_DURATION = 250;

    private final ConstraintLayout mDialogLayout;
    private final ImageView mStar1;
    private final ImageView mStar2;
    private final ImageView mStar3;
    private final int mLevel;
    private final int mScore;
    private final int mStar;

    private final TransitionEffect mTransitionEffect;

    public WinDialog(GameActivity activity, MyLevel level) {
        super(activity);
        setContentView(R.layout.dialog_win);
        setRootLayoutId(R.layout.dialog_game_container);
        setEnterAnimationId(R.anim.enter_from_center);
        setExitAnimationId(android.R.anim.fade_out);
        mDialogLayout = (ConstraintLayout) findViewById(R.id.layout_win_dialog);
        mStar1 = (ImageView) findViewById(R.id.image_win_star_01);
        mStar2 = (ImageView) findViewById(R.id.image_win_star_02);
        mStar3 = (ImageView) findViewById(R.id.image_win_star_03);
        mLevel = level.mLevel;
        mScore = level.mScore;
        mStar = level.mStar;
        mTransitionEffect = new TransitionEffect(activity);
        mTransitionEffect.setListener(this);
        init();
    }

    private void init() {
        // Init level text
        TextView txtLevel = (TextView) findViewById(R.id.txt_level);
        txtLevel.setText(mParent.getResources().getString(R.string.txt_level, mLevel));

        // Init next button
        ImageButton btnNext = (ImageButton) findViewById(R.id.btn_win_next);
        btnNext.setOnClickListener(this);
        UIEffect.createPopUpEffect(btnNext, 10);
        UIEffect.createButtonEffect(btnNext);

        // Init bg animation
        ImageView imageBg = (ImageView) findViewById(R.id.image_fox_bg);
        Animation animation = AnimationUtils.loadAnimation(mParent, R.anim.light_rotate);
        imageBg.startAnimation(animation);

        //Init pop effect
        UIEffect.createPopUpEffect(findViewById(R.id.image_fox));
        UIEffect.createPopUpEffect(imageBg, 2);

        // Update level star form db
        insertOrUpdateStar();

        // Start the animation
        startAnimation();
    }

    private void insertOrUpdateStar() {
        DatabaseHelper databaseHelper = ((MainActivity) mParent).getDatabaseHelper();
        int oldStar = databaseHelper.getLevelStar(mLevel);

        if (oldStar == -1) {
            // If data doesn't exist, we add one
            databaseHelper.insertLevelStar(mStar);
        } else {
            // If data exist and new star is bigger, we update
            if (mStar > oldStar) {
                databaseHelper.updateLevelStar(mLevel, mStar);
            }
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_win_next) {
            mParent.getSoundManager().playSound(MySoundEvent.BUTTON_CLICK);
            dismiss();
        }
    }

    @Override
    protected void onShow() {
        mParent.getSoundManager().playSound(MySoundEvent.GAME_COMPLETE);
    }

    @Override
    protected void onDismiss() {
        stopGame();
        mTransitionEffect.show();
    }

    @Override
    public void onTransition() {
        // Navigate back to map
        mParent.navigateBack();
    }

    public void stopGame() {
        // Override this method to stop the game
    }

    private void startAnimation() {
        // Init score text
        TextView txtScore = (TextView) findViewById(R.id.txt_win_score);
        // Run score animation
        ValueAnimator animator = ValueAnimator.ofFloat(mScore - 150, mScore);
        animator.setDuration(1500);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                txtScore.setText(String.valueOf((int) value));
            }
        });

        // Play score calculate sound
        txtScore.postDelayed(new Runnable() {
            @Override
            public void run() {
                animator.start();
                mParent.getSoundManager().playSound(MySoundEvent.CALCULATE_SCORE);
            }
        }, 500);

        // Start star animation
        if (mStar >= 1) {
            mStar1.postDelayed(new Runnable() {
                @Override
                public void run() {
                    createStarAnimation(mStar1);
                }
            }, 700);
        }
        if (mStar >= 2) {
            mStar2.postDelayed(new Runnable() {
                @Override
                public void run() {
                    createStarAnimation(mStar2);
                }
            }, 1000);
        }
        if (mStar >= 3) {
            mStar3.postDelayed(new Runnable() {
                @Override
                public void run() {
                    createStarAnimation(mStar3);
                }
            }, 1300);
        }
    }

    private void createStarAnimation(ImageView view) {
        view.animate()
                .setDuration(300)
                .scaleX(2)
                .scaleY(2)
                .alpha(1)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view.animate()
                                .setDuration(100)
                                .scaleX(1)
                                .scaleY(1)
                                .setInterpolator(new OvershootInterpolator());
                    }
                });
        // Start animation and sound
        createExplosion(view);
        createSparkle(view);
        mParent.getSoundManager().playSound(MySoundEvent.ADD_STAR);
    }

    private void createExplosion(ImageView view) {
        int x = (int) view.getX();
        int y = (int) view.getY();
        int width = view.getWidth();

        // Top right
        ImageView flash1 = new ImageView(mParent);
        flash1.setImageResource(R.drawable.flash_bar);
        flash1.setX(x + width * 0.5f);
        flash1.setY(y + width * 0.25f);
        flash1.setLayoutParams(new ViewGroup.LayoutParams(width / 4, width / 4));
        flash1.setRotation(45);
        flash1.animate()
                .setDuration(EXPLODE_DURATION)
                .scaleX(6)
                .scaleY(6)
                .alpha(0)
                .x(x + width * 0.5f + width)
                .y(y + width * 0.25f - width)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mDialogLayout.removeView(flash1);
                    }
                });
        mDialogLayout.addView(flash1);

        // Bottom right
        ImageView flash2 = new ImageView(mParent);
        flash2.setImageResource(R.drawable.flash_bar);
        flash2.setX(x + width * 0.5f);
        flash2.setY(y + width * 0.5f);
        flash2.setLayoutParams(new ViewGroup.LayoutParams(width / 4, width / 4));
        flash2.setRotation(135);
        flash2.animate()
                .setDuration(EXPLODE_DURATION)
                .scaleX(6)
                .scaleY(6)
                .alpha(0)
                .x(x + width * 0.5f + width)
                .y(y + width * 0.5f + width)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mDialogLayout.removeView(flash2);
                    }
                });
        mDialogLayout.addView(flash2);

        // Bottom left
        ImageView flash3 = new ImageView(mParent);
        flash3.setImageResource(R.drawable.flash_bar);
        flash3.setX(x + width * 0.25f);
        flash3.setY(y + width * 0.5f);
        flash3.setLayoutParams(new ViewGroup.LayoutParams(width / 4, width / 4));
        flash3.setRotation(225);
        flash3.animate()
                .setDuration(EXPLODE_DURATION)
                .scaleX(6)
                .scaleY(6)
                .alpha(0)
                .x(x + width * 0.25f - width)
                .y(y + width * 0.5f + width)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mDialogLayout.removeView(flash3);
                    }
                });
        mDialogLayout.addView(flash3);

        // Top left
        ImageView flash4 = new ImageView(mParent);
        flash4.setImageResource(R.drawable.flash_bar);
        flash4.setX(x + width * 0.25f);
        flash4.setY(y + width * 0.25f);
        flash4.setLayoutParams(new ViewGroup.LayoutParams(width / 4, width / 4));
        flash4.setRotation(315);
        flash4.animate()
                .setDuration(EXPLODE_DURATION)
                .scaleX(6)
                .scaleY(6)
                .alpha(0)
                .x(x + width * 0.25f - width)
                .y(y + width * 0.25f - width)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mDialogLayout.removeView(flash4);
                    }
                });
        mDialogLayout.addView(flash4);

        // Top
        ImageView flash5 = new ImageView(mParent);
        flash5.setImageResource(R.drawable.flash_bar);
        flash5.setX(x + width * 0.375f);
        flash5.setY(y + width * 0.25f);
        flash5.setLayoutParams(new ViewGroup.LayoutParams(width / 4, width / 4));
        flash5.setRotation(0);
        flash5.animate()
                .setDuration(EXPLODE_DURATION)
                .scaleX(6)
                .scaleY(6)
                .alpha(0)
                .y(y + width * 0.25f - width)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mDialogLayout.removeView(flash5);
                    }
                });
        mDialogLayout.addView(flash5);

        // Right
        ImageView flash6 = new ImageView(mParent);
        flash6.setImageResource(R.drawable.flash_bar);
        flash6.setX(x + width * 0.5f);
        flash6.setY(y + width * 0.375f);
        flash6.setLayoutParams(new ViewGroup.LayoutParams(width / 4, width / 4));
        flash6.setRotation(90);
        flash6.animate()
                .setDuration(EXPLODE_DURATION)
                .scaleX(6)
                .scaleY(6)
                .alpha(0)
                .x(x + width * 0.5f + width)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mDialogLayout.removeView(flash6);
                    }
                });
        mDialogLayout.addView(flash6);
        // Bottom
        ImageView flash7 = new ImageView(mParent);
        flash7.setImageResource(R.drawable.flash_bar);
        flash7.setX(x + width * 0.375f);
        flash7.setY(y + width * 0.5f);
        flash7.setLayoutParams(new ViewGroup.LayoutParams(width / 4, width / 4));
        flash7.setRotation(180);
        flash7.animate()
                .setDuration(EXPLODE_DURATION)
                .scaleX(6)
                .scaleY(6)
                .alpha(0)
                .y(y + width * 0.5f + width)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mDialogLayout.removeView(flash7);
                    }
                });
        mDialogLayout.addView(flash7);
        // Left
        ImageView flash8 = new ImageView(mParent);
        flash8.setImageResource(R.drawable.flash_bar);
        flash8.setX(x + width * 0.25f);
        flash8.setY(y + width * 0.375f);
        flash8.setLayoutParams(new ViewGroup.LayoutParams(width / 4, width / 4));
        flash8.setRotation(270);
        flash8.animate()
                .setDuration(EXPLODE_DURATION)
                .scaleX(6)
                .scaleY(6)
                .alpha(0)
                .x(x + width * 0.25f - width)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mDialogLayout.removeView(flash8);
                    }
                });
        mDialogLayout.addView(flash8);
    }

    private void createSparkle(ImageView view) {
        int width = view.getWidth() / 9;
        int height = view.getHeight() / 9;
        int x = (int) (view.getX() + view.getWidth() * 4 / 9);
        int y = (int) (view.getY() + view.getWidth() * 4 / 9);
        // Init sparkle
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                ImageView sparkle = new ImageView(mParent);
                sparkle.setImageResource(R.drawable.sparkle);
                sparkle.setX(x);
                sparkle.setY(y);
                sparkle.setLayoutParams(new ViewGroup.LayoutParams(width, height));
                sparkle.animate()
                        .setDuration((long) (300 * Math.random() + 300))
                        .scaleX(5)
                        .scaleY(5)
                        .alpha(0)
                        .rotation(Math.random() > 0.5 ? 180 : -180)
                        .x(j < 2 ? (float) (x - width * 9 * Math.random())
                                : (float) (x + width * 9 * Math.random()))
                        .y(i < 2 ? (float) (y - height * 9 * Math.random())
                                : (float) (y + height * 9 * Math.random()))
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                mDialogLayout.removeView(sparkle);
                            }
                        });
                mDialogLayout.addView(sparkle);
            }
        }
    }

}
