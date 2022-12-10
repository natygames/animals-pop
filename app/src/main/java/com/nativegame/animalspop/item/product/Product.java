package com.nativegame.animalspop.item.product;

/**
 * Created by Oscar Liang on 2022/09/18
 */

public class Product {

    private final String mName;
    private final int mPrice;

    private int mDrawableId;
    private int mButtonId;
    private String mDescription;

    public Product(String name, int price) {
        mName = name;
        mPrice = price;
    }

    public String getName() {
        return mName;
    }

    public int getPrice() {
        return mPrice;
    }

    public int getDrawableId() {
        return mDrawableId;
    }

    public int getButtonId() {
        return mButtonId;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public void setView(int drawableResId, int buttonResId) {
        mDrawableId = drawableResId;
        mButtonId = buttonResId;
    }

}
