package com.nativegame.animalspop.prize;

import com.nativegame.animalspop.R;
import com.nativegame.animalspop.database.DatabaseHelper;

import java.util.HashMap;

/**
 * Created by Oscar Liang on 2022/09/18
 */

public class PrizeList {

    private final HashMap<String, Prize> mPrizeList = new HashMap<>();

    // Prize key
    public static final String PRIZE_COIN_50 = "prize_coin_50";
    public static final String PRIZE_COIN_150 = "prize_coin_150";
    public static final String PRIZE_COLOR_BALL = "prize_color_ball";
    public static final String PRIZE_FIREBALL = "prize_fireball";
    public static final String PRIZE_BOMB = "prize_bomb";

    public PrizeList() {
        // Init all the prize
        Prize prizeCoin50 = new Prize(DatabaseHelper.ITEM_COIN, R.drawable.product_coin_50, 50);
        Prize prizeCoin150 = new Prize(DatabaseHelper.ITEM_COIN, R.drawable.product_coin_150, 150);
        Prize prizeColorBall = new Prize(DatabaseHelper.ITEM_COLOR_BALL, R.drawable.product_color_ball, 1);
        Prize prizeFireball = new Prize(DatabaseHelper.ITEM_FIREBALL, R.drawable.product_fireball, 1);
        Prize prizeBomb = new Prize(DatabaseHelper.ITEM_BOMB, R.drawable.product_bomb, 1);
        // Add to list
        mPrizeList.put(PRIZE_COIN_50, prizeCoin50);
        mPrizeList.put(PRIZE_COIN_150, prizeCoin150);
        mPrizeList.put(PRIZE_COLOR_BALL, prizeColorBall);
        mPrizeList.put(PRIZE_FIREBALL, prizeFireball);
        mPrizeList.put(PRIZE_BOMB, prizeBomb);
    }

    public Prize getPrize(String key) {
        return mPrizeList.get(key);
    }

}
