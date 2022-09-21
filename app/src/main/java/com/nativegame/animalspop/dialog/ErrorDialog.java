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

public class ErrorDialog extends GameDialog implements View.OnClickListener {

    private int mSelectedId = R.id.btn_cancel;   // We make sure to quit the game

    public ErrorDialog(GameActivity activity) {
        super(activity);
        setContentView(R.layout.dialog_error);
        setRootLayoutResId(R.layout.dialog_container);
        setEnterAnimationResId(R.anim.enter_from_center);
        setExitAnimationResId(R.anim.exit_to_center);
        init();
    }

    private void init() {
        // Init button
        ImageButton btnCancel = (ImageButton) findViewById(R.id.btn_cancel);
        ImageButton btnRetry = (ImageButton) findViewById(R.id.btn_retry);
        btnCancel.setOnClickListener(this);
        btnRetry.setOnClickListener(this);
        Utils.createButtonEffect(btnCancel);
        Utils.createButtonEffect(btnRetry);

        // Init pop up
        Utils.createPopUpEffect(btnRetry);
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
    protected void onDismiss() {
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
