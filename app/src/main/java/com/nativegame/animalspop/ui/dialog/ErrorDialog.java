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

public class ErrorDialog extends GameDialog implements View.OnClickListener {

    private int mSelectedId = R.id.btn_cancel;   // We make sure to quit the game

    public ErrorDialog(GameActivity activity) {
        super(activity);
        setContentView(R.layout.dialog_error);
        setRootLayoutId(R.layout.dialog_container);
        setEnterAnimationId(R.anim.enter_from_center);
        setExitAnimationId(R.anim.exit_to_center);
        init();
    }

    private void init() {
        // Init button
        ImageButton btnCancel = (ImageButton) findViewById(R.id.btn_cancel);
        ImageButton btnRetry = (ImageButton) findViewById(R.id.btn_retry);
        btnCancel.setOnClickListener(this);
        btnRetry.setOnClickListener(this);
        UIEffect.createButtonEffect(btnCancel);
        UIEffect.createButtonEffect(btnRetry);

        // Init pop up
        UIEffect.createPopUpEffect(btnRetry);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_cancel) {
            mParent.getSoundManager().playSound(MySoundEvent.BUTTON_CLICK);
            mSelectedId = id;
            dismiss();
        } else if (id == R.id.btn_retry) {
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
        if (mSelectedId == R.id.btn_cancel) {
            quit();
        } else if (mSelectedId == R.id.btn_retry) {
            retry();
        }
    }

    public void quit() {
        // Override this method to quit game when no internet connect
    }

    public void retry() {
        // Override this method to try connect again
    }

}
