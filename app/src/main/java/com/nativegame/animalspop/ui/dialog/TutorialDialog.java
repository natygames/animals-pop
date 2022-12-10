package com.nativegame.animalspop.ui.dialog;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.nativegame.animalspop.MainActivity;
import com.nativegame.animalspop.R;
import com.nativegame.animalspop.item.Item;
import com.nativegame.animalspop.ui.UIEffect;
import com.nativegame.animalspop.database.DatabaseHelper;
import com.nativegame.animalspop.level.LevelTutorial;
import com.nativegame.animalspop.level.MyLevel;
import com.nativegame.animalspop.sound.MySoundEvent;
import com.nativegame.nattyengine.ui.GameActivity;
import com.nativegame.nattyengine.ui.GameDialog;

/**
 * Created by Oscar Liang on 2022/09/18
 */

public class TutorialDialog extends GameDialog implements View.OnClickListener {

    private final LevelTutorial mTutorial;

    public TutorialDialog(GameActivity activity, MyLevel level) {
        super(activity);
        setContentView(R.layout.dialog_tutorial);
        setRootLayoutId(R.layout.dialog_game_container);
        setEnterAnimationId(R.anim.enter_from_center);
        setExitAnimationId(R.anim.exit_to_center);
        mTutorial = level.mLevelTutorial;
        init();
    }

    private void init() {
        // Init tutorial text
        TextView txtTutorial = (TextView) findViewById(R.id.txt_tutorial);
        txtTutorial.setText(mTutorial.getStringId());

        // Init tutorial image
        ImageView imageTutorial = (ImageView) findViewById(R.id.image_tutorial);
        imageTutorial.setImageResource(mTutorial.getDrawableId());

        // Init button
        ImageButton btnPlay = (ImageButton) findViewById(R.id.btn_play);
        btnPlay.setOnClickListener(this);
        UIEffect.createButtonEffect(btnPlay);

        // Init pop up
        UIEffect.createPopUpEffect(btnPlay);
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
    protected void onShow() {
        mParent.getSoundManager().playSound(MySoundEvent.SWEEP_IN);
    }

    @Override
    protected void onDismiss() {
        mParent.getSoundManager().playSound(MySoundEvent.SWEEP_OUT);
    }

    @Override
    protected void onHide() {
        switch (mTutorial) {
            case COLOR_BUBBLE:
                // Add one color bubble to db
                addBooster(Item.COLOR_BALL);
                updateBooster();
                // Add color bubble through button
                mParent.findViewById(R.id.btn_color_bubble).performClick();
                break;
            case FIRE_BUBBLE:
                // Add one fire bubble to db
                addBooster(Item.FIREBALL);
                updateBooster();
                // Add fire bubble through button
                mParent.findViewById(R.id.btn_fire_bubble).performClick();
                break;
            case BOMB_BUBBLE:
                // Add one bomb bubble to db
                addBooster(Item.BOMB);
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
