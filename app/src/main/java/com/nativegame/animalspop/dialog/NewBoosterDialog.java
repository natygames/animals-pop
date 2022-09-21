package com.nativegame.animalspop.dialog;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.nativegame.animalspop.R;
import com.nativegame.animalspop.Utils;
import com.nativegame.animalspop.sound.MySoundEvent;
import com.nativegame.engine.ui.GameActivity;
import com.nativegame.engine.ui.GameDialog;

/**
 * Created by Oscar Liang on 2022/09/18
 */

public class NewBoosterDialog extends GameDialog implements View.OnClickListener {

    private final int mDrawableResId;

    public NewBoosterDialog(GameActivity activity, int drawableResId) {
        super(activity);
        setContentView(R.layout.dialog_new_booster);
        setRootLayoutResId(R.layout.dialog_container);
        setEnterAnimationResId(R.anim.enter_from_center);
        setExitAnimationResId(R.anim.exit_to_center);
        mDrawableResId = drawableResId;
        init();
    }

    private void init() {
        // Init button
        ImageButton btnNext = (ImageButton) findViewById(R.id.btn_next);
        btnNext.setOnClickListener(this);
        Utils.createButtonEffect(btnNext);

        // Init booster image
        ImageView imageBooster = (ImageView) findViewById(R.id.image_booster);
        imageBooster.setImageResource(mDrawableResId);

        // Init bg animation
        ImageView imageBg = (ImageView) findViewById(R.id.image_booster_bg);
        Animation animation = AnimationUtils.loadAnimation(mParent, R.anim.light_rotate);
        imageBg.startAnimation(animation);

        // Init pop up
        Utils.createPopUpEffect(btnNext, 2);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_next) {
            mParent.getSoundManager().playSound(MySoundEvent.BUTTON_CLICK);
            dismiss();
        }
    }

    @Override
    public void show() {
        super.show();
        mParent.getSoundManager().playSound(MySoundEvent.PLAYER_WIN);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        mParent.getSoundManager().playSound(MySoundEvent.SWEEP_02);
    }

    @Override
    public void onDismiss() {
        showDialog();
    }

    public void showDialog() {
        // Override this method to show other dialog
    }

}
