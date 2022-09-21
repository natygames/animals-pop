package com.nativegame.animalspop.dialog;

import android.widget.ImageView;
import android.widget.TextView;

import com.nativegame.animalspop.R;
import com.nativegame.animalspop.Utils;
import com.nativegame.animalspop.level.MyLevel;
import com.nativegame.animalspop.sound.MySoundEvent;
import com.nativegame.engine.ui.GameActivity;
import com.nativegame.engine.ui.GameDialog;

/**
 * Created by Oscar Liang on 2022/09/18
 */

public class StartDialog extends GameDialog {

    public StartDialog(GameActivity activity, MyLevel level) {
        super(activity);
        setContentView(R.layout.dialog_start);
        setRootLayoutResId(R.layout.dialog_game_container);
        setEnterAnimationResId(R.anim.enter_from_top);
        setExitAnimationResId(R.anim.exit_to_bottom);
        init(level);
    }

    private void init(MyLevel level) {
        // Init level text
        TextView txtLevel = (TextView) findViewById(R.id.txt_target);
        txtLevel.setText(level.mLevelType.getStringResId());

        // Init target image
        ImageView imageTarget = (ImageView) findViewById(R.id.image_start_target);
        imageTarget.setImageResource(level.mLevelType.getImageResId());

        // Init target text
        TextView txtTarget = (TextView) findViewById(R.id.txt_start_target);
        txtTarget.setText(String.valueOf(level.mTarget));

        // Init player image
        ImageView imagePlayer = (ImageView) findViewById(R.id.image_fox);
        imagePlayer.setImageResource(level.mLevelType.getPlayerResId());

        //Init pop effect
        Utils.createPopUpEffect(imagePlayer);
        Utils.createPopUpEffect(findViewById(R.id.image_fox_bg), 2);

        // Dismiss the dialog after 1500ms
        imageTarget.postDelayed(new Runnable() {
            @Override
            public void run() {
                dismiss();
            }
        }, 1500);
    }

    @Override
    public void show() {
        super.show();
        mParent.getSoundManager().playSound(MySoundEvent.START_GAME);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        mParent.getSoundManager().playSound(MySoundEvent.SWEEP_02);
    }

    @Override
    protected void onDismiss() {
        startGame();
    }

    public void startGame() {
        // Override this method to start the game
    }

}
