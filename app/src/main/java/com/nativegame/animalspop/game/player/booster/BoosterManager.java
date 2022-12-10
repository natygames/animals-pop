package com.nativegame.animalspop.game.player.booster;

import android.app.Activity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.nativegame.animalspop.MainActivity;
import com.nativegame.animalspop.R;
import com.nativegame.animalspop.item.Item;
import com.nativegame.animalspop.ui.UIEffect;
import com.nativegame.animalspop.database.DatabaseHelper;
import com.nativegame.animalspop.game.bubble.BubbleSystem;
import com.nativegame.nattyengine.Game;

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

    public BoosterManager(BubbleSystem bubbleSystem, Game game) {
        mActivity = game.getGameActivity();
        mDatabaseHelper = ((MainActivity) mActivity).getDatabaseHelper();

        // Init booster button
        mBtnColorBubble = (ImageButton) mActivity.findViewById(R.id.btn_color_bubble);
        mBtnFireBubble = (ImageButton) mActivity.findViewById(R.id.btn_fire_bubble);
        mBtnBombBubble = (ImageButton) mActivity.findViewById(R.id.btn_bomb_bubble);

        // Init booster num text
        mTxtColorBubble = (TextView) mActivity.findViewById(R.id.txt_color_bubble);
        mTxtFireBubble = (TextView) mActivity.findViewById(R.id.txt_fire_bubble);
        mTxtBombBubble = (TextView) mActivity.findViewById(R.id.txt_bomb_bubble);

        // Init booster bubble
        mColorBubble = new ColorBubble(bubbleSystem, game);
        mFireBubble = new FireBubble(bubbleSystem, game);
        mBombBubble = new BombBubble(bubbleSystem, game);

        init();
    }

    private void init() {
        // Init button listener
        mBtnColorBubble.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mColorBubbleNum == 0 || !mColorBubble.getEnable()) {
                    return;
                }
                if (mBooster == null) {
                    mColorBubble.addToGame();
                    mBooster = Booster.COLOR_BUBBLE;
                    lockButton();
                } else if (mBooster == Booster.COLOR_BUBBLE) {
                    mColorBubble.removeFromGame();
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
                    mFireBubble.addToGame();
                    mBooster = Booster.FIRE_BUBBLE;
                    lockButton();
                } else if (mBooster == Booster.FIRE_BUBBLE) {
                    // Remove the fire bubble
                    mFireBubble.removeFromGame();
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
                    mBombBubble.addToGame();
                    mBooster = Booster.BOMB_BUBBLE;
                    lockButton();
                } else if (mBooster == Booster.BOMB_BUBBLE) {
                    mBombBubble.removeFromGame();
                    unlockButton();
                    mBooster = null;
                }
            }
        });

        // Init button effect
        UIEffect.createButtonEffect(mBtnColorBubble);
        UIEffect.createButtonEffect(mBtnFireBubble);
        UIEffect.createButtonEffect(mBtnBombBubble);

        // Update state
        UIEffect.clearColorFilter(mBtnColorBubble);
        UIEffect.clearColorFilter(mBtnFireBubble);
        UIEffect.clearColorFilter(mBtnBombBubble);
        UIEffect.clearColorFilter(mTxtColorBubble);
        UIEffect.clearColorFilter(mTxtFireBubble);
        UIEffect.clearColorFilter(mTxtBombBubble);

        initBoosterText();
    }

    public void initBoosterText() {
        // Init booster num from db
        mColorBubbleNum = mDatabaseHelper.getItemNum(Item.COLOR_BALL);
        mFireBubbleNum = mDatabaseHelper.getItemNum(Item.FIREBALL);
        mBombBubbleNum = mDatabaseHelper.getItemNum(Item.BOMB);

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
                mDatabaseHelper.updateItemNum(Item.COLOR_BALL, mColorBubbleNum);
                break;
            case FIRE_BUBBLE:
                mFireBubbleNum--;
                mDatabaseHelper.updateItemNum(Item.FIREBALL, mFireBubbleNum);
                break;
            case BOMB_BUBBLE:
                mBombBubbleNum--;
                mDatabaseHelper.updateItemNum(Item.BOMB, mBombBubbleNum);
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
                UIEffect.createColorFilter(mBtnBombBubble);
                UIEffect.createColorFilter(mBtnColorBubble);
                UIEffect.createColorFilter(mTxtBombBubble);
                UIEffect.createColorFilter(mTxtColorBubble);
                mBtnBombBubble.setEnabled(false);
                mBtnColorBubble.setEnabled(false);
                break;
            case BOMB_BUBBLE:
                UIEffect.createColorFilter(mBtnFireBubble);
                UIEffect.createColorFilter(mBtnColorBubble);
                UIEffect.createColorFilter(mTxtFireBubble);
                UIEffect.createColorFilter(mTxtColorBubble);
                mBtnFireBubble.setEnabled(false);
                mBtnColorBubble.setEnabled(false);
                break;
            case COLOR_BUBBLE:
                UIEffect.createColorFilter(mBtnBombBubble);
                UIEffect.createColorFilter(mBtnFireBubble);
                UIEffect.createColorFilter(mTxtBombBubble);
                UIEffect.createColorFilter(mTxtFireBubble);
                mBtnBombBubble.setEnabled(false);
                mBtnFireBubble.setEnabled(false);
                break;
        }
    }

    private void unlockButton() {
        switch (mBooster) {
            case FIRE_BUBBLE:
                UIEffect.clearColorFilter(mBtnBombBubble);
                UIEffect.clearColorFilter(mBtnColorBubble);
                UIEffect.clearColorFilter(mTxtBombBubble);
                UIEffect.clearColorFilter(mTxtColorBubble);
                mBtnBombBubble.setEnabled(true);
                mBtnColorBubble.setEnabled(true);
                break;
            case BOMB_BUBBLE:
                UIEffect.clearColorFilter(mBtnFireBubble);
                UIEffect.clearColorFilter(mBtnColorBubble);
                UIEffect.clearColorFilter(mTxtFireBubble);
                UIEffect.clearColorFilter(mTxtColorBubble);
                mBtnFireBubble.setEnabled(true);
                mBtnColorBubble.setEnabled(true);
                break;
            case COLOR_BUBBLE:
                UIEffect.clearColorFilter(mBtnBombBubble);
                UIEffect.clearColorFilter(mBtnFireBubble);
                UIEffect.clearColorFilter(mTxtBombBubble);
                UIEffect.clearColorFilter(mTxtFireBubble);
                mBtnBombBubble.setEnabled(true);
                mBtnFireBubble.setEnabled(true);
                break;
        }
    }

}
