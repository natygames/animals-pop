package com.nativegame.animalspop.timer;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Oscar Liang on 2022/09/18
 */

public class WheelTimer {

    private static final String PREFS_NAME = "prefs_setting";
    private static final String LAST_PLAY_TIME_PREF_KEY = "last_play_time";
    private static final long WHEEL_CD_TIME = 86400000;   // 1 day = 86400000ms

    private final SharedPreferences mPrefs;

    public WheelTimer(Activity activity) {
        mPrefs = activity.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public boolean isWheelReady() {
        // Load last time player spin wheel
        long lastTime = mPrefs.getLong(LAST_PLAY_TIME_PREF_KEY, 0);

        // Check is wheel ready
        if (lastTime == 0 || (System.currentTimeMillis() - lastTime > WHEEL_CD_TIME)) {
            return true;
        } else {
            return false;
        }
    }

    public void setWheelTime(long time) {
        // Save current time
        mPrefs.edit()
                .putLong(LAST_PLAY_TIME_PREF_KEY, time)
                .apply();
    }

}
