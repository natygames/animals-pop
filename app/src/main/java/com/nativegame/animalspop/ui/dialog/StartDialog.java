package com.nativegame.animalspop.ui.dialog;

import android.widget.ImageView;
import android.widget.TextView;

import com.nativegame.animalspop.R;
import com.nativegame.animalspop.ui.UIEffect;
import com.nativegame.animalspop.level.MyLevel;
import com.nativegame.animalspop.sound.MySoundEvent;
import com.nativegame.nattyengine.ui.GameActivity;
import com.nativegame.nattyengine.ui.GameDialog;

/**
 * Created by Oscar Liang on 2022/09/18
 */

public class StartDialog extends GameDialog {

    public StartDialog(GameActivity activity, MyLevel level) {
        super(activity);
        setContentView(R.layout.dialog_start);
        setRootLayoutId(R.layout.dialog_game_container);
        setEnterAnimationId(R.anim.enter_from_top);
        setExitAnimationId(R.anim.exit_to_bottom);
        init(level);
    }

    private void init(MyLevel level) {
        // Init level text
        TextView txtLevel = (TextView) findViewById(R.id.txt_target);
        txtLevel.setText(level.mLevelType.getTargetStringId());

        // Init target image
        ImageView imageTarget = (ImageView) findViewById(R.id.image_start_target);
        imageTarget.setImageResource(level.mLevelType.getTargetDrawableId());

        // Init target text
        TextView txtTarget = (TextView) findViewById(R.id.txt_start_target);
        txtTarget.setText(String.valueOf(level.mTarget));

        // Init player image
        ImageView imagePlayer = (ImageView) findViewById(R.id.image_fox);
        imagePlayer.setImageResource(level.mLevelType.getAnimalDrawableId());

        //Init pop effect
        UIEffect.createPopUpEffect(imagePlayer);
        UIEffect.createPopUpEffect(findViewById(R.id.image_fox_bg), 2);

        // Dismiss the dialog after 1500ms
        imageTarget.postDelayed(new Runnable() {
            @Override
            public void run() {
                dismiss();
            }
        }, 1500);
    }

    @Override
    protected void onShow() {
        mParent.getSoundManager().playSound(MySoundEvent.START_GAME);
    }

    @Override
    protected void onDismiss() {
        mParent.getSoundManager().playSound(MySoundEvent.SWEEP_OUT);
    }

    @Override
    protected void onHide() {
        startGame();
    }

    public void startGame() {
        // Override this method to start the game
    }

}
