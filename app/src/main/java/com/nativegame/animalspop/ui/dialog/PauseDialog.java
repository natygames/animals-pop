package com.nativegame.animalspop.ui.dialog;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.nativegame.animalspop.ui.TransitionEffect;
import com.nativegame.animalspop.R;
import com.nativegame.animalspop.ui.UIEffect;
import com.nativegame.animalspop.sound.MySoundEvent;
import com.nativegame.animalspop.sound.MySoundManager;
import com.nativegame.nattyengine.ui.GameActivity;
import com.nativegame.nattyengine.ui.GameDialog;

/**
 * Created by Oscar Liang on 2022/09/18
 */

public class PauseDialog extends GameDialog implements View.OnClickListener,
        TransitionEffect.OnTransitionListener {

    private final int mLevel;
    private final TransitionEffect mTransitionEffect;

    private int mSelectedId = R.id.btn_resume;   // We make sure resume the game;

    public PauseDialog(GameActivity activity, int level) {
        super(activity);
        setContentView(R.layout.dialog_pause);
        setRootLayoutId(R.layout.dialog_game_container);
        setEnterAnimationId(R.anim.enter_from_center);
        setExitAnimationId(R.anim.exit_to_center);
        mLevel = level;
        mTransitionEffect = new TransitionEffect(activity);
        mTransitionEffect.setListener(this);
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
        UIEffect.createButtonEffect(btnMusic);
        UIEffect.createButtonEffect(btnSound);
        UIEffect.createButtonEffect(btnQuit);
        UIEffect.createButtonEffect(btnResume);

        // Init pop up
        UIEffect.createPopUpEffect(btnMusic);
        UIEffect.createPopUpEffect(btnSound, 1);
        UIEffect.createPopUpEffect(btnQuit, 2);

        // Update button
        updateSoundAndMusicButtons();
    }

    private void updateSoundAndMusicButtons() {
        MySoundManager soundManager = (MySoundManager) mParent.getSoundManager();

        // Update music state
        boolean music = soundManager.getMusicState();
        ImageButton btnMusic = (ImageButton) findViewById(R.id.btn_music);
        if (music) {
            btnMusic.setBackgroundResource(R.drawable.btn_music_on);
        } else {
            btnMusic.setBackgroundResource(R.drawable.btn_music_off);
        }

        // Update sound state
        boolean sound = soundManager.getSoundState();
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
            mParent.getSoundManager().switchSoundState();
            updateSoundAndMusicButtons();
        } else if (id == R.id.btn_music) {
            mParent.getSoundManager().switchMusicState();
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
    protected void onShow() {
        mParent.getSoundManager().playSound(MySoundEvent.SWEEP_IN);
    }

    @Override
    protected void onDismiss() {
        mParent.getSoundManager().playSound(MySoundEvent.SWEEP_OUT);
    }

    @Override
    protected void onHide() {
        if (mSelectedId == R.id.btn_quit) {
            mTransitionEffect.show();
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
