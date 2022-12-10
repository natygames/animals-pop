package com.nativegame.animalspop.ui.dialog;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.nativegame.animalspop.R;
import com.nativegame.animalspop.ui.UIEffect;
import com.nativegame.animalspop.sound.MySoundEvent;
import com.nativegame.nattyengine.ui.GameActivity;
import com.nativegame.nattyengine.ui.GameDialog;

/**
 * Created by Oscar Liang on 2022/09/18
 */

public class NewBoosterDialog extends GameDialog implements View.OnClickListener {

    private final int mDrawableResId;

    public NewBoosterDialog(GameActivity activity, int drawableResId) {
        super(activity);
        setContentView(R.layout.dialog_new_booster);
        setRootLayoutId(R.layout.dialog_container);
        mDrawableResId = drawableResId;
        init();
    }

    private void init() {
        // Init button
        ImageButton btnNext = (ImageButton) findViewById(R.id.btn_next);
        btnNext.setOnClickListener(this);
        UIEffect.createButtonEffect(btnNext);

        // Init booster image
        ImageView imageBooster = (ImageView) findViewById(R.id.image_booster);
        imageBooster.setImageResource(mDrawableResId);

        // Init bg animation
        ImageView imageBg = (ImageView) findViewById(R.id.image_booster_bg);
        Animation animation = AnimationUtils.loadAnimation(mParent, R.anim.light_rotate);
        imageBg.startAnimation(animation);

        // Init pop up
        UIEffect.createPopUpEffect(findViewById(R.id.txt_new_booster), 1);
        UIEffect.createPopUpEffect(btnNext, 2);
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
    protected void onShow() {
        mParent.getSoundManager().playSound(MySoundEvent.PLAYER_WIN);
    }

    @Override
    protected void onDismiss() {
        mParent.getSoundManager().playSound(MySoundEvent.SWEEP_OUT);
    }

    @Override
    public void onHide() {
        showDialog();
    }

    public void showDialog() {
        // Override this method to show other dialog
    }

}
