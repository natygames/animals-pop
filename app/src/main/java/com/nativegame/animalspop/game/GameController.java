package com.nativegame.animalspop.game;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.view.View;

import com.nativegame.animalspop.R;
import com.nativegame.animalspop.dialog.AdExtraMoveDialog;
import com.nativegame.animalspop.dialog.LossDialog;
import com.nativegame.animalspop.dialog.StartDialog;
import com.nativegame.animalspop.dialog.TutorialDialog;
import com.nativegame.animalspop.dialog.WinDialog;
import com.nativegame.animalspop.fragment.MyGameFragment;
import com.nativegame.animalspop.game.bubble.bonus.BonusSystem;
import com.nativegame.animalspop.game.player.PlayerWinText;
import com.nativegame.animalspop.game.player.booster.BoosterManager;
import com.nativegame.animalspop.level.MyLevel;
import com.nativegame.animalspop.sound.MySoundEvent;
import com.nativegame.engine.GameEngine;
import com.nativegame.engine.GameEvent;
import com.nativegame.engine.GameObject;
import com.nativegame.animalspop.game.player.BubbleQueue;
import com.nativegame.animalspop.game.bubble.BubbleSystem;
import com.nativegame.animalspop.game.player.BasicBubble;
import com.nativegame.engine.particles.ParticleSystem;

/**
 * Created by Oscar Liang on 2022/09/18
 */

public class GameController extends GameObject {

    private static final String PREFS_NAME = "prefs_tutorial";

    private final MyGameFragment mParentFragment;
    private final BubbleSystem mBubbleSystem;
    private final BonusSystem mBonusSystem;
    private final BasicBubble mBasicBubble;
    private final BoosterManager mBoosterManager;
    private final PlayerWinText mWinText;
    private final ParticleSystem mLeftConfetti;
    private final ParticleSystem mRightConfetti;

    private GameControllerState mState;
    private long mTotalMillis;

    private boolean mExtraLives = true;

    public GameController(GameEngine gameEngine, MyGameFragment mGameFragment) {
        mParentFragment = mGameFragment;
        mBubbleSystem = new BubbleSystem(gameEngine);
        mBonusSystem = new BonusSystem(gameEngine);
        mBasicBubble = new BasicBubble(mBubbleSystem, gameEngine);
        mBoosterManager = new BoosterManager(mBubbleSystem, gameEngine);
        mWinText = new PlayerWinText(gameEngine);
        // Init the confetti
        int[] confettiId = new int[]{
                R.drawable.confetti_blue,
                R.drawable.confetti_green,
                R.drawable.confetti_pink,
                R.drawable.confetti_yellow};
        mLeftConfetti = new ParticleSystem(gameEngine, confettiId, 30)
                .setDuration(1500)
                .setEmissionRate(30)
                .setEmissionPositionX(0)
                .setEmissionRangeY(gameEngine.mScreenHeight / 3f, gameEngine.mScreenHeight * 3 / 4f)
                .setSpeedX(1000, 1500)
                .setSpeedY(-4000, -3000)
                .setAccelerationX(-2, 0)
                .setAccelerationY(5, 10)
                .setInitialRotation(0, 360)
                .setRotationSpeed(-720, 720)
                .setAlpha(255, 0, 500)
                .setScale(0.75f, 0, 1000);
        mRightConfetti = new ParticleSystem(gameEngine, confettiId, 30)
                .setDuration(1500)
                .setEmissionRate(30)
                .setEmissionPositionX(gameEngine.mScreenWidth)
                .setEmissionRangeY(gameEngine.mScreenHeight / 3f, gameEngine.mScreenHeight * 3 / 4f)
                .setSpeedX(-1500, -1000)
                .setSpeedY(-4000, -3000)
                .setAccelerationX(0, 2)
                .setAccelerationY(5, 10)
                .setInitialRotation(0, 360)
                .setRotationSpeed(-720, 720)
                .setAlpha(255, 0, 500)
                .setScale(0.75f, 0, 1000);
    }

