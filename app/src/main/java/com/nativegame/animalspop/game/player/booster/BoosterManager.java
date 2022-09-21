package com.nativegame.animalspop.game.player.booster;

import android.app.Activity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.nativegame.animalspop.MainActivity;
import com.nativegame.animalspop.R;
import com.nativegame.animalspop.Utils;
import com.nativegame.animalspop.database.DatabaseHelper;
import com.nativegame.animalspop.game.bubble.BubbleSystem;
import com.nativegame.engine.GameEngine;

/**
 * Created by Oscar Liang on 2022/09/18
 */

public class BoosterManager {

    private final Activity mActivity;
    private final DatabaseHelper mDatabaseHelper;

    // Booster button
    private final ImageButton mBtnColorBubble;
    private final ImageButton mBtnFireBubble;
    private final ImageButton mBtnBombBubble;

    // Booster text
    private final TextView mTxtColorBubble;
    private final TextView mTxtFireBubble;
    private final TextView mTxtBombBubble;

    // Booster
    private final ColorBubble mColorBubble;
    private final FireBubble mFireBubble;
    private final BombBubble mBombBubble;

    // Booster number
    private int mColorBubbleNum;
    private int mFireBubbleNum;
    private int mBombBubbleNum;

    private Booster mBooster;   // A pointer to current booster using

    private enum Booster {
        COLOR_BUBBLE,
        FIRE_BUBBLE,
        BOMB_BUBBLE
    }

    public BoosterManager(BubbleSystem bubbleSystem, GameEngine gameEngine) {
        mActivity = gameEngine.mActivity;
        mDatabaseHelper = ((MainActivity) mActivity).getDatabaseHelper();

        // Init booster button
        mBtnColorBubble = (ImageButton) gameEngine.mActivity.findViewById(R.id.btn_color_bubble);
        mBtnFireBubble = (ImageButton) gameEngine.mActivity.findViewById(R.id.btn_fire_bubble);
        mBtnBombBubble = (ImageButton) gameEngine.mActivity.findViewById(R.id.btn_bomb_bubble);

        // Init booster num text
        mTxtColorBubble = (TextView) gameEngine.mActivity.findViewById(R.id.txt_color_bubble);
        mTxtFireBubble = (TextView) gameEngine.mActivity.findViewById(R.id.txt_fire_bubble);
        mTxtBombBubble = (TextView) gameEngine.mActivity.findViewById(R.id.txt_bomb_bubble);

        // Init booster bubble
        mColorBubble = new ColorBubble(bubbleSystem, gameEngine);
        mFireBubble = new FireBubble(bubbleSystem, gameEngine);
        mBombBubble = new BombBubble(bubbleSystem, gameEngine);

        init(gameEngine);
    }

