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

public class ExitDialog extends GameDialog implements View.OnClickListener {

    private int mSelectedId;

    public ExitDialog(GameActivity activity) {
        super(activity);
        setContentView(R.layout.dialog_exit);
        setRootLayoutResId(R.layout.dialog_container);
        setEnterAnimationResId(R.anim.enter_from_center);
        setExitAnimationResId(R.anim.exit_to_center);
        init();
    }

    private void init() {
        // Init button
        ImageButton btnExit = (ImageButton) findViewById(R.id.btn_exit);
        ImageButton btnCancel = (ImageButton) findViewById(R.id.btn_cancel);
        btnExit.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        Utils.createButtonEffect(btnExit);
        Utils.createButtonEffect(btnCancel);

        // Init pop up
        Utils.createPopUpEffect(btnExit);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_cancel) {
            mParent.getSoundManager().playSound(MySoundEvent.BUTTON_CLICK);
            dismiss();
        } else if (id == R.id.btn_exit) {
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
        if (mSelectedId == R.id.btn_exit) {
            exit();
        }
    }

    public void exit() {
        // Override this method to exit the app
    }

}
