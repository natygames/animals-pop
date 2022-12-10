package com.nativegame.animalspop.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.nativegame.animalspop.MainActivity;
import com.nativegame.animalspop.item.Item;
import com.nativegame.animalspop.ui.TransitionEffect;
import com.nativegame.animalspop.R;
import com.nativegame.animalspop.ui.UIEffect;
import com.nativegame.animalspop.database.DatabaseHelper;
import com.nativegame.animalspop.ui.dialog.AdLivesDialog;
import com.nativegame.animalspop.ui.dialog.LevelDialog;
import com.nativegame.animalspop.ui.dialog.SettingDialog;
import com.nativegame.animalspop.ui.dialog.ShopDialog;
import com.nativegame.animalspop.ui.dialog.WheelDialog;
import com.nativegame.animalspop.sound.MySoundEvent;
import com.nativegame.animalspop.timer.LivesTimer;
import com.nativegame.animalspop.timer.WheelTimer;
import com.nativegame.nattyengine.ui.GameFragment;

import java.util.ArrayList;

/**
 * Created by Oscar Liang on 2022/09/18
 */

public class MapFragment extends GameFragment implements View.OnClickListener,
        TransitionEffect.OnTransitionListener {

    private static final int TOTAL_LEVEL = 15;

    private DatabaseHelper mDatabaseHelper;
    private LivesTimer mLivesTimer;
    private WheelTimer mWheelTimer;
    private TransitionEffect mTransitionEffect;

    private ArrayList<Integer> mLevelStar;
    private int mCurrentLevel;

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mDatabaseHelper = ((MainActivity) getGameActivity()).getDatabaseHelper();
        mLivesTimer = ((MainActivity) getGameActivity()).getLivesTimer();
        mWheelTimer = new WheelTimer(getGameActivity());
        mTransitionEffect = new TransitionEffect(getGameActivity());
        mTransitionEffect.setListener(this);
        // Load level data from db and init
        mLevelStar = mDatabaseHelper.getAllLevelStar();
        mCurrentLevel = mLevelStar.size() + 1;
        init();
    }

    @Override
    protected void onLayoutCreated() {
        if (mCurrentLevel > TOTAL_LEVEL) {
            return;
        }
        // Scroll to current level position
        ScrollView scrollView = getView().findViewById(R.id.layout_map);
        TextView txtLevel = (TextView) findViewByName("btn_level_" + mCurrentLevel);
        // Align the level button to center
        scrollView.scrollTo(0, txtLevel.getBottom() - scrollView.getHeight() / 2);
    }

    private void init() {
        // Init button
        ImageButton btnSetting = (ImageButton) getView().findViewById(R.id.btn_setting);
        ImageButton btnShop = (ImageButton) getView().findViewById(R.id.btn_shop);
        ImageButton btnCoin = (ImageButton) getView().findViewById(R.id.btn_coin);
        ImageButton btnWheel = (ImageButton) getView().findViewById(R.id.btn_wheel);
        btnSetting.setOnClickListener(this);
        btnShop.setOnClickListener(this);
        btnCoin.setOnClickListener(this);
        btnWheel.setOnClickListener(this);
        UIEffect.createButtonEffect(btnSetting);
        UIEffect.createButtonEffect(btnShop);
        UIEffect.createButtonEffect(btnCoin);
        UIEffect.createButtonEffect(btnWheel);
        // Init lives text
        TextView txtLives = (TextView) getView().findViewById(R.id.txt_lives);
        txtLives.setOnClickListener(this);
        UIEffect.createButtonEffect(txtLives);
        // Init layout
        initLevelButton();
        initLevelStar();
        initAd();
        loadCoin();

        // We only show one dialog at a time
        getView().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mWheelTimer.isWheelReady()) {
                    // Show the wheel dialog
                    showWheelDialogAndShowLevel();
                } else {
                    // Show the current level dialog
                    showLevelDialog(mCurrentLevel);
                }
            }
        }, 1200);
    }

    private View findViewByName(String name) {
        int id = getResources().getIdentifier(name, "id", getGameActivity().getPackageName());
        return getView().findViewById(id);
    }

    private void initLevelButton() {
        // Init button listener and star
        for (int i = 1; i <= TOTAL_LEVEL; i++) {
            // Init button
            TextView txtLevel = (TextView) findViewByName("btn_level_" + i);

            // Init button listener
            if (i <= mCurrentLevel) {
                int level = i;
                txtLevel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showLevelDialog(level);
                        getGameActivity().getSoundManager().playSound(MySoundEvent.BUTTON_CLICK);
                    }
                });
                txtLevel.setBackgroundResource(R.drawable.btn_level);
                txtLevel.setTextColor(getResources().getColor(R.color.brown));
                UIEffect.createButtonEffect(txtLevel);
            } else {
                txtLevel.setBackgroundResource(R.drawable.btn_level_lock);
                txtLevel.setTextColor(getResources().getColor(R.color.white));
            }
        }
    }

    private void initLevelStar() {
        // Init button listener and star
        for (int i = 1; i <= TOTAL_LEVEL; i++) {
            // Init star
            ImageView imageStar = (ImageView) findViewByName("image_level_star_" + i);

            // Update level star
            if (i < mCurrentLevel) {
                int star = mLevelStar.get(i - 1);
                switch (star) {
                    case 1:
                        imageStar.setImageResource(R.drawable.star_set_01);
                        break;
                    case 2:
                        imageStar.setImageResource(R.drawable.star_set_02);
                        break;
                    case 3:
                        imageStar.setImageResource(R.drawable.star_set_03);
                        break;
                }
                imageStar.setVisibility(View.VISIBLE);
            } else {
                imageStar.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void initAd() {
        AdView adView = getView().findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    private void loadCoin() {
        TextView textCoin = (TextView) getView().findViewById(R.id.txt_coin);
        int coin = mDatabaseHelper.getItemNum(Item.COIN);
        textCoin.setText(String.valueOf(coin));
    }

    private void showLevelDialog(int level) {
        if (level > TOTAL_LEVEL) {
            return;
        }
        LevelDialog levelDialog = new LevelDialog(getGameActivity(), level) {
            @Override
            public void navigateToGame() {
                // Check player lives
                if (mLivesTimer.isEnoughLives()) {
                    super.navigateToGame();
                } else {
                    showLiveNotEnoughDialog();
                }
            }

            @Override
            public void startGame() {
                getGameActivity().navigateToFragment(MyGameFragment.newInstance(level));
                // Stop the bgm
                getGameActivity().getSoundManager().unloadMusic();
            }
        };
        showDialog(levelDialog);
    }

    private void showLiveNotEnoughDialog() {
        AdLivesDialog adLivesDialog = new AdLivesDialog(getGameActivity());
        showDialog(adLivesDialog);
    }

    private void showWheelDialog() {
        WheelDialog wheelDialog = new WheelDialog(getGameActivity(), mWheelTimer) {
            @Override
            public void updateCoin() {
                loadCoin();
            }
        };
        showDialog(wheelDialog);
    }

    private void showWheelDialogAndShowLevel() {
        WheelDialog wheelDialog = new WheelDialog(getGameActivity(), mWheelTimer) {
            @Override
            public void showLevel() {
                showLevelDialog(mCurrentLevel);
            }

            @Override
            public void updateCoin() {
                loadCoin();
            }
        };
        showDialog(wheelDialog);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_setting) {
            getGameActivity().getSoundManager().playSound(MySoundEvent.BUTTON_CLICK);
            SettingDialog settingDialog = new SettingDialog(getGameActivity());
            showDialog(settingDialog);
        } else if (id == R.id.btn_shop || id == R.id.btn_coin) {
            getGameActivity().getSoundManager().playSound(MySoundEvent.BUTTON_CLICK);
            ShopDialog shopDialog = new ShopDialog(getGameActivity()) {
                @Override
                public void updateMapCoin() {
                    loadCoin();
                }
            };
            showDialog(shopDialog);
        } else if (id == R.id.btn_wheel) {
            getGameActivity().getSoundManager().playSound(MySoundEvent.BUTTON_CLICK);
            showWheelDialog();
        } else if (id == R.id.txt_lives) {
            getGameActivity().getSoundManager().playSound(MySoundEvent.BUTTON_CLICK);
            if (!mLivesTimer.isLivesFull()) {
                showLiveNotEnoughDialog();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mLivesTimer.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        mLivesTimer.stop();
    }

    @Override
    public boolean onBackPressed() {
        mTransitionEffect.show();
        return true;
    }

    @Override
    public void onTransition() {
        getGameActivity().navigateToFragment(new MenuFragment());
    }

}
