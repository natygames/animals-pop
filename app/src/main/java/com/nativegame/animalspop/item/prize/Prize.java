package com.nativegame.animalspop.item.prize;

/**
 * Created by Oscar Liang on 2022/09/18
 */

public class Prize {

    private final String mName;
    private final int mNum;

    private int mDrawableId;

    public Prize(String name, int num) {
        mName = name;
        mNum = num;
    }

    public String getName() {
        return mName;
    }

    public int getDrawableResId() {
        return mDrawableId;
    }

    public int getNum() {
        return mNum;
    }

    public void setView(int drawableId) {
        mDrawableId = drawableId;
    }

}