    private void init(GameEngine gameEngine) {
        // Init button listener
        mBtnColorBubble.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mColorBubbleNum == 0 || !mColorBubble.getEnable()) {
                    return;
                }
                if (mBooster == null) {
                    mColorBubble.init(gameEngine);
                    mBooster = Booster.COLOR_BUBBLE;
                    lockButton();
                } else if (mBooster == Booster.COLOR_BUBBLE) {
                    mColorBubble.removeFromGameEngine(gameEngine);
                    unlockButton();
                    mBooster = null;
                }
            }
        });
        mBtnFireBubble.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mFireBubbleNum == 0 || !mFireBubble.getEnable()) {
                    return;
                }
                if (mBooster == null) {
                    // Init the fire bubble
                    mFireBubble.init(gameEngine);
                    mBooster = Booster.FIRE_BUBBLE;
                    lockButton();
                } else if (mBooster == Booster.FIRE_BUBBLE) {
                    // Remove the fire bubble
                    mFireBubble.removeFromGameEngine(gameEngine);
                    unlockButton();
                    mBooster = null;
                }
            }
        });
        mBtnBombBubble.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mBombBubbleNum == 0 || !mBombBubble.getEnable()) {
                    return;
                }
                if (mBooster == null) {
                    mBombBubble.init(gameEngine);
                    mBooster = Booster.BOMB_BUBBLE;
                    lockButton();
                } else if (mBooster == Booster.BOMB_BUBBLE) {
                    mBombBubble.removeFromGameEngine(gameEngine);
                    unlockButton();
                    mBooster = null;
                }
            }
        });

        // Init button effect
        Utils.createButtonEffect(mBtnColorBubble);
        Utils.createButtonEffect(mBtnFireBubble);
        Utils.createButtonEffect(mBtnBombBubble);

        // Update state
        Utils.clearColorFilter(mBtnColorBubble);
        Utils.clearColorFilter(mBtnFireBubble);
        Utils.clearColorFilter(mBtnBombBubble);
        Utils.clearColorFilter(mTxtColorBubble);
        Utils.clearColorFilter(mTxtFireBubble);
        Utils.clearColorFilter(mTxtBombBubble);

        initBoosterText();
    }

    public void initBoosterText() {
        // Init booster num from db
        mColorBubbleNum = mDatabaseHelper.getItemNum(DatabaseHelper.ITEM_COLOR_BALL);
        mFireBubbleNum = mDatabaseHelper.getItemNum(DatabaseHelper.ITEM_FIREBALL);
        mBombBubbleNum = mDatabaseHelper.getItemNum(DatabaseHelper.ITEM_BOMB);

        // Init booster num text
        mTxtColorBubble.setText(String.valueOf(mColorBubbleNum));
        mTxtFireBubble.setText(String.valueOf(mFireBubbleNum));
        mTxtBombBubble.setText(String.valueOf(mBombBubbleNum));
    }

    public void setEnable(boolean enable) {
        // We set the booster enable here, since booster may not available in engine
        mFireBubble.setEnable(enable);
        mBombBubble.setEnable(enable);
        mColorBubble.setEnable(enable);
    }

    public void consumeBooster() {
        updateBoosterNum();
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateBoosterText();
            }
        });
    }

    private void updateBoosterNum() {
        switch (mBooster) {
            case COLOR_BUBBLE:
                mColorBubbleNum--;
                mDatabaseHelper.updateItemNum(DatabaseHelper.ITEM_COLOR_BALL, mColorBubbleNum);
                break;
            case FIRE_BUBBLE:
                mFireBubbleNum--;
                mDatabaseHelper.updateItemNum(DatabaseHelper.ITEM_FIREBALL, mFireBubbleNum);
                break;
            case BOMB_BUBBLE:
                mBombBubbleNum--;
                mDatabaseHelper.updateItemNum(DatabaseHelper.ITEM_BOMB, mBombBubbleNum);
                break;
        }
    }

    private void updateBoosterText() {
        switch (mBooster) {
            case COLOR_BUBBLE:
                mTxtColorBubble.setText(String.valueOf(mColorBubbleNum));
                unlockButton();
                break;
            case FIRE_BUBBLE:
                mTxtFireBubble.setText(String.valueOf(mFireBubbleNum));
                unlockButton();
                break;
            case BOMB_BUBBLE:
                mTxtBombBubble.setText(String.valueOf(mBombBubbleNum));
                unlockButton();
                break;
        }
        mBooster = null;
    }

    private void lockButton() {
        switch (mBooster) {
            case FIRE_BUBBLE:
                Utils.createColorFilter(mBtnBombBubble);
                Utils.createColorFilter(mBtnColorBubble);
                Utils.createColorFilter(mTxtBombBubble);
                Utils.createColorFilter(mTxtColorBubble);
                mBtnBombBubble.setEnabled(false);
                mBtnColorBubble.setEnabled(false);
                break;
            case BOMB_BUBBLE:
                Utils.createColorFilter(mBtnFireBubble);
                Utils.createColorFilter(mBtnColorBubble);
                Utils.createColorFilter(mTxtFireBubble);
                Utils.createColorFilter(mTxtColorBubble);
                mBtnFireBubble.setEnabled(false);
                mBtnColorBubble.setEnabled(false);
                break;
            case COLOR_BUBBLE:
                Utils.createColorFilter(mBtnBombBubble);
                Utils.createColorFilter(mBtnFireBubble);
                Utils.createColorFilter(mTxtBombBubble);
                Utils.createColorFilter(mTxtFireBubble);
                mBtnBombBubble.setEnabled(false);
                mBtnFireBubble.setEnabled(false);
                break;
        }
    }

    private void unlockButton() {
        switch (mBooster) {
            case FIRE_BUBBLE:
                Utils.clearColorFilter(mBtnBombBubble);
                Utils.clearColorFilter(mBtnColorBubble);
                Utils.clearColorFilter(mTxtBombBubble);
                Utils.clearColorFilter(mTxtColorBubble);
                mBtnBombBubble.setEnabled(true);
                mBtnColorBubble.setEnabled(true);
                break;
            case BOMB_BUBBLE:
                Utils.clearColorFilter(mBtnFireBubble);
                Utils.clearColorFilter(mBtnColorBubble);
                Utils.clearColorFilter(mTxtFireBubble);
                Utils.clearColorFilter(mTxtColorBubble);
                mBtnFireBubble.setEnabled(true);
                mBtnColorBubble.setEnabled(true);
                break;
            case COLOR_BUBBLE:
                Utils.clearColorFilter(mBtnBombBubble);
                Utils.clearColorFilter(mBtnFireBubble);
                Utils.clearColorFilter(mTxtBombBubble);
                Utils.clearColorFilter(mTxtFireBubble);
                mBtnBombBubble.setEnabled(true);
                mBtnFireBubble.setEnabled(true);
                break;
        }
    }

}