    @Override
    public void startGame(GameEngine gameEngine) {
        mState = GameControllerState.SHIFT_BUBBLE;
        mTotalMillis = 0;
    }

    @Override
    public void addToGameEngine(GameEngine gameEngine, int layer) {
        super.addToGameEngine(gameEngine, layer);
        mLeftConfetti.addToGameEngine(gameEngine, 4);
        mRightConfetti.addToGameEngine(gameEngine, 4);
    }

    @Override
    public void onUpdate(long elapsedMillis, GameEngine gameEngine) {
        switch (mState) {
            case WAITING:
                // Nothing to be done here, we wait for the game event
                break;
            case SHIFT_BUBBLE:
                mTotalMillis += elapsedMillis;
                if (mTotalMillis >= 1000) {
                    mBubbleSystem.shiftBubble();   // Shift down one time when start
                    mState = GameControllerState.START_INTRO;
                    mTotalMillis = 0;
                }
                break;
            case START_INTRO:
                if (mBubbleSystem.isShifting()) {
                    return;
                }
                mBasicBubble.init(gameEngine);
                showStartDialog(gameEngine);
                mState = GameControllerState.WAITING;
                break;
            case PLAYER_WIN:
                if (mBubbleSystem.isShifting()) {
                    return;
                }
                mTotalMillis += elapsedMillis;
                if (mTotalMillis >= 1000) {
                    // Remove the remaining bubble and booster
                    mBasicBubble.removeFromGameEngine(gameEngine);
                    mBubbleSystem.clearBubble();
                    hideBooster();
                    // Show win text and play sound
                    mWinText.init(gameEngine);
                    createConfetti(2000);
                    gameEngine.mSoundManager.playSound(MySoundEvent.PLAYER_WIN);
                    mState = GameControllerState.BONUS_TIME;
                    mTotalMillis = 0;
                }
                break;
            case PLAYER_LOSS:
                if (mBubbleSystem.isShifting()) {
                    return;
                }
                mTotalMillis += elapsedMillis;
                if (mTotalMillis >= 500) {
                    // Check player has extra lives
                    if (mExtraLives) {
                        showExtraMoveDialog(gameEngine);
                        mExtraLives = false;
                        mState = GameControllerState.WAITING;
                        mTotalMillis = 0;
                        return;
                    }
                    // Show loss dialog
                    showLossDialog(gameEngine);
                    mState = GameControllerState.WAITING;
                    mTotalMillis = 0;
                }
                break;
            case BONUS_TIME:
                mTotalMillis += elapsedMillis;
                if (mTotalMillis >= 1200) {
                    // Add bubble left in queue and start bonus time
                    BubbleQueue bubbleQueue = mBasicBubble.getBubbleQueue();
                    while (bubbleQueue.hasBubble()) {
                        mBonusSystem.addBonusBubble(gameEngine, bubbleQueue.popBubble());
                    }
                    mBonusSystem.addToGameEngine(gameEngine, 0);   // Start bonus time automatically
                    mState = GameControllerState.WAITING;
                    mTotalMillis = 0;
                }
                break;
            case NAVIGATE_WIN_DIALOG:
                mTotalMillis += elapsedMillis;
                if (mTotalMillis >= 1000) {
                    // Remove the player
                    gameEngine.onGameEvent(MyGameEvent.REMOVE_PLAYER);
                    // Show win dialog
                    showWinDialog(gameEngine);
                    mState = GameControllerState.WAITING;
                    mTotalMillis = 0;
                }
                break;
        }
    }

