package com.nativegame.animalspop.ui.dialog;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.nativegame.animalspop.R;
import com.nativegame.animalspop.ui.UIEffect;
import com.nativegame.animalspop.sound.MySoundEvent;
import com.nativegame.animalspop.sound.MySoundManager;
import com.nativegame.nattyengine.ui.GameActivity;
import com.nativegame.nattyengine.ui.GameDialog;

/**
 * Created by Oscar Liang on 2022/09/18
 */

public class SettingDialog extends GameDialog implements View.OnClickListener {

    private final SharedPreferences mPrefs;
    private boolean mHintEnable;

    private static final String PREFS_NAME = "prefs_setting";
    private static final String HINT_PREF_KEY = "hint";

    public SettingDialog(GameActivity activity) {
        super(activity);
        setContentView(R.layout.dialog_setting);
        setRootLayoutId(R.layout.dialog_container);
        setEnterAnimationId(R.anim.enter_from_center);
        setExitAnimationId(R.anim.exit_to_center);
        mPrefs = activity.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        mHintEnable = mPrefs.getBoolean(HINT_PREF_KEY, true);
        init();
    }

    private void init() {
        // Init button
        ImageButton btnMusic = (ImageButton) findViewById(R.id.btn_music);
        btnMusic.setOnClickListener(this);
        UIEffect.createButtonEffect(btnMusic);
        ImageButton btnSound = (ImageButton) findViewById(R.id.btn_sound);
        btnSound.setOnClickListener(this);
        UIEffect.createButtonEffect(btnSound);
        ImageButton btnCancel = (ImageButton) findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(this);
        UIEffect.createButtonEffect(btnCancel);

        // Init switch
        ImageButton btnHint = (ImageButton) findViewById(R.id.switch_hint_thumb);
        btnHint.setOnClickListener(this);
        UIEffect.createButtonEffect(btnHint);

        // Init pop up
        UIEffect.createPopUpEffect(btnMusic);
        UIEffect.createPopUpEffect(btnSound, 1);
        UIEffect.createPopUpEffect(btnHint, 2);
        UIEffect.createPopUpEffect(findViewById(R.id.switch_hint_track), 2);

        updateSoundAndMusicButtons();
        btnHint.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateHintButton();
            }
        }, 10);
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

    private void updateHintButton() {
        // Update hint state
        ImageView imageThumb = (ImageView) findViewById(R.id.switch_hint_thumb);
        ImageView imageTrack = (ImageView) findViewById(R.id.switch_hint_track);
        if (mHintEnable) {
            imageThumb.setX(imageTrack.getX() + imageTrack.getWidth() / 2f);
            imageTrack.setImageResource(R.drawable.switch_track_on);
        } else {
            imageThumb.setX(imageTrack.getX());
            imageTrack.setImageResource(R.drawable.switch_track_off);
        }
    }

    private void toggleHintStatus() {
        mHintEnable = !mHintEnable;
        // Save it to preferences
        mPrefs.edit()
                .putBoolean(HINT_PREF_KEY, mHintEnable)
                .apply();
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
        } else if (view.getId() == R.id.switch_hint_thumb) {
            mParent.getSoundManager().playSound(MySoundEvent.BUTTON_CLICK);
            toggleHintStatus();
            updateHintButton();
        } else if (id == R.id.btn_cancel) {
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

}
