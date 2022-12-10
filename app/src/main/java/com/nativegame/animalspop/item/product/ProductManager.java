package com.nativegame.animalspop.item.product;

import android.app.Activity;

import com.nativegame.animalspop.R;
import com.nativegame.animalspop.item.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Oscar Liang on 2022/09/18
 */

public class ProductManager {

    private final List<Product> mProductList = new ArrayList<>();

    public ProductManager(Activity activity) {
        // Init all the product
        Product productWatchAd = new Product(Item.COIN, 0);
        Product productColorBall = new Product(Item.COLOR_BALL, 50);
        Product productFireball = new Product(Item.FIREBALL, 60);
        Product productBomb = new Product(Item.BOMB, 70);

        //Init product view id
        productWatchAd.setView(R.drawable.product_coin_50, R.drawable.btn_watch_ad);
        productColorBall.setView(R.drawable.product_color_ball, R.drawable.btn_price_50);
        productFireball.setView(R.drawable.product_fireball, R.drawable.btn_price_60);
        productBomb.setView(R.drawable.product_bomb, R.drawable.btn_price_70);

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

    public List<Product> getAllProducts() {
        return mProductList;
    }

}
