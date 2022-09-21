package com.nativegame.animalspop.shop;

import android.app.Activity;

import com.nativegame.animalspop.R;
import com.nativegame.animalspop.database.DatabaseHelper;

import java.util.ArrayList;

/**
 * Created by Oscar Liang on 2022/09/18
 */

public class ProductList {

    private final ArrayList<Product> mProductList = new ArrayList<>();

    public ProductList(Activity activity) {
        // Init all thr product
        Product productWatchAd = new Product(DatabaseHelper.ITEM_COIN,
                R.drawable.product_coin_50,
                R.drawable.btn_watch_ad,
                0);
        Product productColorBall = new Product(DatabaseHelper.ITEM_COLOR_BALL,
                R.drawable.product_color_ball,
                R.drawable.btn_price_50,
                50);
        Product productFireball = new Product(DatabaseHelper.ITEM_FIREBALL,
                R.drawable.product_fireball,
                R.drawable.btn_price_60,
                60);
        Product productBomb = new Product(DatabaseHelper.ITEM_BOMB,
                R.drawable.product_bomb,
                R.drawable.btn_price_70,
                70);
        // Init product description
        productWatchAd.setDescription(activity.getString(R.string.txt_coins));
        productColorBall.setDescription(activity.getString(R.string.txt_color_ball));
        productFireball.setDescription(activity.getString(R.string.txt_fireball));
        productBomb.setDescription(activity.getString(R.string.txt_bomb));
        // Add to list
        mProductList.add(productWatchAd);
        mProductList.add(productColorBall);
        mProductList.add(productFireball);
        mProductList.add(productBomb);
    }

    public ArrayList<Product> getProductLis() {
        return mProductList;
    }

}
