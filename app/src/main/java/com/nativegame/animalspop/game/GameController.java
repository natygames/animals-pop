package com.nativegame.animalspop.game;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;

import com.nativegame.animalspop.AdManager;
import com.nativegame.animalspop.MainActivity;
import com.nativegame.animalspop.R;
import com.nativegame.animalspop.ui.dialog.AdExtraMoveDialog;
import com.nativegame.animalspop.ui.dialog.ErrorDialog;
import com.nativegame.animalspop.ui.dialog.LossDialog;
import com.nativegame.animalspop.ui.dialog.StartDialog;
import com.nativegame.animalspop.ui.dialog.TutorialDialog;
import com.nativegame.animalspop.ui.dialog.WinDialog;
import com.nativegame.animalspop.game.bonus.BonusSystem;
import com.nativegame.animalspop.game.effect.WinTextEffect;
import com.nativegame.animalspop.game.player.booster.BoosterManager;
import com.nativegame.animalspop.level.MyLevel;
import com.nativegame.animalspop.sound.MySoundEvent;
import com.nativegame.nattyengine.Game;
import com.nativegame.nattyengine.event.GameEvent;
import com.nativegame.nattyengine.entity.GameObject;
import com.nativegame.animalspop.game.player.BubbleQueue;
import com.nativegame.animalspop.game.bubble.BubbleSystem;
import com.nativegame.animalspop.game.player.BasicBubble;
import com.nativegame.nattyengine.entity.particles.ParticleSystem;
import com.nativegame.nattyengine.ui.GameActivity;

/**
 * Created by Oscar Liang on 2022/09/18
 */

public class GameController extends GameObject implements AdManager.AdRewardListener {

    private static final String PREFS_NAME = "prefs_tutorial";

    private final GameActivity mParent;
    private final BubbleSystem mBubbleSystem;
    private final BonusSystem mBonusSystem;
    private final BasicBubble mBasicBubble;
    private final BoosterManager mBoosterManager;
    private final WinTextEffect mWinText;
    private final ParticleSystem mLeftConfetti;
    private final ParticleSystem mRightConfetti;

    private GameControllerState mState;
    private long mTotalMillis;

    private boolean mExtraLives = true;

    public GameController(Game game) {
        super(game);
        mParent = game.getGameActivity();
        mBubbleSystem = new BubbleSystem(game);
        mBonusSystem = new BonusSystem(game);
        mBasicBubble = new BasicBubble(mBubbleSystem, game);
        mBoosterManager = new BoosterManager(mBubbleSystem, game);
        mWinText = new WinTextEffect(game);

        // Init the confetti
        int[] confettiId = new int[]{
                R.drawable.confetti_blue,
                R.drawable.confetti_green,
                R.drawable.confetti_pink,
                R.drawable.confetti_yellow};
        mLeftConfetti = new ParticleSystem(game, confettiId, 50)
                .setDurationPerParticle(1500)
                .setEmissionRate(30)
                .setEmissionPositionX(0)
                .setEmissionRangeY(game.getScreenHeight() / 3f, game.getScreenHeight() * 3 / 4f)
                .setSpeedX(1000, 1500)
                .setSpeedY(-4000, -3000)
                .setAccelerationX(-2, 0)
                .setAccelerationY(5, 10)
                .setInitialRotation(0, 360)
                .setRotationSpeed(-720, 720)
                .setAlpha(255, 0, 500)
                .setScale(0.75f, 0, 1000)
                .setLayer(Layer.EFFECT_LAYER);
        mRightConfetti = new ParticleSystem(game, confettiId, 50)
                .setDurationPerParticle(1500)
                .setEmissionRate(30)
                .setEmissionPositionX(game.getScreenWidth())
                .setEmissionRangeY(game.getScreenHeight() / 3f, game.getScreenHeight() * 3 / 4f)
                .setSpeedX(-1500, -1000)
                .setSpeedY(-4000, -3000)
                .setAccelerationX(0, 2)
                .setAccelerationY(5, 10)
                .setInitialRotation(0, 360)
                .setRotationSpeed(-720, 720)
                .setAlpha(255, 0, 500)
                .setScale(0.75f, 0, 1000)
                .setLayer(Layer.EFFECT_LAYER);
    }

    @Override
    public void onStart() {
        mState = GameControllerState.SHIFT_BUBBLE;
        mTotalMillis = 0;
        mLeftConfetti.addToGame();
        mRightConfetti.addToGame();
    }

