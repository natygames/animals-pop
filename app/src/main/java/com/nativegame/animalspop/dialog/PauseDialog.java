package com.nativegame.animalspop.dialog;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.nativegame.animalspop.MyTransition;
import com.nativegame.animalspop.R;
import com.nativegame.animalspop.Utils;
import com.nativegame.animalspop.sound.MySoundEvent;
import com.nativegame.animalspop.sound.MySoundManager;
import com.nativegame.engine.ui.GameActivity;
import com.nativegame.engine.ui.GameDialog;

/**
 * Created by Oscar Liang on 2022/09/18
 */

public class PauseDialog extends GameDialog implements View.OnClickListener,
        MyTransition.TransitionListener {

    private final int mLevel;
    private final MyTransition mTransition;

    private int mSelectedId = R.id.btn_resume;   // We make sure resume the game;

    public PauseDialog(GameActivity activity, int level) {
        super(activity);
        setContentView(R.layout.dialog_pause);
        setRootLayoutResId(R.layout.dialog_game_container);
        setEnterAnimationResId(R.anim.enter_from_center);
        setExitAnimationResId(R.anim.exit_to_center);
        mLevel = level;
        mTransition = new MyTransition(activity);
        mTransition.setListener(this);
        init();
    }

    private void init() {
        // Init level text
        TextView txtLevel = (TextView) findViewById(R.id.txt_level);
        txtLevel.setText(mParent.getResources().getString(R.string.txt_level, mLevel));

        // Init button
        ImageButton btnMusic = (ImageButton) findViewById(R.id.btn_music);
        ImageButton btnSound = (ImageButton) findViewById(R.id.btn_sound);
        ImageButton btnQuit = (ImageButton) findViewById(R.id.btn_quit);
        ImageButton btnResume = (ImageButton) findViewById(R.id.btn_resume);
        btnMusic.setOnClickListener(this);
        btnSound.setOnClickListener(this);
        btnQuit.setOnClickListener(this);
        btnResume.setOnClickListener(this);
        Utils.createButtonEffect(btnMusic);
        Utils.createButtonEffect(btnSound);
        Utils.createButtonEffect(btnQuit);
        Utils.createButtonEffect(btnResume);

        // Init pop up
        Utils.createPopUpEffect(btnMusic);
        Utils.createPopUpEffect(btnSound, 1);
        Utils.createPopUpEffect(btnQuit, 2);

        // Update button
        updateSoundAndMusicButtons();
    }

    private void updateSoundAndMusicButtons() {
        MySoundManager soundManager = (MySoundManager) mParent.getSoundManager();

        // Update music state
        boolean music = soundManager.getMusicStatus();
        ImageButton btnMusic = (ImageButton) findViewById(R.id.btn_music);
        if (music) {
            btnMusic.setBackgroundResource(R.drawable.btn_music_on);
        } else {
            btnMusic.setBackgroundResource(R.drawable.btn_music_off);
        }

        // Update sound state
        boolean sound = soundManager.getSoundStatus();
        ImageButton btnSounds = (ImageButton) findViewById(R.id.btn_sound);
        if (sound) {
            btnSounds.setBackgroundResource(R.drawable.btn_sound_on);
        } else {
            btnSounds.setBackgroundResource(R.drawable.btn_sound_off);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_sound) {
            mParent.getSoundManager().toggleSoundState();
            updateSoundAndMusicButtons();
        } else if (id == R.id.btn_music) {
            mParent.getSoundManager().toggleMusicState();
            updateSoundAndMusicButtons();
        } else if (id == R.id.btn_quit) {
            mParent.getSoundManager().playSound(MySoundEvent.BUTTON_CLICK);
            mSelectedId = id;
            dismiss();
        } else if (id == R.id.btn_resume) {
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
        if (mSelectedId == R.id.btn_quit) {
            mTransition.show();
        } else if (mSelectedId == R.id.btn_resume) {
            resumeGame();
        }
    }

    @Override
    public void onTransition() {
        quitGame();
    }

    public void quitGame() {
        // Override this method to quit the game
    }

    public void resumeGame() {
        // Override this method to resume the game
    }

}
