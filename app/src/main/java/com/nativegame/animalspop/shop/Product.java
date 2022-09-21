package com.nativegame.animalspop.shop;

/**
 * Created by Oscar Liang on 2022/09/18
 */

public class Product {

    private final String mName;
    private final int mDrawableResId;
    private final int mButtonResId;
    private final int mPrice;

    private String mDescription;

    public Product(String name, int drawableResId, int buttonResId, int price) {
        mName = name;
        mDrawableResId = drawableResId;
        mButtonResId = buttonResId;
        mPrice = price;
    }

    public String getName() {
        return mName;
    }

    public int getDrawableResId() {
        return mDrawableResId;
    }

    public int getButtonResId() {
        return mButtonResId;
    }

    public int getPrice() {
        return mPrice;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getDescription() {
        return mDescription;
    }

}
