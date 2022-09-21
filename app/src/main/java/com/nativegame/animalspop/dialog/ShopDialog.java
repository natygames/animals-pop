package com.nativegame.animalspop.dialog;

import android.view.View;
import android.widget.ImageButton;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nativegame.animalspop.R;
import com.nativegame.animalspop.Utils;
import com.nativegame.animalspop.database.DatabaseHelper;
import com.nativegame.animalspop.shop.Product;
import com.nativegame.animalspop.shop.ProductAdapter;
import com.nativegame.animalspop.shop.ProductList;
import com.nativegame.animalspop.sound.MySoundEvent;
import com.nativegame.engine.ui.GameActivity;
import com.nativegame.engine.ui.GameDialog;

import java.util.ArrayList;

/**
 * Created by Oscar Liang on 2022/09/18
 */

public class ShopDialog extends GameDialog implements View.OnClickListener {

    public ShopDialog(GameActivity activity) {
        super(activity);
        setContentView(R.layout.dialog_shop);
        setRootLayoutResId(R.layout.dialog_container);
        setEnterAnimationResId(R.anim.enter_from_center);
        setExitAnimationResId(R.anim.exit_to_center);
        init();
    }

    private void init() {
        // Init button
        ImageButton btnCancel = (ImageButton) findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(this);
        Utils.createButtonEffect(btnCancel);

        //Init pop effect
        Utils.createPopUpEffect(findViewById(R.id.image_fox));
        Utils.createPopUpEffect(findViewById(R.id.image_fox_bg), 2);

        initProduct(new ProductList(mParent).getProductLis());
    }

    private void initProduct(ArrayList<Product> productList) {
        ProductAdapter productAdapter = new ProductAdapter(mParent, productList) {
            @Override
            public void showDialog(Product product) {
                if (product.getName().equals(DatabaseHelper.ITEM_COIN)) {
                    // Show the ad dialog
                    AdCoinDialog adCoinDialog = new AdCoinDialog(mParent) {
                        @Override
                        public void updateCoin() {
                            updateMapCoin();
                        }
                    };
                    mParent.showDialog(adCoinDialog);
                } else {
                    // Show the confirm dialog
                    ConfirmDialog confirmDialog = new ConfirmDialog(mParent, product) {
                        @Override
                        public void updateCoin() {
                            updateMapCoin();
                        }
                    };
                    mParent.showDialog(confirmDialog);
                }
            }
        };
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_product);
        recyclerView.setLayoutManager(new LinearLayoutManager(mParent, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(productAdapter);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_cancel) {
            mParent.getSoundManager().playSound(MySoundEvent.BUTTON_CLICK);
            dismiss();
        }
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

    public void updateMapCoin() {
        // Override this method to update coin num
    }

}
