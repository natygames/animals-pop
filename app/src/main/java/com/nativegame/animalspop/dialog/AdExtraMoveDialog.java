package com.nativegame.animalspop.dialog;

import android.view.View;
import android.widget.ImageButton;

import com.nativegame.animalspop.R;
import com.nativegame.animalspop.Utils;
import com.nativegame.animalspop.sound.MySoundEvent;
import com.nativegame.engine.ui.GameActivity;
import com.nativegame.engine.ui.GameDialog;

/**
 * Created by Oscar Liang on 2022/09/18
 */

public class AdExtraMoveDialog extends GameDialog implements View.OnClickListener {

    private int mSelectedId = R.id.btn_quit;   // We make sure to quit the game

    public AdExtraMoveDialog(GameActivity activity) {
        super(activity);
        setContentView(R.layout.dialog_ad_extra_move);
        setRootLayoutResId(R.layout.dialog_container);
        setEnterAnimationResId(R.anim.enter_from_center);
        setExitAnimationResId(R.anim.exit_to_center);
        init();
    }

    private void init() {
        // Init button
        ImageButton btnWatchAd = (ImageButton) findViewById(R.id.btn_watch_ad);
        ImageButton btnQuit = (ImageButton) findViewById(R.id.btn_quit);
        btnWatchAd.setOnClickListener(this);
        btnQuit.setOnClickListener(this);
        Utils.createButtonEffect(btnWatchAd);
        Utils.createButtonEffect(btnQuit);

        // Init pop up
        Utils.createPopUpEffect(btnWatchAd);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_quit) {
            mParent.getSoundManager().playSound(MySoundEvent.BUTTON_CLICK);
            mSelectedId = id;
            dismiss();
        } else if (id == R.id.btn_watch_ad) {
            mParent.getSoundManager().playSound(MySoundEvent.BUTTON_CLICK);
            mSelectedId = id;
            dismiss();
        }
    }

    @Override
    public void show() {
        super.show();
        mParent.getSoundManager().playSound(MySoundEvent.SWEEP_01);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        mParent.getSoundManager().playSound(MySoundEvent.SWEEP_02);
    }

    @Override
    protected void onDismiss() {
        if (mSelectedId == R.id.btn_watch_ad) {
            showAd();
        } else if (mSelectedId == R.id.btn_quit) {
            quit();
        }
    }

    public void showAd() {
        // Override this method to show ad in game
    }

    public void quit() {
        // Override this method to quit the game
    }

}