    @Override
    public void onUpdate(long elapsedMillis) {
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
                mBasicBubble.addToGame();
                showStartDialog();
                mState = GameControllerState.WAITING;
                break;
            case PLAYER_WIN:
                if (mBubbleSystem.isShifting()) {
                    return;
                }
                mTotalMillis += elapsedMillis;
                if (mTotalMillis >= 1000) {
                    // Remove the remaining bubble and booster
                    mBasicBubble.removeFromGame();
                    mBubbleSystem.clearBubble();
                    hideBooster();
                    // Show win text and play sound
                    mWinText.activate();
                    createConfetti(2000);
                    mGame.getSoundManager().playSound(MySoundEvent.PLAYER_WIN);
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
                        showExtraMoveDialog();
                        mExtraLives = false;
                        mState = GameControllerState.WAITING;
                        mTotalMillis = 0;
                        return;
                    }
                    // Show loss dialog
                    showLossDialog();
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
                        mBonusSystem.addBonusBubble(bubbleQueue.popBubble());
                    }
                    mBonusSystem.addToGame();   // Start bonus time automatically
                    mState = GameControllerState.WAITING;
                    mTotalMillis = 0;
                }
                break;
        }
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
                // Hide state and show win dialog
                hideState();
                showWinDialog();
                break;
            case ADD_EXTRA_MOVE:
                // Resume player input
                mBasicBubble.setEnable(true);
                mBoosterManager.setEnable(true);
                break;
        }
    }

    private void createConfetti(long duration) {
        mLeftConfetti.setDuration(duration).emit();
        mRightConfetti.setDuration(duration).emit();
    }

    private void hideBooster() {
        mParent.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mParent.findViewById(R.id.layout_booster).setVisibility(View.INVISIBLE);
            }
        });
    }

    private void hideState() {
        mParent.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mParent.findViewById(R.id.layout_state).setVisibility(View.INVISIBLE);
                mParent.findViewById(R.id.txt_move).setVisibility(View.INVISIBLE);
            }
        });
    }

    private void showStartDialog() {
        mParent.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                StartDialog startDialog = new StartDialog(mParent, (MyLevel) mGame.getLevel()) {
                    @Override
                    public void startGame() {
                        mBasicBubble.setEnable(true);
                        mBoosterManager.setEnable(true);
                        // Show tutorial dialog if need
                        if (((MyLevel) mGame.getLevel()).mLevelTutorial != null) {
                            showTutorialDialog();
                        }
                    }
                };
                mParent.showDialog(startDialog);
                // Also show the move text
                mParent.findViewById(R.id.txt_move).setVisibility(View.VISIBLE);
            }
        });
    }

    private void showTutorialDialog() {
        // Check the tutorial has been showed yet
        SharedPreferences prefs = mParent
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String prefsKey = "level" + mGame.getLevel().mLevel;
        if (!prefs.getBoolean(prefsKey, true)) {
            return;
        } else {
            // Save it to preferences, so the player won't see it again
            prefs.edit()
                    .putBoolean(prefsKey, false)
                    .apply();
        }

        mParent.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TutorialDialog tutorialDialog = new TutorialDialog(mParent,
                        (MyLevel) mGame.getLevel()) {
                    @Override
                    public void updateBooster() {
                        mBoosterManager.initBoosterText();
                    }
                };
                mParent.showDialog(tutorialDialog);
            }
        });
    }

    private void showWinDialog() {
        mParent.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                WinDialog winDialog = new WinDialog(mParent, (MyLevel) mGame.getLevel()) {
                    @Override
                    public void stopGame() {
                        // We stop the game here, or the pause dialog will show
                        mGame.stop();
                    }
                };
                mParent.showDialog(winDialog);
            }
        });
    }

    private void showLossDialog() {
        mParent.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LossDialog lossDialog = new LossDialog(mParent) {
                    @Override
                    public void stopGame() {
                        // We stop the game here, or the pause dialog will show
                        mGame.stop();
                    }
                };
                mParent.showDialog(lossDialog);
            }
        });
    }

    private void showExtraMoveDialog() {
        mParent.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AdExtraMoveDialog extraMoveDialog = new AdExtraMoveDialog(mParent) {
                    @Override
                    public void showAd() {
                        showRewardedAd();
                    }

                    @Override
                    public void quit() {
                        gameEvent(MyGameEvent.GAME_OVER);
                    }
                };
                mParent.showDialog(extraMoveDialog);
            }
        });
    }

    private void showRewardedAd() {
        // Show rewarded ad
        AdManager adManager = ((MainActivity) mParent).getAdManager();
        adManager.setListener(this);
        boolean isConnect = adManager.showRewardAd();
        // Check connection
        if (isConnect) {
            // Pause the game when loading ad, or the pause dialog will show
            mGame.getGameEngine().pauseGame();
        } else {
            // Show error dialog if no internet connect
            ErrorDialog dialog = new ErrorDialog(mParent) {
                @Override
                public void retry() {
                    adManager.requestAd();
                    showRewardedAd();
                }

                @Override
                public void quit() {
                    gameEvent(MyGameEvent.GAME_OVER);
                }
            };
            mParent.showDialog(dialog);
        }
    }

    @Override
    public void onEarnReward() {
        // The ad will pause the game, so we resume it
        mGame.getGameEngine().resumeGame();
        gameEvent(MyGameEvent.ADD_EXTRA_MOVE);
    }

    @Override
    public void onLossReward() {
        // The ad will pause the game, so we resume it
        mGame.getGameEngine().resumeGame();
        gameEvent(MyGameEvent.GAME_OVER);
    }

}
