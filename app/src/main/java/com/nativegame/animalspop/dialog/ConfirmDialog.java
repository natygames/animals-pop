package com.nativegame.animalspop.dialog;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.nativegame.animalspop.MainActivity;
import com.nativegame.animalspop.R;
import com.nativegame.animalspop.Utils;
import com.nativegame.animalspop.database.DatabaseHelper;
import com.nativegame.animalspop.shop.Product;
import com.nativegame.animalspop.sound.MySoundEvent;
import com.nativegame.engine.ui.GameActivity;
import com.nativegame.engine.ui.GameDialog;

/**
 * Created by Oscar Liang on 2022/09/18
 */

public class ConfirmDialog extends GameDialog implements View.OnClickListener {

    private final Product mProduct;
    private final DatabaseHelper mDatabaseHelper;

    private int mSelectedId;

    public ConfirmDialog(GameActivity activity, Product product) {
        super(activity);
        setContentView(R.layout.dialog_confirm);
        setRootLayoutResId(R.layout.dialog_container);
        setEnterAnimationResId(R.anim.enter_from_center);
        setExitAnimationResId(R.anim.exit_to_center);
        mProduct = product;
        mDatabaseHelper = ((MainActivity) activity).getDatabaseHelper();
        init();
    }

    private void init() {
        //Init product image
        ImageView imageProduct = (ImageView) findViewById(R.id.image_product);
        imageProduct.setImageResource(mProduct.getDrawableResId());

        // Init button
        ImageButton btnCancel = (ImageButton) findViewById(R.id.btn_cancel);
        ImageButton btnConfirm = (ImageButton) findViewById(R.id.btn_confirm);
        btnCancel.setOnClickListener(this);
        btnConfirm.setOnClickListener(this);
        Utils.createButtonEffect(btnCancel);
        Utils.createButtonEffect(btnConfirm);

        // Init pop up
        Utils.createPopUpEffect(btnConfirm);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_cancel) {
            mParent.getSoundManager().playSound(MySoundEvent.BUTTON_CLICK);
            dismiss();
        } else if (id == R.id.btn_confirm) {
            mParent.getSoundManager().playSound(MySoundEvent.BUTTON_CLICK);
            mSelectedId = id;
            buy();
            dismiss();
        }
    }

    private void buy() {
        // Check is player has enough saving coin
        int price = mProduct.getPrice();
        int saving = mDatabaseHelper.getItemNum(DatabaseHelper.ITEM_COIN);
        if (saving < price) {
            // Show the ad dialog if saving not enough
            mSelectedId = R.id.btn_watch_ad;
            return;
        }

        // Update coin from db
        mDatabaseHelper.updateItemNum(DatabaseHelper.ITEM_COIN, saving - price);

        // Update item from db
        int num = mDatabaseHelper.getItemNum(mProduct.getName());
        mDatabaseHelper.updateItemNum(mProduct.getName(), num + 1);
    }

    @Override
    public void show() {
        super.show();
        mParent.getSoundManager().playSound(MySoundEvent.SWEEP_01);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        mParent.getSoundManager().playSound(MySoundEvent.SWEEP_02);
    }

    @Override
    protected void onDismiss() {
        if (mSelectedId == R.id.btn_watch_ad) {
            AdCoinDialog adCoinDialog = new AdCoinDialog(mParent);
            mParent.showDialog(adCoinDialog);
        } else if (mSelectedId == R.id.btn_confirm) {
            NewBoosterDialog newBoosterDialog = new NewBoosterDialog(mParent, mProduct.getDrawableResId());
            mParent.showDialog(newBoosterDialog);
            updateCoin();
        }
    }

    public void updateCoin() {
        // Override this method to update coin num
    }

}
