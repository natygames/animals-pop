package com.nativegame.animalspop.ui.dialog;

import android.view.View;
import android.widget.ImageButton;

import com.nativegame.animalspop.R;
import com.nativegame.animalspop.ui.UIEffect;
import com.nativegame.animalspop.sound.MySoundEvent;
import com.nativegame.nattyengine.ui.GameActivity;
import com.nativegame.nattyengine.ui.GameDialog;

/**
 * Created by Oscar Liang on 2022/09/18
 */

public class AdExtraMoveDialog extends GameDialog implements View.OnClickListener {

    private int mSelectedId = R.id.btn_quit;   // We make sure to quit the game

    public AdExtraMoveDialog(GameActivity activity) {
        super(activity);
        setContentView(R.layout.dialog_ad_extra_move);
        setRootLayoutId(R.layout.dialog_container);
        setEnterAnimationId(R.anim.enter_from_center);
        setExitAnimationId(R.anim.exit_to_center);
        init();
    }

    private void init() {
        // Init button
        ImageButton btnWatchAd = (ImageButton) findViewById(R.id.btn_watch_ad);
        ImageButton btnQuit = (ImageButton) findViewById(R.id.btn_quit);
        btnWatchAd.setOnClickListener(this);
        btnQuit.setOnClickListener(this);
        UIEffect.createButtonEffect(btnWatchAd);
        UIEffect.createButtonEffect(btnQuit);

        // Init pop up
        UIEffect.createPopUpEffect(btnWatchAd);
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
    protected void onShow() {
        mParent.getSoundManager().playSound(MySoundEvent.SWEEP_IN);
    }

    @Override
    protected void onDismiss() {
        mParent.getSoundManager().playSound(MySoundEvent.SWEEP_OUT);
    }

    @Override
    protected void onHide() {
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
