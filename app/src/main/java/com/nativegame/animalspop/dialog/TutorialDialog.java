package com.nativegame.animalspop.dialog;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.nativegame.animalspop.MainActivity;
import com.nativegame.animalspop.R;
import com.nativegame.animalspop.Utils;
import com.nativegame.animalspop.database.DatabaseHelper;
import com.nativegame.animalspop.level.LevelTutorial;
import com.nativegame.animalspop.level.MyLevel;
import com.nativegame.animalspop.sound.MySoundEvent;
import com.nativegame.engine.ui.GameActivity;
import com.nativegame.engine.ui.GameDialog;

/**
 * Created by Oscar Liang on 2022/09/18
 */

public class TutorialDialog extends GameDialog implements View.OnClickListener {

    private final LevelTutorial mTutorial;

    public TutorialDialog(GameActivity activity, MyLevel level) {
        super(activity);
        setContentView(R.layout.dialog_tutorial);
        setRootLayoutResId(R.layout.dialog_game_container);
        setEnterAnimationResId(R.anim.enter_from_center);
        setExitAnimationResId(R.anim.exit_to_center);
        mTutorial = level.mLevelTutorial;
        init();
    }

    private void init() {
        // Init tutorial text
        TextView txtTutorial = (TextView) findViewById(R.id.txt_tutorial);
        txtTutorial.setText(mTutorial.getStringResId());

        // Init tutorial image
        ImageView imageTutorial = (ImageView) findViewById(R.id.image_tutorial);
        imageTutorial.setImageResource(mTutorial.getImageResId());

        // Init button
        ImageButton btnPlay = (ImageButton) findViewById(R.id.btn_play);
        btnPlay.setOnClickListener(this);
        Utils.createButtonEffect(btnPlay);

        // Init pop up
        Utils.createPopUpEffect(btnPlay);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_play) {
            mParent.getSoundManager().playSound(MySoundEvent.BUTTON_CLICK);
            dismiss();
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        mParent.getSoundManager().playSound(MySoundEvent.SWEEP_02);
    }

    @Override
    protected void onDismiss() {
        switch (mTutorial) {
            case COLOR_BUBBLE:
                // Add one color bubble to db
                addBooster(DatabaseHelper.ITEM_COLOR_BALL);
                updateBooster();
                // Add color bubble through button
                mParent.findViewById(R.id.btn_color_bubble).performClick();
                break;
            case FIRE_BUBBLE:
                // Add one fire bubble to db
                addBooster(DatabaseHelper.ITEM_FIREBALL);
                updateBooster();
                // Add fire bubble through button
                mParent.findViewById(R.id.btn_fire_bubble).performClick();
                break;
            case BOMB_BUBBLE:
                // Add one bomb bubble to db
                addBooster(DatabaseHelper.ITEM_BOMB);
                updateBooster();
                // Add bomb bubble through button
                mParent.findViewById(R.id.btn_bomb_bubble).performClick();
                break;
        }
    }

    private void addBooster(String name) {
        DatabaseHelper databaseHelper = ((MainActivity) mParent).getDatabaseHelper();
        int numBooster = databaseHelper.getItemNum(name);
        databaseHelper.updateItemNum(name, numBooster + 1);
    }

    public void updateBooster() {
        // Override this method to update booster ui
    }

}
