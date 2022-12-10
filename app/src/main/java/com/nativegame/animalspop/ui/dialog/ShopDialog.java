package com.nativegame.animalspop.ui.dialog;

import android.view.View;
import android.widget.ImageButton;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nativegame.animalspop.R;
import com.nativegame.animalspop.item.Item;
import com.nativegame.animalspop.ui.UIEffect;
import com.nativegame.animalspop.item.product.Product;
import com.nativegame.animalspop.ui.view.ProductAdapter;
import com.nativegame.animalspop.item.product.ProductManager;
import com.nativegame.animalspop.sound.MySoundEvent;
import com.nativegame.nattyengine.ui.GameActivity;
import com.nativegame.nattyengine.ui.GameDialog;

import java.util.List;

/**
 * Created by Oscar Liang on 2022/09/18
 */

public class ShopDialog extends GameDialog implements View.OnClickListener {

    public ShopDialog(GameActivity activity) {
        super(activity);
        setContentView(R.layout.dialog_shop);
        setRootLayoutId(R.layout.dialog_container);
        setEnterAnimationId(R.anim.enter_from_center);
        setExitAnimationId(R.anim.exit_to_center);
        init();
    }

    private void init() {
        // Init button
        ImageButton btnCancel = (ImageButton) findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(this);
        UIEffect.createButtonEffect(btnCancel);

        //Init pop effect
        UIEffect.createPopUpEffect(findViewById(R.id.image_fox));
        UIEffect.createPopUpEffect(findViewById(R.id.image_fox_bg), 2);

        initProduct(new ProductManager(mParent).getAllProducts());
    }

    private void initProduct(List<Product> productList) {
        ProductAdapter productAdapter = new ProductAdapter(mParent, productList) {
            @Override
            public void showDialog(Product product) {
                if (product.getName().equals(Item.COIN)) {
                    // Show the ad dialog
                    showAdCoinDialog();
                } else {
                    // Show the confirm dialog
                    showConfirmDialog(product);
                }
            }
        };
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_product);
        recyclerView.setLayoutManager(new LinearLayoutManager(mParent, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(productAdapter);
    }

    private void showAdCoinDialog() {
        AdCoinDialog adCoinDialog = new AdCoinDialog(mParent) {
            @Override
            public void updateCoin() {
                updateMapCoin();
            }
        };
        mParent.showDialog(adCoinDialog);
    }

    private void showConfirmDialog(Product product) {
        ConfirmDialog confirmDialog = new ConfirmDialog(mParent, product) {
            @Override
            public void updateCoin() {
                updateMapCoin();
            }
        };
        mParent.showDialog(confirmDialog);
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
    protected void onShow() {
        mParent.getSoundManager().playSound(MySoundEvent.SWEEP_IN);
    }

    @Override
    protected void onDismiss() {
        mParent.getSoundManager().playSound(MySoundEvent.SWEEP_OUT);
    }

    public void updateMapCoin() {
        // Override this method to update coin num
    }

}
