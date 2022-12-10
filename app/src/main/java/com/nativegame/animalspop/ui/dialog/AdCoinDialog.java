package com.nativegame.animalspop.ui.dialog;

import android.view.View;
import android.widget.ImageButton;

import com.nativegame.animalspop.AdManager;
import com.nativegame.animalspop.MainActivity;
import com.nativegame.animalspop.R;
import com.nativegame.animalspop.item.Item;
import com.nativegame.animalspop.ui.UIEffect;
import com.nativegame.animalspop.database.DatabaseHelper;
import com.nativegame.animalspop.sound.MySoundEvent;
import com.nativegame.nattyengine.ui.GameActivity;
import com.nativegame.nattyengine.ui.GameDialog;

/**
 * Created by Oscar Liang on 2022/09/18
 */

public class AdCoinDialog extends GameDialog implements View.OnClickListener,
        AdManager.AdRewardListener {

    private static final int REWARD_COIN = 50;

    private int mSelectedId;

    public AdCoinDialog(GameActivity activity) {
        super(activity);
        setContentView(R.layout.dialog_ad_coin);
        setRootLayoutId(R.layout.dialog_container);
        setEnterAnimationId(R.anim.enter_from_center);
        setExitAnimationId(R.anim.exit_to_center);
        init();
    }

    private void init() {
        // Init button
        ImageButton btnCancel = (ImageButton) findViewById(R.id.btn_cancel);
        ImageButton btnWatchAd = (ImageButton) findViewById(R.id.btn_watch_ad);
        btnCancel.setOnClickListener(this);
        btnWatchAd.setOnClickListener(this);
        UIEffect.createButtonEffect(btnCancel);
        UIEffect.createButtonEffect(btnWatchAd);

        // Init pop up
        UIEffect.createPopUpEffect(btnWatchAd);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_cancel) {
            mParent.getSoundManager().playSound(MySoundEvent.BUTTON_CLICK);
            dismiss();
        } else if (id == R.id.btn_watch_ad) {
            mParent.getSoundManager().playSound(MySoundEvent.BUTTON_CLICK);
            mSelectedId = id;
            dismiss();
        }
    }

    private void showAd() {
        AdManager ad = ((MainActivity) mParent).getAdManager();
        ad.setListener(this);
        boolean isConnect = ad.showRewardAd();

        // Show error dialog if no internet connect
        if (!isConnect) {
            ErrorDialog dialog = new ErrorDialog(mParent) {
                @Override
                public void retry() {
                    ((MainActivity) mParent).getAdManager().requestAd();
                    showAd();
                }
            };
            mParent.showDialog(dialog);
        }
    }

    @Override
    public void onEarnReward() {
        DatabaseHelper databaseHelper = ((MainActivity) mParent).getDatabaseHelper();
        // Update coin from db
        int saving = databaseHelper.getItemNum(Item.COIN);
        databaseHelper.updateItemNum(Item.COIN, saving + REWARD_COIN);
        updateCoin();
    }

    @Override
    public void onLossReward() {
        // We do nothing
    }

    @Override
    protected void onShow() {
        mParent.getSoundManager().playSound(MySoundEvent.SWEEP_IN);
    }

    @Override
    protected void onDismiss() {
        mParent.getSoundManager().playSound(MySoundEvent.SWEEP_OUT);
    }

    @Override
    protected void onHide() {
        if (mSelectedId == R.id.btn_watch_ad) {
            showAd();
        }
    }

    public void updateCoin() {
        // Override this method to update coin num
    }

}