    private void showStartDialog(GameEngine gameEngine) {
        mParentFragment.getGameActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                StartDialog startDialog = new StartDialog(mParentFragment.getGameActivity(), (MyLevel) gameEngine.mLevel) {
                    @Override
                    public void startGame() {
                        mBasicBubble.setEnable(true);
                        mBoosterManager.setEnable(true);
                        // Show tutorial dialog if need
                        if (((MyLevel) gameEngine.mLevel).mLevelTutorial != null) {
                            showTutorialDialog(gameEngine);
                        }
                    }
                };
                mParentFragment.showDialog(startDialog);
                // Also show the move text
                mParentFragment.getGameActivity().findViewById(R.id.txt_move).setVisibility(View.VISIBLE);
            }
        });
    }

    private void showTutorialDialog(GameEngine gameEngine) {
        // Check the tutorial has been showed yet
        SharedPreferences prefs = mParentFragment.getGameActivity()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String prefsKey = "level" + gameEngine.mLevel.mLevel;
        if (!prefs.getBoolean(prefsKey, true)) {
            return;
        } else {
            // Save it to preferences, so the player won't see it again
            prefs.edit()
                    .putBoolean(prefsKey, false)
                    .apply();
        }

        mParentFragment.getGameActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TutorialDialog tutorialDialog = new TutorialDialog(mParentFragment.getGameActivity(), (MyLevel) gameEngine.mLevel) {
                    @Override
                    public void updateBooster() {
                        mBoosterManager.initBoosterText();
                    }
                };
                mParentFragment.showDialog(tutorialDialog);
            }
        });
    }

    private void showExtraMoveDialog(GameEngine gameEngine) {
        mParentFragment.getGameActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AdExtraMoveDialog extraMoveDialog = new AdExtraMoveDialog(mParentFragment.getGameActivity()) {
                    @Override
                    public void showAd() {
                        mParentFragment.pauseGameAndShowRewardedAd();
                    }

                    @Override
                    public void quit() {
                        gameEngine.onGameEvent(MyGameEvent.GAME_OVER);
                    }
                };
                mParentFragment.showDialog(extraMoveDialog);
            }
        });
    }

    private void showWinDialog(GameEngine gameEngine) {
        mParentFragment.getGameActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                WinDialog winDialog = new WinDialog(mParentFragment.getGameActivity(), (MyLevel) gameEngine.mLevel) {
                    @Override
                    public void stopGame() {
                        gameEngine.stopGame();
                    }
                };
                mParentFragment.showDialog(winDialog);
            }
        });
    }

    private void showLossDialog(GameEngine gameEngine) {
        mParentFragment.getGameActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LossDialog lossDialog = new LossDialog(mParentFragment.getGameActivity()) {
                    @Override
                    public void stopGame() {
                        gameEngine.stopGame();
                    }
                };
                mParentFragment.showDialog(lossDialog);
            }
        });
    }

    @Override
    public void onDraw(Canvas canvas) {
        // This game object does not draw anything
    }

    @Override
    public void onGameEvent(GameEvent gameEvent) {
        switch ((MyGameEvent) gameEvent) {
            case BOOSTER_CONSUMED:
                // Update the booster
                mBoosterManager.consumeBooster();
                break;
            case EMIT_CONFETTI:
                createConfetti(800);
                break;
            case GAME_WIN:
                // Prevent player input
                mBasicBubble.setEnable(false);
                mBoosterManager.setEnable(false);
                mState = GameControllerState.PLAYER_WIN;
                break;
            case GAME_OVER:
                // Prevent player input
                mBasicBubble.setEnable(false);
                mBoosterManager.setEnable(false);
                mState = GameControllerState.PLAYER_LOSS;
                break;
            case SHOW_WIN_DIALOG:
                hideState();
                mState = GameControllerState.NAVIGATE_WIN_DIALOG;
                break;
            case ADD_EXTRA_MOVE:
                // Resume player input
                mBasicBubble.setEnable(true);
                mBoosterManager.setEnable(true);
                break;
        }
    }

    private void createConfetti(long duration) {
        mLeftConfetti.setTotalDuration(duration).emit();
        mRightConfetti.setTotalDuration(duration).emit();
    }

    private void hideBooster() {
        mParentFragment.getGameActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mParentFragment.getGameActivity().findViewById(R.id.layout_booster).setVisibility(View.INVISIBLE);
            }
        });
    }

    private void hideState() {
        mParentFragment.getGameActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mParentFragment.getGameActivity().findViewById(R.id.layout_state).setVisibility(View.INVISIBLE);
                mParentFragment.getGameActivity().findViewById(R.id.txt_move).setVisibility(View.INVISIBLE);
            }
        });
    }

}
