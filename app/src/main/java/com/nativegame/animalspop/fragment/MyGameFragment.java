package com.nativegame.animalspop.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nativegame.animalspop.AdManager;
import com.nativegame.animalspop.MainActivity;
import com.nativegame.animalspop.R;
import com.nativegame.animalspop.Utils;
import com.nativegame.animalspop.dialog.ErrorDialog;
import com.nativegame.animalspop.dialog.PauseDialog;
import com.nativegame.animalspop.game.MyGameEvent;
import com.nativegame.animalspop.game.counter.combo.ComboCounter;
import com.nativegame.animalspop.game.counter.ProgressBarCounter;
import com.nativegame.animalspop.game.player.Player;
import com.nativegame.animalspop.game.counter.target.CollectTargetCounter;
import com.nativegame.animalspop.game.counter.target.PopTargetCounter;
import com.nativegame.animalspop.game.counter.ScoreCounter;
import com.nativegame.animalspop.level.MyLevel;
import com.nativegame.animalspop.level.LevelType;
import com.nativegame.animalspop.sound.MySoundEvent;
import com.nativegame.engine.GameEngine;
import com.nativegame.engine.GameView;
import com.nativegame.animalspop.game.counter.MoveCounter;
import com.nativegame.animalspop.game.GameController;
import com.nativegame.animalspop.game.input.BasicInputController;
import com.nativegame.engine.ui.GameFragment;

/**
 * Created by Oscar Liang on 2022/09/18
 */

public class MyGameFragment extends GameFragment implements View.OnClickListener,
        AdManager.AdRewardListener {

    private static final String LEVEL = "level";

    private GameEngine mGameEngine;
    private int mLevel;

    public MyGameFragment() {
        // Required empty public constructor
    }

    public static MyGameFragment newInstance(int param) {
        MyGameFragment fragment = new MyGameFragment();
        Bundle args = new Bundle();
        args.putInt(LEVEL, param);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mLevel = getArguments().getInt(LEVEL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_game, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Init pause button
        ImageButton btnPause = (ImageButton) view.findViewById(R.id.btn_pause);
        Utils.createButtonEffect(btnPause);
        btnPause.setOnClickListener(this);
    }

    @Override
    protected void onLayoutCreated() {
        startGame();
    }

    private void startGame() {
        // Init level
        MyLevel level = (MyLevel) getGameActivity().getLevelManager().getLevel(mLevel);

        // Init engine
        mGameEngine = new GameEngine(getGameActivity(), (GameView) getView().findViewById(R.id.game_view));
        mGameEngine.setPixelFactor(3300);
        mGameEngine.setInputController(new BasicInputController(mGameEngine));
        mGameEngine.setLevel(level);
        mGameEngine.setSoundManager(getGameActivity().getSoundManager());

        // Init all the object and add to engine
        new GameController(mGameEngine, this).addToGameEngine(mGameEngine, 0);
        new MoveCounter(mGameEngine).addToGameEngine(mGameEngine, 0);
        new ComboCounter(mGameEngine).addToGameEngine(mGameEngine, 0);
        new ScoreCounter(mGameEngine).addToGameEngine(mGameEngine, 0);
        new ProgressBarCounter(mGameEngine).addToGameEngine(mGameEngine, 0);
        setTargetCounter(level.mLevelType);
        new Player(mGameEngine).addToGameEngine(mGameEngine, 1);
        mGameEngine.startGame();

        // Start bgm
        getGameActivity().getSoundManager().loadMusic(R.raw.village);
    }

    private void setTargetCounter(LevelType type) {
        switch (type) {
            case POP_BUBBLE:
                new PopTargetCounter(mGameEngine).addToGameEngine(mGameEngine, 0);
                break;
            case COLLECT_ITEM:
                new CollectTargetCounter(mGameEngine).addToGameEngine(mGameEngine, 0);
                break;
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_pause) {
            getGameActivity().getSoundManager().playSound(MySoundEvent.BUTTON_CLICK);
            pauseGameAndShowPauseDialog();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mGameEngine.isRunning() && !mGameEngine.isPaused()) {
            pauseGameAndShowPauseDialog();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mGameEngine.stopGame();
        // Stop the game bgm
        getGameActivity().getSoundManager().unloadMusic();
        // Start the menu bgm
        getGameActivity().getSoundManager().loadMusic(R.raw.happy_and_joyful_children);
    }

    @Override
    public boolean onBackPressed() {
        if (mGameEngine.isRunning() && !mGameEngine.isPaused()) {
            pauseGameAndShowPauseDialog();
            return true;
        }
        return super.onBackPressed();
    }

    private void pauseGameAndShowPauseDialog() {
        mGameEngine.pauseGame();
        // Show pause dialog
        PauseDialog dialog = new PauseDialog(getGameActivity(), mLevel) {
            @Override
            public void resumeGame() {
                mGameEngine.resumeGame();
            }

            @Override
            public void quitGame() {
                mGameEngine.stopGame();
                // We reduce one live and navigate back to map
                ((MainActivity) getGameActivity()).getLivesTimer().reduceLive();
                getGameActivity().navigateBack();
            }
        };
        showDialog(dialog);
    }

    public void pauseGameAndShowRewardedAd() {
        // Show rewardedAd
        AdManager ad = ((MainActivity) getGameActivity()).getAdManager();
        ad.setListener(this);
        boolean isConnect = ad.showRewardAd();
        // Check connection
        if (isConnect) {
            // Pause the game when loading Ad, or the pause dialog will show
            mGameEngine.pauseGame();
        } else {
            // Show error dialog if no internet connect
            ErrorDialog dialog = new ErrorDialog(getGameActivity()) {
                @Override
                public void retry() {
                    ad.requestAd();
                    pauseGameAndShowRewardedAd();
                }

                @Override
                public void quit() {
                    mGameEngine.onGameEvent(MyGameEvent.GAME_OVER);
                }
            };
            showDialog(dialog);
        }
    }

    @Override
    public void onEarnReward() {
        // The Ad will pause the game, so we resume it
        mGameEngine.resumeGame();
        mGameEngine.onGameEvent(MyGameEvent.ADD_EXTRA_MOVE);
    }

    @Override
    public void onLossReward() {
        // The Ad will pause the game, so we resume it
        mGameEngine.resumeGame();
        mGameEngine.onGameEvent(MyGameEvent.GAME_OVER);
    }

}
