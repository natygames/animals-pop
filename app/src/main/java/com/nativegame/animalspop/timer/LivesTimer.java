package com.nativegame.animalspop.timer;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.widget.TextView;

import com.nativegame.animalspop.R;

import java.util.Locale;

/**
 * Created by Oscar Liang on 2022/09/18
 */

public class LivesTimer {

    private static final String PREFS_NAME = "prefs_lives";
    private static final String LIVES_PREF_KEY = "lives";
    private static final String MILLIS_LEFT_PREF_KEY = "millis_left";
    private static final String END_TIME_PREF_KEY = "end_time";

    private static final long LIVES_CD = 1200000;   // 20 min = 1200000ms
    private static final int LIVES_MAX = 5;

    private final Activity mActivity;
    private final SharedPreferences mPrefs;

    private TextView mTxtLives;
    private TextView mTxtTime;
    private CountDownTimer mCountDownTimer;

    private int mLivesNum;
    private long mTimeLeftInMillis;
    private long mEndTime;

    public LivesTimer(Activity activity) {
        mActivity = activity;
        mPrefs = activity.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void start() {
        mTxtLives = mActivity.findViewById(R.id.txt_lives);
        mTxtTime = mActivity.findViewById(R.id.txt_lives_time);

        mLivesNum = mPrefs.getInt(LIVES_PREF_KEY, LIVES_MAX);
        if (mLivesNum == LIVES_MAX) {
            updateLivesText();
            return;
        }

        // We get the previous time and calculate the time remaining
        mEndTime = mPrefs.getLong(END_TIME_PREF_KEY, 0);
        mTimeLeftInMillis = mEndTime - System.currentTimeMillis();

        // The timer has finish one cd time
        if (mTimeLeftInMillis < 0) {
            // We first calculate how much time pass
            // and how many lives obtain
            long timePass = -mTimeLeftInMillis;
            long timeRemaining = timePass % LIVES_CD;
            int livesPass = 1 + (int) (timePass / LIVES_CD);

            // We then add back to the previous lives
            // and subtract the remaining time to cd time
            mLivesNum += livesPass;
            mTimeLeftInMillis = LIVES_CD - timeRemaining;

            if (mLivesNum >= LIVES_MAX) {
                mLivesNum = LIVES_MAX;
                mTimeLeftInMillis = 0;
                mEndTime = 0;
            } else {
                startTimer();
            }

        } else {
            // The timer hasn't finish one cd time, so we just resume timer
            startTimer();
        }

        updateLivesText();
    }

    public void stop() {
        // Save it to preferences
        mPrefs.edit()
                .putInt(LIVES_PREF_KEY, mLivesNum)
                .putLong(MILLIS_LEFT_PREF_KEY, mTimeLeftInMillis)
                .putLong(END_TIME_PREF_KEY, mEndTime)
                .apply();

        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
    }

    public void addLive() {
        if (mLivesNum < LIVES_MAX) {
            mLivesNum++;
        }

        // Save it to preferences
        mPrefs.edit()
                .putInt(LIVES_PREF_KEY, mLivesNum)
                .apply();
    }

    public void reduceLive() {
        mLivesNum--;
        // Check is any current time left, so we won't override the current state
        if (mTimeLeftInMillis == 0L) {
            // We set them as start value
            mTimeLeftInMillis = LIVES_CD;
            mEndTime = System.currentTimeMillis() + mTimeLeftInMillis;
        }

        // Save it to preferences
        mPrefs.edit()
                .putInt(LIVES_PREF_KEY, mLivesNum)
                .putLong(MILLIS_LEFT_PREF_KEY, mTimeLeftInMillis)
                .putLong(END_TIME_PREF_KEY, mEndTime)
                .apply();
    }

    public boolean isEnoughLives() {
        return mLivesNum > 0;
    }

    public boolean isLivesFull() {
        return mLivesNum == LIVES_MAX;
    }

    private void updateLivesText() {
        mTxtLives.setText(String.valueOf(mLivesNum));
        if (mLivesNum == 0) {
            mTxtLives.setBackgroundResource(R.drawable.lives_lock);
        } else {
            mTxtLives.setBackgroundResource(R.drawable.lives);
            if (mLivesNum == LIVES_MAX) {
                mTxtTime.setText(mActivity.getResources().getString(R.string.txt_lives_full));
            }
        }
    }

    private void startTimer() {
        mEndTime = System.currentTimeMillis() + mTimeLeftInMillis;
        // Stop the previous timer
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
        // Init the timer
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                // Check if live is full
                if (mLivesNum < LIVES_MAX) {
                    mLivesNum++;

                    if (mLivesNum != LIVES_MAX) {
                        // Live is not full yet, so we start timer recursively
                        mTimeLeftInMillis = LIVES_CD;
                        startTimer();
                    } else {
                        // Live is full, so we reset the time
                        mTimeLeftInMillis = 0;  // Somehow it won't be 0 (Actually a little bigger than 1000)
                        mEndTime = 0;
                        mTxtTime.setText(mActivity.getResources().getString(R.string.txt_lives_full));
                    }
                }

                updateLivesText();
            }
        }.start();
    }

    private void updateCountDownText() {
        int minutes = (int) (mTimeLeftInMillis / 1000) / 60;
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;
        // Convert to time format
        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        // Update time text
        mTxtTime.setText(timeLeftFormatted);
    }

}
