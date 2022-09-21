package com.nativegame.engine.sound;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.SoundPool;

import java.util.HashMap;

public abstract class SoundManager {

    private static final int MAX_STREAMS = 10;
    private static final float DEFAULT_MUSIC_VOLUME = 0.6f;

    private static final String PREFS_NAME = "prefs_sound";
    private static final String SOUNDS_PREF_KEY = "sound";
    private static final String MUSIC_PREF_KEY = "music";

    private final Context mContext;
    private final SharedPreferences mPrefs;
    private MediaPlayer mMusicPlayer;
    private SoundPool mSoundPool;

    private final HashMap<SoundEvent, Integer> mSoundsMap = new HashMap<>();

    private boolean mSoundEnabled;
    private boolean mMusicEnabled;

    protected SoundManager(Context context) {
        mContext = context;
        mPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        mSoundEnabled = mPrefs.getBoolean(SOUNDS_PREF_KEY, true);
        mMusicEnabled = mPrefs.getBoolean(MUSIC_PREF_KEY, true);
        initSoundPool();
    }

    private void initSoundPool() {
        // Use SoundPool.Builder on API 21
        // http://developer.android.com/reference/android/media/SoundPool.Builder.html
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();
        mSoundPool = new SoundPool.Builder()
                .setAudioAttributes(audioAttributes)
                .setMaxStreams(MAX_STREAMS)
                .build();
    }

    //--------------------------------------------------------
    // Methods to change state of sound
    //--------------------------------------------------------
    public void loadSound(SoundEvent event, int soundResId) {
        int soundId = mSoundPool.load(mContext, soundResId, 1);
        mSoundsMap.put(event, soundId);
    }

    public void unloadSound() {
        mSoundPool.release();
        mSoundPool = null;
        mSoundsMap.clear();
    }

    public void playSound(SoundEvent event) {
        if (!mSoundEnabled) {
            return;
        }
        Integer soundId = mSoundsMap.get(event);
        if (soundId != null) {
            // Left Volume, Right Volume, priority (0 == lowest), loop (0 == no) and rate (1.0 normal playback rate)
            mSoundPool.play(soundId, 1.0f, 1.0f, 0, 0, 1.0f);
        }
    }
    //========================================================

    //--------------------------------------------------------
    // Methods to change state of music
    //--------------------------------------------------------
    public void loadMusic(int musicId) {
        // Important to not reuse it. It can be on a strange state
        mMusicPlayer = MediaPlayer.create(mContext, musicId);
        mMusicPlayer.setLooping(true);
        mMusicPlayer.setVolume(DEFAULT_MUSIC_VOLUME, DEFAULT_MUSIC_VOLUME);
        if (mMusicEnabled) {
            mMusicPlayer.start();
        }
    }

    public void pauseMusic() {
        if (mMusicPlayer != null && mMusicEnabled) {
            mMusicPlayer.pause();
        }
    }

    public void resumeMusic() {
        if (mMusicPlayer != null && mMusicEnabled) {
            mMusicPlayer.start();
        }
    }

    public void unloadMusic() {
        if (mMusicPlayer != null) {
            mMusicPlayer.stop();
            mMusicPlayer.release();
            mMusicPlayer = null;
        }
    }
    //========================================================

    public void toggleMusicState() {
        mMusicEnabled = !mMusicEnabled;
        if (mMusicEnabled) {
            if (mMusicPlayer != null) {
                mMusicPlayer.start();
            }
        } else {
            if (mMusicPlayer != null) {
                mMusicPlayer.pause();
            }
        }
        // Save it to preferences
        mPrefs.edit()
                .putBoolean(MUSIC_PREF_KEY, mMusicEnabled)
                .apply();
    }

    public void toggleSoundState() {
        mSoundEnabled = !mSoundEnabled;
        // Save it to preferences
        mPrefs.edit()
                .putBoolean(SOUNDS_PREF_KEY, mSoundEnabled)
                .apply();
    }

    public boolean getMusicStatus() {
        return mMusicEnabled;
    }

    public boolean getSoundStatus() {
        return mSoundEnabled;
    }

}
