package com.nativegame.animalspop.dialog;

import android.widget.ImageView;

import com.nativegame.animalspop.MainActivity;
import com.nativegame.animalspop.MyTransition;
import com.nativegame.animalspop.R;
import com.nativegame.animalspop.Utils;
import com.nativegame.animalspop.sound.MySoundEvent;
import com.nativegame.engine.ui.GameActivity;
import com.nativegame.engine.ui.GameDialog;

/**
 * Created by Oscar Liang on 2022/09/18
 */

public class LossDialog extends GameDialog implements MyTransition.TransitionListener {

    private final MyTransition mTransition;

    public LossDialog(GameActivity activity) {
        super(activity);
        setContentView(R.layout.dialog_loss);
        setRootLayoutResId(R.layout.dialog_game_container);
        setEnterAnimationResId(R.anim.enter_from_top);
        setExitAnimationResId(R.anim.exit_to_bottom);
        mTransition = new MyTransition(activity);
        mTransition.setListener(this);
        init();
    }

    private void init() {
        // Init pop effect
        ImageView imageFox = (ImageView) findViewById(R.id.image_fox);
        Utils.createPopUpEffect(imageFox);

        // Reduce one live
        ((MainActivity) mParent).getLivesTimer().reduceLive();

        // Dismiss the dialog after 1500ms
        imageFox.postDelayed(new Runnable() {
            @Override
            public void run() {
                dismiss();
            }
        }, 1500);
    }

    @Override
    public void show() {
        super.show();
        mParent.getSoundManager().playSound(MySoundEvent.PLAYER_LOSS);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        mParent.getSoundManager().playSound(MySoundEvent.SWEEP_02);
    }

    @Override
    protected void onDismiss() {
        mTransition.show();
    }

    @Override
    public void onTransition() {
        // Stop the game and navigate to fragment
        stopGame();
        mParent.navigateBack();
    }

    public void stopGame() {
        // Override this method to stop the game
    }

}
