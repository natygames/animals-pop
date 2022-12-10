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

public class ExitDialog extends GameDialog implements View.OnClickListener {

    private int mSelectedId;

    public ExitDialog(GameActivity activity) {
        super(activity);
        setContentView(R.layout.dialog_exit);
        setRootLayoutId(R.layout.dialog_container);
        setEnterAnimationId(R.anim.enter_from_center);
        setExitAnimationId(R.anim.exit_to_center);
        init();
    }

    private void init() {
        // Init button
        ImageButton btnExit = (ImageButton) findViewById(R.id.btn_exit);
        ImageButton btnCancel = (ImageButton) findViewById(R.id.btn_cancel);
        btnExit.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        UIEffect.createButtonEffect(btnExit);
        UIEffect.createButtonEffect(btnCancel);

        // Init pop up
        UIEffect.createPopUpEffect(btnExit);
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
    protected void onShow() {
        mParent.getSoundManager().playSound(MySoundEvent.SWEEP_IN);
    }

    @Override
    protected void onDismiss() {
        mParent.getSoundManager().playSound(MySoundEvent.SWEEP_OUT);
    }

    @Override
    protected void onHide() {
        if (mSelectedId == R.id.btn_exit) {
            exit();
        }
    }

    public void exit() {
        // Override this method to exit the app
    }

}
